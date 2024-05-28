package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
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
    val myEventsViewModel: MyEventsViewModel = hiltViewModel()
    val joinedEvents by myEventsViewModel.joinedEvents.collectAsState()
    Button(
        enabled = isOnline && (event.participantCount < event.maxParticipants),
        onClick = { myEventsViewModel.joinOrLeaveEvent(event, refreshEvents) },
        modifier =
            androidx.compose.ui.Modifier.width(buttonWidth)
                .testTag("list_join_event_${event.eventId}")
    ) {
        Text(
            if (joinedEvents.map { it.eventId }.contains(event.eventId))
                stringResource(id = R.string.list_drawer_leave_event)
            else stringResource(id = R.string.list_drawer_join_event)
        )
    }
}
