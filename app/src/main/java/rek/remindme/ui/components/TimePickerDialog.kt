// Adapted from https://issuetracker.google.com/issues/288311426
// There is a DatePickerDialog available on Material 3.
// But no TimePickerDialog yet, weird...

package rek.remindme.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import rek.remindme.R
import rek.remindme.common.Consts
import rek.remindme.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onCancel: () -> Unit,
    onConfirm: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    initialHour: Int? = null,
    initialMinute: Int? = null
) {
    var mode: DisplayMode by remember { mutableStateOf(DisplayMode.Picker) }
    val state: TimePickerState = rememberTimePickerState(
        initialHour = initialHour ?: 0,
        initialMinute = initialMinute ?: 0,
    )

    fun onConfirmClicked() {
        onConfirm(state.hour, state.minute)
    }

    // TimePicker does not provide a default TimePickerDialog, so we use our own PickerDialog:
    // https://issuetracker.google.com/issues/288311426
    PickerDialog(
        modifier = modifier.verticalScroll(rememberScrollState()),
        onDismissRequest = onCancel,
        title = { Text(text = stringResource(R.string.select_time_label)) },
        buttons = {
            DisplayModeToggleButton(
                displayMode = mode,
                onDisplayModeChange = { mode = it },
            )
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.cancel_button_label))
            }
            TextButton(
                modifier = Modifier.testTag(Consts.TestTag.CONFIRM_BUTTON),
                onClick = ::onConfirmClicked
            ) {
                Text(stringResource(R.string.confirm_button_label))
            }
        },
    ) {
        val contentModifier = Modifier.padding(horizontal = 24.dp)
        when (mode) {
            DisplayMode.Picker -> TimePicker(modifier = contentModifier, state = state)
            DisplayMode.Input -> TimeInput(modifier = contentModifier, state = state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DisplayModeToggleButton(
    displayMode: DisplayMode,
    onDisplayModeChange: (DisplayMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (displayMode) {
        DisplayMode.Picker -> IconButton(
            modifier = modifier.testTag(Consts.TestTag.SELECT_INPUT_MODE),
            onClick = { onDisplayModeChange(DisplayMode.Input) },
        ) {
            Icon(
                imageVector = Icons.Filled.Create,
                contentDescription = stringResource(R.string.select_input_mode_desc),
            )
        }
        DisplayMode.Input -> IconButton(
            modifier = modifier,
            onClick = { onDisplayModeChange(DisplayMode.Picker) },
        ) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = stringResource(R.string.select_picker_mode_desc),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PickerDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    buttons: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    AlertDialog(
        modifier = modifier
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min),
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Title
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                    ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(horizontal = 24.dp)
                                .padding(top = 16.dp, bottom = 20.dp),
                        ) {
                            title()
                        }
                    }
                }
                // Content
                CompositionLocalProvider(LocalContentColor provides AlertDialogDefaults.textContentColor) {
                    content()
                }
                // Buttons
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
                    ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                        // TODO This should wrap on small screens, but we can't use AlertDialogFlowRow as it is no public
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp, end = 6.dp, start = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        ) {
                            buttons()
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimePickerDialogPreview() {
    MyApplicationTheme {
        TimePickerDialog(
            onCancel = {},
            onConfirm = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun TimePickerDialogDarkPreview() {
    MyApplicationTheme {
        TimePickerDialog(
            onCancel = {},
            onConfirm = { _, _ -> }
        )
    }
}