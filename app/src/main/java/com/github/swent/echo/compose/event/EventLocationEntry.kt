package com.github.swent.echo.compose.event

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Location

/** this composable contains the Location title, text field and button */
@Composable
fun EventLocationEntry(location: Location, onLocationChanged: (newLocation: Location) -> Unit) {
    var showPickLocationDialog by remember { mutableStateOf(false) }
    if (showPickLocationDialog) {
        SelectLocationDialog(
            currentLocation = location,
            onDismissRequest = { showPickLocationDialog = false },
            onSelectLocation = onLocationChanged
        )
    }
    Row(modifier = Modifier.fillMaxWidth(1f)) {
        EventTextEntry(
            name = stringResource(R.string.edit_event_screen_location),
            value = location.name,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            onLocationChanged(location.copy(name = it))
        }
        FilledIconButton(
            modifier =
                Modifier.padding(horizontal = 5.dp, vertical = 20.dp)
                    .align(Alignment.Bottom)
                    .testTag("Location-button"),
            onClick = { showPickLocationDialog = true }
        ) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription =
                    stringResource(R.string.edit_event_screen_select_location_button)
            )
        }
    }
}

/** This composable is the dialog to select the location on the map the map isn't implemented yet */
@Composable
fun SelectLocationDialog(
    currentLocation: Location,
    onDismissRequest: () -> Unit,
    onSelectLocation: (newLocation: Location) -> Unit
) {
    var lat by remember { mutableStateOf(currentLocation.lat.toString()) }
    var long by remember { mutableStateOf(currentLocation.long.toString()) }

    Dialog(onDismissRequest = onDismissRequest, properties = DialogProperties()) {
        Card(modifier = Modifier.fillMaxWidth().testTag("Location-dialog")) {
            Column(modifier = Modifier.padding(5.dp)) {
                EventEntryName(name = stringResource(R.string.event_location_latitude))
                TextField(
                    value = lat,
                    onValueChange = { lat = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.testTag("event_latitude_text_field")
                )
                EventEntryName(name = stringResource(R.string.event_location_longitude))
                TextField(
                    value = long,
                    onValueChange = { long = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.testTag("event_longitude_text_field")
                )
                Row {
                    OutlinedButton(onClick = onDismissRequest) {
                        Text(stringResource(R.string.edit_event_screen_cancel))
                    }
                    OutlinedButton(
                        modifier = Modifier.testTag("event_location_confirm_button"),
                        onClick = {
                            var newLocation = currentLocation
                            try {
                                newLocation =
                                    currentLocation.copy(
                                        lat = lat.toDouble(),
                                        long = long.toDouble()
                                    )
                            } catch (e: Exception) {
                                // doesn't change location
                                Log.w("edit event select location", e)
                            }
                            onSelectLocation(newLocation)
                            onDismissRequest()
                        }
                    ) {
                        Text(stringResource(R.string.edit_event_screen_confirm))
                    }
                }
            }
        }
    }
}
