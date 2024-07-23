package rek.remindme.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
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

class TestHelper(
    private val _composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
    private val _lang: String
) {

    companion object {
        const val ASSERT_SUFFIX_MESSAGE = "should exists"
    }

    fun getNodeWithTag(tag: String): SemanticsNodeInteraction {
        return _composeTestRule.onNodeWithTag(tag)
            .assertExists("$tag $ASSERT_SUFFIX_MESSAGE")
    }

    fun getNodeWithText(text: String): SemanticsNodeInteraction {
        return _composeTestRule.onNodeWithText(text, substring = false)
            .assertExists("$text $ASSERT_SUFFIX_MESSAGE")
    }

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
            .assertExists("${Consts.TestTag.INPUT_DATE_FIELD} $ASSERT_SUFFIX_MESSAGE")
            .performClick()

        // Finding the input mode for date
        val switchInputModeDesc = if (_lang == "fr") "mode de saisie Texte" else "text input mode"
        _composeTestRule.onNodeWithContentDescription(switchInputModeDesc, substring = true, ignoreCase = true)
            .performClick()

        // Finally, enter the date
        _composeTestRule.onNodeWithContentDescription("Date,", substring = true)
            .performTextReplacement(dateFormat)

        // Confirm
        _composeTestRule.onNodeWithTag(Consts.TestTag.CONFIRM_BUTTON)
            .assertExists("${Consts.TestTag.CONFIRM_BUTTON} $ASSERT_SUFFIX_MESSAGE")
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
            .assertExists("${Consts.TestTag.INPUT_TIME_FIELD} $ASSERT_SUFFIX_MESSAGE")
            .performClick()

        // Finding the input mode for time
        _composeTestRule.onNodeWithTag(Consts.TestTag.SELECT_INPUT_MODE)
            .performClick()

        val forHourMessage = if (_lang == "fr") "en heures" else "for hour"
        _composeTestRule.waitUntilExactlyOneExists(hasContentDescription(forHourMessage) and hasSetTextAction())

        var inputHour = hour
        val is24HourFormat = android.text.format
            .DateFormat.is24HourFormat(_composeTestRule.activity.applicationContext)

        if (hour > 11 && !is24HourFormat) {
            _composeTestRule.onNodeWithText("PM", substring = false)
                .assertExists("PM $ASSERT_SUFFIX_MESSAGE")
                .performClick()

            if (hour > 12) inputHour = hour - 12
        }

        // 12 AM / midnight case
        if (hour == 0 && !is24HourFormat) inputHour = 12

        // Enter the hour
        _composeTestRule.onNodeWithContentDescription(forHourMessage, substring = true)
            .assertExists("hour input $ASSERT_SUFFIX_MESSAGE")
            .performTextReplacement(inputHour.toString())

        // Enter the minute
        val forMinuteMessage = if (_lang == "fr") "en minutes" else "for minutes"
        _composeTestRule.onNodeWithContentDescription(forMinuteMessage, substring = true)
            .assertExists("minute input $ASSERT_SUFFIX_MESSAGE")
            .performTextReplacement(minute.toString())

        // Confirm
        _composeTestRule.onNodeWithTag(Consts.TestTag.CONFIRM_BUTTON)
            .assertExists("${Consts.TestTag.CONFIRM_BUTTON} should exists")
            .performClick()
    }
}