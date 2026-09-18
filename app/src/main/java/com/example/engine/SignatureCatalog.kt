package com.example.engine

import com.example.data.model.MaliciousSignature
import com.example.data.model.ThreatLevel

object SignatureCatalog {
    val KNOWN_SIGNATURES: List<MaliciousSignature> = listOf(
        MaliciousSignature(
            id = "SIG-ARP-001",
            name = "ARP Cache Poisoning / MITM",
            category = "Local Link Interception",
            description = "Gratuitous ARP replies re-mapping gateway MAC to unauthorized rogue hardware for traffic interception.",
            severity = ThreatLevel.CRITICAL,
            targetVector = "ARP Protocol / Layer 2 Broadcast",
            forensicTactics = "Isolate rogue device MAC immediately; lock static ARP cache; trace physical port on switch/router."
        ),
        MaliciousSignature(
            id = "SIG-C2-002",
            name = "Extortion Ransomware C2 Beacon",
            category = "Command & Control",
            description = "Periodic high-frequency HTTP/TLS beaconing to known ransomware extortion leak site infrastructure.",
            severity = ThreatLevel.CRITICAL,
            targetVector = "TCP 443 / 8443 External Outbound",
            forensicTactics = "Capture full PCAP stream; block destination IP at border router; isolate local host from internal storage."
        ),
        MaliciousSignature(
            id = "SIG-RAT-003",
            name = "Webcam RAT Covert Stream (Port 4444/5552)",
            category = "Remote Access Trojan",
            description = "Persistent reverse shell socket actively transmitting audio/video buffers to external listener.",
            severity = ThreatLevel.CRITICAL,
            targetVector = "TCP Reverse Shell / Meterpreter",
            forensicTactics = "Kill suspicious PID immediately; cover physical lenses; collect memory dump for forensic analysis."
        ),
        MaliciousSignature(
            id = "SIG-DNS-004",
            name = "DNS Tunneling Blackmail Exfiltration",
            category = "Data Exfiltration",
            description = "High-entropy base64 encoded TXT record queries leaking confidential credentials and files over UDP 53.",
            severity = ThreatLevel.HIGH_RISK,
            targetVector = "UDP 53 (DNS Queries)",
            forensicTactics = "Filter internal DNS resolver to authenticated recursive servers; inspect TXT payload strings."
        ),
        MaliciousSignature(
            id = "SIG-SYN-005",
            name = "TCP SYN Flood & Port Sweep",
            category = "Reconnaissance / DoS",
            description = "Rapid half-open TCP connections probing local ports 22, 80, 445, 3389, 5901 without completing 3-way handshake.",
            severity = ThreatLevel.SUSPICIOUS,
            targetVector = "TCP SYN Flags (Port Scanning)",
            forensicTactics = "Enable SYN cookies; ban attacking source IP for 24 hours; log access attempt order."
        ),
        MaliciousSignature(
            id = "SIG-SMB-006",
            name = "SMB Ghost / EternalBlue Probe",
            category = "Lateral Movement",
            description = "Malformed SMBv1/v2 negotiation packets targeting Port 445 for remote code execution and network propagation.",
            severity = ThreatLevel.CRITICAL,
            targetVector = "TCP 445 (Microsoft-DS)",
            forensicTactics = "Disable SMBv1 globally; restrict port 445 to trusted domain controller subnets only."
        ),
        MaliciousSignature(
            id = "SIG-SSDP-007",
            name = "UPnP / SSDP Unauthorized Multicast Injection",
            category = "Network Spoofing",
            description = "Rogue UPnP M-SEARCH multicast packets attempting to open external port forwards to internal victim endpoints.",
            severity = ThreatLevel.HIGH_RISK,
            targetVector = "UDP 1900 Multicast",
            forensicTactics = "Disable UPnP on gateway router; inspect WAN forwarding table for newly forged rules."
        )
    )

    fun matchPacket(
        srcIp: String,
        dstPort: Int,
        protocol: String,
        flags: String,
        payloadHex: String
    ): MaliciousSignature? {
        val upperHex = payloadHex.uppercase()
        return when {
            protocol == "ARP" && (upperHex.contains("0002") || upperHex.contains("DEADBEEF") || upperHex.contains("FFFFFFFF")) ->
                KNOWN_SIGNATURES[0]
            (dstPort == 4444 || dstPort == 5552 || upperHex.contains("METERPRETER") || upperHex.contains("SHELL")) ->
                KNOWN_SIGNATURES[2]
            (dstPort == 443 || dstPort == 8443) && (upperHex.contains("TOR") || upperHex.contains("ONION") || upperHex.contains("LOCKBIT")) ->
                KNOWN_SIGNATURES[1]
            protocol == "DNS" && upperHex.length > 80 ->
                KNOWN_SIGNATURES[3]
            protocol == "TCP" && flags.contains("SYN") && (dstPort == 22 || dstPort == 445 || dstPort == 3389 || dstPort == 5901) ->
                KNOWN_SIGNATURES[4]
            dstPort == 445 && upperHex.contains("FF534D42") ->
                KNOWN_SIGNATURES[5]
            protocol == "UDP" && dstPort == 1900 && upperHex.contains("M-SEARCH") ->
                KNOWN_SIGNATURES[6]
            else -> null
        }
    }
}
