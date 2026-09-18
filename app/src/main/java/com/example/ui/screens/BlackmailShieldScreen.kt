package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BlackmailMessage
import com.example.engine.BlackmailAnalyzer
import com.example.ui.components.CyberCard
import com.example.ui.theme.CyberAmberWarning
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCrimsonAlert
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberEmeraldSafe
import com.example.ui.theme.CyberNavyDark
import com.example.ui.theme.CyberPurpleSecondary
import com.example.ui.theme.CyberSurfaceDark
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.viewmodel.NetSentinelViewModel

@Composable
fun BlackmailShieldScreen(
    viewModel: NetSentinelViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messages by viewModel.blackmailMessages.collectAsState()
    val currentResult by viewModel.currentAnalysisResult.collectAsState()
    val messageText by viewModel.inputMessageText.collectAsState()
    val senderIdentifier by viewModel.inputSender.collectAsState()
    val platform by viewModel.inputPlatform.collectAsState()

    var showReportDialogForMessage by remember { mutableStateOf<BlackmailMessage?>(null) }
    var showPlaybookDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberNavyDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Playbook Quick Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BLACKMAIL SHIELD & FORENSIC INTAKE",
                        color = CyberTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Auto-flags extortion tactics & generates law enforcement reports",
                        color = CyberTextSecondary,
                        fontSize = 11.sp
                    )
                }

                OutlinedButton(
                    onClick = { showPlaybookDialog = true },
                    border = BorderStroke(1.dp, CyberAmberWarning),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = CyberAmberWarning, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("TACTICS PLAYBOOK", fontSize = 10.sp, color = CyberAmberWarning, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Intake Form Card
        item {
            CyberCard(
                borderColor = CyberCyanPrimary.copy(alpha = 0.4f),
                backgroundColor = CyberSurfaceDark
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SCAN SUSPICIOUS COMMUNICATION",
                        color = CyberCyanPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = senderIdentifier,
                            onValueChange = { viewModel.setInputSender(it) },
                            label = { Text("Threat Actor ID / Handle", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyanPrimary,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = platform,
                            onValueChange = { viewModel.setInputPlatform(it) },
                            label = { Text("Channel / App", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyanPrimary,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { viewModel.setInputMessageText(it) },
                        label = { Text("Paste blackmail message, SMS, email, or chat text", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyanPrimary,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset buttons to test realistic extortion patterns quickly
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyberSurfaceVariant,
                            modifier = Modifier.clickable {
                                viewModel.setInputMessageText("Hello. I recorded you through your webcam while you were visiting intimate adult sites. I split the screen showing you masturbating and your face. I have extracted your contacts list, facebook friends, and email contacts. You have 24 hours to send 0.05 Bitcoin to bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq or I will publish this video to your family, coworkers, and employer.")
                                viewModel.setInputSender("hacker_shadow99@darkmail.onion")
                                viewModel.setInputPlatform("Email")
                            }
                        ) {
                            Text("Sample Sextortion", color = CyberCyanPrimary, fontSize = 10.sp, modifier = Modifier.padding(6.dp))
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyberSurfaceVariant,
                            modifier = Modifier.clickable {
                                viewModel.setInputMessageText("URGENT: All your personal documents, photos, and databases are encrypted with LockBit 3.0. To receive the decryption key, transfer 1.2 Monero to 48edfHu7V9Z84YzzMa6fUUEoXoFS6D1eeqY6hFVeAd3hGKWDtrqw55q5BCbQf118w3. You have a strict 48 hours deadline countdown. Do not contact the police or your files will be leaked forever.")
                                viewModel.setInputSender("LockBit_Support_Bot")
                                viewModel.setInputPlatform("Telegram")
                            }
                        ) {
                            Text("Sample Ransomware", color = CyberAmberWarning, fontSize = 10.sp, modifier = Modifier.padding(6.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.analyzeCurrentMessage() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberCrimsonAlert,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Report, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ANALYZE & FLAG THREAT", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        if (currentResult != null) {
                            OutlinedButton(
                                onClick = { viewModel.clearCurrentAnalysis() },
                                border = BorderStroke(1.dp, CyberBorder),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("CLEAR", color = CyberTextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Live Analysis Result Banner / Details
        currentResult?.let { result ->
            item {
                CyberCard(
                    borderColor = if (result.isExtortionFlagged) CyberCrimsonAlert else CyberAmberWarning,
                    backgroundColor = if (result.isExtortionFlagged) CyberCrimsonAlert.copy(alpha = 0.1f) else CyberSurfaceDark
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (result.isExtortionFlagged) "CRITICAL EXTORTION DETECTED" else "SUSPICIOUS PATTERN REVIEW",
                                    color = if (result.isExtortionFlagged) CyberCrimsonAlert else CyberAmberWarning,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = result.threatCategory,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (result.isExtortionFlagged) CyberCrimsonAlert else CyberAmberWarning),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${result.threatScore}%",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("DETECTED EXTORTION TACTICS:", color = CyberAmberWarning, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        result.detectedTactics.forEach { tactic ->
                            Text("• $tactic", color = CyberTextPrimary, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (result.extractedCryptoWallets.isNotEmpty()) {
                            Text("EXTRACTED CRYPTO RANSOM WALLET(S):", color = CyberCyanPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            result.extractedCryptoWallets.forEach { wallet ->
                                Text(wallet, color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        Text("DEADLINE PRESSURE: ${result.extractedDeadlines}", color = CyberCrimsonAlert, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CyberSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("TACTICAL COUNTER-MEASURE:", color = CyberEmeraldSafe, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(result.recommendedImmediateAction, color = Color.White, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("NetSentinel Forensic Report", result.lawEnforcementReportDossier)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Formal Law Enforcement Dossier copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberCyanPrimary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("COPY OFFICIAL IC3 LAW ENFORCEMENT DOSSIER", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Flagged Incidents Archive Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HISTORICAL EXTORTION INCIDENT ARCHIVE (${messages.size})",
                    color = CyberTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        if (messages.isEmpty()) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No flagged extortion incidents yet. Analyzed threats will be cataloged here.",
                            color = CyberTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(messages, key = { it.id }) { msg ->
                BlackmailIncidentCard(
                    message = msg,
                    onViewDossier = { showReportDialogForMessage = msg },
                    onMarkReported = { viewModel.markMessageReported(msg) },
                    onDelete = { viewModel.deleteBlackmailMessage(msg) }
                )
            }
        }
    }

    // Law Enforcement Report Dialog
    showReportDialogForMessage?.let { msg ->
        val dossierText = BlackmailAnalyzer.analyzeMessage(
            rawText = msg.rawText,
            senderIdentifier = msg.senderIdentifier,
            platform = msg.platform
        ).lawEnforcementReportDossier

        AlertDialog(
            onDismissRequest = { showReportDialogForMessage = null },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("NetSentinel Forensic Report", dossierText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Dossier copied for submission!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyanPrimary, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("COPY REPORT", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialogForMessage = null }) {
                    Text("CLOSE", color = CyberTextSecondary)
                }
            },
            title = {
                Text("FORMAL LAW ENFORCEMENT REPORT", color = CyberCyanPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyberSurfaceVariant,
                    modifier = Modifier.height(360.dp)
                ) {
                    LazyColumn(modifier = Modifier.padding(10.dp)) {
                        item {
                            Text(
                                text = dossierText,
                                color = CyberCyanPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            },
            containerColor = CyberSurfaceDark,
            textContentColor = CyberTextPrimary
        )
    }

    // Tactical Playbook Dialog
    if (showPlaybookDialog) {
        AlertDialog(
            onDismissRequest = { showPlaybookDialog = false },
            confirmButton = {
                TextButton(onClick = { showPlaybookDialog = false }) {
                    Text("UNDERSTOOD", color = CyberCyanPrimary, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = CyberAmberWarning)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("TACTICAL ANTI-EXTORTION PROTOCOL", color = CyberAmberWarning, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    BlackmailAnalyzer.TACTICAL_PLAYBOOK.forEach { rule ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyberSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = rule,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            },
            containerColor = CyberSurfaceDark,
            textContentColor = CyberTextPrimary
        )
    }
}

@Composable
fun BlackmailIncidentCard(
    message: BlackmailMessage,
    onViewDossier: () -> Unit,
    onMarkReported: () -> Unit,
    onDelete: () -> Unit
) {
    CyberCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = CyberCrimsonAlert.copy(alpha = 0.5f),
        backgroundColor = CyberSurfaceDark
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(CyberCrimsonAlert)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message.threatCategory,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyberCrimsonAlert.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, CyberCrimsonAlert)
                ) {
                    Text(
                        text = "SCORE: ${message.threatScore}/100",
                        color = CyberCrimsonAlert,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text("From: ${message.senderIdentifier} (${message.platform})", color = CyberCyanPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)

            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = CyberSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "\"${message.rawText.take(160)}...\"",
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            if (message.extractedCryptoWallets.isNotBlank()) {
                Text("Wallets: ${message.extractedCryptoWallets}", color = CyberAmberWarning, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (message.isReportedToLawEnforcement) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = CyberEmeraldSafe.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "REPORTED: ${message.caseFileNumber ?: "SUBMITTED"}",
                            color = CyberEmeraldSafe,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onMarkReported,
                        border = BorderStroke(1.dp, CyberEmeraldSafe),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("MARK REPORTED", fontSize = 10.sp, color = CyberEmeraldSafe, fontWeight = FontWeight.Bold)
                    }
                }

                Row {
                    TextButton(onClick = onViewDossier) {
                        Text("VIEW DOSSIER", color = CyberCyanPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CyberTextMuted, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
