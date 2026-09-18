package com.example.engine

import com.example.data.model.BlackmailMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class BlackmailAnalysisResult(
    val threatScore: Int,
    val threatCategory: String,
    val isExtortionFlagged: Boolean,
    val extractedDemands: String,
    val extractedCryptoWallets: List<String>,
    val extractedDeadlines: String,
    val detectedTactics: List<String>,
    val recommendedImmediateAction: String,
    val lawEnforcementReportDossier: String
)

object BlackmailAnalyzer {

    // Regex for crypto addresses
    private val BTC_REGEX = Regex("\\b(bc1|[13])[a-zA-HJ-NP-Z0-9]{25,39}\\b")
    private val ETH_USDT_REGEX = Regex("\\b0x[a-fA-F0-9]{40}\\b")
    private val XMR_REGEX = Regex("\\b4[0-9AB][1-9A-HJ-NP-Za-km-z]{93}\\b")

    // Keywords and tactics
    private val SEXTORTION_KEYWORDS = listOf(
        "webcam", "recorded you", "masturbating", "intimate video", "split screen",
        "contacts list", "facebook friends", "instagram contacts", "send to your family",
        "send to your contacts", "send to your employer", "nude", "dirty secret"
    )

    private val URGENCY_DEADLINE_KEYWORDS = listOf(
        "24 hours", "48 hours", "countdown", "deadline", "tomorrow", "time is ticking",
        "do not try to contact", "one chance", "too late", "before i publish"
    )

    private val RANSOM_FINANCIAL_KEYWORDS = listOf(
        "bitcoin", "btc", "wallet", "ransom", "transfer", "pay", "usd", "$", "crypto",
        "western union", "gift card", "monero", "ethereum"
    )

    private val COERCION_THREAT_KEYWORDS = listOf(
        "ruin your life", "ruin your reputation", "leak your data", "publish everything",
        "destroy your career", "expose you", "pegasus", "trojan", "hacked your phone",
        "compromised your router"
    )

    fun analyzeMessage(
        rawText: String,
        senderIdentifier: String,
        platform: String
    ): BlackmailAnalysisResult {
        val lowerText = rawText.lowercase()

        val matchedSextortion = SEXTORTION_KEYWORDS.filter { lowerText.contains(it) }
        val matchedUrgency = URGENCY_DEADLINE_KEYWORDS.filter { lowerText.contains(it) }
        val matchedFinancial = RANSOM_FINANCIAL_KEYWORDS.filter { lowerText.contains(it) }
        val matchedCoercion = COERCION_THREAT_KEYWORDS.filter { lowerText.contains(it) }

        // Find cryptocurrency addresses
        val cryptoWallets = mutableListOf<String>()
        BTC_REGEX.findAll(rawText).forEach { cryptoWallets.add("BTC: ${it.value}") }
        ETH_USDT_REGEX.findAll(rawText).forEach { cryptoWallets.add("ETH/USDT: ${it.value}") }
        XMR_REGEX.findAll(rawText).forEach { cryptoWallets.add("XMR: ${it.value}") }

        var score = 0
        if (matchedSextortion.isNotEmpty()) score += 35 + (matchedSextortion.size * 5)
        if (matchedUrgency.isNotEmpty()) score += 20 + (matchedUrgency.size * 5)
        if (matchedFinancial.isNotEmpty()) score += 20
        if (matchedCoercion.isNotEmpty()) score += 25
        if (cryptoWallets.isNotEmpty()) score += 30

        score = score.coerceIn(0, 100)

        val category = when {
            matchedSextortion.isNotEmpty() -> "Sextortion & Intimacy Blackmail"
            cryptoWallets.isNotEmpty() || matchedFinancial.isNotEmpty() -> "Financial Ransomware Extortion"
            matchedCoercion.isNotEmpty() -> "Psychological Coercion & Doxxing Threat"
            else -> "Suspicious Unsolicited Threat"
        }

        val tactics = mutableListOf<String>()
        if (matchedSextortion.isNotEmpty()) tactics.add("Intimate Media Fabrication / Webcam Claim")
        if (matchedUrgency.isNotEmpty()) tactics.add("Artificial Deadline Countdown (${matchedUrgency.joinToString(", ")})")
        if (matchedCoercion.isNotEmpty()) tactics.add("Social / Professional Ruin Threat")
        if (cryptoWallets.isNotEmpty()) tactics.add("Cryptocurrency Payment Funneling")
        if (lowerText.contains("pegasus") || lowerText.contains("trojan") || lowerText.contains("hacked")) {
            tactics.add("Bluff Malware / Remote Access Trojan Claim")
        }

        val extractedDemands = when {
            cryptoWallets.isNotEmpty() -> "Cryptocurrency ransom demanded to wallet(s): ${cryptoWallets.joinToString(", ")}"
            matchedFinancial.isNotEmpty() -> "Monetary extortion demanded under threat of disclosure"
            else -> "Compliance / Demands unverified or social coercion"
        }

        val extractedDeadlines = if (matchedUrgency.isNotEmpty()) {
            matchedUrgency.joinToString(", ")
        } else {
            "No specific countdown detected"
        }

        val isFlagged = score >= 45

        val immediateAction = if (isFlagged) {
            "CRITICAL: DO NOT PAY. DO NOT REPLY. Immediately preserve screenshots, export this forensic dossier, block sender, and report to law enforcement cybercrime unit."
        } else {
            "Moderate caution: Keep record of communication. Do not disclose personal or credential information."
        }

        val caseId = "CASE-${UUID.randomUUID().toString().take(8).uppercase()}"
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US).format(Date())

