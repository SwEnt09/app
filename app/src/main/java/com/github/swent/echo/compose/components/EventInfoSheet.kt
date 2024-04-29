package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Event

/**
 * Bottom sheet that shows the details of an event.
 *
 * @param event the event to show
 * @param onJoinButtonPressed callback to join the event
 * @param onShowPeopleButtonPressed callback to show people who joined the event
 * @param onDismiss callback to dismiss the sheet
 * @param onFullyExtended callback when the sheet is fully extended
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventInfoSheet(
    event: Event,
    onJoinButtonPressed: () -> Unit,
    onShowPeopleButtonPressed: () -> Unit,
    onDismiss: () -> Unit,
    onFullyExtended: () -> Unit
) {
    val sheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = false) { value ->
            if (value == SheetValue.Expanded) {
                onFullyExtended()
            }
            true
        }

    // handle the display of the date and time
    val day = event.startDate.dayOfMonth
    val month = event.startDate.monthValue
    val hour = event.startDate.hour
    val minute = event.startDate.minute
    val displayMonth = if (month < 10) "0$month" else month.toString()
    val displayMinute = if (minute < 10) "0$minute" else minute.toString()
    val displayDay = if (day < 10) "0$day" else day.toString()
    val displayHour = if (hour < 10) "0$hour" else hour.toString()
    val displayDate = "$displayDay/$displayMonth\n$displayHour:$displayMinute"

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
                style =
                    TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight(600),
                    )
            )
            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = event.organizer?.name ?: event.creator.name,
                style =
                    TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight(600),
                        color = MaterialTheme.colorScheme.secondary
                    )
            )
            Text(
                modifier = Modifier.padding(top = 48.dp),
                text = event.location.name,
                style =
                    TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight(600),
                        color = MaterialTheme.colorScheme.secondary
                    )
            )
            Text(
                modifier = Modifier.align(Alignment.TopEnd),
                text = displayDate,
                style =
                    TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight(600),
                    )
            )
            Text(
                modifier = Modifier.padding(top = 100.dp),
                text = event.description,
                style =
                    TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(600),
                    )
            )

            Row(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 70.dp)) {
                // icon of a person
                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = "Show people who joined the event",
                    modifier = Modifier.testTag("people_icon")
                )
                // text to show the number of people who joined the event
                Text(
                    text =
                        if (event.maxParticipants <= 0) {
                            "${event.participantCount}"
                        } else {
                            "${event.participantCount}/${event.maxParticipants}"
                        },
                    style =
                        TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight(600),
                        )
                )
            }

            // button to join the event
            Button(
                onClick = onJoinButtonPressed,
                modifier =
                    Modifier.align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp)
                        .width(165.dp)
                        .testTag("join_button"),
            ) {
                Text(
                    text = stringResource(R.string.event_info_sheet_join_event_button_text),
                    style =
                        TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight(600),
                        )
                )
            }
            /* contains the image and the button to show people who have joined the event
            Box(modifier = Modifier.align(Alignment.CenterEnd).width(150.dp).height(200.dp)) {
                // image of the event
                val buttonAlignment =
                    if (event.imageId > 0) {
                        Alignment.BottomEnd
                    } else {
                        Alignment.TopEnd
                    }

                if (event.imageId > 0) { // if the id is zero or negative, then there is no image
                    Image(
                        painter = painterResource(id = event.imageId), // replace with actual image
                        contentDescription = event.title,
                        modifier =
                            Modifier.width(135.dp)
                                .height(135.dp)
                                .align(Alignment.TopEnd)
                                .clip(RoundedCornerShape(8.dp))
                                .testTag("event_image")
                    )
                }
                // button to show people who joined the event
                Button(
                    onClick = onShowPeopleButtonPressed,
                    modifier =
                        Modifier.align(buttonAlignment).width(135.dp).testTag("people_button"),
                ) {
                    // icon of a person
                    Icon(
                        imageVector = Icons.Filled.Face,
                        contentDescription = "Show people who joined the event",
                        modifier = Modifier.testTag("people_button_icon")
                    )
                    // text to show the number of people who joined the event
                    Text(
                        text =
                            if (event.maxParticipants <= 0) {
                                "${event.participantCount}"
                            } else {
                                "${event.participantCount}/${event.maxParticipants}"
                            },
                        style =
                            TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight(600),
                            )
                    )
                }
            }*/
        }
    }
}
