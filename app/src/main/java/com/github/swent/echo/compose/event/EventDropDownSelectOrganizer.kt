package com.github.swent.echo.compose.event

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

/**
 * Organizer button and dropdown list of organizers
 *
 * @param organizerName the name of the current organizer
 * @param organizerList the list of organizers
 * @param enabled the status of the button
 * @param onOrganizerSelected callback called when an organizer is chosen in the list
 */
@Composable
fun EventDropDownSelectOrganizer(
    organizerName: String,
    organizerList: List<String>,
    enabled: Boolean,
    onOrganizerSelected: (organizer: String) -> Unit
) {
    var listDisplayed by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(EVENT_PADDING_BETWEEN_INPUTS).fillMaxWidth()) {
        EventEntryName(name = stringResource(R.string.edit_event_screen_organizer))
        TextButton(
            enabled = enabled,
            onClick = { listDisplayed = true },
            modifier = Modifier,
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
                        if (enabled) {
                            onOrganizerSelected(it)
                        }
                        listDisplayed = false
                    }
                )
            }
        }
    }
}
