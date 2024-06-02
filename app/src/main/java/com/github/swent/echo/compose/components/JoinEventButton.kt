package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Event
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
    // Padding inside button
    val paddingButton = PaddingValues(2.dp)
    // Create a button for joining or leaving the event.
    Button(
        // The button is enabled if the user is online and the event is not full.
        enabled = isOnline && (event.participantCount < event.maxParticipants),
        // When the button is clicked, the user joins or leaves the event.
        onClick = { myEventsViewModel.joinOrLeaveEvent(event, refreshEvents) },
        // Set the width of the button and a test tag for testing purposes.
        modifier = Modifier.width(buttonWidth).testTag("list_join_event_${event.eventId}"),
        contentPadding = paddingButton
    ) {
        // The text of the button depends on whether the user has joined the event.
        Text(
            if (joinedEvents.map { it.eventId }.contains(event.eventId))
            // If the user has joined the event, the button says "Leave".
            stringResource(id = R.string.list_drawer_leave_event)
            else
            // If the user has not joined the event, the button says "Join".
            stringResource(id = R.string.list_drawer_join_event),
            textAlign = TextAlign.Center
        )
    }
}
