package com.github.swent.echo.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.viewmodels.STATUS_THRESHOLD
import java.time.format.DateTimeFormatter

@Composable
fun ListDrawer(
    eventsList: List<Event>,
    section: String,
    semester: String,
    isOnline: Boolean,
    refreshEvents: () -> Unit
) {
    val selectedEvent = remember { mutableStateOf("") }
    // Main column where every items will be displayed, scrollable
    LazyColumn(modifier = Modifier.fillMaxSize().padding(5.dp).testTag("list_drawer")) {
        // Iterate over the list of events and display them
        items(eventsList) { event ->
            EventListItem(
                event = event,
                selectedEvent = selectedEvent,
                section,
                semester,
                isOnline,
                refreshEvents
            )
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
fun EventListItem(
    event: Event,
    selectedEvent: MutableState<String>,
    section: String,
    semester: String,
    isOnline: Boolean,
    refreshEvents: () -> Unit
) {
    // Colors for the background of the list item
    val darkFractionMiddleCircle = 0.8f
    val darkFraction2ndFloor = 0.6f
    val colorBorderCircles = MaterialTheme.colorScheme.secondaryContainer
    val colorMiddleCircle =
        colorBorderCircles.copy(
            red = colorBorderCircles.red * darkFractionMiddleCircle,
            green = colorBorderCircles.green * darkFractionMiddleCircle,
            blue = colorBorderCircles.blue * darkFractionMiddleCircle
        )
    val color2ndFloor =
        colorBorderCircles.copy(
            red = colorBorderCircles.red * darkFraction2ndFloor,
            green = colorBorderCircles.green * darkFraction2ndFloor,
            blue = colorBorderCircles.blue * darkFraction2ndFloor
        )
    val textColor = MaterialTheme.colorScheme.onSecondaryContainer
    // Main container for the list item, two floor : the first is the general information of the
    // event and
    // the second is the detailed information of the event which appears only on clicked
    Column(
        modifier =
            Modifier.clip(RoundedCornerShape(5.dp))
                .fillMaxWidth()
                // Draw the background of the list item
                .drawBehind {
                    // First floor
                    drawCircle(
                        color = colorBorderCircles,
                        radius = 130.dp.toPx(),
                        center = Offset(260.dp.toPx(), 40.dp.toPx())
                    )
                    drawCircle(
                        color = colorMiddleCircle,
                        radius = 130.dp.toPx(),
                        center = Offset(130.dp.toPx(), 40.dp.toPx())
                    )
                    drawCircle(
                        color = colorBorderCircles,
                        radius = 130.dp.toPx(),
                        center = Offset(0.dp.toPx(), 40.dp.toPx())
                    )
                    // Second floor
                    drawRect(
                        color = color2ndFloor,
                        topLeft = Offset(0.dp.toPx(), 80.dp.toPx()),
                        size = Size(400.dp.toPx(), 1000.dp.toPx())
                    )
                }
                // Handle the selection of items
                .clickable {
                    if (selectedEvent.value == event.eventId) selectedEvent.value = ""
                    else selectedEvent.value = event.eventId
                }
                // Add border only to the selected event in order to differentiate it from the
                // others
                .border(
                    if (selectedEvent.value == event.eventId) 2.dp else (-1).dp,
                    color2ndFloor,
                    RoundedCornerShape(5.dp)
                )
                .testTag("list_event_item_${event.eventId}")
    ) {
        // First floor container
        Row(
            modifier =
                Modifier.height(80.dp).fillMaxWidth().testTag("list_event_row_${event.eventId}"),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val paddingItems = 3.dp
            // Display event title
            Text(
                text = event.title,
                modifier =
                    Modifier.padding(horizontal = paddingItems)
                        .weight(1f)
                        .testTag("list_event_title_${event.eventId}"),
                textAlign = TextAlign.Center,
                color = textColor,
            )
            // Display event date
            Text(
                text = event.startDate.format(DateTimeFormatter.ofPattern("E, dd/MM\nHH:mm")),
                modifier =
                    Modifier.padding(horizontal = paddingItems)
                        .weight(1f)
                        .testTag("list_event_date_${event.eventId}"),
                textAlign = TextAlign.Center,
                color = textColor,
            )
            /*
            // Display event distance from user
            Text(
                // TODO: Add a way to get the distance from the event (when we'll have the user's
                // location)
                text = "5km",
                modifier =
                    Modifier.padding(horizontal = paddingItems)
                        .weight(1f)
                        .testTag("list_event_location_${event.eventId}"),
                textAlign = TextAlign.Center
            )
             */
            // Display event status
            Column(
                modifier = Modifier.padding(horizontal = paddingItems).weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display number of participants
                Row {
                    Icon(
                        painter =
                            painterResource(
                                // Choose the icon depending on the event status
                                if (
                                    event.participantCount <
                                        event.maxParticipants * STATUS_THRESHOLD
                                )
                                    R.drawable.pending
                                else if (event.participantCount == event.maxParticipants)
                                    R.drawable.full
                                else R.drawable.confirmed
                            ),
                        contentDescription = stringResource(id = R.string.list_drawer_event_status),
                        tint = textColor,
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "${event.participantCount}/${event.maxParticipants}",
                        modifier = Modifier.testTag("list_event_participants_${event.eventId}"),
                        textAlign = TextAlign.Center,
                        color = textColor,
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                // Display event range (epfl, section, class, ...)
                // Need to be improved to not find only "in" and "ba6" but everything (need to see
                // Noah's implementation)
                Box(
                    modifier =
                        Modifier.clip(RoundedCornerShape(25.dp))
                            .height(25.dp)
                            .width(60.dp)
                            .background(
                                if (event.tags.any { tag -> tag.name.lowercase() == "epfl" })
                                    colorEpfl
                                else if (
                                    event.tags.any { tag ->
                                        tag.name.lowercase() == section.lowercase()
                                    }
                                )
                                    colorSection
                                else if (
                                    event.tags.any { tag ->
                                        tag.name.lowercase() == semester.lowercase()
                                    }
                                )
                                    colorClass
                                else color2ndFloor
                            )
                ) {
                    Text(
                        if (event.tags.any { tag -> tag.name.lowercase() == "epfl" }) "EPFL"
                        else if (
                            event.tags.any { tag -> tag.name.lowercase() == section.lowercase() }
                        )
                            section
                        else if (
                            event.tags.any { tag -> tag.name.lowercase() == semester.lowercase() }
                        )
                            semester
                        else "other",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center),
                        color = textColor,
                    )
                }
            }
        }
        // Second floor container, only displayed when the event is selected
        if (selectedEvent.value == event.eventId) {
            Row(modifier = Modifier.testTag("list_event_details_${event.eventId}")) {
                // Display the event description
                Text(
                    text = event.description,
                    textAlign = TextAlign.Justify,
                    modifier =
                        Modifier.width(200.dp)
                            .padding(5.dp)
                            .testTag("list_event_description_${event.eventId}"),
                    color = textColor,
                )
                Spacer(modifier = Modifier.width(40.dp))
                Column {
                    val buttonWidth = 130.dp
                    // To add when the button will be implemented the next Milestone
                    /*
                    Button(
                        onClick = { },
                        modifier = Modifier.width(buttonWidth),
                    ) {
                        Text(stringResource(id = R.string.list_drawer_view_on_map))
                    }*/
                    // Join event button
                    JoinEventButton(event, isOnline, buttonWidth, refreshEvents)
                }
            }
        }
    }
}
