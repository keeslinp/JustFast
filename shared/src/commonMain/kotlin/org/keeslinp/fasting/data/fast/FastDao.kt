package org.keeslinp.fasting.data.fast

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FastDao {
    @Upsert
    suspend fun start(fast: FastEntity)

    @Update
    suspend fun update(fast: FastEntity)

    @Query("SELECT * FROM FastEntity where endTime is NULL LIMIT 1")
    fun getActiveFast(): Flow<FastEntity?>

    @Query("SELECT * FROM FastEntity where endTime")
    fun getPastFasts(): Flow<List<FastEntity>>

    @Query("DELETE FROM FastEntity where id = :id")
    fun deleteFast(id: Long)
}