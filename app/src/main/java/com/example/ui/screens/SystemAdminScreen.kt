package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TerminalGlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.OracleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemAdminScreen(
    viewModel: OracleViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeLoreTheme by viewModel.loreTheme.collectAsState()
    val activeMode by viewModel.personalityMode.collectAsState()

    // Config option lists
    val availableLoreThemes = listOf("The Whispering Grid", "The Sunken Codex", "The Echo Threshold", "Celestial Gears")
    val availableModes = listOf("Cryptic Gothic", "Techno-Mystic", "Archaic Void")

    var showPurgeConfirmationDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CORE COMPILER INTERFACE",
                        color = AgedGold,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        modifier = Modifier.testTag("admin_screen_title")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ObsidianBlack)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "ADMINISTRATOR PARALLEL CONSOLE //",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MutedSlate
            )

            // active values
            TerminalGlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ACTIVE PHYSICAL REGISTERS:",
                    color = AgedGold,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("GLOBAL LORE SPINDLE", color = MutedSlate, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text(activeLoreTheme, color = ParchmentWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                    }
                    Column {
                        Text("ORACLE PERSONALITY", color = MutedSlate, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text(activeMode, color = ParchmentWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                    }
                }
            }

            // Lore tune config
            TerminalGlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SELECT LORE STORYLINE SPINDLE:",
                    color = AgedGold,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                availableLoreThemes.forEach { theme ->
                    val isSelected = theme == activeLoreTheme
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) AgedGold.copy(alpha = 0.15f) else Color.Transparent)
                            .border(1.dp, if (isSelected) AgedGold else Color.Transparent, RoundedCornerShape(4.dp))
                            .clickable {
                                viewModel.updateSystemConfig(theme, activeMode)
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = theme,
                            color = if (isSelected) BrightGold else MutedSlate,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier.testTag("admin_theme_$theme")
                        )
                        if (isSelected) {
                            Text(
                                text = "ACTIVE",
                                color = PhosphorGreen,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Personality mode tune config
            TerminalGlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "RE-CALIBRATE PERSONALITY MODE:",
                    color = AgedGold,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                availableModes.forEach { mode ->
                    val isSelected = mode == activeMode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) AgedGold.copy(alpha = 0.15f) else Color.Transparent)
                            .border(1.dp, if (isSelected) AgedGold else Color.Transparent, RoundedCornerShape(4.dp))
                            .clickable {
                                viewModel.updateSystemConfig(activeLoreTheme, mode)
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = mode,
                            color = if (isSelected) BrightGold else MutedSlate,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier.testTag("admin_mode_$mode")
                        )
                        if (isSelected) {
                            Text(
                                text = "LOCKED",
                                color = PhosphorGreen,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Purge settings block
            TerminalGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = Color.Red.copy(alpha = 0.5f),
                elevation = 6.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Danger Warning",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HARDWARE DESTRUCT PROTOCOL",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Purge all historical log records, streak cycles, and local database entries. This action completes a cold zero-load flash. Values cannot be retroactively recovered.",
                    color = MutedSlate,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Serif,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        showPurgeConfirmationDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("purge_button")
                ) {
                    Text(
                        text = "PURGE LOCAL CORE LOGS",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Hard confirmation dialog
        if (showPurgeConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showPurgeConfirmationDialog = false },
                title = {
                    Text(
                        text = "ACTIVATE COLD PURGE?",
                        color = Color.Red,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to write-zero the entire local memory? All historical questions and offline bulletins will be completely erased.",
                        color = ParchmentWhite,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif
                    )
                },
                containerColor = DeepCharcoal,
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.purgeSystems()
                            showPurgeConfirmationDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
                        modifier = Modifier.testTag("confirm_purge")
                    ) {
                        Text("EXECUTE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showPurgeConfirmationDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = MutedSlate)
                    ) {
                        Text("ABORT", fontFamily = FontFamily.Monospace)
                    }
                }
            )
        }
    }
}
