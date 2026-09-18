package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ForensicEvidence
import kotlinx.coroutines.flow.Flow

@Dao
interface ForensicEvidenceDao {
    @Query("SELECT * FROM forensic_evidence ORDER BY timestamp DESC")
    fun getAllEvidence(): Flow<List<ForensicEvidence>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(evidence: ForensicEvidence): Long

    @Delete
    suspend fun delete(evidence: ForensicEvidence)

    @Query("DELETE FROM forensic_evidence")
    suspend fun clearAll()
}
