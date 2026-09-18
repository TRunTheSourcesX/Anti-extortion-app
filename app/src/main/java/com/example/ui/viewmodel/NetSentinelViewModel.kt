package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.BlackmailMessage
import com.example.data.model.ForensicEvidence
import com.example.data.model.IpAccessRecord
import com.example.data.model.NetworkDevice
import com.example.data.model.NetworkPacket
import com.example.data.repository.ForensicRepository
import com.example.engine.AudioEvidenceRecorder
import com.example.engine.AudioRecordingState
import com.example.engine.BlackmailAnalysisResult
import com.example.engine.BlackmailAnalyzer
import com.example.engine.NetHunterBridge
import com.example.engine.NetHunterStatus
import com.example.engine.VideoEvidenceManager
import com.example.engine.VideoEvidenceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NetSentinelViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    val repository = ForensicRepository(database)

    val netHunterBridge = NetHunterBridge(application, repository, viewModelScope)
    val audioRecorder = AudioEvidenceRecorder(application, repository, viewModelScope)
    val videoManager = VideoEvidenceManager(application, repository, viewModelScope)

    val netHunterStatus: StateFlow<NetHunterStatus> = netHunterBridge.status
    val recentPackets: StateFlow<List<NetworkPacket>> = netHunterBridge.recentPackets
    val discoveredDevices: StateFlow<List<NetworkDevice>> = netHunterBridge.discoveredDevices

    val ipAccessList: StateFlow<List<IpAccessRecord>> = repository.ipAccessList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blackmailMessages: StateFlow<List<BlackmailMessage>> = repository.blackmailMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val forensicEvidenceList: StateFlow<List<ForensicEvidence>> = repository.forensicEvidenceList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val audioState: StateFlow<AudioRecordingState> = audioRecorder.state
    val videoState: StateFlow<VideoEvidenceState> = videoManager.state

    // Automated Alerting state for high-priority malicious packet detections
    private val _activeAlert = MutableStateFlow<NetworkPacket?>(null)
    val activeAlert: StateFlow<NetworkPacket?> = _activeAlert.asStateFlow()

    // Blackmail Analysis Studio state
    private val _currentAnalysisResult = MutableStateFlow<BlackmailAnalysisResult?>(null)
    val currentAnalysisResult: StateFlow<BlackmailAnalysisResult?> = _currentAnalysisResult.asStateFlow()

    private val _inputMessageText = MutableStateFlow("")
    val inputMessageText: StateFlow<String> = _inputMessageText.asStateFlow()

    private val _inputSender = MutableStateFlow("@unknown_actor")
    val inputSender: StateFlow<String> = _inputSender.asStateFlow()

    private val _inputPlatform = MutableStateFlow("Telegram / SMS")
    val inputPlatform: StateFlow<String> = _inputPlatform.asStateFlow()

    init {
        // Collect threat alert events from NetHunterBridge
        viewModelScope.launch {
            netHunterBridge.threatAlertEvent.collect { alertPacket ->
                _activeAlert.value = alertPacket
            }
        }
    }

    fun dismissActiveAlert() {
        _activeAlert.value = null
    }

    fun toggleKexBridge() {
        netHunterBridge.toggleKexBridge()
    }

    fun toggleTrafficSniffing() {
        if (netHunterStatus.value.isSniffingActive) {
            netHunterBridge.stopTrafficMonitoring()
        } else {
            netHunterBridge.startTrafficMonitoring()
        }
    }

    fun scanLocalSubnet() {
        netHunterBridge.scanLocalSubnet()
    }

    fun setDeviceAuthorized(ip: String, authorized: Boolean) {
        netHunterBridge.setDeviceAuthorized(ip, authorized)
    }

    fun toggleBlockIp(record: IpAccessRecord) {
        viewModelScope.launch {
            repository.toggleBlockIp(record.id, !record.isBlocked)
        }
    }

    fun clearIpRecords() {
        viewModelScope.launch {
            repository.clearIpRecords()
        }
    }

    // Blackmail methods
    fun setInputMessageText(text: String) {
        _inputMessageText.value = text
    }

    fun setInputSender(sender: String) {
        _inputSender.value = sender
    }

    fun setInputPlatform(platform: String) {
        _inputPlatform.value = platform
    }

    fun analyzeCurrentMessage() {
        val text = _inputMessageText.value
        if (text.isBlank()) return
        val result = BlackmailAnalyzer.analyzeMessage(
            rawText = text,
            senderIdentifier = _inputSender.value,
            platform = _inputPlatform.value
        )
        _currentAnalysisResult.value = result

        // Automatically save to database if flagged as extortion
        if (result.isExtortionFlagged) {
            viewModelScope.launch {
                val message = BlackmailMessage(
                    timestamp = System.currentTimeMillis(),
                    senderIdentifier = _inputSender.value,
                    platform = _inputPlatform.value,
                    rawText = text,
                    threatScore = result.threatScore,
                    threatCategory = result.threatCategory,
                    extractedDemands = result.extractedDemands,
                    extractedCryptoWallets = result.extractedCryptoWallets.joinToString(", "),
                    extractedDeadlines = result.extractedDeadlines,
                    detectedTactics = result.detectedTactics.joinToString("; ")
                )
                repository.saveBlackmailMessage(message)
            }
        }
    }

    fun clearCurrentAnalysis() {
        _currentAnalysisResult.value = null
        _inputMessageText.value = ""
    }

    fun deleteBlackmailMessage(message: BlackmailMessage) {
        viewModelScope.launch {
            repository.deleteBlackmailMessage(message)
        }
    }

    fun markMessageReported(message: BlackmailMessage) {
        viewModelScope.launch {
            val caseNum = "IC3-${System.currentTimeMillis() % 1000000}"
            repository.markMessageReported(message.id, caseNum)
        }
    }

    // Audio recording controls
    fun startAudioRecording() {
        audioRecorder.startRecording()
    }

    fun stopAudioRecording() {
        audioRecorder.stopRecording()
    }

    fun setAudioGainBoost(gainDb: Int) {
        audioRecorder.setGainBoost(gainDb)
    }

    fun toggleAudioClarityFilter() {
        audioRecorder.toggleClarityFilter()
    }

    // Video recording controls
    fun startVideoRecording() {
        videoManager.startAmplifiedRecording()
    }

    fun stopVideoRecording() {
        videoManager.stopAmplifiedRecording()
    }

    fun setVideoExposure(ev: Float) {
        videoManager.setExposureEv(ev)
    }

    fun toggleVideoTorch() {
        videoManager.toggleTorch()
    }

    fun deleteEvidence(evidence: ForensicEvidence) {
        viewModelScope.launch {
            repository.deleteEvidence(evidence)
        }
    }

    fun generateFullForensicDossier(): String {
        val totalIps = ipAccessList.value.size
        val suspiciousIps = ipAccessList.value.filter { it.threatLevel != com.example.data.model.ThreatLevel.SAFE }
        val devices = discoveredDevices.value
        val unauth = devices.filter { !it.isAuthorized }
        val msgs = blackmailMessages.value
        val evidence = forensicEvidenceList.value

        return buildString {
            appendLine("===================================================================")
            appendLine("NETSENTINEL COMPREHENSIVE LAW ENFORCEMENT EVIDENCE DOSSIER")
            appendLine("EXTORTION & MALICIOUS INTRUSION FORENSIC REPORT")
            appendLine("GENERATED AT: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", java.util.Locale.US).format(java.util.Date())}")
            appendLine("===================================================================")
            appendLine()
            appendLine("[SECTION 1: NETWORK INTRUSION & IP ACCESS CHRONOLOGY]")
            appendLine("Total IP Access Attempts Logged: $totalIps")
            appendLine("Suspicious/Malicious IPs Identified: ${suspiciousIps.size}")
            appendLine("Chronological Access Order:")
            ipAccessList.value.take(20).forEach { ip ->
                appendLine("  #${ip.orderIndex} | IP: ${ip.ipAddress} | Port: ${ip.targetPort} (${ip.protocol}) | Severity: ${ip.threatLevel} | Signature: ${ip.threatSignature ?: "None"} | Location: ${ip.geoHint ?: "Unknown"}")
            }
            appendLine()
            appendLine("[SECTION 2: UNAUTHORIZED LOCAL CONNECTION DEVICES]")
            appendLine("Total Devices on Subnet: ${devices.size}")
            appendLine("Unauthorized / Rogue Devices Flagged: ${unauth.size}")
            unauth.forEach { dev ->
                appendLine("  * IP: ${dev.ipAddress} | MAC: ${dev.macAddress} | Host: ${dev.hostname} | Vendor: ${dev.vendor} | Threat: ${dev.threatFlag}")
            }
            appendLine()
            appendLine("[SECTION 3: EXTORTION & BLACKMAIL COMMUNICATIONS]")
            appendLine("Total Blackmail Incidents Flagged: ${msgs.size}")
            msgs.forEach { m ->
                appendLine("  * Case ${m.caseFileNumber ?: "PENDING"} | Score: ${m.threatScore}/100 | Platform: ${m.platform} | From: ${m.senderIdentifier}")
                appendLine("    Category: ${m.threatCategory}")
                appendLine("    Demands: ${m.extractedDemands}")
                appendLine("    Wallets: ${m.extractedCryptoWallets}")
                appendLine("    Raw Transcript: \"${m.rawText.take(120)}...\"")
            }
            appendLine()
            appendLine("[SECTION 4: FORENSIC AUDIO & AMPLIFIED VIDEO EVIDENCE VAULT]")
            appendLine("Total Sealed Media Records: ${evidence.size}")
            evidence.forEach { ev ->
                appendLine("  * [${ev.type}] ${ev.title}")
                appendLine("    SHA-256 Checksum: ${ev.sha256Hash}")
                appendLine("    Amplification Details: ${ev.amplificationNotes}")
                appendLine("    File Size: ${ev.fileSizeBytes} bytes | Duration: ${ev.durationSeconds}s")
            }
            appendLine()
            appendLine("===================================================================")
            appendLine("CHAIN OF CUSTODY VERIFIED. SUBMIT DIRECTLY TO CYBERCRIME INTAKE.")
            appendLine("===================================================================")
        }
    }
}
