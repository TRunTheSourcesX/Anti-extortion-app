package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.BlackmailMessage
import com.example.data.model.ForensicEvidence
import com.example.data.model.IpAccessRecord
import kotlinx.coroutines.flow.Flow

class ForensicRepository(private val database: AppDatabase) {
    val ipAccessList: Flow<List<IpAccessRecord>> = database.ipAccessDao().getAllInAccessOrder()
    val blackmailMessages: Flow<List<BlackmailMessage>> = database.blackmailDao().getAllMessages()
    val forensicEvidenceList: Flow<List<ForensicEvidence>> = database.forensicEvidenceDao().getAllEvidence()

    suspend fun recordIpAccess(record: IpAccessRecord) {
        val existing = database.ipAccessDao().findByIp(record.ipAddress)
        if (existing != null) {
            val updated = existing.copy(
                lastAccessTimestamp = record.lastAccessTimestamp,
                accessCount = existing.accessCount + 1,
                targetPort = record.targetPort,
                protocol = record.protocol,
                threatLevel = if (record.threatLevel.ordinal > existing.threatLevel.ordinal) record.threatLevel else existing.threatLevel,
                threatSignature = record.threatSignature ?: existing.threatSignature,
                packetSampleHex = record.packetSampleHex ?: existing.packetSampleHex,
                forensicNotes = record.forensicNotes ?: existing.forensicNotes
            )
            database.ipAccessDao().update(updated)
        } else {
            database.ipAccessDao().insert(record)
        }
    }

    suspend fun toggleBlockIp(id: Long, blocked: Boolean) {
        database.ipAccessDao().setBlocked(id, blocked)
    }

    suspend fun clearIpRecords() {
        database.ipAccessDao().clearAll()
    }

    suspend fun saveBlackmailMessage(message: BlackmailMessage): Long {
        return database.blackmailDao().insert(message)
    }

    suspend fun markMessageReported(id: Long, caseNumber: String) {
        database.blackmailDao().markReported(id, caseNumber)
    }

    suspend fun deleteBlackmailMessage(message: BlackmailMessage) {
        database.blackmailDao().delete(message)
    }

    suspend fun saveEvidence(evidence: ForensicEvidence): Long {
        return database.forensicEvidenceDao().insert(evidence)
    }

    suspend fun deleteEvidence(evidence: ForensicEvidence) {
        database.forensicEvidenceDao().delete(evidence)
    }
}
