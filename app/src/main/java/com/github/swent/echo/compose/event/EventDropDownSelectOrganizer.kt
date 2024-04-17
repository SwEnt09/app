package com.github.swent.echo.compose.event

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.swent.echo.R

/** current organizer button and dropdown list of organizers */
@Composable
fun EventDropDownSelectOrganizer(
    organizerName: String,
    organizerList: List<String>,
    modifier: Modifier = Modifier,
    onOrganizerSelected: (organizer: String) -> Unit
) {
    if (organizerList.size < 1) {
        Log.e("Event Organizer", "the organizer list is too small: < 1")
    }
    var listDisplayed by remember { mutableStateOf(false) }
    Column(modifier = modifier.padding(EVENT_PADDING_BETWEEN_INPUTS).fillMaxWidth()) {
        EventEntryName(
            name = stringResource(R.string.edit_event_screen_organizer),
            modifier = modifier
        )
        TextButton(
            onClick = { listDisplayed = true },
            modifier = modifier,
        ) {
            Text(text = organizerName, style = MaterialTheme.typography.bodyLarge)
            val icon = Icons.Filled.KeyboardArrowDown
            Icon(imageVector = icon, contentDescription = icon.name)
        }
        DropdownMenu(expanded = listDisplayed, onDismissRequest = { listDisplayed = false }) {
            organizerList.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onOrganizerSelected(it)
                        listDisplayed = false
                    }
                )
            }
        }
    }
}
