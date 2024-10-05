package org.keeslinp.fasting.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.keeslinp.fasting.FastNotificationManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidNotificationScheduler : NotificationScheduler, KoinComponent {
    private val fastNotificationManager: FastNotificationManager by inject()
    private val context: Context by inject()
    private val pendingIntent =
        PendingIntent.getBroadcast(
            context,
            1,
            Intent(context, NotifyEndFastBroadcastReceiver::class.java).apply {
                action = "NOTIFY_END_FAST"
            },
            PendingIntent.FLAG_IMMUTABLE
        )

    override fun scheduleFastEnd(timeSeconds: Long) {
        (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)?.apply {
            set(
                AlarmManager.RTC_WAKEUP,
                timeSeconds * 1000,
                pendingIntent
            )
        }
    }

    override fun clearFastEnd() {
        fastNotificationManager.clearFastEnd()
        (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)?.apply {
            cancel(pendingIntent)
        }
    }
}