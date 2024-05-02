package com.github.swent.echo.compose.event

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R

/** a button to delete an event */
@Composable
fun DeleteEventButton(onDelete: () -> Unit) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    OutlinedButton(
        modifier = Modifier.padding(10.dp).testTag("delete-button"),
        onClick = { showConfirmDialog = true },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
    ) {
        Text(
            stringResource(R.string.edit_event_screen_delete_button),
            color = MaterialTheme.colorScheme.error
        )
    }
    if (showConfirmDialog) {
        ConfirmActionDialog(
            text = stringResource(R.string.edit_event_screen_alert_deletion_message),
            onDismissRequest = { showConfirmDialog = false },
            onConfirm = {
                showConfirmDialog = false
                onDelete()
            }
        )
    }
}

/** a confirmation dialog for the delete button */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmActionDialog(text: String, onDismissRequest: () -> Unit, onConfirm: () -> Unit) {
    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.headlineSmall
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = onDismissRequest,
                        modifier = Modifier.padding(5.dp).testTag("delete-cancel")
                    ) {
                        Text(stringResource(R.string.edit_event_screen_cancel))
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.padding(5.dp).testTag("delete-confirm")
                    ) {
                        Text(text = stringResource(R.string.edit_event_screen_confirm))
                    }
                }
            }
        }
    }
}
