package rek.remindme.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import rek.remindme.common.Consts

@HiltAndroidTest
class CompleteTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun e2e() {
        // No reminder yet, we have a message in the middle of the screen
        composeTestRule.onNodeWithTag(Consts.TestTag.EMPTY_REMINDER_LIST_MESSAGE)
            .assertExists("${Consts.TestTag.EMPTY_REMINDER_LIST_MESSAGE} should exists")

        // Click on the + button
        composeTestRule.onNodeWithTag(Consts.TestTag.ADD_REMINDER_BUTTON)
            .assertExists("${Consts.TestTag.ADD_REMINDER_BUTTON} should exists")
            .performClick()
    }
}