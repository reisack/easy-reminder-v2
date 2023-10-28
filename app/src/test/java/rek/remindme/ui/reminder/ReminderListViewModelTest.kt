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

    private val data = mutableListOf<Reminder>()
    private var index: Int = 0

    override val reminders: Flow<List<Reminder>>
        get() = flow { emit(data.toList()) }

    override suspend fun upsert(
        id: Int?,
        title: String,
        description: String,
        unixTimestamp: Long,
        notified: Boolean
    ) {
        val reminder = data.find { reminder -> reminder.uid == id }
        if (reminder != null) {
            val index = data.indexOf(reminder)
            data.removeAt(index)
            data.add(index, Reminder(
                uid = id!!,
                title = title,
                description = description,
                unixTimestamp = unixTimestamp,
                notified = notified
            ))
        }
        else {
            data.add(index++, Reminder(
                title = title,
                description = description,
                unixTimestamp = unixTimestamp,
                notified = notified
            ))
        }
    }

    override suspend fun getById(id: Int): Reminder? {
        return data.find { reminder -> reminder.uid == id }
    }

    override suspend fun deleteById(id: Int) {
        val reminder = data.find { reminder -> reminder.uid == id }
        data.remove(reminder)
    }

    override suspend fun deleteNotified() {
        data.removeAll { reminder -> reminder.notified }
    }
}
