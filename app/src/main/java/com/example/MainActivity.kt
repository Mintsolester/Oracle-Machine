package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.AgedGold
import com.example.ui.theme.BrightGold
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBlack
import com.example.ui.viewmodel.OracleViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val oracleViewModel: OracleViewModel = viewModel()
                MainAppContainer(viewModel = oracleViewModel)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainAppContainer(viewModel: OracleViewModel) {
    var activeTab by remember { mutableStateOf("terminal") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        bottomBar = {
            // Immersive Custom Gold & Glass Bottom Bar
            if (activeTab != "admin") {
                Column(
                    modifier = Modifier
                        .background(ObsidianBlack)
                        .navigationBarsPadding()
                ) {
                    // Divider line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(AgedGold.copy(alpha = 0.25f))
                    )

                    NavigationBar(
                        containerColor = DeepCharcoal,
                        tonalElevation = 6.dp,
                        modifier = Modifier
                            .height(72.dp)
                            .testTag("hardware_nav_bar")
                    ) {
                        val navItems = listOf(
                            NavigationItem("terminal", "TERMINAL", Icons.Default.Home),
                            NavigationItem("decrypt", "DECRYPT", Icons.Default.Search),
                            NavigationItem("logs", "CHRONICLE", Icons.Default.List),
                            NavigationItem("bulletin", "BULLETIN", Icons.Default.Edit)
                        )

                        navItems.forEach { item ->
                            val isSelected = activeTab == item.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { activeTab = item.route },
                                modifier = Modifier.testTag("nav_${item.route}"),
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = if (isSelected) BrightGold else MutedSlate
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        fontSize = 9.sp,
                                        color = if (isSelected) BrightGold else MutedSlate,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color(0x33C5A059)
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ObsidianBlack)
        ) {
            // Screen router with beautiful fade-in transactions
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) togetherWith
                            fadeOut(animationSpec = androidx.compose.animation.core.tween(250))
                }
            ) { targetScreen ->
                when (targetScreen) {
                    "terminal" -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            DailyTerminalScreen(
                                viewModel = viewModel,
                                onNavigateToArtifacts = { activeTab = "decrypt" },
                                modifier = Modifier.fillMaxSize()
                            )

                            // Quick access settings gear on Top Right
                            IconButton(
                                onClick = { activeTab = "admin" },
                                modifier = Modifier
                                    .statusBarsPadding()
                                    .align(Alignment.TopEnd)
                                    .padding(top = 8.dp, end = 16.dp)
                                    .testTag("admin_gear_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Admin Panel",
                                    tint = AgedGold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    "decrypt" -> {
                        ArtifactViewerScreen(
                            viewModel = viewModel,
                            onNavigateBack = { activeTab = "terminal" },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "logs" -> {
                        PastArchivesScreen(
                            viewModel = viewModel,
                            onNavigateBack = { activeTab = "terminal" },
                            onNavigateToArtifacts = { activeTab = "decrypt" },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "bulletin" -> {
                        LoreBulletinScreen(
                            viewModel = viewModel,
                            onNavigateBack = { activeTab = "terminal" },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "admin" -> {
                        SystemAdminScreen(
                            viewModel = viewModel,
                            onNavigateBack = { activeTab = "terminal" },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

data class NavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
