package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blackmail_records")
data class BlackmailMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val senderIdentifier: String,
    val platform: String,
    val rawText: String,
    val threatScore: Int, // 0 to 100
    val threatCategory: String, // e.g. "Sextortion / Webcam", "Financial Ransom", "Doxxing / Defamation"
    val extractedDemands: String,
    val extractedCryptoWallets: String,
    val extractedDeadlines: String,
    val detectedTactics: String,
    val isReportedToLawEnforcement: Boolean = false,
    val caseFileNumber: String? = null
)
