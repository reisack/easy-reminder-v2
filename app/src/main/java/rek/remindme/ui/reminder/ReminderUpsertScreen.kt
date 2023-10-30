package rek.remindme.ui.reminder

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rek.remindme.R
import rek.remindme.ui.components.ReminderDateField
import rek.remindme.ui.components.ReminderTimeField
import rek.remindme.ui.components.SimpleAlertDialog
import rek.remindme.ui.theme.MyApplicationTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReminderUpsertScreen(
    modifier: Modifier = Modifier,
    onReminderSaved: (Int) -> Unit,
    onReminderDeleted: (Int) -> Unit,
    onBack: () -> Unit,
    onBackButtonPressed: () -> Unit,
    viewModel: ReminderUpsertViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val alertDialogOpened = remember { mutableStateOf(false) }

    BackHandler(onBack = onBackButtonPressed)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize(),
        topBar = {
            ReminderUpsertTopAppBar(
                uiState = uiState,
                onBack = onBack,
                onDelete = { alertDialogOpened.value = true }
            )
        },
        content = { innerPadding ->
            ReminderUpsertScreenContent(
                modifier = modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding),
                onSave = viewModel::save,
                onTitleChanged = viewModel::updateTitle,
                onDescriptionChanged = viewModel::updateDescription,
                onDateChanged = viewModel::updateDate,
                onTimeChanged = viewModel::updateTime,
                reminderEditUiState = uiState
            )

            HandleActions(
                uiState = uiState,
                onReminderSaved = onReminderSaved,
                onReminderDeleted = onReminderDeleted
            )

            SimpleAlertDialog(
                isDisplayed = alertDialogOpened,
                textToDisplay = stringResource(R.string.confirm_delete_reminder),
                onConfirm = viewModel::delete
            )

            DisplaySnackbarMessage(
                uiState = uiState,
                snackbarHostState = snackbarHostState,
                onSnackbarMessageShow = viewModel::snackbarMessageShown
            )
        }
    )
}

@Composable
private fun HandleActions(
    uiState: ReminderEditUiState,
    onReminderSaved: (Int) -> Unit,
    onReminderDeleted: (Int) -> Unit,
) {
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onReminderSaved(if (uiState.isUpdateMode) R.string.reminder_updated else R.string.reminder_created)
        }
    }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            onReminderDeleted(R.string.reminder_deleted)
        }
    }
}

@Composable
private fun DisplaySnackbarMessage(
    uiState: ReminderEditUiState,
    snackbarHostState: SnackbarHostState,
    onSnackbarMessageShow: () -> Unit
) {
    uiState.snackbarMessageRes?.let { messageRes ->
        val message = stringResource(id = messageRes)
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            onSnackbarMessageShow()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderUpsertTopAppBar(
    uiState: ReminderEditUiState,
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    TopAppBar(
        title = { if (uiState.isUpdateMode) Text(stringResource(R.string.update_reminder_label)) else Text(stringResource(R.string.new_reminder_label)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_desc)
                )
            }
        },
        actions = {
            if (uiState.isUpdateMode) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete_reminder_desc)
                    )
                }
            }
        }
    )
}

@Composable
internal fun ReminderUpsertScreenContent(
    modifier: Modifier = Modifier,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onDateChanged: (Long) -> Unit,
    onTimeChanged: (Int, Int) -> Unit,
    onSave: () -> Unit,
    reminderEditUiState: ReminderEditUiState
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        ReminderUpsertScreenContentRow {
            OutlinedTextField(
                singleLine = true,
                placeholder = { Text(text = "${stringResource(R.string.title_field_title)} *") },
                value = reminderEditUiState.title,
                onValueChange = onTitleChanged,
                modifier = Modifier.fillMaxSize()
            )
        }

        ReminderUpsertScreenContentRow {
            OutlinedTextField(
                placeholder = { Text(text = stringResource(R.string.description_field_title)) },
                value = reminderEditUiState.description,
                onValueChange = onDescriptionChanged,
                modifier = Modifier.fillMaxSize()
            )
        }

        ReminderUpsertScreenContentRow {
            ReminderDateField(
                onDateChanged = onDateChanged,
                reminderEditUiState = reminderEditUiState
            )
        }

        ReminderUpsertScreenContentRow {
            ReminderTimeField(
                onTimeChanged = onTimeChanged,
                reminderEditUiState = reminderEditUiState
            )
        }

        ReminderUpsertScreenContentRow {
            Button(modifier = Modifier.fillMaxSize(), onClick = onSave) {
                Text(text = stringResource(R.string.save_button_label))
            }
        }
    }
}

@Composable
private fun ReminderUpsertScreenContentRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        ReminderUpsertScreenContent(
            onTitleChanged = { _ -> },
            onDescriptionChanged = { _ -> },
            onDateChanged = { _ -> },
            onTimeChanged = { _, _ -> },
            onSave = {},
            reminderEditUiState = ReminderEditUiState()
        )
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    MyApplicationTheme {
        ReminderUpsertScreenContent(
            onTitleChanged = { _ -> },
            onDescriptionChanged = { _ -> },
            onDateChanged = { _ -> },
            onTimeChanged = { _, _ -> },
            onSave = {},
            reminderEditUiState = ReminderEditUiState()
        )
    }
}