package org.keeslinp.fasting.screens

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import org.keeslinp.fasting.data.fast.FastDao
import org.keeslinp.fasting.data.fast.FastEntity
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class HomeViewModelTest : KoinTest {
    private val fastDao = mock<FastDao>()

    @BeforeTest
    fun setup() {
        startKoin {
            modules(module {
                single<TimeZone> { TimeZone.of("America/Phoenix") }
                single<FastDao> { fastDao }
            })
        }
    }

    @AfterTest
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `first history fast is resumable when no active fast`() {
        every { fastDao.getActiveFast() } returns flowOf(null)
        every { fastDao.getPastFasts() } returns flowOf(
            listOf(
                FastEntity(Uuid.random(), 123, 1234, 100),
                FastEntity(Uuid.random(), 10, 11, 1)
            )
        )

        val model = HomeViewModel()
        val history = runBlocking {
            model.history.filterNotNull().filter { !it.isEmpty() }.first()
        }
        assertTrue(history.first().resumable)
        assertFalse(history[1].resumable)
    }

}