package rek.remindme.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import rek.remindme.common.Consts
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@HiltAndroidTest
class CompleteTest {

    private lateinit var _lang: String

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        _lang = Locale.getDefault().language
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun e2e() {
        // No reminder yet, we have a message in the middle of the screen
        composeTestRule.onNodeWithTag(Consts.TestTag.EMPTY_REMINDER_LIST_MESSAGE)
            .assertExists("${Consts.TestTag.EMPTY_REMINDER_LIST_MESSAGE} should exists")

        // Click on the more vert button
        composeTestRule.onNodeWithTag(Consts.TestTag.MORE_VERT_BUTTON)
            .assertExists("${Consts.TestTag.MORE_VERT_BUTTON} should exists")
            .performClick()

        // Click on delete notified reminders menu item
        composeTestRule.onNodeWithTag(Consts.TestTag.CLEAR_NOTIFIED_REMINDERS_ITEM)
            .assertExists("${Consts.TestTag.CLEAR_NOTIFIED_REMINDERS_ITEM} should exists")
            .performClick()

        // A snackbar message should appears : no notified reminder to delete
        assertExactSnackbarMessage(
            "There is no notified reminders",
            "Il n'y a pas d'anciens rappels à supprimer"
        )


        // Click on the + button
        composeTestRule.onNodeWithTag(Consts.TestTag.ADD_REMINDER_BUTTON)
            .assertExists("${Consts.TestTag.ADD_REMINDER_BUTTON} should exists")
            .performClick()

        // Enter a title
        composeTestRule.onNodeWithTag(Consts.TestTag.INPUT_TITLE_FIELD)
            .assertExists("${Consts.TestTag.INPUT_TITLE_FIELD} should exists")
            .performTextInput("Title test 1")

        // Enter a description
        composeTestRule.onNodeWithTag(Consts.TestTag.INPUT_DESCRIPTION_FIELD)
            .assertExists("${Consts.TestTag.INPUT_DESCRIPTION_FIELD} should exists")
            .performTextInput("This is my description\nfor my first reminder")

        // Enter a date
        val tomorrow = Calendar.getInstance()
        tomorrow.time = Date()
        tomorrow.add(Calendar.DATE, 1)
        performDateInput(tomorrow.time)

        // Enter a time
        val fiveMinutesLater = Calendar.getInstance()
        fiveMinutesLater.time = Date()
        fiveMinutesLater.add(Calendar.MINUTE, 5)
        val hour = fiveMinutesLater.get(Calendar.HOUR_OF_DAY)
        val minute = fiveMinutesLater.get(Calendar.MINUTE)
        performTimeInput(hour, minute)

        // Save the reminder
        composeTestRule.onNodeWithTag(Consts.TestTag.SAVE_BUTTON)
            .assertExists("${Consts.TestTag.SAVE_BUTTON} should exists")
            .performClick()

        // A snackbar message should appears : no notified reminder to delete
        assertExactSnackbarMessage(
            "Reminder created",
            "Rappel créé"
        )

        // Redirect to reminder list
        composeTestRule.waitUntilExactlyOneExists(hasText("Title test 1"))

        composeTestRule.onNodeWithText("Title test 1", substring = false)
            .assertExists("Title test 1 should exists")

        composeTestRule.onNodeWithText("This is my description for my first reminder", substring = false)
            .assertExists("This is my description for my first reminder should exists")

        // Update the reminder
        composeTestRule.onNodeWithText("Title test 1", substring = false)
            .assertExists("Title test 1 should exists")
            .performClick()

        // The title is here
        composeTestRule.onNodeWithText("Title test 1", substring = false)
            .assertExists("Title test 1 should exists")

        // The title is here
        composeTestRule.onNodeWithText("This is my description\nfor my first reminder", substring = false)
            .assertExists("This is my description for my first reminder should exists")

        customWait()
    }

    @OptIn(ExperimentalTestApi::class)
    private fun assertExactSnackbarMessage(message: String, messageFr: String) {
        val snackbarMessage = if (_lang == "fr") messageFr else message

        composeTestRule.waitUntilExactlyOneExists(hasTextExactly(snackbarMessage), 5000)
        composeTestRule.onNodeWithText(text = snackbarMessage, substring = false).assertExists()
    }

    private fun performDateInput(date: Date = Date()) {

        val dateFormat = if (_lang == "fr") {
            SimpleDateFormat("ddMMyyyy").format(date)
        }
        else {
            SimpleDateFormat("MMddyyyy").format(date)
        }

        composeTestRule.onNodeWithTag(Consts.TestTag.INPUT_DATE_FIELD)
            .assertExists("${Consts.TestTag.INPUT_DATE_FIELD} should exists")
            .performClick()
        // Finding the input mode for date
        composeTestRule.onNodeWithContentDescription("input", substring = true)
            .performClick()
        // Finally, enter the date
        composeTestRule.onNodeWithContentDescription("Date", substring = true)
            .performTextReplacement(dateFormat)
        // Confirm
        composeTestRule.onNodeWithTag(Consts.TestTag.CONFIRM_BUTTON)
            .assertExists("${Consts.TestTag.CONFIRM_BUTTON} should exists")
            .performClick()
    }

    @OptIn(ExperimentalTestApi::class)
    private fun performTimeInput(hour: Int, minute: Int) {
        composeTestRule.onNodeWithTag(Consts.TestTag.INPUT_TIME_FIELD)
            .assertExists("${Consts.TestTag.INPUT_TIME_FIELD} should exists")
            .performClick()

        // Finding the input mode for time
        composeTestRule.onNodeWithTag(Consts.TestTag.SELECT_INPUT_MODE)
            .performClick()

        composeTestRule.waitUntilExactlyOneExists(hasContentDescription("for hour") and hasSetTextAction())

        var inputHour = hour
        val is24HourFormat = android.text.format.DateFormat.is24HourFormat(composeTestRule.activity.applicationContext)

        if (hour > 11 && !is24HourFormat) {
            composeTestRule.onNodeWithText("PM", substring = true)
                .assertExists("PM should exists")
                .performClick()

            if (hour > 12) inputHour = hour - 12
        }

        // 12 AM / midnight case
        if (hour == 0 && !is24HourFormat) inputHour = 12

        // Enter the hour
        composeTestRule.onNodeWithContentDescription("for hour", substring = true).assertExists("hour input should exists")
            .performTextReplacement(inputHour.toString())
        // Enter the minute
        composeTestRule.onNodeWithContentDescription("for minutes", substring = true).assertExists("minute input should exists")
            .performTextReplacement(minute.toString())
        composeTestRule.onNodeWithTag(Consts.TestTag.CONFIRM_BUTTON)
            .assertExists("${Consts.TestTag.CONFIRM_BUTTON} should exists")
            .performClick()
    }

    @OptIn(ExperimentalTestApi::class)
    private fun customWait() {
        composeTestRule.waitUntilExactlyOneExists(hasText("thisdoesntexist"), 10000)
    }
}