package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.IpAccessRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface IpAccessDao {
    @Query("SELECT * FROM ip_access_records ORDER BY orderIndex ASC")
    fun getAllInAccessOrder(): Flow<List<IpAccessRecord>>

    @Query("SELECT * FROM ip_access_records WHERE ipAddress = :ip LIMIT 1")
    suspend fun findByIp(ip: String): IpAccessRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: IpAccessRecord): Long

    @Update
    suspend fun update(record: IpAccessRecord)

    @Query("UPDATE ip_access_records SET isBlocked = :blocked WHERE id = :id")
    suspend fun setBlocked(id: Long, blocked: Boolean)

    @Query("DELETE FROM ip_access_records")
    suspend fun clearAll()
}
