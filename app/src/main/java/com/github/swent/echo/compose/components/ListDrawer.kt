package com.github.swent.echo.compose.components

import android.widget.Toast
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
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.data.SAMPLE_EVENTS
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.viewmodels.STATUS_THRESHOLD
import java.time.format.DateTimeFormatter

@Composable
fun ListDrawer(eventsList: List<Event>) {
    val selectedEvent = remember { mutableStateOf("") }
    // Main column where every items will be displayed, scrollable
    LazyColumn(
        modifier =
            Modifier.fillMaxSize().background(Color.White).padding(5.dp).testTag("list_drawer")
    ) {
        // Iterate over the list of events and display them
        items(eventsList) { event ->
            EventListItem(event = event, selectedEvent = selectedEvent, onJoinButtonPressed = {})
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
fun EventListItem(
    event: Event,
    selectedEvent: MutableState<String>,
    onJoinButtonPressed: () -> Unit
) {
    // Colors for the background of the list item
    val fraction1 = 0.8f
    val fraction2 = 0.6f
    val listColor1 = MaterialTheme.colorScheme.primaryContainer
    val listColor2 =
        listColor1.copy(
            red = listColor1.red * fraction1,
            green = listColor1.green * fraction1,
            blue = listColor1.blue * fraction1
        )
    val listColor3 =
        listColor1.copy(
            alpha = 0.8f,
            red = listColor1.red * fraction2,
            green = listColor1.green * fraction2,
            blue = listColor1.blue * fraction2
        )
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
                        color = listColor1,
                        radius = 130.dp.toPx(),
                        center = Offset(260.dp.toPx(), 40.dp.toPx())
                    )
                    drawCircle(
                        color = listColor2,
                        radius = 130.dp.toPx(),
                        center = Offset(130.dp.toPx(), 40.dp.toPx())
                    )
                    drawCircle(
                        color = listColor1,
                        radius = 130.dp.toPx(),
                        center = Offset(0.dp.toPx(), 40.dp.toPx())
                    )
                    // Second floor
                    drawRect(
                        color = listColor3,
                        topLeft = Offset(0.dp.toPx(), 80.dp.toPx()),
                        size = Size(400.dp.toPx(), 80.dp.toPx())
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
                    listColor3,
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
                textAlign = TextAlign.Center
            )
            // Display event date
            Text(
                text = event.startDate.format(DateTimeFormatter.ofPattern("E, dd/MM\nHH:mm")),
                modifier =
                    Modifier.padding(horizontal = paddingItems)
                        .weight(1f)
                        .testTag("list_event_date_${event.eventId}"),
                textAlign = TextAlign.Center
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
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "${event.participantCount}/${event.maxParticipants}",
                        modifier = Modifier.testTag("list_event_participants_${event.eventId}"),
                        textAlign = TextAlign.Center
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
                                    listColor3.copy(alpha = 1f, red = 0.8f)
                                else if (event.tags.any { tag -> tag.name.lowercase() == "in" })
                                    listColor3.copy(alpha = 1f, blue = 0.8f)
                                else if (event.tags.any { tag -> tag.name.lowercase() == "ba6" })
                                    listColor3.copy(alpha = 1f, green = 0.8f)
                                else listColor3
                            )
                ) {
                    Text(
                        if (event.tags.any { tag -> tag.name.lowercase() == "epfl" }) "epfl"
                        else if (event.tags.any { tag -> tag.name.lowercase() == "in" }) "in"
                        else if (event.tags.any { tag -> tag.name.lowercase() == "ba6" }) "ba6"
                        else "other",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
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
                            .testTag("list_event_description_${event.eventId}")
                )
                Spacer(modifier = Modifier.width(40.dp))
                Column {
                    val buttonWidth = 130.dp
                    // get the context
                    val context = LocalContext.current
                    // To add when the button will be implemented the next Milestone
                    /*
                    Button(
                        onClick = { },
                        modifier = Modifier.width(buttonWidth),
                    ) {
                        Text(stringResource(id = R.string.list_drawer_view_on_map))
                    }*/
                    // Join event button
                    Button(
                        onClick = {
                            onJoinButtonPressed()
                            Toast.makeText(
                                    context,
                                    context.getString(R.string.event_successfully_joined),
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        },
                        modifier = Modifier.width(buttonWidth),
                    ) {
                        Text(stringResource(id = R.string.list_drawer_join_event))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ListDrawerPreview() {
    ListDrawer(SAMPLE_EVENTS)
}
