package org.keeslinp.fasting.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FastDao {
    @Upsert
    suspend fun start(fast: FastEntity)

    @Query("SELECT * FROM FastEntity where NOT endTime LIMIT 1")
    fun getActiveFast(): Flow<FastEntity>

    @Query("SELECT * FROM FastEntity where endTime")
    fun getPastFasts(): Flow<List<FastEntity>>

    @Delete(entity = FastEntity::class)
    fun deleteFast(id: Long)
}