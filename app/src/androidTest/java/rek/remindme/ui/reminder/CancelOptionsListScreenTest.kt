package rek.remindme.ui.reminder

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import rek.remindme.common.Consts
import rek.remindme.ui.MainActivity
import rek.remindme.ui.TestHelper
import java.util.Locale

@HiltAndroidTest
class CancelOptionsListScreenTest {

    private lateinit var _testHelper: TestHelper

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        val lang = Locale.getDefault().language
        _testHelper = TestHelper(composeTestRule, lang)
    }

    @Test
    fun cancelDeleteNotifiedReminders() {
        composeTestRule.onNodeWithTag(Consts.TestTag.EMPTY_REMINDER_LIST_MESSAGE)
            .assertDoesNotExist()

        // Click on the more vert button
        _testHelper.getNodeWithTag(Consts.TestTag.MORE_VERT_BUTTON).performClick()

        // Click on delete notified reminders menu item
        _testHelper.getNodeWithTag(Consts.TestTag.CLEAR_NOTIFIED_REMINDERS_ITEM).performClick()

        // Cancel action
        _testHelper.getNodeWithTag(Consts.TestTag.CANCEL_BUTTON).performClick()

        // Notified reminder should not be deleted
        _testHelper.getNodeWithText("notified reminder")

        composeTestRule.onNodeWithTag(Consts.TestTag.EMPTY_REMINDER_LIST_MESSAGE)
            .assertDoesNotExist()
    }
}