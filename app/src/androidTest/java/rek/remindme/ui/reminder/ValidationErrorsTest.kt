package rek.remindme.ui.reminder

import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test
import rek.remindme.common.Consts
import rek.remindme.ui.MainActivityTest

@HiltAndroidTest
class ValidationErrorsTest: MainActivityTest() {

    @Before
    override fun setup() {
        super.setup()
    }

    @Test
    fun validationErrors() {
        // Click on the + button => Go to reminder creation mode
        testHelper.getNodeWithTag(Consts.TestTag.ADD_REMINDER_BUTTON).performClick()
        trySaveReminder()
        showMandatoryFieldsError()

        // Enter a title
        testHelper.getNodeWithTag(Consts.TestTag.INPUT_TITLE_FIELD).performTextInput("Title test 1")
        trySaveReminder()
        showMandatoryFieldsError()

        // Enter a date : today
        testHelper.performDateInput()
        trySaveReminder()
        showMandatoryFieldsError()

        // Enter a time : one minute ago
        testHelper.performTimeInput(-1)
        trySaveReminder()
        showPastReminderError()

        // Enter a time : right now
        testHelper.performTimeInput()
        trySaveReminder()
        showPastReminderError()

        // Enter a time : one minute later
        testHelper.performTimeInput(1)
        trySaveReminder()
        testHelper.assertExactSnackbarMessage("Reminder created", "Rappel créé")

        // Edit the reminder
        testHelper.getNodeWithText("Title test 1").performClick()

        // Enter a time : right now
        testHelper.performTimeInput()
        trySaveReminder()
        showPastReminderError()

        // Erase title
        testHelper.getNodeWithTag(Consts.TestTag.INPUT_TITLE_FIELD).performTextReplacement("")
        trySaveReminder()
        showMandatoryFieldsError()
    }

    private fun trySaveReminder() {
        // Save the reminder
        testHelper.getNodeWithTag(Consts.TestTag.SAVE_BUTTON).performClick()
    }

    private fun showMandatoryFieldsError() {
        // A snackbar message should appears : Mandatory fields are not filled
        testHelper.assertExactSnackbarMessage(
            "Mandatory fields are not filled",
            "Les champs obligatoires ne sont pas remplis"
        )
    }

    private fun showPastReminderError() {
        // A snackbar message should appears : Mandatory fields are not filled
        testHelper.assertExactSnackbarMessage(
            "Reminder cannot be set in the past",
            "Le rappel ne peut pas être défini à une date passée"
        )
    }
}