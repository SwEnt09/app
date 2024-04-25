package com.github.swent.echo.compose.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.viewmodels.event.EventViewModel

val EVENT_PADDING_BETWEEN_INPUTS = 10.dp
/** This screen allows the user to create or edit an event. */
@Composable
fun EventScreen(
    title: String,
    onEventSaveButtonPressed: () -> Unit,
    onEventBackButtonPressed: () -> Unit,
    eventViewModel: EventViewModel
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        EventTitleAndBackButton(title = title, onBackButtonPressed = onEventBackButtonPressed)
        // all the inputs for an event
        EventPropertiesFields(eventViewModel = eventViewModel)
        // save button
        OutlinedButton(
            modifier =
                Modifier.padding(30.dp).align(Alignment.CenterHorizontally).testTag("Save-button"),
            onClick = onEventSaveButtonPressed
        ) {
            Text(stringResource(R.string.edit_event_screen_save))
        }
    }
}

/**
 * Modifiable fields of an event: title, description, tags, location, start date, end date, pictures
 */
@Composable
fun EventPropertiesFields(eventViewModel: EventViewModel) {
    val event by eventViewModel.event.collectAsState()
    val organizerListState = eventViewModel.organizerList.collectAsState()

    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        EventTextEntry(
            name = stringResource(R.string.edit_event_screen_title),
            value = event.title
        ) {
            eventViewModel.setEvent(event.copy(title = it))
        }
        EventTextEntry(
            name = stringResource(R.string.edit_event_screen_description),
            value = event.description
        ) {
            eventViewModel.setEvent(event.copy(description = it))
        }

        EventTagEntry(
            tags = event.tags,
            onTagSelected = { addedTag ->
                eventViewModel.setEvent(event.copy(tags = event.tags + addedTag))
            },
            onTagDeleted = { deletedTag ->
                eventViewModel.setEvent(
                    event.copy(tags = event.tags.filter { t -> t != deletedTag }.toSet())
                )
            }
        )
        var organizerName = event.creator.name
        if (event.organizer != null) {
            organizerName = event.organizer!!.name
        }
        EventDropDownSelectOrganizer(
            organizerName = organizerName,
            organizerList = organizerListState.value
        ) {
            eventViewModel.setOrganizer(it)
        }
        EventLocationEntry(location = event.location) {
            eventViewModel.setEvent(event.copy(location = it))
        }
        EventDateEntry(
            event.startDate,
            event.endDate,
            { eventViewModel.setEvent(event.copy(startDate = it)) }
        ) {
            eventViewModel.setEvent(event.copy(endDate = it))
        }
    }
}
