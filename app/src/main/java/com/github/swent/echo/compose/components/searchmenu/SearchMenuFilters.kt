package com.github.swent.echo.compose.components.searchmenu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.github.swent.echo.R
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/** Composable to display the filters sheet */
@Composable
fun SearchMenuFilters(
    filters: FiltersContainer,
    epflCallback: () -> Unit,
    sectionCallback: () -> Unit,
    classCallback: () -> Unit,
    pendingCallback: () -> Unit,
    confirmedCallback: () -> Unit,
    fullCallback: () -> Unit,
    sortByCallback: (SortBy) -> Unit,
    timeFilterCallback: (Float, Float) -> Unit
) {
    // Content of the Events for filters
    val eventsForItems =
        listOf(
            CheckBoxItems(
                Icons.Filled.Face,
                stringResource(id = R.string.search_menu_filters_epfl),
                filters.epflChecked
            ) {
                epflCallback()
            },
            CheckBoxItems(
                Icons.Filled.Face,
                stringResource(id = R.string.search_menu_filters_section),
                filters.sectionChecked
            ) {
                sectionCallback()
            },
            CheckBoxItems(
                Icons.Filled.Face,
                stringResource(id = R.string.search_menu_filters_class),
                filters.classChecked
            ) {
                classCallback()
            }
        )

    // Content of the Events Status filters
    val eventsStatusItems =
        listOf(
            CheckBoxItems(
                Icons.Filled.Person,
                stringResource(id = R.string.search_menu_filters_pending),
                filters.pendingChecked
            ) {
                pendingCallback()
            },
            CheckBoxItems(
                Icons.Filled.Person,
                stringResource(id = R.string.search_menu_filters_confirmed),
                filters.confirmedChecked
            ) {
                confirmedCallback()
            },
            CheckBoxItems(
                Icons.Filled.Person,
                stringResource(id = R.string.search_menu_filters_full),
                filters.fullChecked
            ) {
                fullCallback()
            }
        )

    Box(modifier = Modifier.fillMaxSize().testTag("search_menu_filters_content")) {
        // Sort by filter
        Row(
            modifier =
                Modifier.align(Alignment.TopStart)
                    .fillMaxWidth()
                    .zIndex(1f)
                    .testTag("sort_by_displayer_container")
        ) {
            SortByDisplayer(filters.sortBy, sortByCallback)
        }
        // Checkbox filters
        Row(
            modifier =
                Modifier.align(Alignment.TopCenter)
                    .absoluteOffset(y = 50.dp)
                    .testTag("checkboxes_container")
        ) {
            // Events for Checkboxes
            CheckBoxesDisplayer(
                stringResource(id = R.string.search_menu_filters_events_for),
                checkBoxItems = eventsForItems
            )
            Spacer(modifier = Modifier.width(100.dp))
            // Events Status Checkboxes
            CheckBoxesDisplayer(
                stringResource(id = R.string.search_menu_filters_events_status),
                checkBoxItems = eventsStatusItems
            )
        }
        Row(modifier = Modifier.align(Alignment.TopCenter).absoluteOffset(y = 170.dp)) {
            DateFilter(filters, timeFilterCallback)
        }
    }
}

/** Data class for the checkboxes */
data class CheckBoxItems(
    val icon: ImageVector,
    val contentDescription: String,
    val checked: Boolean,
    val callback: () -> Unit
)

