package rek.remindme.ui.reminder

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import rek.remindme.data.local.database.Reminder

@RunWith(AndroidJUnit4::class)
class ReminderListScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun reminderListScreenContent_displayReminders() {
        composeTestRule.setContent {
            ReminderListScreenContent(
                items = _fakeData,
                onReminderClick = {}
            )
        }

        composeTestRule.onNodeWithText("Title 1", substring = false).assertExists()
        composeTestRule.onNodeWithText("This is a really long text This is a really long text This is a really long text This is a really lo...", substring = false).assertExists()
        composeTestRule.onNodeWithText("Hello 2 test", substring = false).assertExists()
        composeTestRule.onNodeWithText("Title 3", substring = false).assertExists()
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