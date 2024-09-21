package org.keeslinp.fasting

import org.keeslinp.fasting.data.AppDatabase
import org.keeslinp.fasting.platform.PlatformHelper
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {
    singleOf(::PlatformHelper)
    single<AppDatabase> { get<PlatformHelper>().getDatabaseBuilder().build() }
}

fun startApp(callback: KoinApplication.() -> Unit) {
    startKoin {
        modules(sharedModule)
        callback()
    }
}