package rek.remindme.ui.components

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import rek.remindme.R
import rek.remindme.ui.theme.MyApplicationTheme

@Composable
internal fun ClickableInputField(
    modifier: Modifier = Modifier,
    dialogOpened: MutableState<Boolean>,
    @StringRes placeholderRes: Int,
    getValue: () -> String
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .clickable { dialogOpened.value = true },
        placeholder = { Text(text = "${stringResource(placeholderRes)} *") },
        value = getValue(),
        onValueChange = {},
        enabled = false,
        colors = TextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ClickableInputFieldPreview() {
    MyApplicationTheme {
        ClickableInputField(
            dialogOpened = remember { mutableStateOf(false) },
            placeholderRes = R.string.select_time_label,
            getValue = { "" }
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ClickableInputFieldDarkPreview() {
    MyApplicationTheme {
        ClickableInputField(
            dialogOpened = remember { mutableStateOf(false) },
            placeholderRes = R.string.select_time_label,
            getValue = { "" }
        )
    }
}