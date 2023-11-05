package rek.remindme.ui.reminder

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import rek.remindme.common.Consts
import rek.remindme.ui.MainActivity

@HiltAndroidTest
class CancelOptionsUpsertScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun cancelDeleteReminder() {
        // Go to update mode
        composeTestRule.onNodeWithText("notified reminder")
            .assertExists("notified reminder should exists")
            .performClick()

        composeTestRule.onNodeWithTag(Consts.TestTag.DELETE_REMINDER_BUTTON)
            .assertExists("${Consts.TestTag.DELETE_REMINDER_BUTTON} should exists")
            .performClick()

        // No delete
        composeTestRule.onNodeWithTag(Consts.TestTag.CANCEL_BUTTON)
            .assertExists("${Consts.TestTag.CANCEL_BUTTON} should exists")
            .performClick()

        // Back to reminder list
        composeTestRule.onNodeWithTag(Consts.TestTag.BACK_BUTTON)
            .assertExists("${Consts.TestTag.BACK_BUTTON} should exists")
            .performClick()

        // Reminder should be there
        composeTestRule.onNodeWithText("notified reminder")
            .assertExists("notified reminder should always exists")
    }
}