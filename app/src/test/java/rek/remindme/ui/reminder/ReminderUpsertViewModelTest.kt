package rek.remindme.ui.reminder

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import rek.remindme.R
import rek.remindme.common.Consts
import rek.remindme.data.ReminderRepository
import rek.remindme.data.local.database.Reminder
import java.util.TimeZone

@OptIn(ExperimentalCoroutinesApi::class)
class ReminderUpsertViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var originalTimeZone: TimeZone

    @Before
    fun setUp() {
        originalTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @After
    fun tearDown() {
        TimeZone.setDefault(originalTimeZone)
    }

    @Test
    fun init_withoutReminderId_startsInCreateModeWithoutLookup() = runTest {
        val repository = FakeUpsertReminderRepository()

        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        assertEquals(ReminderEditUiState(), viewModel.uiState.value)
        assertEquals(emptyList<Int>(), repository.requestedIds)
    }

    @Test
    fun init_withExistingReminder_loadsEditableState() = runTest {
        val existing = Reminder(
            uid = 7,
            title = "Dentist",
            description = "Bring insurance card",
            unixTimestamp = 4_102_493_700_000,
            notified = true
        )
        val repository = FakeUpsertReminderRepository(reminderById = existing)

        val viewModel = createViewModel(repository, reminderId = 7)
        advanceUntilIdle()

        assertEquals(listOf(7), repository.requestedIds)
        assertEquals(
            ReminderEditUiState(
                title = existing.title,
                description = existing.description,
                unixTimestampDate = existing.unixTimestamp,
                hour = 13,
                minute = 35,
                notified = true,
                isUpdateMode = true
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun init_withMissingReminder_showsNotFoundSnackbar() = runTest {
        val repository = FakeUpsertReminderRepository(reminderById = null)

        val viewModel = createViewModel(repository, reminderId = 99)
        advanceUntilIdle()

        assertEquals(R.string.reminder_not_found, viewModel.uiState.value.snackbarMessageRes)
        assertFalse(viewModel.uiState.value.isUpdateMode)
    }

    @Test
    fun updateTitle_acceptsFiftyCharactersAndRejectsFiftyOne() = runTest {
        val viewModel = createViewModel(FakeUpsertReminderRepository())
        val accepted = "a".repeat(50)

        viewModel.updateTitle(accepted)
        viewModel.updateTitle("b".repeat(51))

        assertEquals(accepted, viewModel.uiState.value.title)
    }

    @Test
    fun updateDescription_acceptsTwoHundredCharactersAndRejectsTwoHundredOne() = runTest {
        val viewModel = createViewModel(FakeUpsertReminderRepository())
        val accepted = "a".repeat(200)

        viewModel.updateDescription(accepted)
        viewModel.updateDescription("b".repeat(201))

        assertEquals(accepted, viewModel.uiState.value.description)
    }

    @Test
    fun updateDateAndTime_updateEditableState() = runTest {
        val viewModel = createViewModel(FakeUpsertReminderRepository())

        viewModel.updateDate(4_102_444_800_000)
        viewModel.updateTime(hour = 9, minute = 45)

        assertEquals(4_102_444_800_000, viewModel.uiState.value.unixTimestampDate)
        assertEquals(9, viewModel.uiState.value.hour)
        assertEquals(45, viewModel.uiState.value.minute)
    }

    @Test
    fun delete_inCreateMode_marksDeletedWithoutRepositoryCall() = runTest {
        val repository = FakeUpsertReminderRepository()
        val viewModel = createViewModel(repository)

        viewModel.delete()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isDeleted)
        assertEquals(emptyList<Int>(), repository.deletedIds)
    }

    @Test
    fun delete_inUpdateMode_deletesLoadedIdAndMarksDeleted() = runTest {
        val repository = FakeUpsertReminderRepository(reminderById = null)
        val viewModel = createViewModel(repository, reminderId = 12)
        advanceUntilIdle()

        viewModel.delete()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isDeleted)
        assertEquals(listOf(12), repository.deletedIds)
    }

    @Test
    fun save_withInvalidState_showsThenClearsValidationSnackbar() = runTest {
        val repository = FakeUpsertReminderRepository()
        val viewModel = createViewModel(repository)

        viewModel.save()

        assertEquals(
            Consts.Validation.mandatoryFieldsNotFilled,
            viewModel.uiState.value.snackbarMessageRes
        )
        assertEquals(emptyList<UpsertCall>(), repository.upsertCalls)

        viewModel.snackbarMessageShown()

        assertNull(viewModel.uiState.value.snackbarMessageRes)
    }

    @Test
    fun save_inCreateMode_forwardsAllFieldsAndResetsNotified() = runTest {
        val repository = FakeUpsertReminderRepository()
        val viewModel = createViewModel(repository)
        viewModel.updateTitle("Flight")
        viewModel.updateDescription("Check in online")
        viewModel.updateDate(4_102_444_800_000)
        viewModel.updateTime(hour = 8, minute = 30)

        viewModel.save()
        advanceUntilIdle()

        assertEquals(
            listOf(
                UpsertCall(
                    id = null,
                    title = "Flight",
                    description = "Check in online",
                    unixTimestamp = 4_102_475_400_000,
                    notified = false
                )
            ),
            repository.upsertCalls
        )
        assertTrue(viewModel.uiState.value.isSaved)
    }

    @Test
    fun save_inUpdateMode_preservesIdAndResetsNotified() = runTest {
        val existing = Reminder(
            uid = 21,
            title = "Old title",
            description = "Old description",
            unixTimestamp = 4_102_444_800_000,
            notified = true
        )
        val repository = FakeUpsertReminderRepository(reminderById = existing)
        val viewModel = createViewModel(repository, reminderId = 21)
        advanceUntilIdle()
        viewModel.updateTitle("New title")
        viewModel.updateDescription("New description")
        viewModel.updateTime(hour = 18, minute = 5)

        viewModel.save()
        advanceUntilIdle()

        assertEquals(
            UpsertCall(
                id = 21,
                title = "New title",
                description = "New description",
                unixTimestamp = 4_102_509_900_000,
                notified = false
            ),
            repository.upsertCalls.single()
        )
        assertTrue(viewModel.uiState.value.isSaved)
    }

    private fun createViewModel(
        repository: ReminderRepository,
        reminderId: Int? = null
    ): ReminderUpsertViewModel {
        val savedState = if (reminderId == null) {
            SavedStateHandle()
        } else {
            SavedStateHandle(mapOf(Consts.Route.REMINDER_ID_NAV_ARG to reminderId))
        }
        return ReminderUpsertViewModel(repository, savedState)
    }
}

private data class UpsertCall(
    val id: Int?,
    val title: String,
    val description: String,
    val unixTimestamp: Long,
    val notified: Boolean
)

private class FakeUpsertReminderRepository(
    private val reminderById: Reminder? = null
) : ReminderRepository {

    override val reminders: Flow<List<Reminder>> = MutableStateFlow(emptyList())
    val requestedIds = mutableListOf<Int>()
    val deletedIds = mutableListOf<Int>()
    val upsertCalls = mutableListOf<UpsertCall>()

    override suspend fun getById(id: Int): Reminder? {
        requestedIds += id
        return reminderById
    }

    override suspend fun deleteById(id: Int) {
        deletedIds += id
    }

    override suspend fun upsert(
        id: Int?,
        title: String,
        description: String,
        unixTimestamp: Long,
        notified: Boolean
    ) {
        upsertCalls += UpsertCall(id, title, description, unixTimestamp, notified)
    }

    override suspend fun deleteNotified() = unsupported()

    override suspend fun canDeleteNotified(): Boolean = unsupported()

    override suspend fun getClosestReminderToNotify(): Reminder? = unsupported()

    override suspend fun getRemindersToNotify(): List<Reminder> = unsupported()

    override suspend fun updateNotifiedById(id: Int) = unsupported()

    private fun unsupported(): Nothing = error("Unexpected repository call")
}
