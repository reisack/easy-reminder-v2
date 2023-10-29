package rek.remindme.ui.reminder

import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class ReminderListHelperTest {
    @Test
    fun reminderListHelper_formatDescription_noText() = runTest {
        val description = ReminderListHelper.formatDescription("")
        Assert.assertEquals("", description)
    }

    @Test
    fun reminderListHelper_formatDescription_SimpleText() = runTest {
        val text = "No multiline, short text"
        val description = ReminderListHelper.formatDescription(text)
        Assert.assertEquals("No multiline, short text", description)
    }

    @Test
    fun reminderListHelper_formatDescription_MultilineText() = runTest {
        val text ="""t
            |e
            |s
            |t
        """.trimMargin()
        val description = ReminderListHelper.formatDescription(text)
        Assert.assertEquals("t e s t", description)
    }

    @Test
    fun reminderListHelper_formatDescription_MultilineAndLongText() = runTest {
        val text ="""This is a really long text
            |This is a really long text
            |This is a really long text
            |This is a really long text
            |This is a really long text
            |This is a really long text
            |This is a really long text
        """.trimMargin()
        val description = ReminderListHelper.formatDescription(text)
        val expected = "This is a really long text This is a really long text This is a really long text This is a really lo..."
        Assert.assertEquals(expected, description)
    }
}