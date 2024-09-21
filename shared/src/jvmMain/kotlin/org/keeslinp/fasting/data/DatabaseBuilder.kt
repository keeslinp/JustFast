package org.keeslinp.fasting.data

import androidx.room.Room
import androidx.room.RoomDatabase
import org.keeslinp.fasting.data.AppDatabase
import java.io.File

fun getDatabaseBuilderJvm(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "my_room.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}