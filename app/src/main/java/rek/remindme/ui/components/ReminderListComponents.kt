package rek.remindme.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
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
internal fun NotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val context = LocalContext.current

        val hasPermission =
            ContextCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

        val hasNotificationPermission = remember { mutableStateOf(hasPermission) }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted -> hasNotificationPermission.value = isGranted }
        )

        if (!hasNotificationPermission.value) {
            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .zIndex(10f)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Row {
                    Text(
                        textAlign = TextAlign.Center,
                        text = stringResource(id = R.string.message_authorize_notifications_1)
                    )
                }
                Row {
                    Text(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        text = stringResource(id = R.string.message_authorize_notifications_2)
                    )
                }
                Row {
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }) {
                        Text(text = stringResource(id = R.string.button_authorize_notifications))
                    }
                }
            }
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