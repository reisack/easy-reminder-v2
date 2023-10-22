package rek.remindme.common

import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import kotlin.math.abs

class DateTimeHelper {
    companion object {
        fun getReadableTime(unixTimestampDate: Long?): String {
            if (unixTimestampDate == null) {
                return ""
            }

            val date = Date(unixTimestampDate)
            return DateFormat.getTimeInstance(DateFormat.SHORT).format(date)
        }

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

        fun getRemainingOrPastTime(unixTimestampDate: Long?): String {
            if (unixTimestampDate == null) {
                return ""
            }

            val daysInAYearCount = 365.2425f
            val daysInAMonthCount = 30.436874f

            val currentUtcTime = Date().time
            val span = currentUtcTime - unixTimestampDate
            val isReminderPast = currentUtcTime > unixTimestampDate

            val absDays = abs(span / (1000 * 60 * 60 * 24))
            val absHours = abs(span / (1000 * 60 * 60) % 24)
            val absMinutes = abs(span / (1000 * 60) % 60)
            val absSeconds = abs(span / 1000 % 60)

            var timeNumber: Int
            var timeUnit: String

            if (absDays > 365) {
                timeNumber = (absDays / daysInAYearCount).toInt()
                timeUnit = "TODO year"

                // Nearer to next year
                if (absDays % daysInAYearCount >= 182) {
                    timeNumber++
                }
            }
            else if (absDays > 30) {
                timeNumber = (absDays / daysInAMonthCount).toInt()
                timeUnit = "TODO month"

                // Nearer to next month
                if (absDays % daysInAMonthCount >= 15) {
                    timeNumber++

                    // 12 months => 1 year
                    if (timeNumber >= 12) {
                        timeNumber = 1
                        timeUnit = "TODO year"
                    }
                }
            }
            else if (absDays >= 1) {
                timeNumber = absDays.toInt()
                timeUnit = "TODO day"

                // Nearer to next day
                if (absHours >= 12) {
                    timeNumber++

                    // 30 days => 1 month
                    if (timeNumber > 30) {
                        timeNumber = 1
                        timeUnit = "TODO month"
                    }
                }
            }
            else if (absHours >= 1) {
                timeNumber = absHours.toInt()
                timeUnit = "TODO hour"

                // Nearer to next hour
                if (absMinutes >= 30) {
                    timeNumber++

                    // 24 hours => 1 day
                    if (timeNumber >= 24) {
                        timeNumber = 1
                        timeUnit = "TODO day"
                    }
                }
            }
            else if (absMinutes >= 1) {
                timeNumber = absMinutes.toInt()
                timeUnit = "TODO minute"

                // Nearer to next minute
                if (absSeconds >= 30) {
                    timeNumber++

                    //60 minutes => 1 hour
                    if (timeNumber >= 60) {
                        timeNumber = 1
                        timeUnit = "TODO hour"
                    }
                }
            }
            else {
                timeNumber = absSeconds.toInt()
                timeUnit = "TODO second"
            }

            if (timeNumber > 1 && !timeUnit.endsWith("s")) {
                timeUnit += "s"
            }

            // TODO : Handle i18n
            if (isReminderPast) {
                return "$timeNumber $timeUnit"
            }
            else {
                return "$timeNumber $timeUnit"
            }
        }
    }
}