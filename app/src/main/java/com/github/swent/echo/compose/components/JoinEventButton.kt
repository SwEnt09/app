package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.viewmodels.myevents.MyEventStatus
import com.github.swent.echo.viewmodels.myevents.MyEventsViewModel

/**
 * Button to join or leave an event. Automatically disables the button if the user is offline or the
 * event is full. The button text and action changes depending on whether the user has joined the
 * event.
 *
 * @param event the event to join or leave
 * @param isOnline whether the user is online
 * @param buttonWidth the width of the button
 * @param refreshEvents callback to refresh the events
 */
@Composable
fun JoinEventButton(event: Event, isOnline: Boolean, buttonWidth: Dp, refreshEvents: () -> Unit) {
    // Get the ViewModel for managing the user's events.
    val myEventsViewModel: MyEventsViewModel = hiltViewModel()
    // Observe the list of events that the user has joined.
    val joinedEvents by myEventsViewModel.joinedEvents.collectAsState()

    // Snackbar for displaying errors
    val snackbarHostState by remember { mutableStateOf(SnackbarHostState()) }
    val status by myEventsViewModel.status.collectAsState()
    val errorMessage =
        LocalContext.current.resources.getString(R.string.event_join_error_network_failure)
    if (status is MyEventStatus.Error) {
        LaunchedEffect(status) {
            snackbarHostState.showSnackbar(errorMessage, withDismissAction = true)
            myEventsViewModel.resetErrorState()
        }
    }

    Box {
        // Create a button for joining or leaving the event.
        Button(
            // The button is enabled if the user is online and the event is not full.
            enabled = isOnline && (event.participantCount < event.maxParticipants),
            // When the button is clicked, the user joins or leaves the event.
            onClick = { myEventsViewModel.joinOrLeaveEvent(event, refreshEvents) },
            // Set the width of the button and a test tag for testing purposes.
            modifier =
                androidx.compose.ui.Modifier.width(buttonWidth)
                    .align(Alignment.Center)
                    .testTag("list_join_event_${event.eventId}")
        ) {
            // The text of the button depends on whether the user has joined the event.
            Text(
                if (joinedEvents.map { it.eventId }.contains(event.eventId))
                // If the user has joined the event, the button says "Leave".
                stringResource(id = R.string.list_drawer_leave_event)
                else
                // If the user has not joined the event, the button says "Join".
                stringResource(id = R.string.list_drawer_join_event)
            )
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.Center))
    }
}
