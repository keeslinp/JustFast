package org.keeslinp.fasting.services

interface NotificationScheduler {
    fun scheduleFastEnd(timeSeconds: Long)
    fun clearFastEnd()
}