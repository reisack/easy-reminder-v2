package rek.remindme.ui.reminder

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
class CompleteWorkflowWithoutErrorTest {

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
    fun completeWorkflowWithoutError() {
        checkReminderListIsNotEmpty()
        checkNotifiedReminderToDelete()
        checkEmptyReminderList()
        checkNoNotifiedReminderToDelete()
        checkCreateReminderWithValidInformation()
        checkReminderAppearsInList()
        checkUpdateTheReminderWithValidInformation()
        checkReminderUpdatedInList()
        checkNoNotifiedReminderToDelete()
        checkDeleteReminder()
        checkEmptyReminderList()
    }

    private fun checkReminderListIsNotEmpty() {
        // No reminder yet, we have a message in the middle of the screen
        composeTestRule.onNodeWithTag(Consts.TestTag.EMPTY_REMINDER_LIST_MESSAGE)
            .assertDoesNotExist()
    }

    private fun checkEmptyReminderList() {
        // No reminder yet, we have a message in the middle of the screen
        _testHelper.getNodeWithTag(Consts.TestTag.EMPTY_REMINDER_LIST_MESSAGE)
    }

    private fun checkNotifiedReminderToDelete() {
        // Click on the more vert button
        _testHelper.getNodeWithTag(Consts.TestTag.MORE_VERT_BUTTON).performClick()

        // Click on delete notified reminders menu item
        _testHelper.getNodeWithTag(Consts.TestTag.CLEAR_NOTIFIED_REMINDERS_ITEM).performClick()

        // Confirm
        _testHelper.getNodeWithTag(Consts.TestTag.CONFIRM_BUTTON).performClick()
    }

    private fun checkNoNotifiedReminderToDelete() {
        // Click on the more vert button
        _testHelper.getNodeWithTag(Consts.TestTag.MORE_VERT_BUTTON).performClick()

        // Click on delete notified reminders menu item
        _testHelper.getNodeWithTag(Consts.TestTag.CLEAR_NOTIFIED_REMINDERS_ITEM).performClick()

        // A snackbar message should appears : no notified reminder to delete
        _testHelper.assertExactSnackbarMessage(
            "There is no notified reminders",
            "Il n'y a pas d'anciens rappels à supprimer"
        )
    }

    private fun checkCreateReminderWithValidInformation() {
        // Click on the + button
        _testHelper.getNodeWithTag(Consts.TestTag.ADD_REMINDER_BUTTON).performClick()

        // Enter a title
        _testHelper.getNodeWithTag(Consts.TestTag.INPUT_TITLE_FIELD)
            .performTextInput("Title test 1")

        // Enter a description
        _testHelper.getNodeWithTag(Consts.TestTag.INPUT_DESCRIPTION_FIELD)
            .performTextInput("This is my description\nfor my first reminder")

        // Enter a date
        _testHelper.performDateInput()

        // Enter a time
        _testHelper.performTimeInput(10)

        saveReminder("Title test 1", false)
    }

    private fun checkReminderAppearsInList() {
        // Created reminder exists on Reminder list
        _testHelper.getNodeWithText("Title test 1")
        _testHelper.getNodeWithText("This is my description for my first reminder")
    }

    private fun checkUpdateTheReminderWithValidInformation() {
        // Click on the reminder to update it
        _testHelper.getNodeWithText("Title test 1").performClick()

        // Title is filled
        _testHelper.getNodeWithText("Title test 1")
            .performTextInput("Updated 1 : ")

        // Description is filled
        _testHelper.getNodeWithText("This is my description\nfor my first reminder")
            .performTextInput("Updated 2 : ")

        saveReminder("Updated 1 : Title test 1", true)
    }

    private fun checkReminderUpdatedInList() {
        // Created reminder exists on Reminder list
        _testHelper.getNodeWithText("Updated 1 : Title test 1")
        _testHelper.getNodeWithText("Updated 2 : This is my description for my first reminder")
    }

    @OptIn(ExperimentalTestApi::class)
    private fun saveReminder(title: String, isUpdate: Boolean) {
        // Save the reminder
        _testHelper.getNodeWithTag(Consts.TestTag.SAVE_BUTTON).performClick()

        // A snackbar message should appears : no notified reminder to delete
        _testHelper.assertExactSnackbarMessage(
            if (isUpdate) "Reminder updated" else "Reminder created",
            if (isUpdate) "Rappel mis à jour" else "Rappel créé"
        )

        // Redirect to reminder list
        composeTestRule.waitUntilExactlyOneExists(hasText(title))
    }

    private fun checkDeleteReminder() {
        // Go to update mode again
        _testHelper.getNodeWithText("Updated 1 : Title test 1").performClick()

        // Delete dialog
        _testHelper.getNodeWithTag(Consts.TestTag.DELETE_REMINDER_BUTTON).performClick()

        // Confirm
        _testHelper.getNodeWithTag(Consts.TestTag.CONFIRM_BUTTON).performClick()
    }
}