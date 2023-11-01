package rek.remindme.ui.reminder

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rek.remindme.R
import rek.remindme.common.DateTimeHelper
import rek.remindme.data.di.FakeReminderRepository
import rek.remindme.data.local.database.Reminder
import rek.remindme.ui.components.ReminderListSnackbarMessage
import rek.remindme.ui.components.ReminderListSnackbarMessageOnLoad
import rek.remindme.ui.components.ReminderListTopAppBar
import rek.remindme.ui.components.SimpleAlertDialog
import rek.remindme.ui.components.SimpleDeleteSwipe
import rek.remindme.ui.theme.MyApplicationTheme

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
                containerColor = MaterialTheme.colorScheme.primary
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
                onReminderClick = onReminderClick,
                viewModel = viewModel
            )
        }

        SimpleAlertDialog(
            isDisplayed = alertDialogOpened,
            textToDisplay = stringResource(R.string.confirm_notified_reminders),
            onDismiss = {},
            onConfirm = viewModel::clearNotified
        )

        ReminderListSnackbarMessageOnLoad(
            snackbarMessageRes = snackbarMessageResOnLoad,
            snackbarHostState = snackbarHostState
        )

        ReminderListSnackbarMessage(
            snackbarMessageRes = snackbarMessageRes,
            snackbarHostState = snackbarHostState,
            onSnackbarMessageShow = viewModel::snackbarMessageShown,
            handleNotification = viewModel::setNextReminder
        )
    }
}

@Composable
internal fun ReminderListScreenContent(
    items: List<Reminder>,
    modifier: Modifier = Modifier,
    onReminderClick: (Int) -> Unit,
    viewModel: ReminderListViewModel
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        items.forEach {
            SimpleDeleteSwipe(onConfirm = { viewModel.delete(it.uid) }) {
                Card(
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable { onReminderClick(it.uid) }
                ) {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = DateTimeHelper.instance.getReadableDate(it.unixTimestamp),
                                modifier = Modifier.weight(1f)
                            )
                            Text(text = DateTimeHelper.instance.getReadableTime(it.unixTimestamp))
                        }
                        Row {
                            Text(
                                text = DateTimeHelper.instance.getRemainingOrPastTime(it.unixTimestamp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row {
                            Text(text = it.title, fontWeight = FontWeight.Bold)
                        }
                        Row {
                            Text(text = ReminderListHelper.formatDescription(it.description))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReminderListScreenContentPreview() {
    MyApplicationTheme {
        ReminderListScreenContent(
            items = listOf(
                Reminder(title = "Title 1", description = "Hello", unixTimestamp = 1697808658000, notified = false),
                Reminder(title = "Title 2", description = "Hello\nMultiline", unixTimestamp = 1697808658000, notified = false),
                Reminder(title = "Title 3", description = "Hello", unixTimestamp = 1697808658000, notified = true)
            ),
            onReminderClick = {},
            viewModel = ReminderListViewModel(FakeReminderRepository())
        )
    }
}