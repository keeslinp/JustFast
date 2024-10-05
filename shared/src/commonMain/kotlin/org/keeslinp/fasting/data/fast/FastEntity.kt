package org.keeslinp.fasting.data.fast

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid


@Entity(indices = [Index(value = ["endTime"])])
data class FastEntity(
    @PrimaryKey val id: Uuid,
    val startTime: Long,
    val endTime: Long? = null,
    val goalDuration: Long,
) {
    fun display(resumable: Boolean = false) = DisplayFast(
        id = id,
        startTime,
        goalDuration,
        endTime,
        resumable,
    )
}