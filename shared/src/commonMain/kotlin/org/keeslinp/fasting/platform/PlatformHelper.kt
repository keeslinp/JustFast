package org.keeslinp.fasting.platform

import androidx.room.RoomDatabase
import org.keeslinp.fasting.data.AppDatabase
import org.koin.core.component.KoinComponent

expect class PlatformHelper() : KoinComponent {
    fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
}