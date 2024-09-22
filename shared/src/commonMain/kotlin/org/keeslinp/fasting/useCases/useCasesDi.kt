package org.keeslinp.fasting.useCases

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCasesModule = module {
    factoryOf(::ToggleFastUseCase)
}