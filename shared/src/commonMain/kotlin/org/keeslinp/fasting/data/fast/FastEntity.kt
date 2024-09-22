package org.keeslinp.fasting.data.fast

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

val dateFormatter = LocalDateTime.Format {
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    dayOfMonth(Padding.NONE)
    char(',')
    char(' ')
    year(Padding.NONE)
    char(' ')
    amPmHour(Padding.NONE)
    char(':')
    minute(Padding.ZERO)
    char(' ')
    amPmMarker("AM", "PM")
}

@Entity
data class FastEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long,
    val endTime: Long? = null,
) {
    fun formatStartTime() = Instant.fromEpochSeconds(startTime).toLocalDateTime(TimeZone.currentSystemDefault()).format(dateFormatter)
    fun formatEndTime() = endTime?.let { Instant.fromEpochSeconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).format(dateFormatter) }
}

