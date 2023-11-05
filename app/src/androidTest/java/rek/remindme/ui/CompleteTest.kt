package rek.remindme.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import rek.remindme.common.Consts
import java.util.Locale

@HiltAndroidTest
class CompleteTest {

    private lateinit var _lang: String
    private lateinit var _testHelper: TestHelper

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        _lang = Locale.getDefault().language
        _testHelper = TestHelper(composeTestRule, _lang)
    }

    @Test
    fun e2e() {
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
        composeTestRule.onNodeWithTag(Consts.TestTag.EMPTY_REMINDER_LIST_MESSAGE)
            .assertExists("${Consts.TestTag.EMPTY_REMINDER_LIST_MESSAGE} should exists")
    }

    private fun checkNotifiedReminderToDelete() {
        // Click on the more vert button
        composeTestRule.onNodeWithTag(Consts.TestTag.MORE_VERT_BUTTON)
            .assertExists("${Consts.TestTag.MORE_VERT_BUTTON} should exists")
            .performClick()

        // Click on delete notified reminders menu item
        composeTestRule.onNodeWithTag(Consts.TestTag.CLEAR_NOTIFIED_REMINDERS_ITEM)
            .assertExists("${Consts.TestTag.CLEAR_NOTIFIED_REMINDERS_ITEM} should exists")
            .performClick()

        composeTestRule.onNodeWithTag(Consts.TestTag.CONFIRM_BUTTON)
            .assertExists("${Consts.TestTag.CONFIRM_BUTTON} should exists")
            .performClick()
    }

    private fun checkNoNotifiedReminderToDelete() {
        // Click on the more vert button
        composeTestRule.onNodeWithTag(Consts.TestTag.MORE_VERT_BUTTON)
            .assertExists("${Consts.TestTag.MORE_VERT_BUTTON} should exists")
            .performClick()

        // Click on delete notified reminders menu item
        composeTestRule.onNodeWithTag(Consts.TestTag.CLEAR_NOTIFIED_REMINDERS_ITEM)
            .assertExists("${Consts.TestTag.CLEAR_NOTIFIED_REMINDERS_ITEM} should exists")
            .performClick()

        // A snackbar message should appears : no notified reminder to delete
        _testHelper.assertExactSnackbarMessage(
            "There is no notified reminders",
            "Il n'y a pas d'anciens rappels à supprimer"
        )
    }

    private fun checkCreateReminderWithValidInformation() {
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
        _testHelper.performDateInput()

        // Enter a time
        _testHelper.performTimeInput(10)

        saveReminder("Title test 1", false)
    }

    private fun checkReminderAppearsInList() {
        // Created reminder exists on Reminder list
        composeTestRule.onNodeWithText("Title test 1", substring = false)
            .assertExists("Title test 1 should exists")

        composeTestRule.onNodeWithText("This is my description for my first reminder", substring = false)
            .assertExists("This is my description for my first reminder should exists")
    }

    private fun checkUpdateTheReminderWithValidInformation() {
        // Click on the reminder to update it
        composeTestRule.onNodeWithText("Title test 1", substring = false)
            .assertExists("Title test 1 should exists")
            .performClick()

        // Title is filled
        composeTestRule.onNodeWithText("Title test 1", substring = false)
            .assertExists("Title test 1 should exists")
            .performTextInput("Updated 1 : ")

        // Description is filled
        composeTestRule.onNodeWithText("This is my description\nfor my first reminder", substring = false)
            .assertExists("This is my description for my first reminder should exists")
            .performTextInput("Updated 2 : ")

        saveReminder("Updated 1 : Title test 1", true)
    }

    private fun checkReminderUpdatedInList() {
        // Created reminder exists on Reminder list
        composeTestRule.onNodeWithText("Updated 1 : Title test 1", substring = false)
            .assertExists("Updated 1 : Title test 1 should exists")

        composeTestRule.onNodeWithText("Updated 2 : This is my description for my first reminder", substring = false)
            .assertExists("Updated 2 : This is my description for my first reminder should exists")
    }

    @OptIn(ExperimentalTestApi::class)
    private fun saveReminder(title: String, isUpdate: Boolean) {
        // Save the reminder
        composeTestRule.onNodeWithTag(Consts.TestTag.SAVE_BUTTON)
            .assertExists("${Consts.TestTag.SAVE_BUTTON} should exists")
            .performClick()

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
        composeTestRule.onNodeWithText("Updated 1 : Title test 1", substring = false)
            .assertExists("Updated 1 : Title test 1 should exists")
            .performClick()

        composeTestRule.onNodeWithTag(Consts.TestTag.DELETE_REMINDER_BUTTON)
            .assertExists("${Consts.TestTag.DELETE_REMINDER_BUTTON} should exists")
            .performClick()

        composeTestRule.onNodeWithTag(Consts.TestTag.CONFIRM_BUTTON)
            .assertExists("${Consts.TestTag.CONFIRM_BUTTON} should exists")
            .performClick()
    }
}