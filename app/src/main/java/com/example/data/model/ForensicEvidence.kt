package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EvidenceType {
    AUDIO_RECORDING,
    AMPLIFIED_VIDEO,
    PACKET_CAPTURE,
    BLACKMAIL_TRANSCRIPT
}

@Entity(tableName = "forensic_evidence")
data class ForensicEvidence(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: EvidenceType,
    val timestamp: Long,
    val filePath: String,
    val fileSizeBytes: Long,
    val durationSeconds: Long,
    val sha256Hash: String,
    val amplificationNotes: String, // e.g. "+18dB Speech Gain Boost", "+2.5 EV Low-Light Boost"
    val chainOfCustodyVerified: Boolean = true,
    val notes: String = ""
)
