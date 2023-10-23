package rek.remindme.ui.reminder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rek.remindme.common.DateTimeHelper
import rek.remindme.data.ReminderRepository
import javax.inject.Inject

data class ReminderEditUiState(
    val title: String = "",
    val description: String = "",
    val unixTimestampDate: Long? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val notified: Boolean = false,
    val isUpdateMode: Boolean = false,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false
)

@HiltViewModel
class ReminderUpsertViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _reminderId: Int? = savedStateHandle["reminderId"]

    // A MutableStateFlow needs to be created in this ViewModel. The source of truth of the current
    // editable Task is the ViewModel, we need to mutate the UI state directly in methods such as
    // `updateTitle` or `updateDescription`
    private val _uiState = MutableStateFlow(ReminderEditUiState())
    val uiState: StateFlow<ReminderEditUiState> = _uiState.asStateFlow()

    init {
        if (_reminderId != null) {
            viewModelScope.launch {
                val reminder = reminderRepository.getById(_reminderId)
                if (reminder != null) {
                    // TODO : Complete DateTimeHelper
                    _uiState.update {
                        it.copy(
                            title = reminder.title,
                            description = reminder.description,
                            unixTimestampDate = reminder.unixTimestamp,
                            hour = DateTimeHelper.getHourFromTimestamp((reminder.unixTimestamp)),
                            minute = DateTimeHelper.getMinuteFromTimestamp((reminder.unixTimestamp)),
                            notified = reminder.notified,
                            isUpdateMode = true
                        )
                    }
                }
                else {
                    // TODO : Display a toast to inform user that reminder couldn't be found. "New reminder" mode
                }
            }
        }
    }

    fun updateTitle(newTitle: String) {
        _uiState.update {
            it.copy(title = newTitle)
        }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update {
            it.copy(description = newDescription)
        }
    }

    fun updateDate(newUnixTimestamp: Long) {
        _uiState.update {
            it.copy(unixTimestampDate = newUnixTimestamp)
        }
    }

    fun updateTime(hour: Int, minute: Int) {
        _uiState.update {
            it.copy(hour = hour, minute = minute)
        }
    }

    fun delete() {
        // TODO : Add a dialog modal for confirmation
        viewModelScope.launch {
            if (_reminderId != null) {
                reminderRepository.deleteById(_reminderId)
            }

            _uiState.update {
                it.copy(isDeleted = true)
            }
        }
    }

    fun save() {
        if (uiState.value.title.isBlank()
            || uiState.value.description.isBlank()
            || uiState.value.unixTimestampDate == null
            || uiState.value.hour == null
            || uiState.value.minute == null) {
            // TODO : Handle error message
            return
        }

        upsertReminder()
    }

    private fun upsertReminder() {
        viewModelScope.launch {
            val reminderDateTimeInMillis = DateTimeHelper.getUtcDatetimeInMillis(
                uiState.value.unixTimestampDate!!,
                uiState.value.hour!!,
                uiState.value.minute!!
            )

            reminderRepository.upsert(
                _reminderId,
                uiState.value.title,
                uiState.value.description,
                reminderDateTimeInMillis,
                uiState.value.notified
            )

            _uiState.update {
                it.copy(isSaved = true)
            }
        }
    }
}