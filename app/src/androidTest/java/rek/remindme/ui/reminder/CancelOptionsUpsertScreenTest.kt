package rek.remindme.ui.reminder

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
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
class CancelOptionsUpsertScreenTest {

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
    fun cancelDeleteReminder() {
        // Go to update mode
        _testHelper.getNodeWithText("notified reminder").performClick()

        // Delete dialog
        _testHelper.getNodeWithTag(Consts.TestTag.DELETE_REMINDER_BUTTON).performClick()

        // No delete
        _testHelper.getNodeWithTag(Consts.TestTag.CANCEL_BUTTON).performClick()

        // Back to reminder list
        _testHelper.getNodeWithTag(Consts.TestTag.BACK_BUTTON).performClick()

        // Reminder should be there
        composeTestRule.onNodeWithText("notified reminder")
            .assertExists("notified reminder should still exists")
    }
}