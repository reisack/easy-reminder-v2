package rek.remindme.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import rek.remindme.R
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

open class DateTimeHelper protected constructor() {

    companion object {

        @Volatile
        private var _instance: DateTimeHelper? = null

        val instance: DateTimeHelper
            get() = getInternalInstance()

        private fun getInternalInstance(): DateTimeHelper {
            if (_instance == null) {
                synchronized(this) {
                    if (_instance == null) {
                        _instance = DateTimeHelper()
                    }
                }
            }
            return _instance!!
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

        // Compose time logic as a flat sequence
        val (timeNumber, timeUnit) = when {
            absDays > 365 -> {
                var years = (absDays / daysInAYearCount).toInt()
                if (absDays % daysInAYearCount >= 182) years++
                years to stringResource(R.string.year)
            }
            absDays > 30 -> {
                var months = (absDays / daysInAMonthCount).toInt()
                if (absDays % daysInAMonthCount >= 15) months++
                if (months >= 12) 1 to stringResource(R.string.year)
                else months to stringResource(R.string.month)
            }
            absDays >= 1 -> {
                var days = absDays.toInt()
                if (absHours >= 12) days++
                if (days > 30) 1 to stringResource(R.string.month)
                else days to stringResource(R.string.day)
            }
            absHours >= 1 -> {
                var hours = absHours.toInt()
                if (absMinutes >= 30) hours++
                if (hours >= 24) 1 to stringResource(R.string.day)
                else hours to stringResource(R.string.hour)
            }
            absMinutes >= 1 -> {
                var minutes = absMinutes.toInt()
                if (absSeconds >= 30) minutes++
                if (minutes >= 60) 1 to stringResource(R.string.hour)
                else minutes to stringResource(R.string.minute)
            }
            else -> absSeconds.toInt() to stringResource(R.string.second)
        }

        return timeNumber to formatUnit(timeNumber, timeUnit)
    }

    private fun formatUnit(timeNumber: Int, timeUnit: String): String {
        if (timeNumber > 1 && !timeUnit.endsWith("s")) {
            return "${timeUnit}s"
        }

        return timeUnit
    }

    protected open fun getCurrentUnixTimestamp(): Long {
        return Date().time
    }
}