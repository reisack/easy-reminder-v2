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
import java.util.Locale

class TestableDateTimeHelper : DateTimeHelper() {
    override fun getCurrentUnixTimestamp(): Long {
        // Mock current date to : 24 October 2023 13:00:00 UTC
        return 1698152400000
    }
}

@RunWith(AndroidJUnit4::class)
class DateTimeHelperComposableTest {

    private val _dateTimeHelper: TestableDateTimeHelper = TestableDateTimeHelper()
    private lateinit var _lang: String

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        _lang = Locale.getDefault().language
    }

    @Test
    fun dateTimeHelper_getRemainingOrPastTime_10secondsLeft() {
        // 24 October 2023 13:00:10 UTC => 10 seconds left
        testRemainingOrPastTime(1698152410000, "10 seconds left", "Dans 10 secondes")
    }

    @Test
    fun dateTimeHelper_getRemainingOrPastTime_1minuteAgo() {
        // 24 October 2023 12:58:31 UTC => should round to : 1 minute ago
        testRemainingOrPastTime(1698152311000, "1 minute ago", "Il y a 1 minute")
    }

    @Test
    fun dateTimeHelper_getRemainingOrPastTime_2minutesAgo() {
        // 24 October 2023 12:58:29 UTC => should round to : 2 minutes ago
        testRemainingOrPastTime(1698152309000, "2 minutes ago", "Il y a 2 minutes")
    }

    @Test
    fun dateTimeHelper_getRemainingOrPastTime_11hoursLeft() {
        // 24 October 2023 23:31:00 UTC => should round to : 11 hours left
        testRemainingOrPastTime(1698190260000, "11 hours left", "Dans 11 heures")
    }

    @Test
    fun dateTimeHelper_getRemainingOrPastTime_3daysLeft() {
        // 27 October 2023 09:00:00 UTC => should round to : 3 days left
        testRemainingOrPastTime(1698397200000, "3 days left", "Dans 3 jours")
    }

    @Test
    fun dateTimeHelper_getRemainingOrPastTime_6monthLeft() {
        // 10 April 2024 11:45:00 UTC => should round to : 6 months left
        testRemainingOrPastTime(1712749500000, "6 months left", "Dans 6 mois")
    }

    @Test
    fun dateTimeHelper_getRemainingOrPastTime_1yearLeft() {
        // 10 October 2024 11:00:00 UTC => should round to : 1 year left
        testRemainingOrPastTime(1728558000000, "1 year left", "Dans 1 an")
    }

    @Test
    fun dateTimeHelper_getRemainingOrPastTime_2yearLeft() {
        // 10 March 2026 11:00:00 UTC => should round to : 2 year left
        testRemainingOrPastTime(1773140400000, "2 years left", "Dans 2 ans")
    }

    private fun testRemainingOrPastTime(unixTimestamp: Long, displayedTime: String, displayedTimeInFr: String) {
        composeTestRule.setContent {
            Text(
                text = _dateTimeHelper.getRemainingOrPastTime(
                    unixTimestamp = unixTimestamp
                )
            )
        }

        composeTestRule.onNodeWithText(
            text = when (_lang) {
                "fr" -> displayedTimeInFr
                else -> displayedTime
            }
            , substring = false
        ).assertExists()
    }
}