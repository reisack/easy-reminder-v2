package rek.remindme.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
            NotificationPermissionComponent(permissionLauncher = permissionLauncher)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun NotificationPermissionComponent(
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
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
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Button(onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }) {
                Text(text = stringResource(id = R.string.button_authorize_notifications))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
private fun NotificationPermissionComponentPreview() {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { _ -> }
    )

    MyApplicationTheme {
        NotificationPermissionComponent(permissionLauncher = permissionLauncher)
    }
}