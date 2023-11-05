package rek.remindme.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.rules.ActivityScenarioRule
import rek.remindme.common.Consts
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

internal class TestHelper(
    private val _composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
    private val _lang: String
) {
    @OptIn(ExperimentalTestApi::class)
    fun assertExactSnackbarMessage(message: String, messageFr: String) {
        val snackbarMessage = if (_lang == "fr") messageFr else message

        _composeTestRule.waitUntilExactlyOneExists(hasTextExactly(snackbarMessage), 5000)
        _composeTestRule.onNodeWithText(text = snackbarMessage, substring = false).assertExists()
    }

    fun performDateInput(daysOffset: Int = 0) {

        val date = Calendar.getInstance()
        date.time = Date()
        date.add(Calendar.DATE, daysOffset)

        val dateFormat = if (_lang == "fr") {
            SimpleDateFormat("ddMMyyyy").format(date.time)
        }
        else {
            SimpleDateFormat("MMddyyyy").format(date.time)
        }

        _composeTestRule.onNodeWithTag(Consts.TestTag.INPUT_DATE_FIELD)
            .assertExists("${Consts.TestTag.INPUT_DATE_FIELD} should exists")
            .performClick()
        // Finding the input mode for date
        _composeTestRule.onNodeWithContentDescription("input", substring = true)
            .performClick()
        // Finally, enter the date
        _composeTestRule.onNodeWithContentDescription("Date", substring = true)
            .performTextReplacement(dateFormat)
        // Confirm
        _composeTestRule.onNodeWithTag(Consts.TestTag.CONFIRM_BUTTON)
            .assertExists("${Consts.TestTag.CONFIRM_BUTTON} should exists")
            .performClick()
    }

    @OptIn(ExperimentalTestApi::class)
    fun performTimeInput(minutesOffset: Int = 0) {

        val date = Calendar.getInstance()
        date.time = Date()
        date.add(Calendar.MINUTE, minutesOffset)
        val hour = date.get(Calendar.HOUR_OF_DAY)
        val minute = date.get(Calendar.MINUTE)

        _composeTestRule.onNodeWithTag(Consts.TestTag.INPUT_TIME_FIELD)
            .assertExists("${Consts.TestTag.INPUT_TIME_FIELD} should exists")
            .performClick()

        // Finding the input mode for time
        _composeTestRule.onNodeWithTag(Consts.TestTag.SELECT_INPUT_MODE)
            .performClick()

        _composeTestRule.waitUntilExactlyOneExists(hasContentDescription("for hour") and hasSetTextAction())

        var inputHour = hour
        val is24HourFormat = android.text.format
            .DateFormat.is24HourFormat(_composeTestRule.activity.applicationContext)

        if (hour > 11 && !is24HourFormat) {
            _composeTestRule.onNodeWithText("PM", substring = false)
                .assertExists("PM should exists")
                .performClick()

            if (hour > 12) inputHour = hour - 12
        }

        // 12 AM / midnight case
        if (hour == 0 && !is24HourFormat) inputHour = 12

        // Enter the hour
        _composeTestRule.onNodeWithContentDescription("for hour", substring = true).assertExists("hour input should exists")
            .performTextReplacement(inputHour.toString())
        // Enter the minute
        _composeTestRule.onNodeWithContentDescription("for minutes", substring = true).assertExists("minute input should exists")
            .performTextReplacement(minute.toString())
        _composeTestRule.onNodeWithTag(Consts.TestTag.CONFIRM_BUTTON)
            .assertExists("${Consts.TestTag.CONFIRM_BUTTON} should exists")
            .performClick()
    }
}