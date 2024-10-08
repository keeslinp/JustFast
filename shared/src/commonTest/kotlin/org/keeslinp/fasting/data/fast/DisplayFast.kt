package org.keeslinp.fasting.data.fast

import kotlinx.datetime.TimeZone
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class DisplayFastTest: KoinTest {
    @BeforeTest
    fun setup() {
        startKoin {
           modules(module {
               single<TimeZone> { TimeZone.of("America/Phoenix") }
           })
        }
    }

    @AfterTest
    fun teardown() {
        stopKoin()
    }

    @Test
    fun durationText() {
        assertEquals("10 hours", DisplayFast(Uuid.random(), 0, 60 * 60 * 10, null).durationText)
    }

    @Test
    fun startDate() {
        assertEquals("Wed, Sep 25", DisplayFast(Uuid.random(), 1727315122, goalDuration = 1000, null).startDate)
    }
    @Test
    fun startTime() {
        assertEquals("Sep 25, 2024 6:45 PM", DisplayFast(Uuid.random(), 1727315122, goalDuration = 1000, null).startTime)
    }
    @Test
    fun endTime() {
        assertEquals("Sep 25, 2024 6:46 PM", DisplayFast(Uuid.random(), 1727315122, goalDuration = 60, null).endTime)
    }
}
