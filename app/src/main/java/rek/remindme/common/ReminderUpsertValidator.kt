package rek.remindme.common

import androidx.annotation.StringRes
import rek.remindme.R
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
            return R.string.reminder_mandatory_fields_not_filled
        }

        if (reminderDatetimeBelowNow(unixTimestampDate!!, hour!!, minute!!)) {
            return R.string.reminder_set_in_past
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
        val reminderDateTimeInMillis = DateTimeHelper.getUtcDatetimeInMillis(
            unixTimestampDate, hour, minute
        )

        return reminderDateTimeInMillis <= getCurrentUnixTimestamp()
    }

    protected open fun getCurrentUnixTimestamp(): Long {
        return Date().time
    }
}