/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

// TODO : Will be moved to an upcoming ReminderUpsertScreen
data class ReminderEdit(
    var title: String = "",
    var description: String = "",
    var unixTimestamp: Long = System.currentTimeMillis(),
    val alreadyNotified: Boolean = false
)

@HiltViewModel
class ReminderListViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    val uiState: StateFlow<ReminderUiState> = reminderRepository
        .reminders.map<List<Reminder>, ReminderUiState>(::Success)
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    val reminderEdit: ReminderEdit = ReminderEdit()

    fun addReminder(title: String, description: String, unixTimestamp: Long, alreadyNotified: Boolean) {
        viewModelScope.launch {
            reminderRepository.add(title, description, unixTimestamp, alreadyNotified)
        }
    }
}

sealed interface ReminderUiState {
    object Loading : ReminderUiState
    data class Error(val throwable: Throwable) : ReminderUiState
    data class Success(val data: List<Reminder>) : ReminderUiState
}
