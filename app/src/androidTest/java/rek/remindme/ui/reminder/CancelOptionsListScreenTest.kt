package rek.remindme.ui.reminder

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import rek.remindme.common.Consts
import rek.remindme.ui.MainActivityTest

@HiltAndroidTest
class CancelOptionsListScreenTest: MainActivityTest() {

    @Before
    override fun setup() {
        super.setup()
    }

    @Test
    fun cancelDeleteNotifiedReminders() {
        composeTestRule.onNodeWithTag(Consts.TestTag.EMPTY_REMINDER_LIST_MESSAGE)
            .assertDoesNotExist()

        // Click on the more vert button
        testHelper.getNodeWithTag(Consts.TestTag.MORE_VERT_BUTTON).performClick()

        // Click on delete notified reminders menu item
        testHelper.getNodeWithTag(Consts.TestTag.CLEAR_NOTIFIED_REMINDERS_ITEM).performClick()

        // Cancel action
        testHelper.getNodeWithTag(Consts.TestTag.CANCEL_BUTTON).performClick()

        // Notified reminder should not be deleted
        testHelper.getNodeWithText("notified reminder")

        composeTestRule.onNodeWithTag(Consts.TestTag.EMPTY_REMINDER_LIST_MESSAGE)
            .assertDoesNotExist()
    }
}