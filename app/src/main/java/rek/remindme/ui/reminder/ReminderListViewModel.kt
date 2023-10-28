package rek.remindme.ui.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import rek.remindme.data.ReminderRepository
import rek.remindme.data.local.database.Reminder
import rek.remindme.ui.reminder.ReminderUiState.Error
import rek.remindme.ui.reminder.ReminderUiState.Loading
import rek.remindme.ui.reminder.ReminderUiState.Success
import javax.inject.Inject

@HiltViewModel
class ReminderListViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    val uiState: StateFlow<ReminderUiState> = reminderRepository
        .reminders.map<List<Reminder>, ReminderUiState>(::Success)
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun clearNotified() {
        // TODO : Add a dialog Modal for confirmation
        viewModelScope.launch {
            reminderRepository.deleteNotified()
        }
    }
}

sealed interface ReminderUiState {
    object Loading : ReminderUiState
    data class Error(val throwable: Throwable) : ReminderUiState
    data class Success(val data: List<Reminder>) : ReminderUiState
}
