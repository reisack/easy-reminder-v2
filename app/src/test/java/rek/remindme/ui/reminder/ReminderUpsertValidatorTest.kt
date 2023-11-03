package rek.remindme.ui.reminder

import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import rek.remindme.common.Consts
import java.util.TimeZone

class TestableReminderUpsertValidator : ReminderUpsertValidator() {

    override fun getCurrentUnixTimestamp(): Long {
        // 27 October 2023 11:00:00 UTC
        return 1698404400000
    }
}

class ReminderUpsertValidatorTest {

    private val _validator: TestableReminderUpsertValidator = TestableReminderUpsertValidator()

    init {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun reminderUpsertValidator_validate_titleIsBlank() = runTest {
        val title = ""
        val description = ""
        val date = 1698364800000 // 27 October 2023 00:00:00 UTC
        val hour = 14
        val minute = 36

        val messageRes = _validator.validate(title, description, date, hour, minute)

        Assert.assertEquals(Consts.Validation.mandatoryFieldsNotFilled, messageRes)
    }

    @Test
    fun reminderUpsertValidator_validate_dateIsNull() = runTest {
        val title = "My reminder"
        val description = ""
        val date: Long? = null
        val hour = 14
        val minute = 36

        val messageRes = _validator.validate(title, description, date, hour, minute)

        Assert.assertEquals(Consts.Validation.mandatoryFieldsNotFilled, messageRes)
    }

    @Test
    fun reminderUpsertValidator_validate_timeIsNull() = runTest {
        val title = "My reminder"
        val description = ""
        val date = 1698364800000 // 27 October 2023 00:00:00 UTC
        val hour: Int? = null
        val minute: Int? = null

        val messageRes = _validator.validate(title, description, date, hour, minute)

        Assert.assertEquals(Consts.Validation.mandatoryFieldsNotFilled, messageRes)
    }

    @Test
    fun reminderUpsertValidator_validate_dateIsPast() = runTest {
        val title = "My reminder"
        val description = ""
        val date = 1698364800000 // 27 October 2023 00:00:00 UTC
        val hour = 10
        val minute = 59

        val messageRes = _validator.validate(title, description, date, hour, minute)

        Assert.assertEquals(Consts.Validation.setInPast, messageRes)
    }

    @Test
    fun reminderUpsertValidator_validate_dateIsRightNow() = runTest {
        val title = "My reminder"
        val description = ""
        val date = 1698364800000 // 27 October 2023 00:00:00 UTC
        val hour = 11
        val minute = 0

        val messageRes = _validator.validate(title, description, date, hour, minute)

        Assert.assertEquals(Consts.Validation.setInPast, messageRes)
    }

    @Test
    fun reminderUpsertValidator_validate_ok() = runTest {
        val title = "My reminder"
        val description = ""
        val date = 1698364800000 // 27 October 2023 00:00:00 UTC
        val hour = 11
        val minute = 1

        val messageRes = _validator.validate(title, description, date, hour, minute)

        // No error
        Assert.assertEquals(0, messageRes)
    }

    @Test
    fun reminderUpsertValidator_validate_titleIsNotTooLong() = runTest {
        val title = "Lorem ipsum dolor sit amet, consectetur vestibulum" // 50 characters
        val description = ""
        val date = 1698364800000 // 27 October 2023 00:00:00 UTC
        val hour = 11
        val minute = 1

        val messageRes = _validator.validate(title, description, date, hour, minute)

        // No error
        Assert.assertEquals(0, messageRes)
    }

    @Test
    fun reminderUpsertValidator_validate_titleIsTooLong() = runTest {
        val title = "Lorem ipsum dolor sit amet, consectetur vestibulum." // 51 characters
        val description = ""
        val date = 1698364800000 // 27 October 2023 00:00:00 UTC
        val hour = 11
        val minute = 1

        val messageRes = _validator.validate(title, description, date, hour, minute)

        // No error
        Assert.assertEquals(Consts.Validation.titleMaxCharactersReached, messageRes)
    }

    @Test
    fun reminderUpsertValidator_validate_descIsNotTooLong() = runTest {
        val title = "my reminder"
        val description = """Lorem ipsum dolor sit amet.
            Sed ultricies eu orci vitae pharetra.
            Ut vel arcu euismod, interdum mauris ut, ultrices nulla.
            Morbi massa diam, molestie at semper nec.""" // 200 characters
        val date = 1698364800000 // 27 October 2023 00:00:00 UTC
        val hour = 11
        val minute = 1

        val messageRes = _validator.validate(title, description, date, hour, minute)

        // No error
        Assert.assertEquals(0, messageRes)
    }

    @Test
    fun reminderUpsertValidator_validate_descIsTooLong() = runTest {
        val title = "my reminder"
        val description = """Lorem ipsum dolor sit amet.
            Sed ultricies eu orci vitae pharetra.
            Ut vel arcu euismod, interdum mauris ut, ultrices nulla.
            Morbi massa diam, molestie at semper nec .""" // 201 characters
        val date = 1698364800000 // 27 October 2023 00:00:00 UTC
        val hour = 11
        val minute = 1

        val messageRes = _validator.validate(title, description, date, hour, minute)

        // No error
        Assert.assertEquals(Consts.Validation.descMaxCharactersReached, messageRes)
    }
}