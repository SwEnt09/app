package com.github.swent.echo.compose.event

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.viewmodels.event.EventStatus
import com.github.swent.echo.viewmodels.event.EventViewModel

val EVENT_PADDING_BETWEEN_INPUTS = 10.dp
/** This screen allows the user to create or edit an event. */
@Composable
fun EventScreen(
    title: String,
    canDelete: Boolean = false,
    onEventSaved: () -> Unit,
    onEventDeleted: () -> Unit = {},
    onEventBackButtonPressed: () -> Unit,
    eventViewModel: EventViewModel
) {
    val focusManager = LocalFocusManager.current
    val snackBarHostState by remember { mutableStateOf(SnackbarHostState()) }
    val eventStatus by eventViewModel.status.collectAsState()

    var saveButtonText by remember { mutableIntStateOf(R.string.edit_event_screen_save) }
    var saveButtonClicked by remember { mutableStateOf(false) }
    val localContext = LocalContext.current

    if (saveButtonClicked && eventStatus is EventStatus.Saved) {
        saveButtonClicked = false
        saveButtonText = R.string.edit_event_screen_save
        onEventSaved()
    } else if (eventStatus is EventStatus.Error) {
        LaunchedEffect(eventStatus) {
            val errorMessage =
                localContext.resources.getString((eventStatus as EventStatus.Error).errorRef)
            snackBarHostState.showSnackbar("Error : $errorMessage")
            eventViewModel.dismissError()
        }
    } else if (eventStatus is EventStatus.Saving) {
        saveButtonText = R.string.edit_event_screen_saving
    } else {
        saveButtonText = R.string.edit_event_screen_save
    }

    Scaffold(
        topBar = {
            EventTitleAndBackButton(title = title, onBackButtonPressed = onEventBackButtonPressed)
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState, modifier = Modifier.testTag("snackbar"))
        }
    ) { padding ->
        Column(
            modifier =
                Modifier.padding(padding)
                    .verticalScroll(state = rememberScrollState())
                    .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
        ) {
            // all the inputs for an event
            EventPropertiesFields(eventViewModel = eventViewModel)
            // save button
            Row(
                modifier = Modifier.fillMaxWidth().padding(EVENT_PADDING_BETWEEN_INPUTS),
                horizontalArrangement = Arrangement.End
            ) {
                if (canDelete) {
                    DeleteEventButton {
                        // TODO: delete the event in the repository
                        onEventDeleted()
                    }
                }
                OutlinedButton(
                    modifier = Modifier.padding(10.dp).testTag("Save-button"),
                    onClick = {
                        focusManager.clearFocus()
                        eventViewModel.saveEvent()
                        saveButtonClicked = true
                    }
                ) {
                    Text(stringResource(saveButtonText))
                }
            }
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
        EventMaxNumberOfParticipantsEntry(event.maxParticipants) { newMax ->
            eventViewModel.setEvent(event.copy(maxParticipants = newMax))
        }
    }
}
