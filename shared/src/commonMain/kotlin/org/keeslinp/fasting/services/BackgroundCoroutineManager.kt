package org.keeslinp.fasting.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class BackgroundCoroutineManager {
    private val scope = CoroutineScope(SupervisorJob())
    private val services: List<CoroutineService> = listOf(EndFastService())
    fun start() {
        scope.launch {
            services.forEach {
                launch { it.start(scope) }
            }
        }
    }
    fun stop() {
        scope.cancel()
    }
}