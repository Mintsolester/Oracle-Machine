package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ParchmentScroll
import com.example.ui.components.TerminalGlassCard
import com.example.ui.components.GoldGlowText
import com.example.ui.theme.*
import com.example.ui.viewmodel.OracleUiState
import com.example.ui.viewmodel.OracleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactViewerScreen(
    viewModel: OracleViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isInterpreting by viewModel.isInterpreting.collectAsState()
    val interpretationText by viewModel.interpretationText.collectAsState()

    var activeArtifactTab by remember { mutableStateOf("TAROT") }
    val mainScrollState = rememberScrollState()

    // Get current active session
    val sessionList by viewModel.sessions.collectAsState()
    val mostRecentSession = sessionList.firstOrNull()
    val activeQuestion = mostRecentSession?.question ?: "Unknown Inquiry"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DECRYPTION ENGINE",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = AgedGold,
                        modifier = Modifier.testTag("decryption_title")
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AgedGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianBlack,
                    titleContentColor = AgedGold
                )
            )
        },
        containerColor = ObsidianBlack
    ) { innerPadding ->
        if (uiState !is OracleUiState.Success) {
            // Emptiness/No session generated today yet
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(ObsidianBlack),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔮", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "NO RECENT CHRONICLE RECORDS",
                        color = AgedGold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Submit a daily question at the main terminal console first.",
                        color = MutedSlate,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            val art = (uiState as OracleUiState.Success).artifact

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(ObsidianBlack)
            ) {
                // Tab Selection slider
                val tabs = listOf("TAROT", "CLIPPING", "DIARY", "TRANSCRIPT", "CIPHER")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .background(DeepCharcoal)
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    tabs.forEach { tab ->
                        val isSelected = tab == activeArtifactTab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isSelected) AgedGold else CharcoalSlate
                                )
                                .clickable {
                                    activeArtifactTab = tab
                                    // Clear interpretation text on tab switch
                                    viewModel.updateSystemConfig(viewModel.loreTheme.value, viewModel.personalityMode.value)
                                }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag("tab_$tab")
                        ) {
                            Text(
                                text = tab,
                                color = if (isSelected) ObsidianBlack else ParchmentWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Main Viewer Scroll layout
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(mainScrollState)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Question Reference Card
                    TerminalGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = AgedGold.copy(alpha = 0.2f),
                        elevation = 2.dp
                    ) {
                        Text(
                            text = "SEEKER'S QUESTION:",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MutedSlate,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "\"$activeQuestion\"",
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Serif,
                            color = ParchmentWhite,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Render selected Artifact Layout
                    AnimatedContent(
                        targetState = activeArtifactTab,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        }
                    ) { tab ->
                        when (tab) {
                            "TAROT" -> TarotCardView(art.symbolicCardTitle, art.symbolicScenePrompt, art.tarotCardMeaning)
                            "CLIPPING" -> NewspaperClippingView(art.newspaperHeadline, art.newspaperContent, art.newspaperDate)
                            "DIARY" -> DiaryPageView(art.diaryNotebookText)
                            "TRANSCRIPT" -> AudioTranscriptView(art.audioTranscript)
                            "CIPHER" -> CipherMapView(art.hiddenCode, art.mapCoordinates, art.mapLocationText)
                        }
                    }

                    // Interpretation engine module
                    TerminalGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("interpretation_cabinet"),
                        borderColor = AgedGold.copy(alpha = 0.3f),
                        elevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "HERMENEUTIC CORE INTERPRETER",
                                color = AgedGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Intelligence core info",
                                tint = AgedGold.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Consult the machine to synthesize connections or translate underlying ciphers in relation to your original question.",
                            color = MutedSlate,
                            fontFamily = FontFamily.Serif,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        val activeValueString = when (activeArtifactTab) {
                            "TAROT" -> "${art.symbolicCardTitle}: ${art.tarotCardMeaning}"
                            "CLIPPING" -> "${art.newspaperHeadline}: ${art.newspaperContent}"
                            "DIARY" -> art.diaryNotebookText
                            "TRANSCRIPT" -> art.audioTranscript
                            else -> "${art.hiddenCode} at ${art.mapCoordinates}"
                        }

                        val actionText = if (activeArtifactTab == "CIPHER") "DECRYPT CLUE SEQUENCE" else "ANALYZE METAPHOR"

                        Button(
                            onClick = {
                                viewModel.runInterpretation(
                                    question = activeQuestion,
                                    artifactType = activeArtifactTab,
                                    artifactValue = activeValueString,
                                    translateClue = (activeArtifactTab == "CIPHER")
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AgedGold,
                                contentColor = ObsidianBlack
                            ),
                            shape = RoundedCornerShape(4.dp),
                            enabled = !isInterpreting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("interpret_button")
                        ) {
                            if (isInterpreting) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        color = ObsidianBlack,
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "SYNTHESIZING RESONANCE...",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else {
                                Text(
                                    text = actionText,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        // Display fetched interpretation
                        if (interpretationText.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, AgedGold.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                    .background(ObsidianBlack.copy(alpha = 0.5f))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "OUTPUT // PARSED LOGICAL CLUES:",
                                        color = PhosphorGreen,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    Text(
                                        text = interpretationText,
                                        color = ParchmentWhite,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Serif,
                                        lineHeight = 19.sp,
                                        modifier = Modifier.testTag("interpretation_result")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Sub-View Tarot Card representation
@Composable
fun TarotCardView(title: String, prompt: String, meaning: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .border(2.dp, AgedGold, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = DeepCharcoal)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TAROT OF THE CORIDORS",
                color = MutedSlate,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Symbolic Sketch area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(1.dp, AgedGold.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                    .background(ObsidianBlack)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌌", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = prompt,
                        color = ParchmentWhite,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            GoldGlowText(
                text = title.uppercase(),
                fontSize = 18f,
                fontFamily = FontFamily.Serif
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = meaning,
                color = MutedSlate,
                fontSize = 13.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

// Sub-View Newspaper clipping representation
@Composable
fun NewspaperClippingView(headline: String, content: String, date: String) {
    ParchmentScroll(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "THE CHRONICLE DISPATCH",
            color = VintageInk,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "DATE OF RECORD: $date  ||  ARCHIVAL SECTION IV",
            color = VintageInk.copy(alpha = 0.5f),
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = headline,
            color = VintageInk,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = content,
            color = VintageInk.copy(alpha = 0.85f),
            fontFamily = FontFamily.Serif,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}

// Sub-View Diary Notebook representation
@Composable
fun DiaryPageView(notebookText: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFC4B29E), RoundedCornerShape(4.dp))
            .background(Color(0xFFFCF9F2)) // elegant antique clean writing page
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "DIARY LEAF FRAGMENT",
                    fontSize = 10.sp,
                    color = Color.DarkGray,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "RECOVERED // TORN",
                    fontSize = 10.sp,
                    color = Color(0xFFA52A2A),
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = notebookText,
                color = Color(0xFF141416), // beautiful vintage sepia pen color
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                lineHeight = 22.sp,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

// Sub-View Audio Transcript representation
@Composable
fun AudioTranscriptView(audioTranscript: String) {
    TerminalGlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = PhosphorGreen.copy(alpha = 0.4f),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "COMMUNICATION INTERCEPT: 147.9MHz",
                color = PhosphorGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.Red)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "TRANSCRIPT DATA RECORD:",
            color = MutedSlate,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Box holding transcript text with typewriter style
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f))
                .border(1.dp, PhosphorGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                .padding(12.dp)
        ) {
            Text(
                text = audioTranscript,
                color = PhosphorGreen,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

// Sub-View Cipher / Coordinate map representation
@Composable
fun CipherMapView(code: String, coordinates: String, locationText: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Code Section card
        TerminalGlassCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = 3.dp
        ) {
            Text(
                text = "CIPHER COUPLING MATRIX // LOGICAL CLUE :",
                color = AgedGold,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = code,
                color = BrightGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Coordinate Card representation
        TerminalGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = Color(0xFF1E88E5).copy(alpha = 0.4f),
            elevation = 3.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Coordinates pinpoint",
                    tint = Color(0xFF1E88E5),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "RADAR LOCATION FIX:",
                        color = MutedSlate,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = coordinates,
                        color = Color(0xFF64B5F6),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "COORDINATE DETAILS:",
                color = MutedSlate,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            Text(
                text = locationText,
                color = ParchmentWhite,
                fontSize = 13.sp,
                fontFamily = FontFamily.Serif,
                lineHeight = 17.sp
            )
        }
    }
}
