package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ThreatLevel {
    SAFE,
    SUSPICIOUS,
    HIGH_RISK,
    CRITICAL
}

@Entity(tableName = "ip_access_records")
data class IpAccessRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderIndex: Int, // Exact order of access attempt
    val ipAddress: String,
    val firstAccessTimestamp: Long,
    val lastAccessTimestamp: Long,
    val accessCount: Int,
    val targetPort: Int,
    val protocol: String,
    val threatLevel: ThreatLevel,
    val threatSignature: String? = null,
    val reverseDns: String? = null,
    val geoHint: String? = null,
    val isBlocked: Boolean = false,
    val packetSampleHex: String? = null,
    val forensicNotes: String? = null
)
