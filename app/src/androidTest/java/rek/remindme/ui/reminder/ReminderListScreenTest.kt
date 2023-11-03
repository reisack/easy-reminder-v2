package rek.remindme.ui.reminder

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import rek.remindme.data.di.FakeReminderRepository
import rek.remindme.data.local.database.Reminder

/**
 * UI tests for [ReminderListScreen].
 */
@RunWith(AndroidJUnit4::class)
class ReminderListScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        composeTestRule.setContent {
            ReminderListScreenContent(items = _fakeData, onReminderClick = {}, viewModel = ReminderListViewModel(FakeReminderRepository()))
        }
    }

    @Test
    fun firstItem_exists() {
        composeTestRule.onNodeWithText(_fakeData.first().title, substring = true).assertExists().performClick()
    }
}

private val _fakeData = listOf(
    Reminder(title = "Title 1", description = "Hello 1", unixTimestamp = 1697808658, notified = false),
    Reminder(title = "Title 2", description = "Hello 2", unixTimestamp = 1697808658, notified = false),
    Reminder(title = "Title 3", description = "Hello 3", unixTimestamp = 1697808658, notified = false)
)
