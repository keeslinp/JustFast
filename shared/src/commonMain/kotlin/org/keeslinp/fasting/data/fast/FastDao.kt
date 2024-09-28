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

    @Query("SELECT * FROM FastEntity where endTime ORDER BY endTime DESC")
    fun getPastFasts(): Flow<List<FastEntity>>

    @Query("DELETE FROM FastEntity where id = :id")
    suspend fun deleteFast(id: Long)

    @Query("SELECT * FROM FastEntity where id is :id LIMIT 1")
     suspend fun getFast(id: Long): FastEntity?
}