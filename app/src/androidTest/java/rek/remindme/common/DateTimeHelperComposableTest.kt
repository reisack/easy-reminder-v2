package rek.remindme.common

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class DateTimeHelperComposableTest {

    // Mock current date to : Tuesday 24 October 2023 13:00:00 UTC
    private val _currentDate: Long = 1698152400000

    private lateinit var lang: String

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        lang = Locale.getDefault().language
    }

    @Test
    fun dateTimeHelper_getRemainingOrPastTime_10secondsLeft() {
        // Tuesday 24 October 2023 13:00:10 UTC => 10 seconds left
        testRemainingOrPastTime(1698152410000, "10 seconds left", "Dans 10 secondes")
    }

    @Test
    fun dateTimeHelper_getRemainingOrPastTime_1minuteAgo() {
        // Tuesday 24 October 2023 12:58:31 UTC => should round to : 1 minute ago
        testRemainingOrPastTime(1698152311000, "1 minute ago", "Il y a 1 minute")
    }

    @Test
    fun dateTimeHelper_getRemainingOrPastTime_2minutesAgo() {
        // Tuesday 24 October 2023 12:58:29 UTC => should round to : 2 minutes ago
        testRemainingOrPastTime(1698152309000, "2 minutes ago", "Il y a 2 minutes")
    }

    private fun testRemainingOrPastTime(unixTimestamp: Long, displayedTime: String, displayedTimeInFr: String) {
        composeTestRule.setContent {
            Text(
                text = DateTimeHelper.getRemainingOrPastTime(
                    unixTimestamp = unixTimestamp,
                    mockCurrentDate = Date(_currentDate)
                )
            )
        }

        when (lang) {
            "fr" -> composeTestRule.onNodeWithText(text = displayedTimeInFr, substring = false).assertExists()
            else -> composeTestRule.onNodeWithText(text = displayedTime, substring = false).assertExists()
        }
    }
}