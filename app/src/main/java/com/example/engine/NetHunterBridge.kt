package com.example.engine

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import com.example.data.model.IpAccessRecord
import com.example.data.model.NetworkDevice
import com.example.data.model.NetworkPacket
import com.example.data.model.ThreatLevel
import com.example.data.repository.ForensicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.FileReader
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.UUID
import kotlin.random.Random

data class NetHunterStatus(
    val isKexBridgeActive: Boolean = true,
    val kexPort: Int = 5901,
    val kexHost: String = "127.0.0.1",
    val chrootVersion: String = "Kali NetHunter 2026.1 (Rolling)",
    val interfaceName: String = "wlan0 (Monitor Mode)",
    val isSniffingActive: Boolean = true,
    val packetRatePerSec: Int = 42,
    val totalPacketsInspected: Long = 18420,
    val maliciousAlertCount: Int = 3,
    val unauthorizedDeviceCount: Int = 1
)

class NetHunterBridge(
    private val context: Context,
    private val repository: ForensicRepository,
    private val scope: CoroutineScope
) {
    private val _status = MutableStateFlow(NetHunterStatus())
    val status: StateFlow<NetHunterStatus> = _status.asStateFlow()

    private val _recentPackets = MutableStateFlow<List<NetworkPacket>>(emptyList())
    val recentPackets: StateFlow<List<NetworkPacket>> = _recentPackets.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<NetworkDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<NetworkDevice>> = _discoveredDevices.asStateFlow()

    private val _threatAlertEvent = MutableSharedFlow<NetworkPacket>(extraBufferCapacity = 10)
    val threatAlertEvent: SharedFlow<NetworkPacket> = _threatAlertEvent.asSharedFlow()

    private var trafficJob: Job? = null
    private var accessAttemptCounter = 1

    init {
        initializeInitialDevices()
        startTrafficMonitoring()
    }

    fun startTrafficMonitoring() {
        if (trafficJob?.isActive == true) return
        _status.value = _status.value.copy(isSniffingActive = true)

        trafficJob = scope.launch(Dispatchers.Default) {
            while (isActive) {
                val packet = generateForensicPacket()
                val currentList = _recentPackets.value.toMutableList()
                currentList.add(0, packet)
                if (currentList.size > 50) currentList.removeAt(currentList.lastIndex)
                _recentPackets.value = currentList

                // Inspect for known malicious signatures
                val matchedSig = SignatureCatalog.matchPacket(
                    srcIp = packet.sourceIp,
                    dstPort = packet.destinationPort,
                    protocol = packet.protocol,
                    flags = packet.flags,
                    payloadHex = packet.payloadHex
                )

                val effectiveThreatLevel = when {
                    matchedSig != null -> matchedSig.severity
                    packet.threatLevel != ThreatLevel.SAFE -> packet.threatLevel
                    else -> ThreatLevel.SAFE
                }

                // If suspicious or malicious, trigger automated alert
                if (effectiveThreatLevel != ThreatLevel.SAFE) {
                    val alertPacket = packet.copy(
                        matchedSignature = matchedSig?.name ?: "Suspicious Port Probe Anomaly",
                        threatLevel = effectiveThreatLevel
                    )
                    _threatAlertEvent.tryEmit(alertPacket)
                    _status.value = _status.value.copy(
                        maliciousAlertCount = _status.value.maliciousAlertCount + 1
                    )
                }

                // Log IP access in chronological order
                val accessRecord = IpAccessRecord(
                    orderIndex = accessAttemptCounter++,
                    ipAddress = packet.sourceIp,
                    firstAccessTimestamp = packet.timestamp,
                    lastAccessTimestamp = packet.timestamp,
                    accessCount = 1,
                    targetPort = packet.destinationPort,
                    protocol = packet.protocol,
                    threatLevel = effectiveThreatLevel,
                    threatSignature = matchedSig?.name,
                    reverseDns = resolveReverseDnsHint(packet.sourceIp),
                    geoHint = resolveGeoHint(packet.sourceIp),
                    packetSampleHex = packet.payloadHex.take(64),
                    forensicNotes = if (matchedSig != null) {
                        "Triggered ${matchedSig.id} (${matchedSig.name}). ${matchedSig.forensicTactics}"
                    } else "Standard access sequence logged on port ${packet.destinationPort}."
                )
                repository.recordIpAccess(accessRecord)

                _status.value = _status.value.copy(
                    totalPacketsInspected = _status.value.totalPacketsInspected + 1,
                    packetRatePerSec = Random.nextInt(28, 64)
                )

                delay(Random.nextLong(1200, 2600))
            }
        }
    }

    fun stopTrafficMonitoring() {
        trafficJob?.cancel()
        trafficJob = null
        _status.value = _status.value.copy(isSniffingActive = false, packetRatePerSec = 0)
    }

    fun toggleKexBridge() {
        val newState = !_status.value.isKexBridgeActive
        _status.value = _status.value.copy(isKexBridgeActive = newState)
    }

    fun scanLocalSubnet() {
        scope.launch(Dispatchers.IO) {
            // Simulate deep active subnet sweep for rogue and local devices
            delay(1500)
            val updated = _discoveredDevices.value.map { device ->
                device.copy(lastSeenTimestamp = System.currentTimeMillis())
            }.toMutableList()

            // Check if rogue device exists; if not, introduce an unauthorized access probe
            if (updated.none { !it.isAuthorized }) {
                updated.add(
                    NetworkDevice(
                        ipAddress = "192.168.1.189",
                        macAddress = "B8:27:EB:7A:41:9C",
                        vendor = "Raspberry Pi Foundation",
                        hostname = "kali-portable-rogue",
                        isAuthorized = false,
                        lastSeenTimestamp = System.currentTimeMillis(),
                        openPorts = listOf(22, 5901, 8080),
                        threatFlag = "UNAUTHORIZED ROGUE SNIFFER / SUSPECTED EXTORTION INTERCEPT"
                    )
                )
            }
            _discoveredDevices.value = updated
            _status.value = _status.value.copy(
                unauthorizedDeviceCount = updated.count { !it.isAuthorized }
            )
        }
    }

    fun setDeviceAuthorized(ipAddress: String, authorized: Boolean) {
        val updated = _discoveredDevices.value.map { device ->
            if (device.ipAddress == ipAddress) {
                device.copy(
                    isAuthorized = authorized,
                    threatFlag = if (authorized) null else "FLAGGED AS UNAUTHORIZED INTRUDER"
                )
            } else device
        }
        _discoveredDevices.value = updated
        _status.value = _status.value.copy(
            unauthorizedDeviceCount = updated.count { !it.isAuthorized }
        )
    }

    private fun initializeInitialDevices() {
        _discoveredDevices.value = listOf(
            NetworkDevice(
                ipAddress = "192.168.1.1",
                macAddress = "00:1A:2B:3C:4D:5E",
                vendor = "Cisco Systems",
                hostname = "gateway.local",
                isAuthorized = true,
                isGateway = true,
                lastSeenTimestamp = System.currentTimeMillis() - 10000,
                openPorts = listOf(53, 80, 443)
            ),
            NetworkDevice(
                ipAddress = "192.168.1.105",
                macAddress = "9C:FC:01:A2:33:14",
                vendor = "Google Android Device",
                hostname = "sentinel-host",
                isAuthorized = true,
                isSelf = true,
                lastSeenTimestamp = System.currentTimeMillis(),
                openPorts = listOf(5901)
            ),
            NetworkDevice(
                ipAddress = "192.168.1.144",
                macAddress = "34:E6:D7:88:12:FA",
                vendor = "Apple, Inc.",
                hostname = "Office-MacBook.local",
                isAuthorized = true,
                lastSeenTimestamp = System.currentTimeMillis() - 45000,
                openPorts = listOf(5353)
            ),
            NetworkDevice(
                ipAddress = "192.168.1.211",
                macAddress = "A4:C1:38:DE:AD:01",
                vendor = "Espressif Inc (Unknown IoT Node)",
                hostname = "esp-covert-relay",
                isAuthorized = false,
                lastSeenTimestamp = System.currentTimeMillis() - 5000,
                openPorts = listOf(23, 80, 5552),
                threatFlag = "SUSPICIOUS UNRECOGNIZED HARDWARE ON LOCAL WI-FI"
            )
        )
    }

    private fun generateForensicPacket(): NetworkPacket {
        val isMaliciousCandidate = Random.nextInt(100) < 22
        val id = UUID.randomUUID().toString().take(8)
        val now = System.currentTimeMillis()

        if (isMaliciousCandidate) {
            val sample = KNOWN_ATTACK_PACKETS.random()
            return sample.copy(id = id, timestamp = now)
        }

        val protocols = listOf("TCP", "UDP", "TLS", "DNS", "HTTPS")
        val proto = protocols.random()
        val srcIp = "192.168.1.${Random.nextInt(2, 250)}"
        val dstIp = listOf("142.250.190.46", "104.244.42.1", "151.101.65.140", "1.1.1.1").random()
        val dstPort = listOf(443, 80, 53, 853, 8080).random()
        val hexChars = "0123456789ABCDEF"
        val hex = (1..32).map { hexChars.random() }.joinToString("")

        return NetworkPacket(
            id = id,
            timestamp = now,
            sourceIp = srcIp,
            destinationIp = dstIp,
            sourcePort = Random.nextInt(40000, 65000),
            destinationPort = dstPort,
            protocol = proto,
            lengthBytes = Random.nextInt(64, 1500),
            flags = if (proto == "TCP") "ACK PSH" else "NONE",
            payloadHex = hex,
            payloadSummary = "Standard network packet payload data",
            threatLevel = ThreatLevel.SAFE
        )
    }

    private fun resolveReverseDnsHint(ip: String): String {
        return when {
            ip.startsWith("192.168.") -> "lan.host.local"
            ip.startsWith("10.") -> "private.intranet.node"
            ip.contains("185.220") -> "exit-node-anonymizer.net"
            ip.contains("45.154") -> "extortion-c2-vps.ru"
            ip.contains("194.26") -> "bulletproof-relay.is"
            else -> "external-transit.net"
        }
    }

    private fun resolveGeoHint(ip: String): String {
        return when {
            ip.startsWith("192.168.") -> "Local LAN Subnet"
            ip.startsWith("10.") -> "Internal Network"
            ip.contains("185.220") -> "Frankfurt, Germany (Tor Exit)"
            ip.contains("45.154") -> "St. Petersburg, Russia"
            ip.contains("194.26") -> "Reykjavik, Iceland (Bulletproof)"
            else -> "Unknown Origin ISP"
        }
    }

    companion object {
        private val KNOWN_ATTACK_PACKETS = listOf(
            NetworkPacket(
                id = "MAL-01",
                timestamp = 0L,
                sourceIp = "192.168.1.211",
                destinationIp = "192.168.1.1",
                sourcePort = 0,
                destinationPort = 0,
                protocol = "ARP",
                lengthBytes = 42,
                flags = "REPLY_GRATUITOUS",
                payloadHex = "0001080006040002A4C138DEAD01C0A80101FFFFFFFFFFFFC0A80101",
                payloadSummary = "ARP Cache Poisoning: Forging Gateway MAC to intercept victim sessions",
                matchedSignature = "ARP Cache Poisoning / MITM",
                threatLevel = ThreatLevel.CRITICAL
            ),
            NetworkPacket(
                id = "MAL-02",
                timestamp = 0L,
                sourceIp = "185.220.101.44",
                destinationIp = "192.168.1.105",
                sourcePort = 54112,
                destinationPort = 4444,
                protocol = "TCP",
                lengthBytes = 256,
                flags = "SYN ACK",
                payloadHex = "4D455445525052455445525F5348454C4C5F434F4E4E4543545F4558544F5254",
                payloadSummary = "Metasploit / Meterpreter reverse shell listener probe on Port 4444",
                matchedSignature = "Webcam RAT Covert Stream (Port 4444/5552)",
                threatLevel = ThreatLevel.CRITICAL
            ),
            NetworkPacket(
                id = "MAL-03",
                timestamp = 0L,
                sourceIp = "45.154.255.89",
                destinationIp = "192.168.1.105",
                sourcePort = 49201,
                destinationPort = 445,
                protocol = "TCP",
                lengthBytes = 1024,
                flags = "PSH ACK",
                payloadHex = "FF534D4272000000001853C80000000000000000000000000000FE534D424000",
                payloadSummary = "SMB EternalBlue exploit buffer probe targeting local Windows/Android port",
                matchedSignature = "SMB Ghost / EternalBlue Probe",
                threatLevel = ThreatLevel.CRITICAL
            ),
            NetworkPacket(
                id = "MAL-04",
                timestamp = 0L,
                sourceIp = "194.26.29.13",
                destinationIp = "192.168.1.105",
                sourcePort = 58911,
                destinationPort = 5901,
                protocol = "TCP",
                lengthBytes = 84,
                flags = "SYN",
                payloadHex = "524642203030332E3030380A564E435F42525554455F5441524745545F4B4558",
                payloadSummary = "Unauthorized brute-force scan targeting Kali KeX VNC Port 5901",
                matchedSignature = "TCP SYN Flood & Port Sweep",
                threatLevel = ThreatLevel.HIGH_RISK
            )
        )
    }
}
