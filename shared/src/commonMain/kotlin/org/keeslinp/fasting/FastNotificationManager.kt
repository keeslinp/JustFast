package org.keeslinp.fasting

interface FastNotificationManager {
    fun setup()
    fun notifyFastEnd()
    fun clearFastEnd()
    fun requestPermission()
}