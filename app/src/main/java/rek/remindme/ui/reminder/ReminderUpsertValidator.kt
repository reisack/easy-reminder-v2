package rek.remindme.ui.reminder

import androidx.annotation.StringRes
import rek.remindme.common.Consts
import rek.remindme.common.DateTimeHelper
import java.util.Date

open class ReminderUpsertValidator {

    @StringRes
    fun validate(
        title: String,
        unixTimestampDate: Long?,
        hour: Int?,
        minute: Int?
    ): Int {
        if (hasEmptyMandatoryFields(title, unixTimestampDate, hour, minute)) {
            return Consts.Validation.mandatoryFieldsNotFilled
        }

        if (reminderDatetimeBelowNow(unixTimestampDate!!, hour!!, minute!!)) {
            return Consts.Validation.setInPast
        }

        // Code 0 : no error
        return 0
    }

    private fun hasEmptyMandatoryFields(
        title: String,
        unixTimestampDate: Long?,
        hour: Int?,
        minute: Int?
    ): Boolean {
        return title.isBlank() || unixTimestampDate == null || hour == null || minute == null
    }

    private fun reminderDatetimeBelowNow(unixTimestampDate: Long, hour: Int, minute: Int): Boolean {
        val reminderDateTimeInMillis = DateTimeHelper.instance.getUtcDatetimeInMillis(
            unixTimestampDate, hour, minute
        )

        return reminderDateTimeInMillis <= getCurrentUnixTimestamp()
    }

    protected open fun getCurrentUnixTimestamp(): Long {
        return Date().time
    }
}