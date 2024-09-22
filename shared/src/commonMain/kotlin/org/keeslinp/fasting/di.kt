package org.keeslinp.fasting

import org.keeslinp.fasting.data.dataModule
import org.keeslinp.fasting.platform.PlatformHelper
import org.keeslinp.fasting.useCases.useCasesModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {
    singleOf(::PlatformHelper)
}

fun startApp(callback: KoinApplication.() -> Unit) {
    startKoin {
        modules(sharedModule)
        modules(useCasesModule)
        modules(dataModule)
        callback()
    }
}