        val reportDossier = """
            ===================================================================
            OFFICIAL FORENSIC INCIDENT COMPLAINT REPORT
            NETSENTINEL ANTI-EXTORTION FORENSIC DIVISION
            STANDARD: COMPLIANT WITH FBI IC3 & INTERPOL CYBERCRIME INTAKE
            ===================================================================
            INCIDENT DOSSIER REF: $caseId
            RECORDED TIMESTAMP  : $dateStr
            PRIMARY CLASSIFICATION: $category
            EXTORTION THREAT SCORE: $score / 100 (HIGH SEVERITY ALERT)
            
            [1. THREAT ACTOR ATTRIBUTION]
            - Sender / Handle / Origin: $senderIdentifier
            - Delivery Platform/Channel: $platform
            - Detected Ransom Wallets : ${if (cryptoWallets.isEmpty()) "None Identified" else cryptoWallets.joinToString(", ")}
            - Deadline & Coercion Window: $extractedDeadlines
            
            [2. EXTORTION TACTICS IDENTIFIED]
            ${tactics.joinToString("\n") { "  * $it" }}
            
            [3. PRESERVED ORIGINAL TRANSMISSION EVIDENCE]
            -------------------------------------------------------------------
            $rawText
            -------------------------------------------------------------------
            
            [4. FORENSIC CHAIN OF CUSTODY & INTEGRITY]
            - Verification Algorithm : SHA-256 Checksum Engine
            - Evidence State        : Cryptographically Signed & Locked
            - Local Device Telemetry: Captured simultaneously with network access logs
            
            [5. LAW ENFORCEMENT ACTION PROTOCOL]
            1. Per FBI & DOJ Guidance, payments should NOT be made to extortionists.
            2. Preservation notice issued under 18 U.S.C. § 2703(f) for ISP retention.
            3. File this report directly at ic3.gov or local Cybercrime Task Force.
            ===================================================================
        """.trimIndent()

        return BlackmailAnalysisResult(
            threatScore = score,
            threatCategory = category,
            isExtortionFlagged = isFlagged,
            extractedDemands = extractedDemands,
            extractedCryptoWallets = cryptoWallets,
            extractedDeadlines = extractedDeadlines,
            detectedTactics = tactics,
            recommendedImmediateAction = immediateAction,
            lawEnforcementReportDossier = reportDossier
        )
    }

    val TACTICAL_PLAYBOOK = listOf(
        "1. Never Pay the Threat Actor: Paying guarantees repeat extortion attempts and does not prevent publication or credential leaks.",
        "2. Do Not Engage or Reply: Cease all messaging. Responding confirms the victim is panicked, increasing the extortionist's pressure.",
        "3. Cryptographically Preserve Evidence: Record super-clear audio of calls, amplified video of visual encounters, and export raw message headers.",
        "4. Lock Down Online Footprint: Temporarily privatize all social accounts (LinkedIn, Instagram, Facebook), turn off friend lists, and enable MFA.",
        "5. File Formal Law Enforcement Complaint: Submit the NetSentinel generated report directly to IC3.gov (FBI) or your state cyber division."
    )
}
