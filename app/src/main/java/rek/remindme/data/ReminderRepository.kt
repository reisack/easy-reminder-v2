package rek.remindme.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import rek.remindme.data.local.database.Reminder
import rek.remindme.data.local.database.ReminderDao
import javax.inject.Inject

interface ReminderRepository {
    val reminders: Flow<List<Reminder>>

    suspend fun upsert(id: Int?, title: String, description: String, unixTimestamp: Long, notified: Boolean)
    suspend fun getById(id: Int): Reminder?
    suspend fun deleteById(id: Int)
    suspend fun deleteNotified()
    suspend fun canDeleteNotified(): Boolean
    suspend fun getClosestReminderToNotify(): Reminder?
    suspend fun updateNotifiedById(id: Int)
}

class DefaultReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao
) : ReminderRepository {

    override val reminders: Flow<List<Reminder>> =
        reminderDao.getReminders().map {
            items -> items.map {
                Reminder(
                    uid = it.uid,
                    title = it.title,
                    description = it.description,
                    unixTimestamp = it.unixTimestamp,
                    notified = it.notified
                )
            }
        }

    override suspend fun upsert(
        id: Int?,
        title: String,
        description: String,
        unixTimestamp: Long,
        notified: Boolean
    ) {
        reminderDao.upsert(Reminder(
            uid = id ?: 0,
            title = title,
            description = description,
            unixTimestamp = unixTimestamp,
            notified = notified
        ))
    }

    override suspend fun getById(id: Int): Reminder? {
        return reminderDao.getById(id)
    }

    override suspend fun deleteById(id: Int) {
        reminderDao.deleteById(id)
    }

    override suspend fun deleteNotified() {
        reminderDao.deleteNotified()
    }

    override suspend fun canDeleteNotified(): Boolean {
        return reminderDao.canDeleteNotified()
    }

    override suspend fun getClosestReminderToNotify(): Reminder? {
        return reminderDao.getClosestReminderToNotify()
    }

    override suspend fun updateNotifiedById(id: Int) {
        reminderDao.updateNotifiedById(id)
    }
}
