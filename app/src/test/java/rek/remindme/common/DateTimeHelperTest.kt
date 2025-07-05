package rek.remindme.common

import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

class DateTimeHelperTest {

    init {
        Locale.setDefault(Locale.US)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun dateTimeHelper_getReadableTime_timestampIsNull() = runTest {
        val timestamp: Long? = null
        val result = DateTimeHelper.instance.getReadableTime(timestamp)
        Assert.assertEquals("", result)
    }

    @Test
    fun dateTimeHelper_getReadableTime_timestampIsNotNull() = runTest {
        val timestamp: Long = 1698167443000
        val result = DateTimeHelper.instance.getReadableTime(timestamp)
        Assert.assertEquals("5:10 PM", result)
    }

    @Test
    fun dateTimeHelper_getReadableTime_hourOrMinuteIsNull() = runTest {
        val hour: Int? = null
        val minute = 42

        val result = DateTimeHelper.instance.getReadableTime(hour, minute)
        Assert.assertEquals("", result)
    }

    @Test
    fun dateTimeHelper_getReadableTime_hourAndMinuteAreNotNull() = runTest {
        val hour = 16
        val minute = 5

        val result = DateTimeHelper.instance.getReadableTime(hour, minute)
        Assert.assertEquals("4:05 PM", result)
    }

    @Test
    fun dateTimeHelper_getReadableDate_timestampIsNull() = runTest {
        val timestamp: Long? = null
        val result = DateTimeHelper.instance.getReadableDate(timestamp)
        Assert.assertEquals("", result)
    }

    @Test
    fun dateTimeHelper_getReadableDate_timestampIsNotNull() = runTest {
        val timestamp: Long = 1698167443000
        val result = DateTimeHelper.instance.getReadableDate(timestamp)
        Assert.assertEquals("Tuesday, October 24, 2023", result)
    }

    @Test
    fun dateTimeHelper_getHourFromTimestamp() = runTest {
        val timestamp: Long = 1698167443000
        val result = DateTimeHelper.instance.getHourFromTimestamp(timestamp)
        Assert.assertEquals(17, result)
    }

    @Test
    fun dateTimeHelper_getMinuteFromTimestamp() = runTest {
        val timestamp: Long = 1698167443000
        val result = DateTimeHelper.instance.getMinuteFromTimestamp(timestamp)
        Assert.assertEquals(10, result)
    }

    @Test
    fun dateTimeHelper_getUtcDatetimeInMillis() = runTest {
        val timestampDate: Long = 1698105600000
        val hour = 18
        val minute = 46

        val result = DateTimeHelper.instance.getUtcDatetimeInMillis(timestampDate, hour, minute)
        Assert.assertEquals(1698173160000, result)
    }
}