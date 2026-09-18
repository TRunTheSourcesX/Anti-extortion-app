package com.example.data.model

data class NetworkPacket(
    val id: String,
    val timestamp: Long,
    val sourceIp: String,
    val destinationIp: String,
    val sourcePort: Int,
    val destinationPort: Int,
    val protocol: String,
    val lengthBytes: Int,
    val flags: String,
    val payloadHex: String,
    val payloadSummary: String,
    val matchedSignature: String? = null,
    val threatLevel: ThreatLevel = ThreatLevel.SAFE
)
