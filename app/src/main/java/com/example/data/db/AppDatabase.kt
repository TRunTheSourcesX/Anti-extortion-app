package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.BlackmailMessage
import com.example.data.model.ForensicEvidence
import com.example.data.model.IpAccessRecord

@Database(
    entities = [
        IpAccessRecord::class,
        BlackmailMessage::class,
        ForensicEvidence::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ipAccessDao(): IpAccessDao
    abstract fun blackmailDao(): BlackmailDao
    abstract fun forensicEvidenceDao(): ForensicEvidenceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "netsentinel_forensic.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}
