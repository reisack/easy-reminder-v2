package rek.remindme.common

import java.text.DateFormat
import java.util.Calendar
import java.util.Date

class DisplayHelper {
    companion object {
        fun displayHour(hour: Int?, minute: Int?): String {
            if (hour == null || minute == null) {
                return ""
            }

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            return DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)
        }

        fun displayDate(unixTimestampDate: Long?): String {
            if (unixTimestampDate == null) {
                return ""
            }

            val date = Date(unixTimestampDate)
            return DateFormat.getDateInstance(DateFormat.FULL).format(date)
        }
    }
}