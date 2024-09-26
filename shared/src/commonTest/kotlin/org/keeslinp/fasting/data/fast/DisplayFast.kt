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
        assertEquals("10 hours", DisplayFast(0, 0, 60 * 60 * 10).durationText)
    }

    @Test
    fun startDate() {
        assertEquals("Wed, Sep 25", DisplayFast(0, 1727315122, 1727315122 + 1000).startDate)
    }
    @Test
    fun startTime() {
        assertEquals("Sep 25, 2024 6:45 PM", DisplayFast(0, 1727315122, 1727315122 + 1000).startTime)
    }
    @Test
    fun endTime() {
        assertEquals("Sep 25, 2024 6:46 PM", DisplayFast(0, 1727315122, 1727315122 + 60).endTime)
    }
}