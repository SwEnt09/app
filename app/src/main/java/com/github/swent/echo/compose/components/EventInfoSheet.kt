package com.github.swent.echo.compose.components

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
            Text(
                text = event.title,
                style = MaterialTheme.typography.displaySmall,
                maxLines = 1,
            )
            Text(
                modifier = Modifier.padding(top = 38.dp),
                text = event.organizer?.name ?: event.creator.name,
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.secondary
                    ),
                maxLines = 1,
            )
            Text(
                modifier = Modifier.padding(top = 62.dp),
                text = event.location.name,
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.secondary
                    ),
                maxLines = 1,
            )
            Text(
                modifier = Modifier.align(Alignment.TopEnd),
                text = displayDate,
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                modifier = Modifier.padding(top = 100.dp),
                text = event.description,
                style = MaterialTheme.typography.bodyLarge
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
