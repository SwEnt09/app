package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.viewmodels.myevents.MyEventsViewModel

@Composable
fun JoinEventButton(event: Event, isOnline: Boolean, buttonWidth: Dp) {
    val context = LocalContext.current
    val myEventsViewModel: MyEventsViewModel = hiltViewModel()
    val joinedEvents by myEventsViewModel.joinedEvents.collectAsState()
    Button(
        enabled = isOnline,
        onClick = { myEventsViewModel.joinOrLeaveEvent(event) },
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
