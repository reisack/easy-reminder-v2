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

import rek.remindme.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rek.remindme.data.local.database.Reminder

@Composable
fun ReminderListScreen(
    modifier: Modifier = Modifier,
    viewModel: ReminderListViewModel = hiltViewModel()
) {
    val items by viewModel.uiState.collectAsStateWithLifecycle()
    if (items is ReminderUiState.Success) {
        ReminderListScreenContent(
            items = (items as ReminderUiState.Success).data,
            onSave = viewModel::addReminder,
            modifier = modifier,
            reminderEdit = viewModel.reminderEdit
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun ReminderListScreenContent(
    items: List<Reminder>,
    onSave: (name: String, description: String, unixTimestamp: Long, alreadyNotified: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    reminderEdit: ReminderEdit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("TODO Ma liste") }
            )
        },
        floatingActionButton = { 
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "TODO Créer un rappel")
            }
        },
        content = { innerPadding ->
            Column(
                modifier = modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding)
            ) {
                var title by remember { mutableStateOf("") }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextField(
                        value = title,
                        onValueChange = {
                            title = it
                            reminderEdit.title = it
                        }
                    )
                }

                var description by remember { mutableStateOf("") }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextField(
                        value = description,
                        onValueChange = {
                            description = it
                            reminderEdit.description = it
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(modifier = Modifier.width(96.dp), onClick = {
                        onSave(
                            reminderEdit.title,
                            reminderEdit.description,
                            reminderEdit.unixTimestamp,
                            reminderEdit.alreadyNotified)
                    }) {
                        Text("Save")
                    }
                }

                items.forEach {
                    Text("Saved item: ${it.title} | ${it.description}")
                }
            }
        }
    )
}

// Previews

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        ReminderListScreenContent(
            items = listOf(
                Reminder(title = "Title 1", description = "Hello", unixTimestamp = 1697808658, alreadyNotified = false),
                Reminder(title = "Title 2", description = "Hello", unixTimestamp = 1697808658, alreadyNotified = false),
                Reminder(title = "Title 3", description = "Hello", unixTimestamp = 1697808658, alreadyNotified = false)
            ),
            onSave = { _, _, _, _ -> },
            reminderEdit = ReminderEdit()
        )
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    MyApplicationTheme {
        ReminderListScreenContent(
            items = listOf(
                Reminder(title = "Title 1", description = "Hello", unixTimestamp = 1697808658, alreadyNotified = false),
                Reminder(title = "Title 2", description = "Hello", unixTimestamp = 1697808658, alreadyNotified = false),
                Reminder(title = "Title 3", description = "Hello", unixTimestamp = 1697808658, alreadyNotified = false)
            ),
            onSave = { _, _, _, _ -> },
            reminderEdit = ReminderEdit()
        )
    }
}
