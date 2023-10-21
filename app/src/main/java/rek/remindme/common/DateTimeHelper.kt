package rek.remindme.common

import java.text.DateFormat
import java.util.Calendar
import java.util.Date

class DateTimeHelper {
    companion object {
        fun getReadableTime(hour: Int?, minute: Int?): String {
            if (hour == null || minute == null) {
                return ""
            }

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            return DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)
        }

        fun getReadableDate(unixTimestampDate: Long?): String {
            if (unixTimestampDate == null) {
                return ""
            }

            val date = Date(unixTimestampDate)
            return DateFormat.getDateInstance(DateFormat.FULL).format(date)
        }

        fun getUtcDatetimeInMillis(unixTimestampDate: Long, hour: Int, minute: Int): Long {
            val calendar = Calendar.getInstance()
            calendar.time = Date(unixTimestampDate)
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            return calendar.timeInMillis
        }
    }
}