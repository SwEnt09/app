package com.github.swent.echo.compose.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.swent.echo.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/** date & time buttons with a label (e.g. start date) above it */
val MILLISECINDAY = 24 * 60 * 60 * 1000

/** the year max value in date picker from this year */
val YEARRANGE = 10

/** Event date entry: start and end date */
@Composable
fun EventDateEntry(
    startDate: ZonedDateTime,
    endDate: ZonedDateTime,
    onStartDateChanged: (newStartDate: ZonedDateTime) -> Unit,
    onEndDateChanged: (newEndDate: ZonedDateTime) -> Unit
) {
    Row(
        modifier =
            Modifier.padding(
                horizontal = EVENT_PADDING_BETWEEN_INPUTS - 1.dp, // enough space for two buttons
                vertical = EVENT_PADDING_BETWEEN_INPUTS
            ),
        // horizontalArrangement = Arrangement.SpaceAround
    ) {
        EventDateEntryUnit(
            label = stringResource(R.string.edit_event_screen_start_date),
            currentDate = startDate
        ) { newDate ->
            onStartDateChanged(newDate)
        }
        EventDateEntryUnit(
            label = stringResource(R.string.edit_event_screen_end_date),
            currentDate = endDate
        ) { newDate ->
            onEndDateChanged(newDate)
        }
    }
}

/** Composable of a date and time: each one represented by a button */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDateEntryUnit(
    label: String,
    currentDate: ZonedDateTime,
    onDateChanged: (newDate: ZonedDateTime) -> Unit
) {
    var date by remember { mutableStateOf(currentDate.toLocalDate()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState =
        rememberDatePickerState(
            date.toEpochDay() * MILLISECINDAY,
            null,
            IntRange(LocalDate.now().year, LocalDate.now().year + YEARRANGE),
            DisplayMode.Picker
        )
    var time by remember { mutableStateOf(currentDate.toLocalTime()) }
    var showTimePicker by remember { mutableStateOf(false) }
    Column {
        EventEntryName(name = label)
        ElevatedButton(
            modifier = Modifier.testTag("$label-button"),
            onClick = { showDatePicker = true }
        ) {
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                date =
                                    LocalDate.ofEpochDay(
                                        datePickerState.selectedDateMillis!! / MILLISECINDAY
                                    )
                                onDateChanged(date.atTime(time).atZone(ZoneId.systemDefault()))
                                showDatePicker = false
                            }
                        ) {
                            Text(stringResource(R.string.edit_event_screen_confirm_date))
                        }
                    }
                ) {
                    DatePicker(
                        modifier = Modifier.testTag("$label-dialog"),
                        state = datePickerState
                    )
                }
            } else {
                Text(date.toString())
            }
        }

        val timePickerState by remember {
            mutableStateOf(TimePickerState(time.hour, time.minute, true))
        }
        ElevatedButton(
            modifier = Modifier.testTag("$label-time-button"),
            onClick = { showTimePicker = true }
        ) {
            if (showTimePicker) {
                Dialog(
                    onDismissRequest = { showTimePicker = false },
                ) {
                    Card {
                        TimePicker(
                            modifier = Modifier.testTag("$label-time-dialog"),
                            state = timePickerState
                        )
                        Button(
                            modifier =
                                Modifier.align(Alignment.End)
                                    .padding(5.dp)
                                    .testTag("$label-time-dialog-button"),
                            onClick = {
                                time = LocalTime.of(timePickerState.hour, timePickerState.minute)
                                showTimePicker = false
                            }
                        ) {
                            Text(stringResource(R.string.edit_event_screen_confirm_date))
                        }
                    }
                }
            } else {
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                Text(time.format(formatter))
            }
        }
    }
}
