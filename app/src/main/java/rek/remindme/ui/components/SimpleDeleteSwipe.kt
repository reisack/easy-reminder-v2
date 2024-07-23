package rek.remindme.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import rek.remindme.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SimpleDeleteSwipe(
    onConfirm: () -> Unit,
    content: @Composable (RowScope.() -> Unit)
) {
    val isSwiped = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val dismissState = swipeDismissState(isSwiped)
    val swipeBackgroundColor = swipeBackgroundColor(dismissState)

    SimpleAlertDialog(
        isDisplayed = isSwiped,
        textToDisplay = stringResource(R.string.confirm_delete_reminder),
        onDismiss = { scope.launch { dismissState.reset() } },
        onConfirm = {
            scope.launch { dismissState.reset() }
            onConfirm()
        }
    )

    setOf(SwipeToDismissBoxValue.EndToStart,
        SwipeToDismissBoxValue.StartToEnd
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp)
                    .background(swipeBackgroundColor)
            ) {
                SwipeIcon(
                    modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    )
                )

                Spacer(Modifier.weight(1f))

                SwipeIcon(
                    modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    )
                )
            }
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        content = content
    )
}

@Composable
private fun SwipeIcon(modifier: Modifier) {
    Icon(
        modifier = modifier.padding(8.dp),
        imageVector = Icons.Filled.Delete,
        tint = MaterialTheme.colorScheme.onPrimary,
        contentDescription = stringResource(id = R.string.delete_reminder_desc)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun swipeDismissState(isSwiped: MutableState<Boolean>): SwipeToDismissBoxState {
    return rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.StartToEnd || it == SwipeToDismissBoxValue.EndToStart) {
                isSwiped.value = true
                true
            }
            else false
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun swipeBackgroundColor(dismissState: SwipeToDismissBoxState): Color {
    return if (dismissState.dismissDirection == null) {
        Color.Transparent
    } else {
        MaterialTheme.colorScheme.primary
    }
}