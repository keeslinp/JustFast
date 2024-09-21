package org.keeslinp.fasting.platform

import android.content.Context
import androidx.room.RoomDatabase
import org.keeslinp.fasting.data.AppDatabase
import org.keeslinp.fasting.data.getDatabaseBuilderAndroid
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PlatformHelper : KoinComponent {
    private val ctx: Context by inject()
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> = getDatabaseBuilderAndroid(ctx)
}