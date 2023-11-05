package rek.remindme.ui.components

import android.content.Context
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import rek.remindme.R
import rek.remindme.common.Consts
import rek.remindme.common.DateTimeHelper
import rek.remindme.ui.reminder.ReminderEditUiState
import rek.remindme.ui.theme.MyApplicationTheme
import java.util.Date

@Composable
internal fun ReminderTimeField(
    onTimeChanged: (Int, Int) -> Unit,
    reminderEditUiState: ReminderEditUiState
) {
    val timePickerDialogOpened = remember { mutableStateOf(false) }

    ClickableInputField(
        modifier = Modifier.testTag(Consts.TestTag.INPUT_TIME_FIELD),
        dialogOpened = timePickerDialogOpened,
        placeholderRes = R.string.time_field_title,
        getValue = { DateTimeHelper.instance.getReadableTime(reminderEditUiState.hour, reminderEditUiState.minute) }
    )

    if (timePickerDialogOpened.value) {
        TimePickerDialog(
            onCancel = {
                timePickerDialogOpened.value = false
            },
            onConfirm = { hour, minute ->
                onTimeChanged(hour, minute)
                timePickerDialogOpened.value = false
            },
            initialHour = reminderEditUiState.hour,
            initialMinute = reminderEditUiState.minute
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReminderDateField(
    onDateChanged: (Long) -> Unit,
    reminderEditUiState: ReminderEditUiState
) {
    val datePickerDialogOpened = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    ClickableInputField(
        modifier = Modifier.testTag(Consts.TestTag.INPUT_DATE_FIELD),
        dialogOpened = datePickerDialogOpened,
        placeholderRes = R.string.date_field_title,
        getValue = { DateTimeHelper.instance.getReadableDate(reminderEditUiState.unixTimestampDate) }
    )

    if (datePickerDialogOpened.value) {
        DatePickerDialog(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            onDismissRequest = {
                datePickerDialogOpened.value = false
            },
            confirmButton = {
                Button(
                    modifier = Modifier.testTag(Consts.TestTag.CONFIRM_BUTTON),
                    onClick = {
                        if (datePickerState.selectedDateMillis != null) {
                            onDateChanged(datePickerState.selectedDateMillis!!)
                        }
                        datePickerDialogOpened.value = false
                    }
                ) {
                    Text(text = stringResource(R.string.confirm_button_label))
                }
            },
            dismissButton = {
                Button(onClick = {
                    datePickerDialogOpened.value = false
                }) {
                    Text(text = stringResource(R.string.cancel_button_label))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                dateValidator = { timestamp ->
                    // timestamp >= current timestamp Date (Time 00:00) - milliseconds in 24 hour
                    timestamp >= Date().time - (1000 * 60 * 60 * 24)
                }
            )
        }
    }
}

@Composable
internal fun HandleActions(
    uiState: ReminderEditUiState,
    onReminderSaved: (Int) -> Unit,
    onReminderDeleted: (Int) -> Unit,
    handleNotification: (Context) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            handleNotification(context)
            onReminderSaved(if (uiState.isUpdateMode) R.string.reminder_updated else R.string.reminder_created)
        }
    }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            handleNotification(context)
            onReminderDeleted(R.string.reminder_deleted)
        }
    }
}

@Composable
internal fun ReminderUpsertSnackbarMessage(
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
internal fun ReminderUpsertTopAppBar(
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

@Preview(showBackground = true)
@Composable
private fun ReminderUpsertTopAppBarPreview() {
    MyApplicationTheme {
        ReminderUpsertTopAppBar(
            uiState = ReminderEditUiState(),
            onBack = {},
            onDelete = {}
        )
    }
}