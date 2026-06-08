package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.DatabaseRepository
import com.example.data.database.LorePost
import com.example.data.database.OracleSession
import com.example.data.model.ArtifactPackage
import com.example.data.network.GeminiRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

sealed interface OracleUiState {
    object Idle : OracleUiState
    object Loading : OracleUiState
    data class Success(val artifact: ArtifactPackage) : OracleUiState
    data class Error(val message: String) : OracleUiState
}

class OracleViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = DatabaseRepository(db.oracleSessionDao(), db.lorePostDao())
    private val geminiRepo = GeminiRepository()
    private val sharedPrefs: SharedPreferences = application.getSharedPreferences("oracle_machine_prefs", Context.MODE_PRIVATE)

    // Flowing States
    val sessions: StateFlow<List<OracleSession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lorePosts: StateFlow<List<LorePost>> = repository.allLorePosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow<OracleUiState>(OracleUiState.Idle)
    val uiState: StateFlow<OracleUiState> = _uiState.asStateFlow()

    private val _hasAskedToday = MutableStateFlow(false)
    val hasAskedToday: StateFlow<Boolean> = _hasAskedToday.asStateFlow()

    private val _countdownText = MutableStateFlow("")
    val countdownText: StateFlow<String> = _countdownText.asStateFlow()

    private val _streakCount = MutableStateFlow(0)
    val streakCount: StateFlow<Int> = _streakCount.asStateFlow()

    // Interpretation results
    private val _interpretationText = MutableStateFlow("")
    val interpretationText: StateFlow<String> = _interpretationText.asStateFlow()

    private val _isInterpreting = MutableStateFlow(false)
    val isInterpreting: StateFlow<Boolean> = _isInterpreting.asStateFlow()

    // Config states
    private val _loreTheme = MutableStateFlow("The Whispering Grid")
    val loreTheme: StateFlow<String> = _loreTheme.asStateFlow()

    private val _personalityMode = MutableStateFlow("Cryptic Gothic")
    val personalityMode: StateFlow<String> = _personalityMode.asStateFlow()

    private var countdownJob: Job? = null

    init {
        // Load custom config values from shared preferences
        _loreTheme.value = sharedPrefs.getString("lore_theme", "The Whispering Grid") ?: "The Whispering Grid"
        _personalityMode.value = sharedPrefs.getString("personality_mode", "Cryptic Gothic") ?: "Cryptic Gothic"

        // Perform initial checklists
        viewModelScope.launch {
            checkIfAskedToday()
            calculateActiveStreak()
            prepopulateLorePostsIfEmpty()
        }
        startCountdownTimer()
    }

    // Checking if already asked question today
    suspend fun checkIfAskedToday() {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val todaySession = repository.getSessionByDate(todayStr)
        _hasAskedToday.value = todaySession != null
        if (todaySession != null && _uiState.value is OracleUiState.Idle) {
            val art = ArtifactPackage.fromJson(todaySession.responseJson)
            _uiState.value = OracleUiState.Success(art)
        }
    }

    private fun startCountdownTimer() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                val diffMs = getMillisUntilMidnight()
                if (diffMs <= 0) {
                    _hasAskedToday.value = false
                    _countdownText.value = "00:00:00"
                } else {
                    val hours = diffMs / (3600 * 1000)
                    val minutes = (diffMs % (3600 * 1000)) / (60 * 1000)
                    val seconds = (diffMs % (60 * 1000)) / 1000
                    _countdownText.value = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
                }
                delay(1000)
            }
        }
    }

    private fun getMillisUntilMidnight(): Long {
        val now = Calendar.getInstance()
        val midnight = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 24)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return midnight.timeInMillis - now.timeInMillis
    }

    // Streak logic on-the-fly based on past dates
    fun calculateActiveStreak() {
        viewModelScope.launch {
            sessions.collect { list ->
                if (list.isEmpty()) {
                    _streakCount.value = 0
                    return@collect
                }
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val sortedUniqueDates = list.map { it.dateString }
                    .distinct()
                    .sortedByDescending { it }

                val todayStr = format.format(Date())
                val todayDate = format.parse(todayStr) ?: return@collect
                val firstDate = format.parse(sortedUniqueDates[0]) ?: return@collect

                val diffSecs = (todayDate.time - firstDate.time) / 1000
                val diffDays = diffSecs / (24 * 3600)

                // If gap is more than 1 day, streak is broken
                if (diffDays > 1) {
                    _streakCount.value = 0
                    return@collect
                }

                var streak = 1
                for (i in 0 until sortedUniqueDates.size - 1) {
                    val current = format.parse(sortedUniqueDates[i]) ?: break
                    val next = format.parse(sortedUniqueDates[i + 1]) ?: break
                    val gap = (current.time - next.time) / (1000 * 24 * 3600)
                    if (gap == 1L) {
                        streak++
                    } else if (gap > 1L) {
                        break
                    }
                }
                _streakCount.value = streak
            }
        }
    }

    // Submit daily question routine
    fun selectSessionPackage(artifact: ArtifactPackage) {
        _uiState.value = OracleUiState.Success(artifact)
        _hasAskedToday.value = true
    }

    fun askOracle(question: String) {
        viewModelScope.launch {
            if (_hasAskedToday.value) {
                _uiState.value = OracleUiState.Error("The terminal has cycled. Submit another inquiry when the clocks align.")
                return@launch
            }
            _uiState.value = OracleUiState.Loading

            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val pastThemes = sessions.value.map { it.theme }.take(10)

            try {
                // Call Gemini API Repository
                val resultJson = geminiRepo.generateOracleResponse(
                    question = question,
                    activeLoreTheme = _loreTheme.value,
                    userPastThemes = pastThemes
                )

                val artifact = ArtifactPackage.fromJson(resultJson)

                // Write session and trigger update in database
                val newSession = OracleSession(
                    question = question,
                    timestamp = System.currentTimeMillis(),
                    dateString = todayStr,
                    responseJson = resultJson,
                    theme = artifact.theme
                )
                repository.insertSession(newSession)

                _uiState.value = OracleUiState.Success(artifact)
                _hasAskedToday.value = true
                calculateActiveStreak()

            } catch (e: Exception) {
                Log.e("OracleMachine", "Failed asking oracle", e)
                _uiState.value = OracleUiState.Error("Failed to reach terminal core: ${e.localizedMessage}")
            }
        }
    }

    // Interpretation triggers
    fun runInterpretation(question: String, artifactType: String, artifactValue: String, translateClue: Boolean = false) {
        viewModelScope.launch {
            _isInterpreting.value = true
            _interpretationText.value = ""
            try {
                val interpretation = geminiRepo.interpretArtifact(question, artifactType, artifactValue, translateClue)
                _interpretationText.value = interpretation
            } catch (e: Exception) {
                _interpretationText.value = "The interpretive grid encountered a syntax loop. Try again."
            } finally {
                _isInterpreting.value = false
            }
        }
    }

    // Community post submissions
    fun submitToLoreBoard(post: LorePost) {
        viewModelScope.launch {
            repository.insertLorePost(post)
        }
    }

    // Admin updates
    fun updateSystemConfig(theme: String, mode: String) {
        _loreTheme.value = theme
        _personalityMode.value = mode
        sharedPrefs.edit().apply {
            putString("lore_theme", theme)
            putString("personality_mode", mode)
            apply()
        }
    }

    fun purgeSystems() {
        viewModelScope.launch {
            repository.clearAllSessions()
            repository.clearAllLore()
            _uiState.value = OracleUiState.Idle
            _hasAskedToday.value = false
            _streakCount.value = 0
            prepopulateLorePostsIfEmpty()
        }
    }

    // Prepopulate initial bulletin cards is empty
    private suspend fun prepopulateLorePostsIfEmpty() {
        // Collect first item to check if database is empty
        delay(200) // wait brief second for DB lookup to warm up
        if (lorePosts.value.isEmpty()) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val cal = Calendar.getInstance()
            
            cal.add(Calendar.DAY_OF_YEAR, -2)
            repository.insertLorePost(LorePost(
                author = "Seer_901",
                dateString = sdf.format(cal.time),
                questionSnippet = "What is buried underneath the old mining shaft?",
                messageSnippet = "THE DROWNED CHRONICLE: An anomalous gravity fluctuation shook the bedrock...",
                userInterpretation = "The coordinates point directly to a concrete bunker on Sector 4 coordinates. I'm taking a geiger counter tomorrow.",
                timestamp = cal.timeInMillis
            ))

            cal.add(Calendar.DAY_OF_YEAR, 1)
            repository.insertLorePost(LorePost(
                author = "Gilded_Loom",
                dateString = sdf.format(cal.time),
                questionSnippet = "Who is the entity writing messages?",
                messageSnippet = "THE OBSIDIAN HOURGLASS: Time has migrated, leaking a cold golden dust...",
                userInterpretation = "Guys, I translated the hidden cipher from my audit. It literally says 'WE CHOSE THE COLD CORE'. We aren't alone on this spire.",
                timestamp = cal.timeInMillis
            ))
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
