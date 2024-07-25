package rek.remindme.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rek.remindme.R
import rek.remindme.common.Consts
import rek.remindme.ui.theme.MyApplicationTheme

@Composable
fun SimpleAlertDialog(
    isDisplayed: MutableState<Boolean>,
    textToDisplay: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (isDisplayed.value) {
        SimpleAlertDialogComponent(
            textToDisplay = textToDisplay,
            onDismiss = {
                onDismiss()
                isDisplayed.value = false
                        },
            onConfirm = {
                onConfirm()
                isDisplayed.value = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleAlertDialogComponent(
    textToDisplay: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.warning_title),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 20.sp
                )

                Text(
                    text = textToDisplay,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Row(Modifier.padding(top = 10.dp)) {
                    SimpleAlertDialogButton(
                        onClick = onDismiss,
                        textButton = R.string.cancel_button_label,
                        modifier = Modifier.testTag(Consts.TestTag.CANCEL_BUTTON).weight(1f)
                    )

                    SimpleAlertDialogButton(
                        onClick = onConfirm,
                        textButton = R.string.confirm_button_label,
                        modifier = Modifier.testTag(Consts.TestTag.CONFIRM_BUTTON).weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleAlertDialogButton(
    onClick: () -> Unit,
    @StringRes textButton: Int,
    modifier: Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().padding(8.dp)
    ) {
        Text(text = stringResource(textButton))
    }
}

@Preview(showBackground = true)
@Composable
private fun SimpleAlertDialogComponentPreview() {
    MyApplicationTheme {
        SimpleAlertDialogComponent(
            textToDisplay = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce finibus, felis nec vehicula euismod.",
            onDismiss = {},
            onConfirm = {}
        )
    }
}