/** Composable to display the checkboxes in the correct format */
@Composable
fun CheckBoxesDisplayer(title: String, checkBoxItems: List<CheckBoxItems>) {
    Column {
        Text(title, modifier = Modifier.testTag("checkboxes_title"))
        Spacer(modifier = Modifier.height(10.dp))
        checkBoxItems.forEach { checkBoxItem ->
            Row(modifier = Modifier.testTag("${checkBoxItem.contentDescription}_checkbox_row")) {
                Icon(checkBoxItem.icon, contentDescription = checkBoxItem.contentDescription)
                Checkbox(
                    checked = checkBoxItem.checked,
                    onCheckedChange = { checkBoxItem.callback() },
                    modifier =
                        Modifier.height(25.dp)
                            .width(25.dp)
                            .testTag("${checkBoxItem.contentDescription}_checkbox")
                )
                Text(checkBoxItem.contentDescription)
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

/** Composable to display the dropdown menu for the sort by filter */
@Composable
fun SortByDisplayer(sortBy: SortBy, sortByCallback: (SortBy) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Button(
            onClick = { expanded = !expanded },
            shape = RoundedCornerShape(10),
            modifier = Modifier.width(170.dp).testTag("sort_by_button")
        ) {
            Text(
                if (sortBy == SortBy.NONE) stringResource(id = R.string.search_menu_filters_sort_by)
                else stringResource(id = stringResourceSortBy(sortBy.stringKey))
            )
            Icon(
                if (!expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                contentDescription = stringResource(id = R.string.search_menu_filters_sort_by)
            )
        }
        // Check if the sort by filter is expanded
        if (expanded) {
            SortBy.entries.forEach {
                Button(
                    onClick = {
                        sortByCallback(it)
                        expanded = false
                    },
                    shape = RoundedCornerShape(5),
                    modifier = Modifier.width(170.dp).height(35.dp).testTag(it.stringKey)
                ) {
                    Text(stringResource(id = stringResourceSortBy(it.stringKey)))
                }
            }
        }
    }
}

/** Composable to select the date range for the events */
@Composable
fun DateFilter(filters: FiltersContainer, timeSliderCallback: (Float, Float) -> Unit) {
    var sliderPosition by remember { mutableStateOf(filters.from..filters.to) }
    Column(modifier = Modifier.width(350.dp)) {
        // Slider for the date range
        RangeSlider(
            value = sliderPosition,
            steps = 13,
            onValueChange = { range ->
                sliderPosition = range
                timeSliderCallback(sliderPosition.start, sliderPosition.endInclusive)
            },
            valueRange = 0f..14f,
            onValueChangeFinished = {},
            modifier = Modifier.testTag("search_menu_time_slider")
        )
        // Text under the slider to display the date range
        Box(modifier = Modifier.absoluteOffset(y = (-10).dp)) {
            // Function to calculate the offset of the text when the range is big enough to display
            // two dates
            fun sliderTextOffsetSolo(x: Float): Dp = (23 * x - 10).dp
            // Function to calculate the offset of the text when the range is too close to display
            // two dates
            fun sliderTextOffsetCombine(x: Float, y: Float): Dp =
                // Check if the left side of the slider is too close to the start
                if (x < 0.5f) {
                    sliderTextOffsetSolo(x)
                    // Check if the right side of the slider is too close to the end
                } else if (y > 13.5f) {
                    (sliderTextOffsetSolo(y) - 60.dp)
                    // In between
                } else {
                    (sliderTextOffsetSolo((x + y) / 2) - 35.dp)
                }
            // Choose if we display two dates or one combination of them
            // For simplicity, if both dates are equal, we simply display one on the other
            // (they will have same offset)
            if ((filters.to - filters.from) > 2.5f || (filters.to - filters.from) < 0.5) {
                Text(
                    floatToDate(filters.from).format(DateTimeFormatter.ofPattern("dd/MM")),
                    modifier =
                        Modifier.offset(sliderTextOffsetSolo(filters.from))
                            .testTag("search_menu_filter_from")
                )
                Text(
                    floatToDate(filters.to).format(DateTimeFormatter.ofPattern("dd/MM")) +
                        if (filters.to.roundToInt() == 14) "+" else "",
                    modifier =
                        Modifier.offset(sliderTextOffsetSolo(filters.to))
                            .testTag("search_menu_filter_to")
                )
            } else {
                Text(
                    floatToDate(filters.from).format(DateTimeFormatter.ofPattern("dd/MM")) +
                        " - " +
                        floatToDate(filters.to).format(DateTimeFormatter.ofPattern("dd/MM")) +
                        if (filters.to.roundToInt() == 14) "+" else "",
                    modifier =
                        Modifier.offset(sliderTextOffsetCombine(filters.from, filters.to))
                            .testTag("search_menu_filter_from_and_to")
                )
            }
        }
    }
}

// Function to convert a float from the slider into a date
fun floatToDate(value: Float): ZonedDateTime {
    val valueToFloat = value.roundToLong()
    return ZonedDateTime.now().plusDays(valueToFloat)
}
