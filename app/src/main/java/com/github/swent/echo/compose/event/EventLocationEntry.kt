package com.github.swent.echo.compose.event

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Location

/** this composable contains the Location title, text field and button */
@Composable
fun EventLocationEntry(
    modifier: Modifier,
    location: Location,
    onLocationChanged: (newLocation: Location) -> Unit
) {
    var showPickLocationDialog by remember { mutableStateOf(false) }
    if (showPickLocationDialog) {
        SelectLocationDialog(
            currentLocation = location,
            onDismissRequest = { showPickLocationDialog = false },
            onSelectLocation = { onLocationChanged(it) }
        )
    }
    Row(modifier = modifier.fillMaxWidth(1f)) {
        EventTextEntry(
            name = stringResource(R.string.edit_event_screen_location),
            value = location.name,
            modifier = modifier.fillMaxWidth(0.8f)
        ) {
            onLocationChanged(location.copy(name = it))
        }
        FilledIconButton(
            modifier =
                modifier
                    .padding(horizontal = 5.dp, vertical = 20.dp)
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

    Dialog(onDismissRequest = { onDismissRequest() }, properties = DialogProperties()) {
        Card(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f).testTag("Location-dialog")) {
            Text("map implementation placeholder")
            // TODO: implement map to choose location
        }
    }
}
