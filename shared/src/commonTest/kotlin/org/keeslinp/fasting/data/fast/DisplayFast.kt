package org.keeslinp.fasting.data.fast

import kotlin.test.Test
import kotlin.test.assertEquals

class DisplayFastTest {

    @Test
    fun durationText() {
        assertEquals("10 hours", DisplayFast(0, 0, 60 * 60 * 10).durationText)
    }

    @Test
    fun startDate() {
        assertEquals("Wed, Sep 25", DisplayFast(0, 1727315122, 1727315122 + 1000).startDate)
    }
}