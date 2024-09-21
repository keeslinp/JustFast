package org.keeslinp.fasting.platform

import androidx.room.RoomDatabase
import org.keeslinp.fasting.data.AppDatabase
import org.keeslinp.fasting.data.getDatabaseBuilderJvm
import org.koin.core.component.KoinComponent

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PlatformHelper actual constructor() : KoinComponent {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> = getDatabaseBuilderJvm()

}