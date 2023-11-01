package rek.remindme.common

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
        if (p0 != null && p1 != null) {
            val validActions = listOf(Intent.ACTION_BOOT_COMPLETED, Consts.System.ALARM_RECEIVER_ID)

            if (validActions.contains(p1.action)) {
                val reminders = repository.getRemindersToNotify()
                if (reminders.any()) {
                    reminders.forEach { reminder ->
                        val notificationManager = p0.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                        if (notificationManager != null) {
                            createNotificationChannel(notificationManager)

                            val notification = NotificationCompat.Builder(p0, Consts.System.APP_ID)
                                .setContentTitle(reminder.title)
                                .setContentText(reminder.description)
                                .setContentIntent(getPendingIntent(p0))
                                .setAutoCancel(true)
                                .setTicker("${reminder.title} ${reminder.description}")
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .build()

                            notificationManager.notify(reminder.uid, notification)
                            repository.updateNotifiedById(reminder.uid)
                        }
                    }
                }

                setNextReminder(p0, repository)
            }
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
            if (notificationManager.getNotificationChannel(Consts.System.APP_ID) == null) {
                val channel = NotificationChannel(Consts.System.APP_ID, Consts.System.APP_ID, NotificationManager.IMPORTANCE_HIGH)
                channel.enableLights(true)
                channel.enableVibration(true)
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private suspend fun setNextReminder(context: Context?, repository: ReminderRepository) {
        if (context != null) {
            val nextReminder = repository.getClosestReminderToNotify()
            if (nextReminder != null) {
                ReminderScheduler.setNextReminder(context, nextReminder.unixTimestamp)
            }
        }
    }
}