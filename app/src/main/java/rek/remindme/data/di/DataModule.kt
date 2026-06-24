package rek.remindme.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import rek.remindme.data.DefaultReminderRepository
import rek.remindme.data.ReminderRepository
import rek.remindme.data.local.database.Reminder
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
fun interface DataModule {

    @Singleton
    @Binds
    fun bindsReminderRepository(
        reminderRepository: DefaultReminderRepository
    ): ReminderRepository
}

class FakeReminderRepository @Inject constructor() : ReminderRepository {
    private val _fakeReminders = MutableStateFlow(
        listOf(Reminder(1, "notified reminder", "", 1699204829000, true))
    )
    private var counterId = 1

    override val reminders: Flow<List<Reminder>> = _fakeReminders.asStateFlow()

    override suspend fun upsert(
        id: Int?,
        title: String,
        description: String,
        unixTimestamp: Long,
        notified: Boolean
    ) {
        if (id == null) {
            val reminder = Reminder(++counterId, title, description, unixTimestamp, notified)
            _fakeReminders.update { it + reminder }
        }
        else {
            val reminder = Reminder(id, title, description, unixTimestamp, notified)
            _fakeReminders.update { reminders ->
                reminders.filterNot { it.uid == id } + reminder
            }
        }
    }

    override suspend fun getById(id: Int): Reminder? {
        return _fakeReminders.value.find { it.uid == id }
    }

    override suspend fun deleteById(id: Int) {
        _fakeReminders.update { reminders -> reminders.filterNot { it.uid == id } }
    }

    override suspend fun deleteNotified() {
        _fakeReminders.update { reminders -> reminders.filterNot { it.notified } }
    }

    override suspend fun canDeleteNotified(): Boolean {
        return _fakeReminders.value.any { it.notified }
    }

    override suspend fun getClosestReminderToNotify(): Reminder? {
        val notNotifiedReminders = _fakeReminders.value.filter { !it.notified }
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
