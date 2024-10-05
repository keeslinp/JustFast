package org.keeslinp.fasting.data.fast

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface FastDao {
    @Upsert
    suspend fun start(fast: FastEntity)

    @Update
    suspend fun update(fast: FastEntity)

    @Query("SELECT * FROM FastEntity where endTime is NULL LIMIT 1")
    fun getActiveFast(): Flow<FastEntity?>

    @Query("SELECT COUNT(*) FROM FastEntity where endTime is NULL")
    fun getActiveFastCount(): Flow<Int>

    @Query("SELECT id FROM FastEntity where endTime ORDER BY endTime DESC")
    fun getPastFasts(): Flow<List<Uuid>>

    @Query("SELECT id FROM FastEntity where endTime ORDER BY endTime DESC LIMIT 1")
    fun getMostRecentFast(): Flow<Uuid?>

    @Query("DELETE FROM FastEntity where id = :id")
    suspend fun deleteFast(id: Uuid)

    @Query("SELECT * FROM FastEntity where id is :id LIMIT 1")
    suspend fun getFast(id: Uuid): FastEntity?

    @Query("SELECT * FROM FastEntity where id is :id LIMIT 1")
    fun watchFast(id: Uuid): Flow<FastEntity?>
}