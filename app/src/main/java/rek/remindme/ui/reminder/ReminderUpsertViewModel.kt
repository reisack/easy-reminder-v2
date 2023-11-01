package rek.remindme.ui.reminder

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rek.remindme.R
import rek.remindme.common.Consts
import rek.remindme.common.DateTimeHelper
import rek.remindme.common.ReminderScheduler
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
    val isDeleted: Boolean = false,
    val snackbarMessageRes: Int? = null
)

@HiltViewModel
class ReminderUpsertViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _reminderId: Int? = savedStateHandle[Consts.Route.REMINDER_ID_NAV_ARG]

    // A MutableStateFlow needs to be created in this ViewModel. The source of truth of the current
    // editable Task is the ViewModel, we need to mutate the UI state directly in methods such as
    // `updateTitle` or `updateDescription`
    private val _uiState = MutableStateFlow(ReminderEditUiState())
    val uiState: StateFlow<ReminderEditUiState> = _uiState.asStateFlow()

    private val validator: ReminderUpsertValidator = ReminderUpsertValidator()

    init {
        if (_reminderId != null) {
            viewModelScope.launch {
                val reminder = reminderRepository.getById(_reminderId)
                if (reminder != null) {
                    _uiState.update {
                        it.copy(
                            title = reminder.title,
                            description = reminder.description,
                            unixTimestampDate = reminder.unixTimestamp,
                            hour = DateTimeHelper.instance.getHourFromTimestamp((reminder.unixTimestamp)),
                            minute = DateTimeHelper.instance.getMinuteFromTimestamp((reminder.unixTimestamp)),
                            notified = reminder.notified,
                            isUpdateMode = true
                        )
                    }
                }
                else {
                    _uiState.update {
                        it.copy(
                            snackbarMessageRes = R.string.reminder_not_found
                        )
                    }
                }
            }
        }
    }

    fun updateTitle(newTitle: String) {
        if (newTitle.length <= 50) {
            _uiState.update {
                it.copy(title = newTitle)
            }
        }
    }

    fun updateDescription(newDescription: String) {
        if (newDescription.length <= 200) {
            _uiState.update {
                it.copy(description = newDescription)
            }
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

        val messageRes: Int = validator.validate(
            uiState.value.title,
            uiState.value.description,
            uiState.value.unixTimestampDate,
            uiState.value.hour,
            uiState.value.minute
        )

        if (messageRes > 0) {
            _uiState.update {
                it.copy(
                    snackbarMessageRes = messageRes
                )
            }
        }
        else {
            upsertReminder()
        }
    }

    private fun upsertReminder() {
        viewModelScope.launch {
            val reminderDateTimeInMillis = DateTimeHelper.instance.getUtcDatetimeInMillis(
                uiState.value.unixTimestampDate!!,
                uiState.value.hour!!,
                uiState.value.minute!!
            )

            reminderRepository.upsert(
                _reminderId,
                uiState.value.title,
                uiState.value.description,
                reminderDateTimeInMillis,
                false
            )

            _uiState.update {
                it.copy(isSaved = true)
            }
        }
    }

    fun snackbarMessageShown() {
        _uiState.update {
            it.copy(snackbarMessageRes = null)
        }
    }

    fun setNextReminder(context: Context) {
        viewModelScope.launch {
            val reminder = reminderRepository.getClosestReminderToNotify()
            if (reminder != null) {
                ReminderScheduler.setNextReminder(context, reminder.unixTimestamp)
            }
        }
    }
}