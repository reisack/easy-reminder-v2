package rek.remindme.common

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import rek.remindme.R
import rek.remindme.data.ReminderRepository
import rek.remindme.ui.MainActivity
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@AndroidEntryPoint
class AlarmReceiver: BroadcastReceiver() {

    @Inject
    lateinit var repository: ReminderRepository

    override fun onReceive(p0: Context?, p1: Intent?) = goAsync {
        val reminder = repository.getClosestReminderToNotify()

        if (reminder != null && p0 != null) {
            val notificationManager = p0.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            if (notificationManager != null) {
                createNotificationChannel(notificationManager)

                val intent = Intent(p0, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(p0, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                val notification = NotificationCompat.Builder(p0, Consts.APP_ID)
                    .setContentTitle(reminder.title)
                    .setContentText(reminder.description)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setTicker("${reminder.title} ${reminder.description}")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .build()

                notificationManager.notify(reminder.uid, notification)
                repository.updateNotifiedById(reminder.uid)
            }
        }

        val nextReminder = repository.getClosestReminderToNotify()
        if (nextReminder != null && p0 != null) {
            ReminderScheduler.setNextReminder(p0, nextReminder.unixTimestamp)
        }
    }

    // https://stackoverflow.com/questions/74111692/run-coroutine-functions-on-broadcast-receiver
    private fun BroadcastReceiver.goAsync(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ) {
        val pendingResult = goAsync()
        @OptIn(DelicateCoroutinesApi::class) // Must run globally; there's no teardown callback.
        GlobalScope.launch(context) {
            try {
                block()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(Consts.APP_ID) == null) {
                val channel = NotificationChannel(Consts.APP_ID, Consts.APP_ID, NotificationManager.IMPORTANCE_HIGH)
                channel.enableLights(true)
                channel.enableVibration(true)
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}

class ReminderScheduler {
    companion object {
        fun setNextReminder(context: Context, timestamp: Long) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

            if (alarmManager != null) {
                val intent = Intent(context, AlarmReceiver::class.java)
                intent.action = "rek.remindme.alarm-receiver"

                val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent)
                    }
                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent)
                }
                else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent)
                }
            }
        }
    }
}