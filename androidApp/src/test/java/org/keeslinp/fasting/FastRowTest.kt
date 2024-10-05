package org.keeslinp.fasting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import kotlinx.datetime.TimeZone
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.keeslinp.fasting.data.fast.DisplayFast
import org.keeslinp.fasting.ui.FastRow
import org.keeslinp.fasting.ui.theme.JustFastTheme
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.robolectric.RobolectricTestRunner
import kotlin.uuid.Uuid

@RunWith(RobolectricTestRunner::class)
class FastRowTest: KoinTest {
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single<TimeZone> { TimeZone.of("America/Phoenix") }
        })
    }
    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun `render start date`() {
        composeTestRule.setContent {
            JustFastTheme {
                FastRow(fast = DisplayFast(Uuid.random(), startSeconds = 1727315122, goalDuration = 1000, endSeconds = null), updater = {}, delete = {}, expanded = false, toggleExpanded = {})
            }
        }
        composeTestRule.onNodeWithTag("start-date", useUnmergedTree = true).assertTextEquals("Wed, Sep 25")
    }

    @Test
    fun `show and hide start time`() {
        composeTestRule.setContent {
            var expanded by remember { mutableStateOf(false) }
            JustFastTheme {
                FastRow(fast = DisplayFast(Uuid.random(), 1727315122, goalDuration = 1000, endSeconds = null), updater = {}, delete = {}, expanded = expanded, toggleExpanded = { expanded = !expanded })
            }
        }
        composeTestRule.onNodeWithText("Start:", useUnmergedTree = true).assertIsNotDisplayed()
        composeTestRule.onNodeWithContentDescription("Expand").performClick()
        composeTestRule.onNodeWithText("Start:", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Collapse").performClick()
        composeTestRule.onNodeWithText("Start:", useUnmergedTree = true).assertIsNotDisplayed()
    }
}
