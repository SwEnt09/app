package com.github.swent.echo.compose.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.swent.echo.R
import com.github.swent.echo.compose.map.LocationDisplayer
import com.github.swent.echo.compose.map.LocationSelector
import com.github.swent.echo.data.model.Location

/**
 * This composable contains the Location title, text field and button.
 *
 * @param location the current location
 * @param enabled the status of the button to select the location
 * @param onLocationChanged a callback called when the location is changed
 */
@Composable
fun EventLocationEntry(
    location: Location,
    enabled: Boolean,
    onLocationChanged: (Location) -> Unit
) {
    var editLocation by remember { mutableStateOf(false) }
    if (editLocation) {
        SelectLocationDialog(
            currentLocation = location,
            onDismissRequest = { editLocation = false }
        ) {
            onLocationChanged(it)
        }
    }
    Column(modifier = Modifier.fillMaxWidth(1f)) {
        EventTextEntry(
            name = stringResource(R.string.edit_event_screen_location),
            value = location.name,
        ) {
            onLocationChanged(location.copy(name = it))
        }
        Box(modifier = Modifier.aspectRatio(1.5F).padding(EVENT_PADDING_BETWEEN_INPUTS)) {
            LocationDisplayer(position = location.toLatLng())
            Box(
                modifier =
                    Modifier.matchParentSize()
                        .clickable(
                            enabled = enabled,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { editLocation = true }
                        )
                        .testTag("Location-button")
            )
        }
    }
}

/**
 * This composable is the dialog to select the location on the map.
 *
 * @param currentLocation the current location
 * @param onDismissRequest a callback called when the user dismiss the dialog
 * @param onSelectLocation a callback called when a location is selected by the user
 */
@Composable
fun SelectLocationDialog(
    currentLocation: Location,
    onDismissRequest: () -> Unit,
    onSelectLocation: (newLocation: Location) -> Unit
) {
    var point by remember { mutableStateOf(currentLocation.toLatLng()) }

    Dialog(onDismissRequest = onDismissRequest, properties = DialogProperties()) {
        Card(modifier = Modifier.fillMaxWidth().testTag("Location-dialog")) {
            Column(modifier = Modifier.padding(5.dp)) {
                Box(modifier = Modifier.aspectRatio(1.5F)) {
                    LocationSelector(initialLocation = currentLocation.toLatLng()) { point = it }
                }
                Row {
                    OutlinedButton(onClick = onDismissRequest) {
                        Text(stringResource(R.string.edit_event_screen_cancel))
                    }
                    OutlinedButton(
                        modifier = Modifier.testTag("event_location_confirm_button"),
                        onClick = {
                            val newLocation = Location(currentLocation.name, point)
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
