package rek.remindme.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import rek.remindme.data.local.database.Reminder
import rek.remindme.data.local.database.ReminderDao

class DefaultReminderRepositoryTest {

    @Test
    fun reminderRepository_upsert_ExistingKeepSameId() = runTest {
        val repository = DefaultReminderRepository(FakeReminderDao())

        repository.upsert(3,"title 1", "desc", System.currentTimeMillis(), false)

        val updatedReminder = repository.getById(3)
        assertEquals(3, updatedReminder!!.uid)
    }

    @Test
    fun reminderRepository_upsert_newHasIdZeroOnDao() = runTest {
        val repository = DefaultReminderRepository(FakeReminderDao())

        repository.upsert(null,"title 1", "desc", System.currentTimeMillis(), false)

        val updatedReminder = repository.getById(0)
        assertEquals(0, updatedReminder!!.uid)
    }
}

private class FakeReminderDao : ReminderDao {

    private val _data = mutableListOf<Reminder>()

    override fun getReminders(): Flow<List<Reminder>> = flow {
        throw NotImplementedError()
    }

    override suspend fun getById(id: Int): Reminder? {
        return _data.find { reminder -> reminder.uid == id }
    }

    override suspend fun upsert(item: Reminder) {
        _data.add(item)
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