package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NetworkPacket
import com.example.data.model.ThreatLevel
import com.example.engine.SignatureCatalog
import com.example.ui.components.CyberCard
import com.example.ui.components.MetricTile
import com.example.ui.components.ThreatLevelBadge
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MonitorScreen(
    viewModel: NetSentinelViewModel,
    modifier: Modifier = Modifier
) {
    val status by viewModel.netHunterStatus.collectAsState()
    val packets by viewModel.recentPackets.collectAsState()
    var selectedPacketForForensics by remember { mutableStateOf<NetworkPacket?>(null) }
    var showSignaturesDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberNavyDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Kali NetHunter KeX Status Card
        item {
            CyberCard(
                borderColor = if (status.isKexBridgeActive) CyberCyanPrimary else CyberBorder,
                backgroundColor = CyberSurfaceDark
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                    .background(if (status.isKexBridgeActive) CyberEmeraldSafe else CyberAmberWarning)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "KALI NETHUNTER KeX ENGINE",
                                color = CyberTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (status.isKexBridgeActive) "KeX ON" else "KeX STANDBY",
                                color = if (status.isKexBridgeActive) CyberCyanPrimary else CyberTextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = status.isKexBridgeActive,
                                onCheckedChange = { viewModel.toggleKexBridge() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CyberCyanPrimary,
                                    checkedTrackColor = CyberCyanPrimary.copy(alpha = 0.3f),
                                    uncheckedThumbColor = CyberTextMuted,
                                    uncheckedTrackColor = CyberSurfaceVariant
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyberSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("BRIDGE PORT", color = CyberTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("${status.kexHost}:${status.kexPort}", color = CyberCyanPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyberSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("INTERFACE", color = CyberTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(status.interfaceName, color = CyberEmeraldSafe, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = status.chrootVersion,
                            color = CyberTextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Button(
                            onClick = { viewModel.toggleTrafficSniffing() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (status.isSniffingActive) CyberCrimsonAlert.copy(alpha = 0.2f) else CyberCyanPrimary.copy(alpha = 0.2f),
                                contentColor = if (status.isSniffingActive) CyberCrimsonAlert else CyberCyanPrimary
                            ),
                            border = BorderStroke(1.dp, if (status.isSniffingActive) CyberCrimsonAlert else CyberCyanPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (status.isSniffingActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (status.isSniffingActive) "PAUSE SNIFFER" else "START SNIFFER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Live Telemetry Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    label = "Throughput",
                    value = "${status.packetRatePerSec} pkt/s",
                    subtext = "Live inspection",
                    accentColor = CyberCyanPrimary,
                    modifier = Modifier.weight(1f)
                )

                MetricTile(
                    label = "Total Packets",
                    value = "${status.totalPacketsInspected}",
                    subtext = "Deep payload scanned",
                    accentColor = CyberPurpleSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    label = "Threat Alerts",
                    value = "${status.maliciousAlertCount}",
                    subtext = "Signatures tripped",
                    accentColor = if (status.maliciousAlertCount > 0) CyberCrimsonAlert else CyberEmeraldSafe,
                    modifier = Modifier.weight(1f)
                )

                MetricTile(
                    label = "Rogue Devices",
                    value = "${status.unauthorizedDeviceCount}",
                    subtext = "Unauthorized on LAN",
                    accentColor = if (status.unauthorizedDeviceCount > 0) CyberAmberWarning else CyberEmeraldSafe,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Malicious Packet Signature Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MALICIOUS SIGNATURE SCANNER",
                        color = CyberTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "${SignatureCatalog.KNOWN_SIGNATURES.size} Known extortion & exploit signatures active",
                        color = CyberTextSecondary,
                        fontSize = 11.sp
                    )
                }

                OutlinedButton(
                    onClick = { showSignaturesDialog = true },
                    border = BorderStroke(1.dp, CyberCyanPrimary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("VIEW SIGNATURES", fontSize = 11.sp, color = CyberCyanPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Live Packet Inspection Feed Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE FORENSIC TRAFFIC STREAM",
                    color = CyberTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "Tap packet for hex & headers",
                    color = CyberCyanPrimary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Packet list items
        if (packets.isEmpty()) {
            item {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = CyberBorder
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Awaiting live packet capture from wlan0 interface...",
                            color = CyberTextMuted,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        } else {
            items(packets, key = { it.id }) { packet ->
                PacketRowItem(
                    packet = packet,
                    onClick = { selectedPacketForForensics = packet }
                )
            }
        }
    }

    // Packet Forensic Hex Inspector Dialog
    selectedPacketForForensics?.let { packet ->
        AlertDialog(
            onDismissRequest = { selectedPacketForForensics = null },
            confirmButton = {
                TextButton(onClick = { selectedPacketForForensics = null }) {
                    Text("CLOSE", color = CyberCyanPrimary, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("PACKET FORENSIC DETAILS", color = CyberCyanPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    ThreatLevelBadge(level = packet.threatLevel)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val dateFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date(packet.timestamp))
                    Text("Timestamp: $dateFormatted", color = CyberTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text("Source: ${packet.sourceIp}:${packet.sourcePort}", color = CyberTextPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    Text("Destination: ${packet.destinationIp}:${packet.destinationPort}", color = CyberTextPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Text("Protocol: ${packet.protocol} | Flags: ${packet.flags} | Length: ${packet.lengthBytes}B", color = CyberCyanPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)

                    if (packet.matchedSignature != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyberCrimsonAlert.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, CyberCrimsonAlert.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("MATCHED SIGNATURE:", color = CyberCrimsonAlert, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(packet.matchedSignature, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Text("Summary: ${packet.payloadSummary}", color = CyberTextSecondary, fontSize = 12.sp)

                    Text("RAW HEXADECIMAL DUMP:", color = CyberTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CyberSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = packet.payloadHex.chunked(2).joinToString(" "),
                            color = CyberCyanPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            containerColor = CyberSurfaceDark,
            textContentColor = CyberTextPrimary
        )
    }

    // Signatures catalog dialog
    if (showSignaturesDialog) {
        AlertDialog(
            onDismissRequest = { showSignaturesDialog = false },
            confirmButton = {
                TextButton(onClick = { showSignaturesDialog = false }) {
                    Text("CLOSE", color = CyberCyanPrimary, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text("MALICIOUS SIGNATURE DATABASE", color = CyberCyanPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                LazyColumn(
                    modifier = Modifier.height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(SignatureCatalog.KNOWN_SIGNATURES) { sig ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CyberSurfaceVariant,
                            border = BorderStroke(1.dp, if (sig.severity == ThreatLevel.CRITICAL) CyberCrimsonAlert.copy(alpha = 0.4f) else CyberBorder)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(sig.id, color = CyberCyanPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                    ThreatLevelBadge(level = sig.severity)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(sig.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(sig.description, color = CyberTextSecondary, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Target: ${sig.targetVector}", color = CyberAmberWarning, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text("Tactic: ${sig.forensicTactics}", color = CyberEmeraldSafe, fontSize = 10.sp)
                            }
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
fun PacketRowItem(
    packet: NetworkPacket,
    onClick: () -> Unit
) {
    val borderColor = when (packet.threatLevel) {
        ThreatLevel.CRITICAL -> CyberCrimsonAlert
        ThreatLevel.HIGH_RISK -> CyberCrimsonAlert.copy(alpha = 0.6f)
        ThreatLevel.SUSPICIOUS -> CyberAmberWarning.copy(alpha = 0.6f)
        ThreatLevel.SAFE -> CyberBorder
    }

    val bgColor = if (packet.threatLevel != ThreatLevel.SAFE) {
        CyberCrimsonAlert.copy(alpha = 0.08f)
    } else {
        CyberSurfaceDark
    }

    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        borderColor = borderColor,
        backgroundColor = bgColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${packet.protocol} • ${packet.sourceIp}:${packet.sourcePort} -> :${packet.destinationPort}",
                        color = if (packet.threatLevel != ThreatLevel.SAFE) CyberCrimsonAlert else CyberCyanPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    ThreatLevelBadge(level = packet.threatLevel)
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = packet.matchedSignature ?: packet.payloadSummary,
                    color = if (packet.matchedSignature != null) Color.White else CyberTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (packet.matchedSignature != null) FontWeight.Bold else FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Hex: ${packet.payloadHex.take(24)}... (${packet.lengthBytes} bytes, flags: ${packet.flags})",
                    color = CyberTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
