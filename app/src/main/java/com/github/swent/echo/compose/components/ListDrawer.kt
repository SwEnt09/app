package com.github.swent.echo.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Event
import java.time.format.DateTimeFormatter

@Composable
fun ListDrawer(eventsList: List<Event>) {
    val selectedEvent = remember { mutableStateOf("") }
    LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White).padding(5.dp)) {
        items(eventsList) { event ->
            EventListItem(event = event, selectedEvent = selectedEvent)
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
fun EventListItem(event: Event, selectedEvent: MutableState<String>) {
    Column(
        modifier =
            Modifier.clip(RoundedCornerShape(5.dp))
                .fillMaxWidth()
                .background(
                    if (selectedEvent.value == event.eventId)
                        MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.secondaryContainer
                )
                .clickable {
                    if (selectedEvent.value == event.eventId) selectedEvent.value = ""
                    else selectedEvent.value = event.eventId
                }
    ) {
        Row(
            modifier = Modifier.height(60.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val paddingItems = 3.dp
            val widthSmallItems = 50.dp
            val widthLargeItems = 95.dp
            Text(
                // We should add a way to get the childest tag from event.tags . For now, I use
                // eventId
                text = event.eventId,
                modifier = Modifier.padding(horizontal = paddingItems).width(widthSmallItems),
                textAlign = TextAlign.Center
            )
            Text(
                text = event.title,
                modifier = Modifier.padding(horizontal = paddingItems).width(widthLargeItems),
                textAlign = TextAlign.Center
            )
            Text(
                text = event.startDate.format(DateTimeFormatter.ofPattern("E, dd/MM\nHH:mm")),
                modifier = Modifier.padding(horizontal = paddingItems).width(widthLargeItems),
                textAlign = TextAlign.Center
            )
            Text(
                // TODO: Add a way to get the distance from the event (when we'll have the user's
                // location)
                text = "5km",
                modifier = Modifier.padding(horizontal = paddingItems).width(widthSmallItems),
                textAlign = TextAlign.Center
            )
            Text(
                text = "${event.participantCount}/${event.maxParticipants}",
                modifier = Modifier.padding(horizontal = paddingItems).width(widthSmallItems),
                textAlign = TextAlign.Center
            )
        }
        if (selectedEvent.value == event.eventId) {
            Row {
                Text(
                    text = event.description,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.width(200.dp)
                )
                Spacer(modifier = Modifier.width(40.dp))
                Column {
                    val buttonWidth = 130.dp
                    Button(
                        onClick = { /*TODO*/},
                        modifier = Modifier.width(buttonWidth),
                    ) {
                        Text(stringResource(id = R.string.list_drawer_view_on_map))
                    }
                    Button(
                        onClick = { /*TODO*/},
                        modifier = Modifier.width(buttonWidth),
                    ) {
                        Text(stringResource(id = R.string.list_drawer_join_event))
                    }
                }
            }
        }
    }
}