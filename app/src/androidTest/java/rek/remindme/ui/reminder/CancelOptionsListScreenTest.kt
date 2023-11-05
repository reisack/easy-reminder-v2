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
class CancelOptionsListScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun cancelDeleteNotifiedReminders() {
        composeTestRule.onNodeWithTag(Consts.TestTag.EMPTY_REMINDER_LIST_MESSAGE)
            .assertDoesNotExist()

        // Click on the more vert button
        composeTestRule.onNodeWithTag(Consts.TestTag.MORE_VERT_BUTTON)
            .assertExists("${Consts.TestTag.MORE_VERT_BUTTON} should exists")
            .performClick()

        // Click on delete notified reminders menu item
        composeTestRule.onNodeWithTag(Consts.TestTag.CLEAR_NOTIFIED_REMINDERS_ITEM)
            .assertExists("${Consts.TestTag.CLEAR_NOTIFIED_REMINDERS_ITEM} should exists")
            .performClick()

        // Cancel action
        composeTestRule.onNodeWithTag(Consts.TestTag.CANCEL_BUTTON)
            .assertExists("${Consts.TestTag.CANCEL_BUTTON} should exists")
            .performClick()

        // Notified reminder should not be deleted
        composeTestRule.onNodeWithText("notified reminder")
            .assertExists("notified reminder should exists")

        composeTestRule.onNodeWithTag(Consts.TestTag.EMPTY_REMINDER_LIST_MESSAGE)
            .assertDoesNotExist()
    }
}