package rek.remindme.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
        val message = if (_lang == "fr") {
            "Il n'y a pas d'anciens rappels à supprimer"
        }
        else {
            "There is no notified reminders"
        }
        composeTestRule.onNodeWithText(text = message, substring = false).assertExists()


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
        composeTestRule.onNodeWithTag(Consts.TestTag.INPUT_DATE_FIELD)
            .assertExists("${Consts.TestTag.INPUT_DATE_FIELD} should exists")
            .performClick()
        // Finding the input mode for date
        composeTestRule.onNodeWithContentDescription("input", substring = true)
            .performClick()
        // Finally, enter the date
        composeTestRule.onNodeWithContentDescription("Date", substring = true)
            .performTextInput("12122023")
        // Confirm
        composeTestRule.onNodeWithTag(Consts.TestTag.CONFIRM_BUTTON)
            .assertExists("${Consts.TestTag.CONFIRM_BUTTON} should exists")
            .performClick()

        // Enter a time
        composeTestRule.onNodeWithTag(Consts.TestTag.INPUT_TIME_FIELD)
            .assertExists("${Consts.TestTag.INPUT_TIME_FIELD} should exists")
            .performClick()
        // Finding the input mode for time
        composeTestRule.onNodeWithTag(Consts.TestTag.SELECT_INPUT_MODE)
            .performClick()
        // Enter the hour
        composeTestRule.onNodeWithContentDescription("hour", substring = true)
            .performTextInput("10")
        // Enter the minute
        composeTestRule.onNodeWithContentDescription("hour", substring = true)
            .performTextInput("36")
        composeTestRule.onNodeWithTag(Consts.TestTag.CONFIRM_BUTTON)
            .assertExists("${Consts.TestTag.CONFIRM_BUTTON} should exists")
            .performClick()

        // Save the reminder
        composeTestRule.onNodeWithTag(Consts.TestTag.SAVE_BUTTON)
            .assertExists("${Consts.TestTag.SAVE_BUTTON} should exists")
            .performClick()

        // Redirect to reminder list
        composeTestRule.waitUntilExactlyOneExists(hasTextExactly("Title test 1"))

        composeTestRule.onNodeWithText("Title test 1", substring = true)
            .assertExists("Title test 1 should exists")

        composeTestRule.onNodeWithText("This is my description for my first reminder")
            .assertExists("This is my description for my first reminder should exists")
    }
}