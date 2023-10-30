package rek.remindme.ui.reminder

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rek.remindme.R
import rek.remindme.common.DateTimeHelper
import rek.remindme.data.local.database.Reminder
import rek.remindme.ui.components.SimpleAlertDialog
import rek.remindme.ui.theme.MyApplicationTheme
import rek.remindme.ui.theme.Red80

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReminderListScreen(
    @StringRes snackbarMessageResOnLoad: Int,
    modifier: Modifier = Modifier,
    onNewReminder: () -> Unit,
    onReminderClick: (Int) -> Unit,
    onBackButtonPressed: () -> Unit,
    viewModel: ReminderListViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val snackbarMessageRes by viewModel.snackbarMessageRes.collectAsStateWithLifecycle()
    val alertDialogOpened = remember { mutableStateOf(false) }

    BackHandler(onBack = onBackButtonPressed)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize(),
        topBar = {
            ReminderListTopAppBar(
                alertDialogOpened = alertDialogOpened,
                canClearNotified = { viewModel.canClearNotified(alertDialogOpened) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewReminder,
                containerColor = Red80
            ) {
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

        SimpleAlertDialog(
            isDisplayed = alertDialogOpened,
            textToDisplay = stringResource(R.string.confirm_notified_reminders),
            onConfirm = viewModel::clearNotified
        )

        DisplaySnackbarMessageOnLoad(
            snackbarMessageRes = snackbarMessageResOnLoad,
            snackbarHostState = snackbarHostState
        )

        DisplaySnackbarMessage(
            snackbarMessageRes = snackbarMessageRes,
            snackbarHostState = snackbarHostState,
            onSnackbarMessageShow = viewModel::snackbarMessageShown
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

@Composable
private fun DisplaySnackbarMessage(
    snackbarMessageRes: Int?,
    snackbarHostState: SnackbarHostState,
    onSnackbarMessageShow: () -> Unit
) {
    snackbarMessageRes?.let { messageRes ->
        val message = stringResource(id = messageRes)
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            onSnackbarMessageShow()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderListTopAppBar(
    alertDialogOpened: MutableState<Boolean>,
    canClearNotified: (MutableState<Boolean>) -> Unit
) {
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
                            canClearNotified(alertDialogOpened)
                            expanded = false
                        }
                    )

                    val uriHandler = LocalUriHandler.current
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.privacy)) },
                        onClick = {
                            uriHandler.openUri("https://reisack.github.io/EasyReminder/privacy.html")
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
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        items.forEach {
            Card(
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clickable { onReminderClick(it.uid) }
            ) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = DateTimeHelper.instance.getReadableDate(it.unixTimestamp),
                            color = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = DateTimeHelper.instance.getReadableTime(it.unixTimestamp),
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                    Row {
                        Text(
                            text = DateTimeHelper.instance.getRemainingOrPastTime(it.unixTimestamp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row {
                        Text(
                            text = it.title,
                            color = MaterialTheme.colorScheme.onTertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row {
                        Text(
                            text = ReminderListHelper.formatDescription(it.description),
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            }
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
