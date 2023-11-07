package rek.remindme.ui.reminder

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import rek.remindme.common.Consts
import rek.remindme.ui.MainActivityTest

@HiltAndroidTest
class CancelOptionsUpsertScreenTest: MainActivityTest() {

    @Before
    override fun setup() {
        super.setup()
    }

    @Test
    fun cancelDeleteReminder() {
        // Go to update mode
        testHelper.getNodeWithText("notified reminder").performClick()

        // Delete dialog
        testHelper.getNodeWithTag(Consts.TestTag.DELETE_REMINDER_BUTTON).performClick()

        // No delete
        testHelper.getNodeWithTag(Consts.TestTag.CANCEL_BUTTON).performClick()

        // Back to reminder list
        testHelper.getNodeWithTag(Consts.TestTag.BACK_BUTTON).performClick()

        // Reminder should be there
        composeTestRule.onNodeWithText("notified reminder")
            .assertExists("notified reminder should still exists")
    }
}