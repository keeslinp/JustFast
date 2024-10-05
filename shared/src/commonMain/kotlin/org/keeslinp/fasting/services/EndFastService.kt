package org.keeslinp.fasting.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.keeslinp.fasting.FastNotificationManager
import org.keeslinp.fasting.data.fast.FastDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EndFastService: KoinComponent, CoroutineService {
    private val fastDao: FastDao by inject()
    private val notificationScheduler: NotificationScheduler by inject()
    override suspend fun start(scope: CoroutineScope) {
        fastDao.getActiveFast().onEach {
            if (it == null) {
                notificationScheduler.clearFastEnd()
            } else {
                notificationScheduler.scheduleFastEnd(it.startTime + it.goalDuration)
            }
        }.launchIn(scope)
    }
}