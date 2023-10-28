package rek.remindme.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import rek.remindme.R
import rek.remindme.common.DateTimeHelper
import rek.remindme.ui.reminder.ReminderEditUiState
import rek.remindme.ui.theme.MyApplicationTheme

@Composable
fun ReminderTimeField(
    onTimeChanged: (Int, Int) -> Unit,
    reminderEditUiState: ReminderEditUiState
) {
    val timePickerDialogOpened = remember { mutableStateOf(false) }

    TextField(
        modifier = Modifier.clickable { timePickerDialogOpened.value = true },
        placeholder = {
            Text(text = stringResource(R.string.time_field_title))
        },
        value = DateTimeHelper.instance.getReadableTime(reminderEditUiState.hour, reminderEditUiState.minute),
        onValueChange = {},
        enabled = false
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
fun ReminderDateField(
    onDateChanged: (Long) -> Unit,
    reminderEditUiState: ReminderEditUiState
) {
    val datePickerDialogOpened = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    TextField(
        modifier = Modifier.clickable { datePickerDialogOpened.value = true },
        placeholder = {
            Text(text = stringResource(R.string.date_field_title))
        },
        value = DateTimeHelper.instance.getReadableDate(reminderEditUiState.unixTimestampDate),
        onValueChange = {},
        enabled = false
    )

    if (datePickerDialogOpened.value) {
        DatePickerDialog(
            onDismissRequest = {
                datePickerDialogOpened.value = false
            },
            confirmButton = {
                Button(onClick = {
                    if (datePickerState.selectedDateMillis != null) {
                        onDateChanged(datePickerState.selectedDateMillis!!)
                    }

                    datePickerDialogOpened.value = false
                }) {
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
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimeFieldDefaultPreview() {
    MyApplicationTheme {
        ReminderTimeField(
            onTimeChanged = { _, _ -> },
            reminderEditUiState = ReminderEditUiState()
        )
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun TimeFieldPortraitPreview() {
    MyApplicationTheme {
        ReminderTimeField(
            onTimeChanged = { _, _ -> },
            reminderEditUiState = ReminderEditUiState()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DateFieldDefaultPreview() {
    MyApplicationTheme {
        ReminderDateField(
            onDateChanged = { _ -> },
            reminderEditUiState = ReminderEditUiState()
        )
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun DateFieldPortraitPreview() {
    MyApplicationTheme {
        ReminderDateField(
            onDateChanged = { _ -> },
            reminderEditUiState = ReminderEditUiState()
        )
    }
}