package org.keeslinp.fasting.useCases

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import org.keeslinp.fasting.data.fast.FastDao
import org.keeslinp.fasting.data.fast.FastEntity
import org.keeslinp.fasting.FastNotificationManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.uuid.Uuid

class ToggleFastUseCase: KoinComponent {
    private val fastDao: FastDao by inject()

    suspend fun toggleFast() {
        val now = Clock.System.now().epochSeconds;
        val activeFast = fastDao.getActiveFast().firstOrNull()
        if (activeFast != null) {
            fastDao.update(activeFast.copy(endTime = now))
        } else {
            fastDao.start(FastEntity(startTime = now, goalDuration = 16 * 60 * 60, id = Uuid.random()))
        }
    }
}
