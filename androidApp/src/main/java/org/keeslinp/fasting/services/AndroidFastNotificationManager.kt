package org.keeslinp.fasting.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import co.touchlab.kermit.Logger
import org.keeslinp.fasting.FastNotificationManager
import org.keeslinp.fasting.MainActivity
import org.keeslinp.fasting.R
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidFastNotificationManager : KoinComponent, FastNotificationManager {
    private val context: Context by inject()
    private val channelId = "FASTING_CHANNEL"
    private val logger = Logger.withTag("AndroidFastNotificationManager")
    companion object {
        const val FAST_END = 1;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setup() {
        logger.i { "Setup"}
        val channel = NotificationChannel(
            channelId,
            "Fasting Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notify you when your target fast duration is reached"
        }
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }

    override fun notifyFastEnd() {
        logger.i { "Notifying that the fast has ended" }
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                logger.w { "Don't have permission for notifications, aborting" }
                return@with
            }
            // notificationId is a unique int for each notification that you must define.
            notify(
                FAST_END,
                NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Goal reached")
                    .setContentText("You have reached your goal duration. Time to end your fast.")
                    .setContentIntent(
                        PendingIntent.getActivity(
                            context,
                            0,
                            Intent(context, MainActivity::class.java),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                    .addAction(
                        0,
                        "Stop",
                        PendingIntent.getBroadcast(
                            context,
                            0,
                            Intent(context, EndFastBroadcastReceiver::class.java).apply {
                                action = "END_FAST"
                            },
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                    .setAutoCancel(true)
                    .build()
            )
        }
    }

    override fun clearFastEnd() {
        logger.i { "Clearing the fast end notification" }
        NotificationManagerCompat.from(context).cancel(FAST_END)
    }
    override fun requestPermission() {
        TODO("Not yet implemented")
    }
}