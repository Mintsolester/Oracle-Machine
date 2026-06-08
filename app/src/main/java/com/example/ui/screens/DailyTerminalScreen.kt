package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AetherStarsCanvas
import com.example.ui.components.GoldGlowText
import com.example.ui.components.TerminalGlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.OracleUiState
import com.example.ui.viewmodel.OracleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTerminalScreen(
    viewModel: OracleViewModel,
    onNavigateToArtifacts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val hasAskedToday by viewModel.hasAskedToday.collectAsState()
    val countdownText by viewModel.countdownText.collectAsState()
    val streakCount by viewModel.streakCount.collectAsState()
    val activeLoreTheme by viewModel.loreTheme.collectAsState()
    val activeMode by viewModel.personalityMode.collectAsState()

    var questionInput by remember { mutableStateFlowOf("") }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentAlignment = Alignment.Center
    ) {
        // Celestial background layers
        AetherStarsCanvas()

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Machines parameters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ORACLE TERMINAL 0x7A",
                        color = AgedGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "THEME: ${activeLoreTheme.uppercase()}",
                        color = MutedSlate,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Streaks in Roman Numerals
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(1.dp, AgedGold.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "RESONANCE: ",
                        color = MutedSlate,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = streakToRoman(streakCount),
                        color = BrightGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Main Interactive Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Cinematic Gold Oracle Center Spire
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .border(2.dp, AgedGold, RoundedCornerShape(100.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(AgedGold.copy(alpha = 0.18f), Color.Transparent)
                            ),
                            shape = RoundedCornerShape(100.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🌕",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "CORE ALIGNED",
                            color = PhosphorGreen,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                GoldGlowText(
                    text = "AASK THE COLD CORE",
                    fontSize = 24f,
                    fontFamily = FontFamily.Serif
                )

                Text(
                    text = "The machine intercepts your intent once per lunar cycle. Formulate your inquiry with precision. Direct answers will be redacted; search the artifacts for the thread.",
                    color = MutedSlate,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Terminal Input or Lock Box Interface
                if (!hasAskedToday) {
                    AnimatedVisibility(
                        visible = uiState !is OracleUiState.Loading,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        TerminalGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            elevation = 8.dp
                        ) {
                            OutlinedTextField(
                                value = questionInput,
                                onValueChange = { if (it.length <= 150) questionInput = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("inquiry_input"),
                                placeholder = {
                                    Text(
                                        "Whisper your inquiry here...",
                                        color = MutedSlate.copy(alpha = 0.5f),
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 14.sp
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = ParchmentWhite,
                                    unfocusedTextColor = ParchmentWhite,
                                    focusedBorderColor = AgedGold,
                                    unfocusedBorderColor = AgedGold.copy(alpha = 0.3f),
                                    cursorColor = BrightGold
                                ),
                                shape = RoundedCornerShape(8.dp),
                                textStyle = LocalTextStyle.current.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 14.sp
                                ),
                                trailingIcon = {
                                    Text(
                                        text = "${150 - questionInput.length}",
                                        color = MutedSlate,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (questionInput.isNotBlank()) {
                                        focusManager.clearFocus()
                                        viewModel.askOracle(questionInput)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AgedGold,
                                    contentColor = ObsidianBlack
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("submit_button"),
                                enabled = questionInput.isNotBlank()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Transmit",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "TRANSMIT QUERY",
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }
                    }

                    // Loading State block
                    if (uiState is OracleUiState.Loading) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            CircularProgressIndicator(color = AgedGold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "DECRYPTING Celestial Spindle...",
                                color = BrightGold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.testTag("terminal_loading_label")
                            )
                            Text(
                                text = "Fusing chronodetector arrays. Do not cycle power.",
                                color = MutedSlate,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Serif,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Lock countdown state (Asked Today already)
                    TerminalGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        borderColor = ShadowRed.copy(alpha = 0.5f),
                        elevation = 6.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "TEMPORAL LOCK ACTIVE",
                                color = ShadowRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp,
                                modifier = Modifier.testTag("lock_header")
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = countdownText,
                                color = BrightGold,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.testTag("lock_countdown")
                            )

                            Text(
                                text = "seconds until next alignment window.",
                                color = MutedSlate,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Serif
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = onNavigateToArtifacts,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GlassCard,
                                    contentColor = AgedGold
                                ),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, AgedGold),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("inspect_clues_button")
                                ) {
                                Text(
                                    text = "INSPECT RECENT ARTIFACTS",
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }

                // Error State block
                if (uiState is OracleUiState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, ShadowRed, RoundedCornerShape(6.dp))
                            .background(ShadowRed.copy(alpha = 0.15f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Anomaly warning",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = (uiState as OracleUiState.Error).message,
                            color = ParchmentWhite,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Bottom Status Spindle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "OPERATING SECURITY ACCORD // ENCRYPTION: GOTHIC Vigenère",
                    color = MutedSlate.copy(alpha = 0.4f),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

// Helper to convert ints to atmospheric Roman Numerals
fun streakToRoman(num: Int): String {
    if (num <= 0) return "VOID"
    val map = mapOf(
        10 to "X", 9 to "IX", 5 to "V", 4 to "IV", 1 to "I"
    )
    var remaining = num
    val sb = java.lang.StringBuilder()
    for ((value, sym) in map) {
        while (remaining >= value) {
            sb.append(sym)
            remaining -= value
        }
    }
    return sb.toString()
}

// Custom simple StateFlow of helper for state management
fun <T> mutableStateFlowOf(init: T) = mutableStateOf(init)
