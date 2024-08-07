package rek.remindme.ui.reminder

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rek.remindme.R
import rek.remindme.common.Consts
import rek.remindme.data.local.database.Reminder
import rek.remindme.ui.components.EmptyReminderList
import rek.remindme.ui.components.NotificationPermission
import rek.remindme.ui.components.ReminderCard
import rek.remindme.ui.components.ReminderListSnackbarMessage
import rek.remindme.ui.components.ReminderListSnackbarMessageOnLoad
import rek.remindme.ui.components.ReminderListTopAppBar
import rek.remindme.ui.components.SimpleAlertDialog
import rek.remindme.ui.theme.MyApplicationTheme

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

    NotificationPermission()
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
                modifier = Modifier.testTag(Consts.TestTag.ADD_REMINDER_BUTTON),
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
                onReminderDelete = viewModel::delete
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
    @Suppress("UNUSED_PARAMETER") onReminderDelete: (Int) -> Unit,
) {
    if(!items.any()) {
        EmptyReminderList()
    }
    else {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
        ) {
            items.forEach { reminder ->
                // ReminderCard should have been encapsulated on a SimpleDeleteSwipe, an example below :
                //
                // SimpleDeleteSwipe(onConfirm = { onReminderDelete(reminder.uid) }) {
                //     ReminderCard(reminder = reminder, onReminderClick = onReminderClick)
                // }
                //
                // Unfortunately, tests revealed that the SwipeToDismiss material3 component is impossible to use
                // because swipe is activated too easily when scrolling (in november 2023).
                // The problem has been reported here by a user : https://issuetracker.google.com/issues/252334353
                //
                // SimpleDeleteSwipe is kept in source code, hoping that a future material3 version
                // will correct the problem.
                // Anyway, swipe to dismiss is not a key feature.

                // UPDATE OF July 23, 2024 : A big improve has been done on SwipeToDismiss
                // The SwipeToDismissBoxState exposes positionalThreshold, which gives better results
                // But still not OK as velocityThreshold is not exposed
                // It's still too easily triggered on a scroll
                ReminderCard(reminder = reminder, onReminderClick = onReminderClick)
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
            onReminderDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FloatingActionButtonPreview() {
    MyApplicationTheme {
        FloatingActionButton(
            onClick = {},
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(R.string.add_reminder_desc))
        }
    }
}