package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.model.EvidenceType
import com.example.data.model.ForensicEvidence
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EvidenceStudioScreen(
    viewModel: NetSentinelViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var selectedStudioTab by remember { mutableIntStateOf(0) }
    val audioState by viewModel.audioState.collectAsState()
    val videoState by viewModel.videoState.collectAsState()
    val evidenceList by viewModel.forensicEvidenceList.collectAsState()

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasAudioPermission = permissions[Manifest.permission.RECORD_AUDIO] ?: hasAudioPermission
        hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: hasCameraPermission
    }

    var showExportDossierDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberNavyDark)
    ) {
        // Tab row
        TabRow(
            selectedTabIndex = selectedStudioTab,
            containerColor = CyberSurfaceDark,
            contentColor = CyberCyanPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedStudioTab]),
                    color = CyberCyanPrimary,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedStudioTab == 0,
                onClick = { selectedStudioTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CLEAR AUDIO", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            )

            Tab(
                selected = selectedStudioTab == 1,
                onClick = { selectedStudioTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AMPLIFIED VIDEO", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            )

            Tab(
                selected = selectedStudioTab == 2,
                onClick = { selectedStudioTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("VAULT (${evidenceList.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            )
        }

        when (selectedStudioTab) {
            0 -> {
                // Audio recording view
                AudioRecorderView(
                    viewModel = viewModel,
                    audioState = audioState,
                    hasPermission = hasAudioPermission,
                    onRequestPermission = {
                        permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                    }
                )
            }
            1 -> {
                // Video recording view
                VideoAmplifierView(
                    viewModel = viewModel,
                    videoState = videoState,
                    hasPermission = hasCameraPermission,
                    onRequestPermission = {
                        permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
                    }
                )
            }
            2 -> {
                // Vault list view
                EvidenceVaultView(
                    evidenceList = evidenceList,
                    onDelete = { viewModel.deleteEvidence(it) },
                    onExportAll = { showExportDossierDialog = true }
                )
            }
        }
    }

    if (showExportDossierDialog) {
        val masterDossier = viewModel.generateFullForensicDossier()
        AlertDialog(
            onDismissRequest = { showExportDossierDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("NetSentinel Master Dossier", masterDossier)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Full Forensic Dossier copied to clipboard!", Toast.LENGTH_SHORT).show()
                        showExportDossierDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyanPrimary, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("COPY MASTER DOSSIER", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDossierDialog = false }) {
                    Text("CLOSE", color = CyberTextSecondary)
                }
            },
            title = {
                Text("MASTER LAW ENFORCEMENT DOSSIER", color = CyberCyanPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyberSurfaceVariant,
                    modifier = Modifier.height(340.dp)
                ) {
                    LazyColumn(modifier = Modifier.padding(10.dp)) {
                        item {
                            Text(
                                text = masterDossier,
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
}

@Composable
fun AudioRecorderView(
    viewModel: NetSentinelViewModel,
    audioState: com.example.engine.AudioRecordingState,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CyberCard(
                borderColor = if (audioState.isRecording) CyberCrimsonAlert else CyberCyanPrimary.copy(alpha = 0.5f),
                backgroundColor = CyberSurfaceDark
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "HIGH-DEFINITION VOICE RECORDER",
                        color = CyberCyanPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Records super clear threat actor speech with hardware noise filter",
                        color = CyberTextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Pulse indicator / decibel meter
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(
                                if (audioState.isRecording) CyberCrimsonAlert.copy(alpha = 0.15f * pulseAlpha)
                                else CyberSurfaceVariant
                            )
                            .border(
                                2.dp,
                                if (audioState.isRecording) CyberCrimsonAlert else CyberBorder,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = if (audioState.isRecording) CyberCrimsonAlert else CyberCyanPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                            if (audioState.isRecording) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format(Locale.US, "%02d:%02d", audioState.durationSeconds / 60, audioState.durationSeconds % 60),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${audioState.currentDecibels.toInt()} dB",
                                    color = CyberAmberWarning,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            } else {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("STANDBY", color = CyberTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Amplification gain selector
                    Text("SPEECH CLARITY GAIN AMPLIFICATION", color = CyberTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(6, 12, 18, 24).forEach { gain ->
                            val isSelected = audioState.gainBoostDb == gain
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) CyberCyanPrimary else CyberSurfaceVariant,
                                border = BorderStroke(1.dp, if (isSelected) CyberCyanPrimary else CyberBorder),
                                modifier = Modifier.clickable { viewModel.setAudioGainBoost(gain) }
                            ) {
                                Text(
                                    text = "+${gain}dB",
                                    color = if (isSelected) Color.Black else CyberTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Voice filter switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Voice Isolation Filter", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Acoustic echo cancellation & noise suppression", color = CyberTextSecondary, fontSize = 10.sp)
                        }

                        Switch(
                            checked = audioState.clarityFilterEnabled,
                            onCheckedChange = { viewModel.toggleAudioClarityFilter() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberCyanPrimary,
                                checkedTrackColor = CyberCyanPrimary.copy(alpha = 0.3f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!hasPermission) {
                        Button(
                            onClick = onRequestPermission,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyanPrimary, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("GRANT MICROPHONE PERMISSION", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                if (audioState.isRecording) viewModel.stopAudioRecording()
                                else viewModel.startAudioRecording()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (audioState.isRecording) CyberCrimsonAlert else CyberEmeraldSafe,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (audioState.isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (audioState.isRecording) "STOP & SEAL FORENSIC AUDIO" else "START HIGH-CLARITY RECORDING",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    audioState.errorMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = CyberCrimsonAlert, fontSize = 11.sp)
                    }
                }
            }
        }

        audioState.lastSavedEvidence?.let { saved ->
            item {
                CyberCard(
                    borderColor = CyberEmeraldSafe,
                    backgroundColor = CyberEmeraldSafe.copy(alpha = 0.08f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = CyberEmeraldSafe, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("EVIDENTIARY RECORD SEALED & STORED", color = CyberEmeraldSafe, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(saved.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Amplification: ${saved.amplificationNotes}", color = CyberTextSecondary, fontSize = 11.sp)
                        Text("Duration: ${saved.durationSeconds}s | Size: ${saved.fileSizeBytes}B", color = CyberTextSecondary, fontSize = 11.sp)
                        Text("SHA-256 Checksum: ${saved.sha256Hash}", color = CyberCyanPrimary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
fun VideoAmplifierView(
    viewModel: NetSentinelViewModel,
    videoState: com.example.engine.VideoEvidenceState,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            CyberCard(
                borderColor = if (videoState.isRecording) CyberCrimsonAlert else CyberCyanPrimary.copy(alpha = 0.5f),
                backgroundColor = CyberSurfaceDark
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "AMPLIFIED FORENSIC VIDEO RECORDER",
                                color = CyberCyanPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "Optical/Digital EV gain boost for threat actor identification",
                                color = CyberTextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        IconButton(onClick = { viewModel.toggleVideoTorch() }) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = "Torch",
                                tint = if (videoState.isTorchEnabled) CyberAmberWarning else CyberTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Live Camera Viewfinder Box with on-screen forensic evidentiary watermark
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black)
                            .border(1.dp, if (videoState.isRecording) CyberCrimsonAlert else CyberBorder, RoundedCornerShape(10.dp))
                    ) {
                        if (hasPermission) {
                            AndroidView(
                                factory = { ctx ->
                                    val previewView = PreviewView(ctx)
                                    viewModel.videoManager.bindCamera(lifecycleOwner, previewView)
                                    previewView
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Videocam, contentDescription = null, tint = CyberTextMuted, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Camera permission needed", color = CyberTextMuted, fontSize = 12.sp)
                                }
                            }
                        }

                        // Evidentiary On-Screen Watermark HUD
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            val timeUtc = SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'", Locale.US).format(Date())
                            Text("REC TIME: $timeUtc", color = CyberCyanPrimary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            Text("GAIN BOOST: +${videoState.exposureCompensationEv} EV | TORCH: ${videoState.isTorchEnabled}", color = CyberAmberWarning, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            if (videoState.isRecording) {
                                Text("SEAL HASH: ${videoState.currentSha256Checksum}", color = CyberCrimsonAlert, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (videoState.isRecording) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp),
                                shape = RoundedCornerShape(4.dp),
                                color = CyberCrimsonAlert
                            ) {
                                Text(
                                    text = "LIVE RECORDING ${String.format(Locale.US, "%02d:%02d", videoState.durationSeconds / 60, videoState.durationSeconds % 60)}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Exposure amplification slider
                    Text(
                        text = "LOW-LIGHT AMPLIFICATION GAIN: +${String.format(Locale.US, "%.1f", videoState.exposureCompensationEv)} EV",
                        color = CyberAmberWarning,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Slider(
                        value = videoState.exposureCompensationEv,
                        onValueChange = { viewModel.setVideoExposure(it) },
                        valueRange = 0.0f..3.0f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberAmberWarning,
                            activeTrackColor = CyberAmberWarning,
                            inactiveTrackColor = CyberSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!hasPermission) {
                        Button(
                            onClick = onRequestPermission,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyanPrimary, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("GRANT CAMERA & AUDIO PERMISSIONS", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                if (videoState.isRecording) viewModel.stopVideoRecording()
                                else viewModel.startVideoRecording()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (videoState.isRecording) CyberCrimsonAlert else CyberCyanPrimary,
                                contentColor = if (videoState.isRecording) Color.White else Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (videoState.isRecording) Icons.Default.Stop else Icons.Default.Videocam,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (videoState.isRecording) "STOP & SEAL AMPLIFIED VIDEO" else "RECORD AMPLIFIED VIDEO EVIDENCE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EvidenceVaultView(
    evidenceList: List<ForensicEvidence>,
    onDelete: (ForensicEvidence) -> Unit,
    onExportAll: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
                        text = "FORENSIC EVIDENCE LOCKER",
                        color = CyberTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Cryptographically hashed chain-of-custody recordings",
                        color = CyberTextSecondary,
                        fontSize = 11.sp
                    )
                }

                if (evidenceList.isNotEmpty()) {
                    Button(
                        onClick = onExportAll,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyanPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("EXPORT DOSSIER", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (evidenceList.isEmpty()) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No evidence recordings captured yet. Use Clear Audio or Amplified Video to collect evidence.",
                            color = CyberTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(evidenceList, key = { it.id }) { item ->
                EvidenceItemCard(
                    evidence = item,
                    onDelete = { onDelete(item) }
                )
            }
        }
    }
}

@Composable
fun EvidenceItemCard(
    evidence: ForensicEvidence,
    onDelete: () -> Unit
) {
    val dateFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(evidence.timestamp))

    CyberCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = CyberCyanPrimary.copy(alpha = 0.3f),
        backgroundColor = CyberSurfaceDark
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (evidence.type == EvidenceType.AMPLIFIED_VIDEO) Icons.Default.Videocam else Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = CyberCyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(evidence.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = CyberEmeraldSafe.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "SHA-256 SEALED",
                        color = CyberEmeraldSafe,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("Captured: $dateFormatted", color = CyberTextSecondary, fontSize = 11.sp)
            Text("Amplification: ${evidence.amplificationNotes}", color = CyberAmberWarning, fontSize = 11.sp)
            Text("Duration: ${evidence.durationSeconds}s | Size: ${evidence.fileSizeBytes} bytes", color = CyberTextMuted, fontSize = 11.sp)

            Spacer(modifier = Modifier.height(4.dp))
            Text("SHA-256: ${evidence.sha256Hash}", color = CyberCyanPrimary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CyberTextMuted, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
