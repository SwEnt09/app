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
    val eventState = eventViewModel.getEvent().collectAsState()
    val event = eventState.value
    var title by remember { mutableStateOf(event.title) }
    var description by remember { mutableStateOf(event.description) }
    var tags by remember { mutableStateOf(event.tags) }
    var tagText by remember { mutableStateOf("") }
    var location by remember { mutableStateOf(event.location) }
    var startDate by remember { mutableStateOf(event.startDate) }
    var endDate by remember { mutableStateOf(event.endDate) }
    var organizer by remember { mutableStateOf(event.organizerName) }
    val organizerList by remember {
        mutableStateOf(eventViewModel.getOrganizerList().value)
    } // avoid recomputation

    Column(modifier = modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        EventTextEntry(name = stringResource(R.string.edit_event_screen_title), value = title) {
            title = it
            eventViewModel.setEvent(event.copy(title = title))
        }
        EventTextEntry(
            name = stringResource(R.string.edit_event_screen_description),
            value = description
        ) {
            description = it
            eventViewModel.setEvent(event.copy(description = description))
        }

        EventTagEntry(
            modifier = modifier,
            tags = tags,
            tagText = tagText,
            onTagFieldChanged = {
                tagText = it
                val tag = eventViewModel.getAndAddTagFromString(tagText)
                if (tag != null) {
                    tags += tag
                    tagText = ""
                }
            },
            onTagPressed = {
                tags = tags.filter { t -> t != it }.toSet()
                eventViewModel.deleteTag(it)
            }
        )

        EventDropDownSelectOrganizer(
            organizerName = organizer,
            organizerList = organizerList,
            modifier = modifier,
            onOrganizerSelected = {
                organizer = it
                eventViewModel.setOrganizer(it)
            }
        )
        EventLocationEntry(
            modifier = modifier,
            location = location,
            onLocationChanged = {
                location = it
                eventViewModel.setEvent(event.copy(location = location))
            }
        )
        EventDateEntry(startDate, endDate, modifier, { startDate = it }, { endDate = it })
    }
}
