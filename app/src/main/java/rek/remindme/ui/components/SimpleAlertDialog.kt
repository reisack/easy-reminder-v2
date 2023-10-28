package rek.remindme.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rek.remindme.R

@Composable
fun SimpleAlertDialog(
    isDisplayed: MutableState<Boolean>,
    textToDisplay: String,
    onConfirm: () -> Unit,
) {
    if (isDisplayed.value) {
        SimpleAlertDialogComponent(
            textToDisplay = textToDisplay,
            onDismiss = { isDisplayed.value = false },
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
    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)) {
                Text(
                    text = stringResource(R.string.warning_title),
                    modifier = Modifier.padding(8.dp), fontSize = 20.sp
                )

                Text(
                    text = textToDisplay,
                    modifier = Modifier.padding(8.dp)
                )

                Row(Modifier.padding(top = 10.dp)) {
                    Button(
                        onClick = onDismiss,
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .weight(1F)
                    ) {
                        Text(text = stringResource(R.string.cancel_button_label))
                    }

                    Button(
                        onClick = onConfirm,
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .weight(1F)
                    ) {
                        Text(text = stringResource(R.string.confirm_button_label))
                    }
                }
            }
        }
    }
}