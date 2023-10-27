package rek.remindme.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import rek.remindme.R
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

open class DateTimeHelper {

    companion object {

        @Volatile
        private var instance: DateTimeHelper? = null

        fun getInstance(): DateTimeHelper {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = DateTimeHelper()
                    }
                }
            }
            return instance!!
        }
    }

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
        val formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date)

        val formattedDateCapitalized = formattedDate.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        return formattedDateCapitalized
    }

    fun getHourFromTimestamp(unixTimestamp: Long): Int {
        val calendar = getCalendarWithTimestamp(unixTimestamp)
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    fun getMinuteFromTimestamp(unixTimestamp: Long): Int {
        val calendar = getCalendarWithTimestamp(unixTimestamp)
        return calendar.get(Calendar.MINUTE)
    }

    fun getUtcDatetimeInMillis(unixTimestampDate: Long, hour: Int, minute: Int): Long {
        val calendar = getCalendarWithTimestamp(unixTimestampDate)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }

    private fun getCalendarWithTimestamp(unixTimestamp: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = Date(unixTimestamp)
        return calendar
    }

    @Composable
    fun getRemainingOrPastTime(unixTimestamp: Long?): String {
        if (unixTimestamp == null) {
            return ""
        }

        val currentUtcTime = getCurrentUnixTimestamp()
        val span = currentUtcTime - unixTimestamp
        val isReminderPast = currentUtcTime > unixTimestamp

        val (timeNumber: Int, timeUnit: String) = calculateRemainingOrPastTime(span)

        return if (isReminderPast) {
            stringResource(R.string.time_past, timeNumber, timeUnit)
        } else {
            stringResource(R.string.time_future, timeNumber, timeUnit)
        }
    }

    @Composable
    private fun calculateRemainingOrPastTime(span: Long): Pair<Int, String> {
        val daysInAYearCount = 365.2425f
        val daysInAMonthCount = 30.436874f

        val absDays = abs(span / (1000 * 60 * 60 * 24))
        val absHours = abs(span / (1000 * 60 * 60) % 24)
        val absMinutes = abs(span / (1000 * 60) % 60)
        val absSeconds = abs(span / 1000 % 60)

        var timeNumber: Int
        var timeUnit: String

        if (absDays > 365) {
            timeNumber = (absDays / daysInAYearCount).toInt()
            timeUnit = stringResource(R.string.year)

            // Nearer to next year
            if (absDays % daysInAYearCount >= 182) {
                timeNumber++
            }
        } else if (absDays > 30) {
            timeNumber = (absDays / daysInAMonthCount).toInt()
            timeUnit = stringResource(R.string.month)

            // Nearer to next month
            if (absDays % daysInAMonthCount >= 15) {
                timeNumber++

                // 12 months => 1 year
                if (timeNumber >= 12) {
                    timeNumber = 1
                    timeUnit = stringResource(R.string.year)
                }
            }
        } else if (absDays >= 1) {
            timeNumber = absDays.toInt()
            timeUnit = stringResource(R.string.day)

            // Nearer to next day
            if (absHours >= 12) {
                timeNumber++

                // 30 days => 1 month
                if (timeNumber > 30) {
                    timeNumber = 1
                    timeUnit = stringResource(R.string.month)
                }
            }
        } else if (absHours >= 1) {
            timeNumber = absHours.toInt()
            timeUnit = stringResource(R.string.hour)

            // Nearer to next hour
            if (absMinutes >= 30) {
                timeNumber++

                // 24 hours => 1 day
                if (timeNumber >= 24) {
                    timeNumber = 1
                    timeUnit = stringResource(R.string.day)
                }
            }
        } else if (absMinutes >= 1) {
            timeNumber = absMinutes.toInt()
            timeUnit = stringResource(R.string.minute)

            // Nearer to next minute
            if (absSeconds >= 30) {
                timeNumber++

                //60 minutes => 1 hour
                if (timeNumber >= 60) {
                    timeNumber = 1
                    timeUnit = stringResource(R.string.hour)
                }
            }
        } else {
            timeNumber = absSeconds.toInt()
            timeUnit = stringResource(R.string.second)
        }

        timeUnit = getPluralIfNeeded(timeNumber, timeUnit)

        return Pair(timeNumber, timeUnit)
    }

    private fun getPluralIfNeeded(timeNumber: Int, timeUnit: String): String {
        if (timeNumber > 1 && !timeUnit.endsWith("s")) {
            return timeUnit + "s"
        }

        return timeUnit
    }

    protected open fun getCurrentUnixTimestamp(): Long {
        return Date().time
    }
}