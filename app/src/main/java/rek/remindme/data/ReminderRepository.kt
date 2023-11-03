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
    suspend fun getRemindersToNotify(): List<Reminder>
    suspend fun updateNotifiedById(id: Int)
}

class DefaultReminderRepository @Inject constructor(
    private val _reminderDao: ReminderDao
) : ReminderRepository {

    override val reminders: Flow<List<Reminder>> =
        _reminderDao.getReminders().map {
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
        _reminderDao.upsert(Reminder(
            uid = id ?: 0,
            title = title,
            description = description,
            unixTimestamp = unixTimestamp,
            notified = notified
        ))
    }

    override suspend fun getById(id: Int): Reminder? {
        return _reminderDao.getById(id)
    }

    override suspend fun deleteById(id: Int) {
        _reminderDao.deleteById(id)
    }

    override suspend fun deleteNotified() {
        _reminderDao.deleteNotified()
    }

    override suspend fun canDeleteNotified(): Boolean {
        return _reminderDao.canDeleteNotified()
    }

    override suspend fun getClosestReminderToNotify(): Reminder? {
        return _reminderDao.getClosestReminderToNotify()
    }

    override suspend fun getRemindersToNotify(): List<Reminder> {
        return _reminderDao.getRemindersToNotify()
    }

    override suspend fun updateNotifiedById(id: Int) {
        _reminderDao.updateNotifiedById(id)
    }
}
