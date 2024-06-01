package com.github.swent.echo.compose.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import java.time.format.DateTimeFormatter

/**
 * Composable to display a list of events.
 *
 * @param eventsList The list of events to display.
 * @param isOnline A boolean indicating whether the user is online.
 * @param refreshEvents The callback to refresh the events.
 * @param viewOnMap The callback to view the event on the map. If null, the view on map button will
 *   not be displayed.
 * @param modify The callback to modify the event.
 * @param onTagPressed The callback to handle tag presses.
 * @param distances The list of distances to the events. If null, the distance will not be
 *   displayed.
 * @param userId The user ID. If null, none of the events can be modified.
 */
@Composable
fun ListDrawer(
    eventsList: List<Event>,
    isOnline: Boolean,
    refreshEvents: () -> Unit,
    viewOnMap: ((Event) -> Unit)? = null,
    modify: ((Event) -> Unit) = {},
    onTagPressed: (Tag) -> Unit = {},
    distances: List<Double>? = null,
    userId: String? = null,
) {
    val selectedEvent = remember { mutableStateOf("") }
    // Main column where every items will be displayed, scrollable
    LazyColumn(
        modifier =
            Modifier.fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("list_drawer")
    ) {
        // Iterate over the list of events and display them
        items(eventsList.size) { index ->
            val event = eventsList[index]
            val canModifyEvent = event.creator.userId == userId

            EventListItem(
                event = event,
                selectedEvent = selectedEvent,
                isOnline = isOnline,
                refreshEvents = refreshEvents,
                viewOnMap = viewOnMap,
                modify = modify,
                onTagPressed = onTagPressed,
                canModifyEvent = canModifyEvent,
                distance = distances?.get(index),
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

/**
 * Composable to display a single event in the list.
 *
 * @param event The event to display.
 * @param selectedEvent The ID of the selected event.
 * @param isOnline A boolean indicating whether the user is online.
 * @param refreshEvents The callback to refresh the events.
 * @param viewOnMap The callback to view the event on the map. If null, the view on map button will
 *   not be displayed.
 * @param modify The callback to modify the event.
 * @param onTagPressed The callback to handle tag presses.
 * @param canModifyEvent A boolean indicating whether the user can modify the event.
 * @param distance The distance to the event. If null, the distance will not be displayed.
 */
@Composable
fun EventListItem(
    event: Event,
    selectedEvent: MutableState<String>,
    isOnline: Boolean,
    refreshEvents: () -> Unit,
    viewOnMap: ((Event) -> Unit)?,
    modify: ((Event) -> Unit),
    onTagPressed: (Tag) -> Unit,
    canModifyEvent: Boolean,
    distance: Double?,
) {
    // Format the date to be displayed
    val date = event.startDate.format(DateTimeFormatter.ofPattern("E, dd.MM.yyyy HH:mm"))
    // Format the association name to be displayed
    val association = event.organizer?.name?.let { "$it • " } ?: ""
    // Format the distance to be displayed
    val dist = distance?.let { "${it}km • " } ?: ""

    // Layout constants
    val spaceBetweenTagChips = 6.dp
    val spaceBetweenElements = 12.dp

    // Colors
    val onSurfaceFaded = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

    // Display the event in a card
    Card(
        modifier =
            Modifier.clickable {
                    // If the event is already selected, deselect it, otherwise select it
                    if (selectedEvent.value == event.eventId) selectedEvent.value = ""
                    else selectedEvent.value = event.eventId
                }
                .testTag("list_event_item_${event.eventId}")
                .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fist floor of the card, with the title, date, association and number of participants
            Row(
                modifier = Modifier.fillMaxWidth().testTag("list_event_row_${event.eventId}"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    // Title
                    Text(
                        text = event.title,
                        modifier =
                            Modifier.fillMaxWidth(0.8f)
                                .testTag("list_event_title_${event.eventId}"),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Date, association and number of participants
                    Text(
                        text = "$association$dist$date",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.testTag("list_event_date_${event.eventId}"),
                    )
                }
                // Number of participants
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painterResource(R.drawable.full),
                        contentDescription = "Number of participants",
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${event.participantCount} / ${event.maxParticipants}",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.testTag("list_event_participants_${event.eventId}")
                    )
                }
            }
            // Second floor of the card, with the tags, description and buttons. Appears only if
            // the event is selected
            if (selectedEvent.value == event.eventId) {
                // Tags corresponding to the event
                val tags = event.tags.toList()
                val iconButtonColors =
                    IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    )

                Spacer(modifier = Modifier.height(spaceBetweenElements))
                HorizontalDivider(thickness = 2.dp, color = onSurfaceFaded)
                Spacer(modifier = Modifier.height(spaceBetweenElements))
                // Display the related tags in a horizontal scrollable row
                LazyRow(
                    modifier =
                        Modifier.fillMaxWidth().testTag("list_event_details_${event.eventId}"),
                    horizontalArrangement = Arrangement.spacedBy(spaceBetweenTagChips)
                ) {
                    items(tags) { tag ->
                        // Each tag is displayed as a chip
                        AssistChip(
                            onClick = { onTagPressed(tag) },
                            label = { Text(tag.name) },
                            border =
                                AssistChipDefaults.assistChipBorder(
                                    enabled = true,
                                    borderColor = MaterialTheme.colorScheme.primary,
                                    borderWidth = 1.dp,
                                ),
                            colors =
                                AssistChipDefaults.assistChipColors(
                                    labelColor = MaterialTheme.colorScheme.primary,
                                ),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(spaceBetweenElements))
                // Description of the event
                Text(
                    event.description,
                    modifier = Modifier.testTag("list_event_description_${event.eventId}")
                )
                Spacer(modifier = Modifier.height(spaceBetweenElements))
                // Buttons to view the event on the map and to modify the event
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row {
                        // View on map button
                        viewOnMap?.let {
                            IconButton(
                                onClick = { it(event) },
                                modifier = Modifier.testTag("view_on_map_${event.eventId}"),
                                colors = iconButtonColors,
                            ) {
                                Icon(
                                    painterResource(R.drawable.map_icon),
                                    contentDescription = "View on map",
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        // Modify button
                        if (canModifyEvent) {
                            IconButton(
                                onClick = { modify(event) },
                                modifier = Modifier.testTag("modify_event_${event.eventId}"),
                                colors = iconButtonColors,
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Modify",
                                )
                            }
                        }
                    }
                    // Join event button
                    JoinEventButton(event, isOnline, 130.dp, refreshEvents)
                }
            }
        }
    }
}
