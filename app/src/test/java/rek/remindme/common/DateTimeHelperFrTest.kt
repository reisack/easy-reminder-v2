package rek.remindme.common

import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

class DateTimeHelperFrTest {

    init {
        Locale.setDefault(Locale.FRANCE)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun dateTimeHelper_getReadableTime_timestampIsNotNull() = runTest {
        val timestamp: Long = 1698167443000
        val result = DateTimeHelper.getReadableTime(timestamp)
        Assert.assertEquals("17:10", result)
    }

    @Test
    fun dateTimeHelper_getReadableTime_hourAndMinuteAreNotNull() = runTest {
        val hour = 16
        val minute = 5

        val result = DateTimeHelper.getReadableTime(hour, minute)
        Assert.assertEquals("16:05", result)
    }

    @Test
    fun dateTimeHelper_getReadableDate_timestampIsNotNull() = runTest {
        val timestamp: Long = 1698167443000
        val result = DateTimeHelper.getReadableDate(timestamp)
        Assert.assertEquals("Mardi 24 octobre 2023", result)
    }

    @Test
    fun dateTimeHelper_getHourFromTimestamp() = runTest {
        val timestamp: Long = 1698167443000
        val result = DateTimeHelper.getHourFromTimestamp(timestamp)
        Assert.assertEquals(17, result)
    }

    @Test
    fun dateTimeHelper_getMinuteFromTimestamp() = runTest {
        val timestamp: Long = 1698167443000
        val result = DateTimeHelper.getMinuteFromTimestamp(timestamp)
        Assert.assertEquals(10, result)
    }

    @Test
    fun dateTimeHelper_getUtcDatetimeInMillis() = runTest {
        val timestampDate: Long = 1698105600000
        val hour = 18
        val minute = 46

        val result = DateTimeHelper.getUtcDatetimeInMillis(timestampDate, hour, minute)
        Assert.assertEquals(1698173160000, result)
    }
}