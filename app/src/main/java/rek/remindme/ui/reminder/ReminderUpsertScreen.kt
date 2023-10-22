package rek.remindme.ui.reminder

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rek.remindme.R
import rek.remindme.ui.components.ReminderDateField
import rek.remindme.ui.components.ReminderTimeField
import rek.remindme.ui.theme.MyApplicationTheme

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReminderUpsertScreen(
    modifier: Modifier = Modifier,
    onReminderSaved: () -> Unit,
    onReminderDeleted: () -> Unit,
    onBack: () -> Unit,
    viewModel: ReminderUpsertViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
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
                        IconButton(onClick = viewModel::delete) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.delete_reminder_desc)
                            )
                        }
                    }
                }
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

            LaunchedEffect(uiState.isSaved) {
                if (uiState.isSaved) {
                    onReminderSaved()
                }
            }

            LaunchedEffect(uiState.isDeleted) {
                if (uiState.isDeleted) {
                    onReminderDeleted()
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
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                placeholder = {
                    Text(text = stringResource(R.string.title_field_title))
                },
                value = reminderEditUiState.title,
                onValueChange = onTitleChanged
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                placeholder = {
                    Text(text = stringResource(R.string.description_field_title))
                },
                value = reminderEditUiState.description,
                onValueChange = onDescriptionChanged
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReminderDateField(
                onDateChanged = onDateChanged,
                reminderEditUiState = reminderEditUiState
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReminderTimeField(
                onTimeChanged = onTimeChanged,
                reminderEditUiState = reminderEditUiState
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(modifier = Modifier.width(96.dp), onClick = onSave) {
                Text(text = stringResource(R.string.save_button_label))
            }
        }
    }
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