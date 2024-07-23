package rek.remindme.ui.reminder

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import rek.remindme.data.local.database.Reminder
import rek.remindme.ui.TestHelper

@HiltAndroidTest
class ReminderListScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun reminderListScreenContent_displayReminders() {
        composeTestRule.setContent {
            ReminderListScreenContent(
                items = _fakeData,
                onReminderClick = {},
                onReminderDelete = {}
            )
        }

        composeTestRule.onNodeWithText("Title 1", substring = false)
            .assertExists("Title 1 ${TestHelper.ASSERT_SUFFIX_MESSAGE}")

        composeTestRule.onNodeWithText("This is a really long text This is a really long text This is a really long text This is a really lo...", substring = false)
            .assertExists("This is a really long text This is a really long... ${TestHelper.ASSERT_SUFFIX_MESSAGE}")

        composeTestRule.onNodeWithText("Hello 2 test", substring = false)
            .assertExists("Hello 2 test ${TestHelper.ASSERT_SUFFIX_MESSAGE}")

        composeTestRule.onNodeWithText("Title 3", substring = false)
            .assertExists("Title 3 ${TestHelper.ASSERT_SUFFIX_MESSAGE}")
    }
}

private val _fakeData = listOf(
    Reminder(title = "Title 1", description = """This is a really long text
        |This is a really long text
        |This is a really long text
        |This is a really long text""".trimMargin(), unixTimestamp = 1697808658, notified = false),
    Reminder(title = "Title 2", description = "Hello 2\ntest", unixTimestamp = 1697808658, notified = false),
    Reminder(title = "Title 3", description = "Hello 3", unixTimestamp = 1697608658, notified = true)
)