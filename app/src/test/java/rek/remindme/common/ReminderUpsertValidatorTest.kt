package rek.remindme.common

import org.junit.Assert
import org.junit.Test
import rek.remindme.ui.reminder.ReminderUpsertValidator
import java.util.TimeZone

class TestableReminderUpsertValidator : ReminderUpsertValidator() {

    override fun getCurrentUnixTimestamp(): Long {
        // 27 October 2023 11:00:00 UTC
        return 1698404400000
    }
}

class ReminderUpsertValidatorTest {

    private val validator: TestableReminderUpsertValidator = TestableReminderUpsertValidator()

    init {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun reminderUpsertValidator_validate_titleIsBlank() {
        val title = ""
        val date = 1698364800000 // 27 October 2023 00:00:00 UTC
        val hour = 14
        val minute = 36

        val messageRes = validator.validate(title, date, hour, minute)

        Assert.assertEquals(Consts.Validation.mandatoryFieldsNotFilled, messageRes)
    }

    @Test
    fun reminderUpsertValidator_validate_dateIsNull() {
        val title = "My reminder"
        val date: Long? = null
        val hour = 14
        val minute = 36

        val messageRes = validator.validate(title, date, hour, minute)

        Assert.assertEquals(Consts.Validation.mandatoryFieldsNotFilled, messageRes)
    }

    @Test
    fun reminderUpsertValidator_validate_timeIsNull() {
        val title = "My reminder"
        val date = 1698364800000 // 27 October 2023 00:00:00 UTC
        val hour: Int? = null
        val minute: Int? = null

        val messageRes = validator.validate(title, date, hour, minute)

        Assert.assertEquals(Consts.Validation.mandatoryFieldsNotFilled, messageRes)
    }

    @Test
    fun reminderUpsertValidator_validate_DateIsPast() {
        val title = "My reminder"
        val date = 1698364800000 // 27 October 2023 00:00:00 UTC
        val hour = 10
        val minute = 59

        val messageRes = validator.validate(title, date, hour, minute)

        Assert.assertEquals(Consts.Validation.setInPast, messageRes)
    }

    @Test
    fun reminderUpsertValidator_validate_DateIsRightNow() {
        val title = "My reminder"
        val date = 1698364800000 // 27 October 2023 00:00:00 UTC
        val hour = 11
        val minute = 0

        val messageRes = validator.validate(title, date, hour, minute)

        Assert.assertEquals(Consts.Validation.setInPast, messageRes)
    }

    @Test
    fun reminderUpsertValidator_validate_Ok() {
        val title = "My reminder"
        val date = 1698364800000 // 27 October 2023 00:00:00 UTC
        val hour = 11
        val minute = 1

        val messageRes = validator.validate(title, date, hour, minute)

        // No error
        Assert.assertEquals(0, messageRes)
    }
}