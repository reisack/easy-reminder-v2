package rek.remindme.ui.reminder

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import rek.remindme.data.ReminderRepository
import rek.remindme.data.local.database.Reminder

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ReminderListViewModelTest {
    @Test
    fun uiState_initiallyLoading() = runTest {
        val viewModel = ReminderListViewModel(FakeReminderRepository())
        assertEquals(viewModel.uiState.first(), ReminderUiState.Loading)
    }
}

private class FakeReminderRepository : ReminderRepository {

    private val _data = mutableListOf<Reminder>()
    private var _index: Int = 0

    override val reminders: Flow<List<Reminder>>
        get() = flow { emit(_data.toList()) }

    override suspend fun upsert(
        id: Int?,
        title: String,
        description: String,
        unixTimestamp: Long,
        notified: Boolean
    ) {
        val reminder = _data.find { reminder -> reminder.uid == id }
        if (reminder != null) {
            val index = _data.indexOf(reminder)
            _data.removeAt(index)
            _data.add(index, Reminder(
                uid = id!!,
                title = title,
                description = description,
                unixTimestamp = unixTimestamp,
                notified = notified
            ))
        }
        else {
            _data.add(_index++, Reminder(
                title = title,
                description = description,
                unixTimestamp = unixTimestamp,
                notified = notified
            ))
        }
    }

    override suspend fun getById(id: Int): Reminder? {
        return _data.find { reminder -> reminder.uid == id }
    }

    override suspend fun deleteById(id: Int) {
        val reminder = _data.find { reminder -> reminder.uid == id }
        _data.remove(reminder)
    }

    override suspend fun deleteNotified() {
        _data.removeAll { reminder -> reminder.notified }
    }

    override suspend fun canDeleteNotified(): Boolean {
        return _data.any { reminder -> reminder.notified }
    }

    override suspend fun getClosestReminderToNotify(): Reminder? {
        return _data.filter { reminder -> !reminder.notified }.minBy { reminder -> reminder.unixTimestamp }
    }

    override suspend fun getRemindersToNotify(): List<Reminder> {
        return _data.filter { reminder -> !reminder.notified }
    }

    override suspend fun updateNotifiedById(id: Int) {
        val reminder = _data.find { reminder -> reminder.uid == id }
        if (reminder != null) {
            val newReminder = reminder.copy(notified = true)

            val index = _data.indexOf(reminder)
            _data.removeAt(index)
            _data.add(index, newReminder)
        }
    }
}
