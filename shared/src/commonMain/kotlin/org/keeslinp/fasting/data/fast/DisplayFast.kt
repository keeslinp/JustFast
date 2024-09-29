package org.keeslinp.fasting.data.fast

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.roundToInt

val dateFormatter = LocalDate.Format {
    dayOfWeek(DayOfWeekNames.ENGLISH_ABBREVIATED)
    char(',')
    char(' ')
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    dayOfMonth(Padding.NONE)
}

val dateTimeFormatter = LocalDateTime.Format {
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

data class DisplayFast(
    val id: Long,
    val startSeconds: Long,
    val goalDuration: Long,
    val endSeconds: Long,
    val resumable: Boolean = false,
) : KoinComponent {
    constructor(id: Long, startSeconds: Long, goalDuration: Long, endSeconds: Long?, resumable: Boolean = false) : this(
        id,
        startSeconds,
        goalDuration,
        endSeconds ?: startSeconds + goalDuration,
        resumable,
    )

    private val timeZone: TimeZone by inject()
    val startDate: String by lazy {
        Instant.fromEpochSeconds(startSeconds).toLocalDateTime(timeZone).date.format(
            dateFormatter
        )
    }
    val startTime: String by lazy {
        Instant.fromEpochSeconds(startSeconds).toLocalDateTime(timeZone).format(dateTimeFormatter)
    }
    val endTime: String by lazy {
        Instant.fromEpochSeconds(endSeconds).toLocalDateTime(timeZone).format(dateTimeFormatter)
    }
    val durationText: String by lazy { "${((endSeconds - startSeconds).toFloat() / 3600).roundToInt()} hours" }
}