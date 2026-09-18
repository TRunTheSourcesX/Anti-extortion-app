package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BlackmailMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface BlackmailDao {
    @Query("SELECT * FROM blackmail_records ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<BlackmailMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: BlackmailMessage): Long

    @Update
    suspend fun update(message: BlackmailMessage)

    @Delete
    suspend fun delete(message: BlackmailMessage)

    @Query("UPDATE blackmail_records SET isReportedToLawEnforcement = 1, caseFileNumber = :caseNumber WHERE id = :id")
    suspend fun markReported(id: Long, caseNumber: String)
}
