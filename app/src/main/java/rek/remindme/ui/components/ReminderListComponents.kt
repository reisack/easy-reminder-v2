package rek.remindme.ui.components

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rek.remindme.R
import rek.remindme.ui.theme.MyApplicationTheme

@Composable
internal fun ReminderListSnackbarMessageOnLoad(
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
internal fun ReminderListSnackbarMessage(
    snackbarMessageRes: Int?,
    snackbarHostState: SnackbarHostState,
    onSnackbarMessageShow: () -> Unit,
    handleNotification: (Context) -> Unit
) {
    val context = LocalContext.current

    snackbarMessageRes?.let { messageRes ->
        val message = stringResource(id = messageRes)
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            onSnackbarMessageShow()

            if (messageRes == R.string.reminder_deleted) {
                handleNotification(context)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReminderListTopAppBar(
    alertDialogOpened: MutableState<Boolean>,
    canClearNotified: (MutableState<Boolean>) -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.reminder_list_title)) },
        actions = {
            val expanded = remember { mutableStateOf(false) }
            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                IconButton(onClick = { expanded.value = !expanded.value }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.back_desc)
                    )
                }
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false}
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.clear_notified_reminders)) },
                        onClick = {
                            canClearNotified(alertDialogOpened)
                            expanded.value = false
                        }
                    )

                    val uriHandler = LocalUriHandler.current
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.privacy)) },
                        onClick = {
                            uriHandler.openUri("https://reisack.github.io/EasyReminder/privacy.html")
                            expanded.value = false
                        }
                    )
                }
            }
        }
    )
}

@Composable
internal fun EmptyReminderList() {
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row {
            Text(
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                letterSpacing = 2.sp,
                lineHeight = 32.sp,
                text = stringResource(id = R.string.empty_reminder_list_message)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReminderListTopAppBarPreview() {
    MyApplicationTheme {
        ReminderListTopAppBar(
            alertDialogOpened = remember { mutableStateOf(false) },
            canClearNotified = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyReminderListPreview() {
    MyApplicationTheme {
        EmptyReminderList()
    }
}