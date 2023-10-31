package rek.remindme.common

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import rek.remindme.R
import java.util.Date

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p0 != null) {
            val notificationManager = p0.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            if (notificationManager != null) {
                ReminderScheduler.createNotificationChannel(notificationManager)

                val notification = NotificationCompat.Builder(p0, Consts.APP_ID)
                    .setContentText("This is a test")
                    .setContentTitle("Hello !")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .build()

                notificationManager.notify(1, notification)
            }
            ReminderScheduler.setNextReminder(p0)
        }
    }
}

class ReminderScheduler {
    companion object {
        fun createNotificationChannel(notificationManager: NotificationManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (notificationManager.getNotificationChannel(Consts.APP_ID) == null) {
                    val channel = NotificationChannel(Consts.APP_ID, Consts.APP_ID, NotificationManager.IMPORTANCE_HIGH)
                    notificationManager.createNotificationChannel(channel)
                }
            }
        }

        fun setNextReminder(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

            if (alarmManager != null) {
                val intent = Intent(context, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                // TODO : Look for closest reminder timestamp to come
                val timestamp = Date().time + 10000

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