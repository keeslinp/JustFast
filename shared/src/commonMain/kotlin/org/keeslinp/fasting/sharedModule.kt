package org.keeslinp.fasting

import kotlinx.datetime.TimeZone
import org.keeslinp.fasting.data.dataModule
import org.keeslinp.fasting.platform.PlatformHelper
import org.keeslinp.fasting.services.BackgroundCoroutineManager
import org.keeslinp.fasting.useCases.useCasesModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {
    singleOf(::PlatformHelper)
    single<TimeZone>{ TimeZone.currentSystemDefault() }
    single { BackgroundCoroutineManager() }
    includes(useCasesModule, dataModule)
}