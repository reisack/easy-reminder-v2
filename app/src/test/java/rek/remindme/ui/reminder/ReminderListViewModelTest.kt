package rek.remindme.ui.reminder

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import rek.remindme.R
import rek.remindme.data.ReminderRepository
import rek.remindme.data.local.database.Reminder

@OptIn(ExperimentalCoroutinesApi::class)
class ReminderListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun uiState_emitsReminderList() = runTest {
        val reminders = listOf(reminder(id = 1), reminder(id = 2))
        val repository = FakeReminderRepository(remindersFlow = MutableStateFlow(reminders))
        val viewModel = ReminderListViewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        advanceUntilIdle()

        assertEquals(ReminderUiState.Success(reminders), viewModel.uiState.value)
    }

    @Test
    fun uiState_emitsRepositoryFailure() = runTest {
        val failure = IllegalStateException("Unable to load reminders")
        val repository = FakeReminderRepository(
            remindersFlow = flow { throw failure }
        )
        val viewModel = ReminderListViewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value as ReminderUiState.Error
        assertSame(failure, state.throwable)
    }

    @Test
    fun clearNotified_deletesAndShowsThenClearsSnackbar() = runTest {
        val repository = FakeReminderRepository()
        val viewModel = ReminderListViewModel(repository)

        viewModel.clearNotified()
        advanceUntilIdle()

        assertEquals(1, repository.deleteNotifiedCalls)
        assertEquals(R.string.notified_reminders_cleared, viewModel.snackbarMessageRes.value)

        viewModel.snackbarMessageShown()

        assertNull(viewModel.snackbarMessageRes.value)
    }

    @Test
    fun canClearNotified_whenNothingCanBeDeleted_showsSnackbar() = runTest {
        val repository = FakeReminderRepository(canDeleteNotifiedResult = false)
        val viewModel = ReminderListViewModel(repository)
        val alertDialogOpened = mutableStateOf(false)

        viewModel.canClearNotified(alertDialogOpened)
        advanceUntilIdle()

        assertFalse(alertDialogOpened.value)
        assertEquals(R.string.no_notified_reminders, viewModel.snackbarMessageRes.value)
    }

    @Test
    fun canClearNotified_whenOldReminderExists_opensDialog() = runTest {
        val repository = FakeReminderRepository(canDeleteNotifiedResult = true)
        val viewModel = ReminderListViewModel(repository)
        val alertDialogOpened = mutableStateOf(false)

        viewModel.canClearNotified(alertDialogOpened)
        advanceUntilIdle()

        assertTrue(alertDialogOpened.value)
        assertNull(viewModel.snackbarMessageRes.value)
    }

    @Test
    fun delete_deletesRequestedReminderAndShowsSnackbar() = runTest {
        val repository = FakeReminderRepository()
        val viewModel = ReminderListViewModel(repository)

        viewModel.delete(42)
        advanceUntilIdle()

        assertEquals(listOf(42), repository.deletedIds)
        assertEquals(R.string.reminder_deleted, viewModel.snackbarMessageRes.value)
    }
}

private class FakeReminderRepository(
    remindersFlow: Flow<List<Reminder>> = MutableStateFlow(emptyList()),
    private val canDeleteNotifiedResult: Boolean = false
) : ReminderRepository {

    override val reminders: Flow<List<Reminder>> = remindersFlow
    var deleteNotifiedCalls: Int = 0
    val deletedIds = mutableListOf<Int>()

    override suspend fun deleteById(id: Int) {
        deletedIds += id
    }

    override suspend fun deleteNotified() {
        deleteNotifiedCalls++
    }

    override suspend fun canDeleteNotified(): Boolean = canDeleteNotifiedResult

    override suspend fun upsert(
        id: Int?,
        title: String,
        description: String,
        unixTimestamp: Long,
        notified: Boolean
    ) = unsupported()

    override suspend fun getById(id: Int): Reminder? = unsupported()

    override suspend fun getClosestReminderToNotify(): Reminder? = unsupported()

    override suspend fun getRemindersToNotify(): List<Reminder> = unsupported()

    override suspend fun updateNotifiedById(id: Int) = unsupported()

    private fun unsupported(): Nothing = error("Unexpected repository call")
}

private fun reminder(
    id: Int,
    title: String = "Reminder $id",
    description: String = "Description $id",
    unixTimestamp: Long = 4_102_444_800_000,
    notified: Boolean = false
) = Reminder(id, title, description, unixTimestamp, notified)
