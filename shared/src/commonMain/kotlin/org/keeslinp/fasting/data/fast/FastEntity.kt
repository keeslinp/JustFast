package org.keeslinp.fasting.data.fast

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class FastEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long,
    val endTime: Long? = null,
    val goalDuration: Long,
) {
    fun display() = DisplayFast(
        id = id,
        startTime,
        goalDuration,
        endTime,
    )
}
