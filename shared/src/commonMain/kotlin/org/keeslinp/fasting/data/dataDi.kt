package org.keeslinp.fasting.data

import org.keeslinp.fasting.data.fast.FastDao
import org.keeslinp.fasting.platform.PlatformHelper
import org.koin.dsl.module

val dataModule = module {
    single<AppDatabase> { get<PlatformHelper>().getDatabaseBuilder().build() }
    single<FastDao> { get<AppDatabase>().getFastDao() }
}