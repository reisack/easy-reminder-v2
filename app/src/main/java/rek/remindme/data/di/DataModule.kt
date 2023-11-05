package rek.remindme.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import rek.remindme.data.DefaultReminderRepository
import rek.remindme.data.ReminderRepository
import rek.remindme.data.local.database.Reminder
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsReminderRepository(
        reminderRepository: DefaultReminderRepository
    ): ReminderRepository
}

class FakeReminderRepository @Inject constructor() : ReminderRepository {
    private val _fakeReminders = mutableListOf<Reminder>()
    private var counterId = 0

    override var reminders: Flow<List<Reminder>> = flow {
        while (true) {
            val fakes = getFakeReminders()
            emit(fakes)
            delay(200)
        }
    }

    private suspend fun getFakeReminders(): List<Reminder> {
        val newList = mutableListOf<Reminder>()
        _fakeReminders.forEach { newList.add(it) }
        return newList.toList()
    }

    override suspend fun upsert(
        id: Int?,
        title: String,
        description: String,
        unixTimestamp: Long,
        notified: Boolean
    ) {
        if (id == null) {
            val reminder = Reminder(++counterId, title, description, unixTimestamp, notified)
            _fakeReminders.add(reminder)
        }
        else {
            val reminder = Reminder(id, title, description, unixTimestamp, notified)
            _fakeReminders.removeAll { it.uid == id }
            _fakeReminders.add(reminder)
        }
    }

    override suspend fun getById(id: Int): Reminder? {
        throw NotImplementedError()
    }

    override suspend fun deleteById(id: Int) {
        throw NotImplementedError()
    }

    override suspend fun deleteNotified() {
        _fakeReminders.removeAll { reminder -> reminder.notified }
    }

    override suspend fun canDeleteNotified(): Boolean {
        return _fakeReminders.any { reminder -> reminder.notified }
    }

    override suspend fun getClosestReminderToNotify(): Reminder? {
        val notNotifiedReminders = _fakeReminders.filter { !it.notified }
        return if (notNotifiedReminders.any()) {
            notNotifiedReminders.minBy { it.unixTimestamp }
        }
        else null
    }

    override suspend fun getRemindersToNotify(): List<Reminder> {
        throw NotImplementedError()
    }

    override suspend fun updateNotifiedById(id: Int) {
        throw NotImplementedError()
    }
}
