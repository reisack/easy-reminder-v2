package rek.remindme.ui.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rek.remindme.data.ReminderRepository
import javax.inject.Inject

data class ReminderEditUiState(
    val title: String = "",
    val description: String = "",
    val unixTimestampDate: Long? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val unixTimestamp: Long = System.currentTimeMillis(),
    val alreadyNotified: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class ReminderUpsertViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
) : ViewModel() {
    // A MutableStateFlow needs to be created in this ViewModel. The source of truth of the current
    // editable Task is the ViewModel, we need to mutate the UI state directly in methods such as
    // `updateTitle` or `updateDescription`
    private val _uiState = MutableStateFlow(ReminderEditUiState())
    val uiState: StateFlow<ReminderEditUiState> = _uiState.asStateFlow()

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

    fun save() {
        if (uiState.value.title.isEmpty() || uiState.value.description.isEmpty()) {
            // TODO : Handle error message
            return
        }

        addReminder()
    }

    private fun addReminder() {
        viewModelScope.launch {
            reminderRepository.add(
                uiState.value.title,
                uiState.value.description,
                uiState.value.unixTimestamp,
                uiState.value.alreadyNotified
            )

            _uiState.update {
                it.copy(isSaved = true)
            }
        }
    }
}