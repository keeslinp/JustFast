package org.keeslinp.fasting.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.keeslinp.fasting.useCases.ToggleFastUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EndFastBroadcastReceiver: BroadcastReceiver(), KoinComponent {
    private val toggleFastUseCase: ToggleFastUseCase by inject()
    override fun onReceive(p0: Context?, p1: Intent?) {
        val result = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            toggleFastUseCase.toggleFast()
        }.invokeOnCompletion {
            result.finish()
        }
    }
}