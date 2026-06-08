package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.LorePost
import com.example.ui.components.TerminalGlassCard
import com.example.ui.components.GoldGlowText
import com.example.ui.theme.*
import com.example.ui.viewmodel.OracleUiState
import com.example.ui.viewmodel.OracleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoreBulletinScreen(
    viewModel: OracleViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lorePosts by viewModel.lorePosts.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var showAnonymousSubmitModal by remember { mutableStateOf(false) }

    // Inputs inside modal
    var codenameInput by remember { mutableStateOf("") }
    var commentaryInput by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "THE AETHER BULLETIN",
                        color = AgedGold,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        modifier = Modifier.testTag("bulletin_screen_title")
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ObsidianBlack)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Intro banner
                Text(
                    text = "COMMUNITY MYSTERY INTERCEPTOR //",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MutedSlate
                )
                Text(
                    text = "A shared, anonymous frequency. Seekers worldwide post active readings, code snippets, and coordinate reports to solve the global storyline.",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Serif,
                    color = ParchmentWhite,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Shared Bulletins List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(lorePosts) { post ->
                        LorePostCard(post = post)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Submit lore button at bottom
                Button(
                    onClick = {
                        showAnonymousSubmitModal = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AgedGold,
                        contentColor = ObsidianBlack
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("publish_post_trigger")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Post logo",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OFFER LOGS TO THE BULLETIN",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Simple Dialog popup for submitting
            if (showAnonymousSubmitModal) {
                AlertDialog(
                    onDismissRequest = { showAnonymousSubmitModal = false },
                    title = {
                        Text(
                            text = "ANONYMOUS TRANSMISSION",
                            color = AgedGold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    containerColor = DeepCharcoal,
                    tonalElevation = 8.dp,
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Submit your active daily artifact summary to the shared archive. This is only permitted if you have successfully generated a reading today.",
                                color = ParchmentWhite,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Serif
                            )

                            if (uiState !is OracleUiState.Success) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(ShadowRed.copy(alpha = 0.15f))
                                        .border(1.dp, ShadowRed, RoundedCornerShape(4.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "REGISTRATION BLOCKED: No active readings recorded in terminal cache today. Ask a question first.",
                                        color = ParchmentWhite,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else {
                                OutlinedTextField(
                                    value = codenameInput,
                                    onValueChange = { codenameInput = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("author_codename_field"),
                                    label = { Text("Agent Codename (e.g. Seer_408)", color = MutedSlate, fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = ParchmentWhite,
                                        unfocusedTextColor = ParchmentWhite,
                                        focusedBorderColor = AgedGold,
                                        unfocusedBorderColor = AgedGold.copy(alpha = 0.3f)
                                    ),
                                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                )

                                OutlinedTextField(
                                    value = commentaryInput,
                                    onValueChange = { commentaryInput = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .testTag("commentary_field"),
                                    label = { Text("Your Interpretative commentary...", color = MutedSlate, fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = ParchmentWhite,
                                        unfocusedTextColor = ParchmentWhite,
                                        focusedBorderColor = AgedGold,
                                        unfocusedBorderColor = AgedGold.copy(alpha = 0.3f)
                                    ),
                                    textStyle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 12.sp)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        val active = uiState as? OracleUiState.Success
                        Button(
                            onClick = {
                                if (active != null) {
                                    val authorName = if (codenameInput.isBlank()) "Anonymous_Seeker" else codenameInput
                                    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                                    viewModel.submitToLoreBoard(
                                        LorePost(
                                            author = authorName,
                                            dateString = dateStr,
                                            questionSnippet = active.artifact.oracleMessage,
                                            messageSnippet = "${active.artifact.symbolicCardTitle}: ${active.artifact.hiddenCode}",
                                            userInterpretation = commentaryInput
                                        )
                                    )
                                    showAnonymousSubmitModal = false
                                }
                            },
                            enabled = active != null && commentaryInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AgedGold,
                                contentColor = ObsidianBlack
                            ),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.testTag("confirm_publish_button")
                        ) {
                            Text(
                                "TRANSMIT DATA",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showAnonymousSubmitModal = false },
                            colors = ButtonDefaults.textButtonColors(contentColor = MutedSlate)
                        ) {
                            Text("ABORT", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LorePostCard(post: LorePost) {
    TerminalGlassCard(
        modifier = Modifier.fillMaxWidth().testTag("lore_card_${post.id}"),
        borderColor = AgedGold.copy(alpha = 0.3f),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "INTERCEPTED FROM: ${post.author.uppercase()}",
                color = AgedGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = post.dateString,
                color = MutedSlate,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Question snippet box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MutedSlate.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(8.dp)
        ) {
            Column {
                Text(
                    text = "RECEIPT ORACLE ARTIFACTS:",
                    color = BrightGold,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = post.messageSnippet,
                    color = ParchmentWhite,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "AGENT COMMENTARY & DECRYPTION ANALYSIS:",
            color = MutedSlate,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = post.userInterpretation,
            color = ParchmentWhite,
            fontFamily = FontFamily.Serif,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
