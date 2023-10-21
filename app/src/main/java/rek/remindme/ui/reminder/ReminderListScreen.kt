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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rek.remindme.data.local.database.Reminder
import rek.remindme.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReminderListScreen(
    modifier: Modifier = Modifier,
    onNewReminder: () -> Unit,
    viewModel: ReminderListViewModel = hiltViewModel()
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("TODO Ma liste") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewReminder) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "TODO Créer un rappel")
            }
        },
        content = { innerPadding ->
            val items by viewModel.uiState.collectAsStateWithLifecycle()
            if (items is ReminderUiState.Success) {
                ReminderListScreenContent(
                    items = (items as ReminderUiState.Success).data,
                    modifier = modifier
                        .consumeWindowInsets(innerPadding)
                        .padding(innerPadding),
                )
            }
        }
    )
}

@Composable
internal fun ReminderListScreenContent(
    items: List<Reminder>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        items.forEach {
            Text("Saved item: ${it.title} | ${it.description} | ${it.unixTimestamp}")
        }
    }
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
            )
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
            )
        )
    }
}
