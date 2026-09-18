package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.EvidenceType
import com.example.data.model.ThreatLevel

class Converters {
    @TypeConverter
    fun fromThreatLevel(value: ThreatLevel): String = value.name

    @TypeConverter
    fun toThreatLevel(value: String): ThreatLevel = try {
        ThreatLevel.valueOf(value)
    } catch (_: Exception) {
        ThreatLevel.SAFE
    }

    @TypeConverter
    fun fromEvidenceType(value: EvidenceType): String = value.name

    @TypeConverter
    fun toEvidenceType(value: String): EvidenceType = try {
        EvidenceType.valueOf(value)
    } catch (_: Exception) {
        EvidenceType.AUDIO_RECORDING
    }
}
