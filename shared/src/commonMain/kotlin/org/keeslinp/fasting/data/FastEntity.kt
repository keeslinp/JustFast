package org.keeslinp.fasting.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FastEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: String,
    val endTime: String?,
)

