package org.keeslinp.fasting.platform

import androidx.room.RoomDatabase
import org.keeslinp.fasting.data.AppDatabase
import org.keeslinp.fasting.data.getDatabaseBuilderIos
import org.koin.core.annotation.Single

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PlatformHelper {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> =
        getDatabaseBuilderIos()
}