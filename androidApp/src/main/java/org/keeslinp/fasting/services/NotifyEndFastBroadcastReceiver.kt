package org.keeslinp.fasting.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.touchlab.kermit.Logger
import org.keeslinp.fasting.FastNotificationManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotifyEndFastBroadcastReceiver: BroadcastReceiver(), KoinComponent {
    private val logger = Logger.withTag("NotifyEndFastBroadcastReceiver")
    private val fastNotificationManager: FastNotificationManager by inject()
    override fun onReceive(context: Context?, intent: Intent?) {
        logger.i { "Received broadcast NOTIFY_END_FAST" }
        fastNotificationManager.notifyFastEnd()
    }
}
