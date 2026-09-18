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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.IpAccessRecord
import com.example.data.model.NetworkDevice
import com.example.data.model.ThreatLevel
import com.example.ui.components.CyberCard
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
fun DevicesAndIpsScreen(
    viewModel: NetSentinelViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val ipRecords by viewModel.ipAccessList.collectAsState()
    val devices by viewModel.discoveredDevices.collectAsState()
    var selectedRecordForForensics by remember { mutableStateOf<IpAccessRecord?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberNavyDark)
    ) {
        // Tab row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CyberSurfaceDark,
            contentColor = CyberCyanPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CyberCyanPrimary,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lan, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("IP ACCESS ORDER (${ipRecords.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    val rogueCount = devices.count { !it.isAuthorized }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Devices, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (rogueCount > 0) "DEVICES ($rogueCount ROGUE)" else "DEVICES (${devices.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (rogueCount > 0) CyberCrimsonAlert else CyberCyanPrimary
                        )
                    }
                }
            )
        }

        if (selectedTab == 0) {
            // Chronological IP Access List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CHRONOLOGICAL FORENSIC IP LOG",
                                color = CyberTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Recorded in exact sequential order of access attempts",
                                color = CyberTextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        if (ipRecords.isNotEmpty()) {
                            TextButton(
                                onClick = { viewModel.clearIpRecords() },
                                colors = ButtonDefaults.textButtonColors(contentColor = CyberCrimsonAlert)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("CLEAR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (ipRecords.isEmpty()) {
                    item {
                        CyberCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Lan, contentDescription = null, tint = CyberTextMuted, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("No IP access attempts logged yet.", color = CyberTextMuted, fontSize = 13.sp)
                                Text("Start traffic sniffer to capture live network access attempts.", color = CyberTextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                } else {
                    items(ipRecords, key = { it.id }) { record ->
                        IpAccessCard(
                            record = record,
                            onToggleBlock = { viewModel.toggleBlockIp(record) },
                            onClick = { selectedRecordForForensics = record }
                        )
                    }
                }
            }
        } else {
            // Local Subnet Device Scanner
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "LOCAL CONNECTION DEVICES",
                                color = CyberTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Detects unauthorized Wi-Fi & KeX bridge intruders",
                                color = CyberTextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = { viewModel.scanLocalSubnet() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberCyanPrimary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SCAN SUBNET", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                items(devices, key = { it.ipAddress }) { device ->
                    DeviceCard(
                        device = device,
                        onToggleAuthorization = {
                            viewModel.setDeviceAuthorized(device.ipAddress, !device.isAuthorized)
                        }
                    )
                }
            }
        }
    }

    // IP Forensic Details Dialog
    selectedRecordForForensics?.let { record ->
        AlertDialog(
            onDismissRequest = { selectedRecordForForensics = null },
            confirmButton = {
                TextButton(onClick = { selectedRecordForForensics = null }) {
                    Text("CLOSE", color = CyberCyanPrimary, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ACCESS ATTEMPT #${record.orderIndex}", color = CyberCyanPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    ThreatLevelBadge(level = record.threatLevel)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val firstDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(record.firstAccessTimestamp))
                    val lastDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(record.lastAccessTimestamp))

                    Text("IP Address: ${record.ipAddress}", color = Color.White, fontSize = 14.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    Text("Access Count: ${record.accessCount} attempts", color = CyberTextPrimary, fontSize = 12.sp)
                    Text("Port & Protocol: Port ${record.targetPort} (${record.protocol})", color = CyberCyanPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Text("Reverse DNS: ${record.reverseDns ?: "None"}", color = CyberTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text("Geolocation / Network: ${record.geoHint ?: "Unknown"}", color = CyberTextSecondary, fontSize = 11.sp)
                    Text("First Seen: $firstDate", color = CyberTextMuted, fontSize = 11.sp)
                    Text("Last Seen:  $lastDate", color = CyberTextMuted, fontSize = 11.sp)

                    if (record.threatSignature != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyberCrimsonAlert.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, CyberCrimsonAlert.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("FLAGGED SIGNATURE:", color = CyberCrimsonAlert, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(record.threatSignature, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (record.packetSampleHex != null) {
                        Text("PAYLOAD SNAPSHOT:", color = CyberTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyberSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = record.packetSampleHex.chunked(2).joinToString(" "),
                                color = CyberCyanPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    record.forensicNotes?.let {
                        Text("FORENSIC EVIDENCE ADVICE:", color = CyberAmberWarning, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(it, color = CyberTextPrimary, fontSize = 11.sp)
                    }
                }
            },
            containerColor = CyberSurfaceDark,
            textContentColor = CyberTextPrimary
        )
    }
}

@Composable
fun IpAccessCard(
    record: IpAccessRecord,
    onToggleBlock: () -> Unit,
    onClick: () -> Unit
) {
    val borderColor = when {
        record.isBlocked -> CyberCrimsonAlert
        record.threatLevel == ThreatLevel.CRITICAL -> CyberCrimsonAlert
        record.threatLevel == ThreatLevel.HIGH_RISK -> CyberCrimsonAlert.copy(alpha = 0.6f)
        record.threatLevel == ThreatLevel.SUSPICIOUS -> CyberAmberWarning
        else -> CyberBorder
    }

    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        borderColor = borderColor,
        backgroundColor = if (record.isBlocked) CyberCrimsonAlert.copy(alpha = 0.08f) else CyberSurfaceDark
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Order index circular badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CyberSurfaceVariant)
                    .border(1.dp, CyberCyanPrimary.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${record.orderIndex}",
                    color = CyberCyanPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = record.ipAddress,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    ThreatLevelBadge(level = record.threatLevel)
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Port ${record.targetPort} (${record.protocol}) • ${record.accessCount} attempts • ${record.geoHint ?: "Network"}",
                    color = CyberTextSecondary,
                    fontSize = 11.sp
                )

                if (record.threatSignature != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Trip: ${record.threatSignature}",
                        color = CyberCrimsonAlert,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onToggleBlock) {
                Icon(
                    imageVector = if (record.isBlocked) Icons.Default.Block else Icons.Default.Security,
                    contentDescription = if (record.isBlocked) "Unblock IP" else "Block IP",
                    tint = if (record.isBlocked) CyberCrimsonAlert else CyberTextSecondary
                )
            }
        }
    }
}

@Composable
fun DeviceCard(
    device: NetworkDevice,
    onToggleAuthorization: () -> Unit
) {
    val borderColor = if (!device.isAuthorized) CyberCrimsonAlert else CyberBorder
    val bgColor = if (!device.isAuthorized) CyberCrimsonAlert.copy(alpha = 0.08f) else CyberSurfaceDark

    CyberCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = borderColor,
        backgroundColor = bgColor
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
                            .background(if (device.isAuthorized) CyberEmeraldSafe else CyberCrimsonAlert)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = device.hostname,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (device.isSelf) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CyberCyanPrimary.copy(alpha = 0.2f)
                        ) {
                            Text("THIS DEVICE", color = CyberCyanPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }
                    if (device.isGateway) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CyberPurpleSecondary.copy(alpha = 0.2f)
                        ) {
                            Text("GATEWAY", color = CyberPurpleSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (device.isAuthorized) CyberEmeraldSafe.copy(alpha = 0.15f) else CyberCrimsonAlert.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, if (device.isAuthorized) CyberEmeraldSafe.copy(alpha = 0.4f) else CyberCrimsonAlert)
                ) {
                    Text(
                        text = if (device.isAuthorized) "AUTHORIZED" else "ROGUE INTRUDER",
                        color = if (device.isAuthorized) CyberEmeraldSafe else CyberCrimsonAlert,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("IP: ${device.ipAddress}", color = CyberCyanPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("MAC: ${device.macAddress}", color = CyberTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("Vendor: ${device.vendor}", color = CyberTextSecondary, fontSize = 11.sp)

            if (device.threatFlag != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyberCrimsonAlert.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = CyberCrimsonAlert, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(device.threatFlag, color = CyberCrimsonAlert, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (!device.isSelf) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onToggleAuthorization,
                        border = BorderStroke(1.dp, if (device.isAuthorized) CyberCrimsonAlert else CyberEmeraldSafe),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (device.isAuthorized) "FLAG AS ROGUE" else "MARK AS AUTHORIZED",
                            fontSize = 11.sp,
                            color = if (device.isAuthorized) CyberCrimsonAlert else CyberEmeraldSafe,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
