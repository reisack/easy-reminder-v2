package rek.remindme.ui.reminder

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rek.remindme.R
import rek.remindme.common.ReminderScheduler
import rek.remindme.data.ReminderRepository
import rek.remindme.data.local.database.Reminder
import rek.remindme.ui.reminder.ReminderUiState.Error
import rek.remindme.ui.reminder.ReminderUiState.Loading
import rek.remindme.ui.reminder.ReminderUiState.Success
import javax.inject.Inject

@HiltViewModel
class ReminderListViewModel @Inject constructor(
    private val _reminderRepository: ReminderRepository
) : ViewModel() {

    private val _snackbarMessageRes: MutableStateFlow<Int?> = MutableStateFlow(null)
    val snackbarMessageRes: StateFlow<Int?> = _snackbarMessageRes.asStateFlow()

    private val _canDeleteNotified: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _canDeleteNotifiedState: StateFlow<Boolean> = _canDeleteNotified.asStateFlow()

    val uiState: StateFlow<ReminderUiState> = _reminderRepository
        .reminders.map<List<Reminder>, ReminderUiState>(::Success)
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun clearNotified() {
        viewModelScope.launch {
            _reminderRepository.deleteNotified()
            _snackbarMessageRes.update { R.string.notified_reminders_cleared }
        }
    }

    fun canClearNotified(alertDialogOpened: MutableState<Boolean>) {
        viewModelScope.launch {
            _canDeleteNotified.update { _reminderRepository.canDeleteNotified() }

            if (!_canDeleteNotifiedState.value) {
                _snackbarMessageRes.update { R.string.no_notified_reminders }
            }
            else {
                alertDialogOpened.value = true
            }
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            _reminderRepository.deleteById(id)
            _snackbarMessageRes.update { R.string.reminder_deleted }
        }
    }

    fun snackbarMessageShown() {
        _snackbarMessageRes.update { null }
    }

    fun setNextReminder(context: Context) {
        viewModelScope.launch {
            val reminder = _reminderRepository.getClosestReminderToNotify()
            if (reminder != null) {
                ReminderScheduler.setNextReminder(context, reminder.unixTimestamp)
            }
        }
    }
}

sealed interface ReminderUiState {
    data object Loading : ReminderUiState
    data class Error(val throwable: Throwable) : ReminderUiState
    data class Success(val data: List<Reminder>) : ReminderUiState
}