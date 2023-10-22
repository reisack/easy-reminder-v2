package rek.remindme.ui.reminder

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
        if (uiState.value.title.isBlank()
            || uiState.value.description.isBlank()
            || uiState.value.unixTimestampDate == null
            || uiState.value.hour == null
            || uiState.value.minute == null) {
            // TODO : Handle error message
            return
        }

        addReminder()
    }

    private fun addReminder() {
        viewModelScope.launch {

            val reminderDateTimeInMillis = DateTimeHelper.getUtcDatetimeInMillis(
                uiState.value.unixTimestampDate!!,
                uiState.value.hour!!,
                uiState.value.minute!!
            )

            reminderRepository.add(
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