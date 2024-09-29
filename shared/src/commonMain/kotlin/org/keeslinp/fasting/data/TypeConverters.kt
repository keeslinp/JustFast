package org.keeslinp.fasting.data

import androidx.room.TypeConverter
import kotlin.uuid.Uuid

class UuidTypeConverters {
    @TypeConverter
    fun fromUuid(value: Uuid): ByteArray = value.toByteArray()

    @TypeConverter
    fun toUuid(value: ByteArray): Uuid = Uuid.fromByteArray(value)
}