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

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rek.remindme.R
import rek.remindme.common.DateTimeHelper
import rek.remindme.data.local.database.Reminder
import rek.remindme.ui.theme.MyApplicationTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReminderListScreen(
    @StringRes snackbarMessageRes: Int,
    modifier: Modifier = Modifier,
    onNewReminder: () -> Unit,
    onReminderClick: (Int) -> Unit,
    onBackButtonPressed: () -> Unit,
    viewModel: ReminderListViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    BackHandler(onBack = onBackButtonPressed)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize(),
        topBar = {
            ReminderListTopAppBar(clearNotified = viewModel::clearNotified)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewReminder) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(R.string.add_reminder_desc))
            }
        },
    ) { innerPadding ->
        val items by viewModel.uiState.collectAsStateWithLifecycle()
        if (items is ReminderUiState.Success) {
            ReminderListScreenContent(
                items = (items as ReminderUiState.Success).data,
                modifier = modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding),
                onReminderClick = onReminderClick
            )
        }

        DisplaySnackbarMessageOnLoad(
            snackbarMessageRes = snackbarMessageRes,
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
private fun DisplaySnackbarMessageOnLoad(
    @StringRes snackbarMessageRes: Int,
    snackbarHostState: SnackbarHostState
) {
    if (snackbarMessageRes != 0) {
        val message = stringResource(id = snackbarMessageRes)
        LaunchedEffect(snackbarMessageRes) {
            snackbarHostState.showSnackbar(message)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderListTopAppBar(clearNotified: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.reminder_list_title)) },
        actions = {
            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.back_desc)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false}
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.clear_notified_reminders)) },
                        onClick = {
                            clearNotified()
                            expanded = false
                        }
                    )
                }
            }
        }
    )
}

@Composable
internal fun ReminderListScreenContent(
    items: List<Reminder>,
    modifier: Modifier = Modifier,
    onReminderClick: (Int) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        items.forEach {
            Text(
                modifier = Modifier.clickable { onReminderClick(it.uid) },
                text = "${
                DateTimeHelper.instance.getReadableDate(it.unixTimestamp)}\n${DateTimeHelper.instance.getReadableTime(it.unixTimestamp)}\n" +
                    "${DateTimeHelper.instance.getRemainingOrPastTime(it.unixTimestamp)}\n${it.title}\n${it.description}\nuid : ${it.uid}"
            )
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
                Reminder(title = "Title 1", description = "Hello", unixTimestamp = 1697808658, notified = false),
                Reminder(title = "Title 2", description = "Hello", unixTimestamp = 1697808658, notified = false),
                Reminder(title = "Title 3", description = "Hello", unixTimestamp = 1697808658, notified = false)
            ),
            onReminderClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    MyApplicationTheme {
        ReminderListScreenContent(
            items = listOf(
                Reminder(title = "Title 1", description = "Hello", unixTimestamp = 1697808658, notified = false),
                Reminder(title = "Title 2", description = "Hello", unixTimestamp = 1697808658, notified = false),
                Reminder(title = "Title 3", description = "Hello", unixTimestamp = 1697808658, notified = false)
            ),
            onReminderClick = {}
        )
    }
}
