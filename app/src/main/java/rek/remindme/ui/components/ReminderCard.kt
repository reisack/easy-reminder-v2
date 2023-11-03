package rek.remindme.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import rek.remindme.common.DateTimeHelper
import rek.remindme.data.local.database.Reminder
import rek.remindme.ui.reminder.ReminderListHelper
import rek.remindme.ui.theme.MyApplicationTheme

@Composable
internal fun ReminderCard(
    reminder: Reminder,
    onReminderClick: (Int) -> Unit
) {
    if (!reminder.notified) {
        ReminderCardComponent(
            reminder = reminder,
            onReminderClick = onReminderClick
        )
    }
    else {
        NotifiedReminderCardComponent(
            reminder = reminder,
            onReminderClick = onReminderClick
        )
    }
}

@Composable
private fun ReminderCardComponent(
    reminder: Reminder,
    onReminderClick: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onReminderClick(reminder.uid) }
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = DateTimeHelper.instance.getReadableDate(reminder.unixTimestamp),
                    modifier = Modifier.weight(1f)
                )
                Text(text = DateTimeHelper.instance.getReadableTime(reminder.unixTimestamp))
            }
            Row {
                Text(
                    text = DateTimeHelper.instance.getRemainingOrPastTime(reminder.unixTimestamp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row { Text(text = reminder.title, fontWeight = FontWeight.Bold) }
            Row { Text(text = ReminderListHelper.formatDescription(reminder.description)) }
        }
    }
}

@Composable
private fun NotifiedReminderCardComponent(
    reminder: Reminder,
    onReminderClick: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Color.Gray),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onReminderClick(reminder.uid) }
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = DateTimeHelper.instance.getReadableDate(reminder.unixTimestamp),
                    modifier = Modifier.weight(1f),
                    fontStyle = FontStyle.Italic
                )
                Text(
                    text = DateTimeHelper.instance.getReadableTime(reminder.unixTimestamp),
                    fontStyle = FontStyle.Italic
                )
            }
            Row {
                Text(
                    text = DateTimeHelper.instance.getRemainingOrPastTime(reminder.unixTimestamp),
                    fontStyle = FontStyle.Italic
                )
            }
            Row {
                Text(
                    text = reminder.title,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
            }
            Row {
                Text(
                    text = ReminderListHelper.formatDescription(reminder.description),
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FutureReminderCardPreview() {
    FutureReminderPreview()
}

@Preview(showBackground = true)
@Composable
private fun NotifiedReminderCardPreview() {
    NotifiedReminderPreview()
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun FutureReminderCardDarkPreview() {
    FutureReminderPreview()
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun NotifiedReminderCardDarkPreview() {
    NotifiedReminderPreview()
}

@Composable
private fun FutureReminderPreview() {
    val reminder = Reminder(1, "Title", "Description", 1699043110000, false)

    MyApplicationTheme {
        ReminderCard(
            reminder = reminder,
            onReminderClick = { _ -> }
        )
    }
}

@Composable
private fun NotifiedReminderPreview() {
    val reminder = Reminder(1, "Title", "Description", 1699043110000, true)

    MyApplicationTheme {
        ReminderCard(
            reminder = reminder,
            onReminderClick = { _ -> }
        )
    }
}