package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.OracleSession
import com.example.data.model.ArtifactPackage
import com.example.ui.components.TerminalGlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.OracleUiState
import com.example.ui.viewmodel.OracleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastArchivesScreen(
    viewModel: OracleViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToArtifacts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.sessions.collectAsState()
    var searchFilter by remember { mutableStateOf("") }

    val filteredSessions = remember(sessions, searchFilter) {
        if (searchFilter.isBlank()) {
            sessions
        } else {
            sessions.filter {
                it.question.contains(searchFilter, ignoreCase = true) ||
                        it.theme.contains(searchFilter, ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "THE CHRONICLE LOGS",
                        color = AgedGold,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        modifier = Modifier.testTag("archive_screen_title")
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
                .padding(16.dp)
        ) {
            // Description of past logbook
            Text(
                text = "SYSTEM ARCHIVE DIRECTORY //",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MutedSlate
            )
            Text(
                text = "Each inquiry permanently transforms the focal state of the machine core. Below are past resonance records. Select a chronicle block to reload it into the extraction matrices.",
                fontSize = 12.sp,
                fontFamily = FontFamily.Serif,
                color = ParchmentWhite,
                lineHeight = 17.sp,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchFilter,
                onValueChange = { searchFilter = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("archive_search"),
                placeholder = {
                    Text(
                        "Search logs by keywords or themes...",
                        color = MutedSlate.copy(alpha = 0.5f),
                        fontFamily = FontFamily.Serif,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = AgedGold.copy(alpha = 0.6f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = ParchmentWhite,
                    unfocusedTextColor = ParchmentWhite,
                    focusedBorderColor = AgedGold,
                    unfocusedBorderColor = AgedGold.copy(alpha = 0.2f),
                    cursorColor = BrightGold
                ),
                shape = RoundedCornerShape(6.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // History List
            if (filteredSessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(1.dp, AgedGold.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                        .background(DeepCharcoal.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📜", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "NO COMPLETED INQUIRIES FOUND",
                            color = MutedSlate,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredSessions) { session ->
                        PastSessionCard(
                            session = session,
                            onClick = {
                                // Load this targeted logged session JSON into uiState directly!
                                val pkg = ArtifactPackage.fromJson(session.responseJson)
                                viewModel.selectSessionPackage(pkg)
                                onNavigateToArtifacts()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PastSessionCard(
    session: OracleSession,
    onClick: () -> Unit
) {
    TerminalGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("past_card_${session.id}"),
        borderColor = AgedGold.copy(alpha = 0.25f),
        elevation = 3.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LOG: ${session.dateString}",
                color = MutedSlate,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            // Theme pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(AgedGold.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = session.theme.uppercase(),
                    color = BrightGold,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "\"${session.question}\"",
            color = ParchmentWhite,
            fontSize = 13.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            fontStyle = FontStyle.Italic,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "COORDINATE MATCH ON 100%",
                color = PhosphorGreen,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "RE-DECRYPT FRAGMENTS →",
                color = AgedGold,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
