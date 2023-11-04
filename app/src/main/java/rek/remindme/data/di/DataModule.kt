package rek.remindme.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import rek.remindme.data.ReminderRepository
import rek.remindme.data.DefaultReminderRepository
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
    private val _fakeReminders = listOf<Reminder>()

    override val reminders: Flow<List<Reminder>> = flowOf(_fakeReminders)

    override suspend fun upsert(
        id: Int?,
        title: String,
        description: String,
        unixTimestamp: Long,
        notified: Boolean
    ) {
        throw NotImplementedError()
    }

    override suspend fun getById(id: Int): Reminder? {
        throw NotImplementedError()
    }

    override suspend fun deleteById(id: Int) {
        throw NotImplementedError()
    }

    override suspend fun deleteNotified() {
        throw NotImplementedError()
    }

    override suspend fun canDeleteNotified(): Boolean {
        throw NotImplementedError()
    }

    override suspend fun getClosestReminderToNotify(): Reminder? {
        throw NotImplementedError()
    }

    override suspend fun getRemindersToNotify(): List<Reminder> {
        throw NotImplementedError()
    }

    override suspend fun updateNotifiedById(id: Int) {
        throw NotImplementedError()
    }
}
