package rek.remindme.ui.reminder

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
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
class ValidationErrorsTest {

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
    fun validationErrors() {
        // Click on the + button => Go to reminder creation mode
        _testHelper.getNodeWithTag(Consts.TestTag.ADD_REMINDER_BUTTON).performClick()
        trySaveReminder()
        showMandatoryFieldsError()

        // Enter a title
        _testHelper.getNodeWithTag(Consts.TestTag.INPUT_TITLE_FIELD).performTextInput("Title test 1")
        trySaveReminder()
        showMandatoryFieldsError()

        // Enter a date : today
        _testHelper.performDateInput()
        trySaveReminder()
        showMandatoryFieldsError()

        // Enter a time : one minute ago
        _testHelper.performTimeInput(-1)
        trySaveReminder()
        showPastReminderError()

        // Enter a time : right now
        _testHelper.performTimeInput()
        trySaveReminder()
        showPastReminderError()

        // Enter a time : one minute later
        _testHelper.performTimeInput(1)
        trySaveReminder()
        _testHelper.assertExactSnackbarMessage("Reminder created", "Rappel créé")

        // Edit the reminder
        _testHelper.getNodeWithText("Title test 1").performClick()

        // Enter a time : right now
        _testHelper.performTimeInput()
        trySaveReminder()
        showPastReminderError()

        // Erase title
        _testHelper.getNodeWithTag(Consts.TestTag.INPUT_TITLE_FIELD).performTextReplacement("")
        trySaveReminder()
        showMandatoryFieldsError()
    }

    private fun trySaveReminder() {
        // Save the reminder
        _testHelper.getNodeWithTag(Consts.TestTag.SAVE_BUTTON).performClick()
    }

    private fun showMandatoryFieldsError() {
        // A snackbar message should appears : Mandatory fields are not filled
        _testHelper.assertExactSnackbarMessage(
            "Mandatory fields are not filled",
            "Les champs obligatoires ne sont pas remplis"
        )
    }

    private fun showPastReminderError() {
        // A snackbar message should appears : Mandatory fields are not filled
        _testHelper.assertExactSnackbarMessage(
            "Reminder cannot be set in the past",
            "Le rappel ne peut pas être défini à une date passée"
        )
    }
}