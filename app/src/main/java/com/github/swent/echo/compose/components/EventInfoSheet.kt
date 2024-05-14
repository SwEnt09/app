package com.github.swent.echo.compose.components

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Event
import java.time.format.DateTimeFormatter

/**
 * Bottom sheet that shows the details of an event.
 *
 * @param event the event to show
 * @param onJoinButtonPressed callback to join the event
 * @param onDismiss callback to dismiss the sheet
 * @param onFullyExtended callback when the sheet is fully extended
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventInfoSheet(
    event: Event,
    onJoinButtonPressed: () -> Unit,
    onDismiss: () -> Unit,
    onFullyExtended: () -> Unit,
    canModifyEvent: Boolean,
    onModifyEvent: () -> Unit,
    isOnline: Boolean,
) {
    val sheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = false) { value ->
            if (value == SheetValue.Expanded) {
                onFullyExtended()
            }
            true
        }

    // handle the display of the date and time
    val formatter = DateTimeFormatter.ofPattern("dd/MM\nHH:mm")
    val displayDate = formatter.format(event.startDate)

    // get the context
    val context = LocalContext.current

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize().testTag("event_info_sheet"),
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        // Sheet content

        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 32.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.TopStart).fillMaxWidth(0.7f)) {
                Text(
                    modifier = Modifier.testTag("event_info_sheet_title"),
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = event.organizer?.name ?: event.creator.name,
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.secondary
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = event.location.name,
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.secondary
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(10.dp))
                TagUI(tags = event.tags.toList(), selectedTagId = null, onTagClick = {})
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                modifier = Modifier.align(Alignment.TopEnd),
                text = displayDate,
                style = MaterialTheme.typography.headlineLarge,
            )

            Row(
                modifier =
                    Modifier.align(Alignment.BottomCenter)
                        .padding(bottom = 70.dp, end = if (canModifyEvent) 140.dp else 0.dp)
            ) {
                // icon of a person
                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = "Show people who joined the event",
                    modifier = Modifier.testTag("people_icon")
                )
                Spacer(modifier = Modifier.width(5.dp))
                // text to show the number of people who joined the event
                Text(
                    text =
                        if (event.maxParticipants <= 0) {
                            "${event.participantCount}"
                        } else {
                            "${event.participantCount}/${event.maxParticipants}"
                        },
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Row(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp),
            ) {
                // button to join the event
                Button(
                    enabled = isOnline,
                    onClick = {
                        onJoinButtonPressed()
                        Toast.makeText(
                                context,
                                context.getString(R.string.event_successfully_joined),
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    },
                    modifier = Modifier.testTag("join_button_event_info_sheet"),
                ) {
                    Text(
                        text = stringResource(R.string.event_info_sheet_join_event_button_text),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                // button to modify the event
                if (canModifyEvent) {
                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        enabled = isOnline,
                        onClick = onModifyEvent,
                        modifier = Modifier.testTag("modify_button"),
                    ) {
                        Text(
                            text = stringResource(R.string.event_info_sheet_modify_event),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
