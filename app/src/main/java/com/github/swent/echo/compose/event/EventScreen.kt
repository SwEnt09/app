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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
        EventTitleAndBackButton(
            modifier = Modifier,
            title = title,
            onBackButtonPressed = { onEventBackButtonPressed() }
        )
        // all the inputs for an event
        EventPropertiesFields(modifier = Modifier, eventViewModel = eventViewModel)
        // save button
        OutlinedButton(
            modifier =
                Modifier.padding(30.dp).align(Alignment.CenterHorizontally).testTag("Save-button"),
            onClick = { onEventSaveButtonPressed() }
        ) {
            Text(stringResource(R.string.edit_event_screen_save))
        }
    }
}

/**
 * Modifiable fields of an event: title, description, tags, location, start date, end date, pictures
 */
@Composable
fun EventPropertiesFields(modifier: Modifier, eventViewModel: EventViewModel) {
    val event by eventViewModel.event.collectAsState()
    var tagText by remember { mutableStateOf("") }
    val organizerListState = eventViewModel.organizerList.collectAsState()

    Column(modifier = modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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
            modifier = modifier,
            tags = event.tags,
            tagText = tagText,
            onTagFieldChanged = {
                tagText = it
                val tag = eventViewModel.getAndAddTagFromString(tagText)
                if (tag != null) {
                    tagText = ""
                }
            },
            onTagPressed = { eventViewModel.deleteTag(it) }
        )
        var organizerName = event.creator.name
        if (event.organizer != null) {
            organizerName = event.organizer!!.name
        }
        EventDropDownSelectOrganizer(
            organizerName = organizerName,
            organizerList = organizerListState.value,
            modifier = modifier,
            onOrganizerSelected = { eventViewModel.setOrganizer(it) }
        )
        EventLocationEntry(
            modifier = modifier,
            location = event.location,
            onLocationChanged = { eventViewModel.setEvent(event.copy(location = it)) }
        )
        EventDateEntry(
            event.startDate,
            event.endDate,
            modifier,
            { eventViewModel.setEvent(event.copy(startDate = it)) },
            { eventViewModel.setEvent(event.copy(endDate = it)) }
        )
    }
}