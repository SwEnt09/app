package com.github.swent.echo.compose.components.searchmenu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.github.swent.echo.R
import com.github.swent.echo.compose.components.Dropdown
import com.github.swent.echo.compose.components.colorClass
import com.github.swent.echo.compose.components.colorEpfl
import com.github.swent.echo.compose.components.colorSection
import com.github.swent.echo.viewmodels.MapOrListMode
import com.github.swent.echo.viewmodels.SortBy
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.math.roundToLong

// This Composable function is responsible for displaying the search menu filters.
@Composable
fun SearchMenuFilters(
    filters: FiltersContainer, // The current state of the filters
    epflCallback: () -> Unit, // Callback function when the EPFL filter is toggled
    sectionCallback: () -> Unit, // Callback function when the section filter is toggled
    classCallback: () -> Unit, // Callback function when the class filter is toggled
    pendingCallback: () -> Unit, // Callback function when the pending filter is toggled
    confirmedCallback: () -> Unit, // Callback function when the confirmed filter is toggled
    fullCallback: () -> Unit, // Callback function when the full filter is toggled
    sortByCallback: (Int) -> Unit, // Callback function when the sort by filter is changed
    timeFilterCallback: (Float, Float) -> Unit, // Callback function when the time filter is changed
    mode: MapOrListMode, // The current display mode (map or list)
    followedAssociations: List<String>, // The list of followed associations
    selectedAssociation: Int, // The currently selected association
    associationCallback: (Int) -> Unit // Callback function when the association filter is changed
) {
    // Define the items for the "Events for" filter section
    val eventsForItems =
        listOf(
            CheckBoxItems(
                painterResource(id = R.drawable.paint),
                stringResource(id = R.string.search_menu_filters_epfl),
                filters.epflChecked,
                tint = colorEpfl
            ) {
                epflCallback()
            },
            CheckBoxItems(
                painterResource(id = R.drawable.paint),
                stringResource(id = R.string.section),
                filters.sectionChecked,
                tint = colorSection
            ) {
                sectionCallback()
            },
            CheckBoxItems(
                painterResource(id = R.drawable.paint),
                stringResource(id = R.string.search_menu_filters_class),
                filters.classChecked,
                tint = colorClass
            ) {
                classCallback()
            }
        )

    // Define the items for the "Events status" filter section
    val eventsStatusItems =
        listOf(
            CheckBoxItems(
                painterResource(id = R.drawable.pending),
                stringResource(id = R.string.search_menu_filters_pending),
                filters.pendingChecked,
                LocalContentColor.current
            ) {
                pendingCallback()
            },
            CheckBoxItems(
                painterResource(id = R.drawable.confirmed),
                stringResource(id = R.string.search_menu_filters_confirmed),
                filters.confirmedChecked,
                LocalContentColor.current
            ) {
                confirmedCallback()
            },
            CheckBoxItems(
                painterResource(id = R.drawable.full),
                stringResource(id = R.string.search_menu_filters_full),
                filters.fullChecked,
                LocalContentColor.current
            ) {
                fullCallback()
            }
        )

    // Define the vertical space between elements
    val verticalSpacer = 8.dp

    // Define the layout of the filters
    Column(
        modifier = Modifier.fillMaxSize().testTag("search_menu_filters_content"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the dropdowns for selecting the association and the sort order
        TwoBoxesDisplayer(
            {
                Dropdown(
                    "Associations",
                    followedAssociations,
                    selectedAssociation,
                    associationCallback
                )
            },
            // Display the sort by dropdown only in list mode
            if (mode == MapOrListMode.LIST) {
                {
                    Dropdown(
                        "Sort By",
                        SortBy.entries.map { stringResource(it.stringKey) },
                        filters.sortBy.ordinal,
                        sortByCallback
                    )
                }
            } else {
                null
            }
        )
        Spacer(modifier = Modifier.height(verticalSpacer))
        // Display the checkboxes for selecting the event filters
        TwoBoxesDisplayer(
            {
                CheckBoxesDisplayer(
                    stringResource(id = R.string.search_menu_filters_events_for),
                    checkBoxItems = eventsForItems
                )
            },
            {
                CheckBoxesDisplayer(
                    stringResource(id = R.string.search_menu_filters_events_status),
                    checkBoxItems = eventsStatusItems
                )
            }
        )
        Spacer(modifier = Modifier.height(verticalSpacer))
        // Display the date filter
        DateFilter(filters, timeFilterCallback)
    }
}

/** Data class for the checkboxes */
data class CheckBoxItems(
    val icon: Painter,
    val contentDescription: String,
    val checked: Boolean,
    val tint: Color,
    val callback: () -> Unit
)

@Composable
fun CheckBoxesDisplayer(title: String, checkBoxItems: List<CheckBoxItems>, modifier: Modifier = Modifier) {
    // Define the space between the title and the items, and between the items themselves
    val spaceBetweenTitleAndItems = 10.dp
    val spaceBetweenItems = 5.dp
    // Define the size of the checkboxes
    val checkboxSize = 25.dp

    // Start a column to arrange the elements vertically
    Column {
        // Display the title
        Text(title, modifier = Modifier.testTag("checkboxes_title"))
        // Add some space after the title
        Spacer(modifier = Modifier.height(spaceBetweenTitleAndItems))

        // For each item in the list of checkboxes
        checkBoxItems.forEach { checkBoxItem ->
            // Start a row to arrange the elements horizontally
            Row(modifier = Modifier.testTag("${checkBoxItem.contentDescription}_checkbox_row")) {
                // Display the icon for the checkbox
                Icon(
                    checkBoxItem.icon,
                    contentDescription = checkBoxItem.contentDescription,
                    tint = checkBoxItem.tint
                )
                // Display the checkbox itself
                Checkbox(
                    checked = checkBoxItem.checked,
                    // When the checkbox is clicked, call the callback function
                    onCheckedChange = { checkBoxItem.callback() },
                    modifier =
                        modifier.size(checkboxSize)
                            .testTag("${checkBoxItem.contentDescription}_checkbox")
                )
                Text(checkBoxItem.contentDescription, fontSize = 13.sp)
            }
            // Add some space after the checkbox
            Spacer(modifier = Modifier.height(spaceBetweenItems))
        }
    }
}

/** Composable to select the date range for the events */
@Composable
fun DateFilter(filters: FiltersContainer, timeSliderCallback: (Float, Float) -> Unit) {
    val sliderLength = (LocalConfiguration.current.screenWidthDp - 40).dp
    val lowerBoundSlider = 0f
    val upperBoundSlider = 14f
    val steps = 13
    val textOffsetX = (-12).dp
    val textOffsetY = (-10).dp
    val multiplyFactorHorizontalOffset = 4
    val leftSideTooCloseToStart = 0.5f
    val rightSideTooCloseToEnd = 13.5f
    val combineOffsetEnd = 60.dp
    val combineOffsetInBetween = 35.dp
    val minDistanceJoinDates = 2.5f
    val minDistanceOneDate = 0.5f
    Column(modifier = Modifier.width(sliderLength)) {
        // Slider for the date range
        RangeSlider(
            value = filters.from..filters.to,
            steps = steps,
            onValueChange = { range -> timeSliderCallback(range.start, range.endInclusive) },
            valueRange = lowerBoundSlider..upperBoundSlider,
            onValueChangeFinished = {},
            modifier = Modifier.fillMaxWidth().testTag("search_menu_time_slider")
        )
        // Text under the slider to display the date range
        Box(modifier = Modifier.offset(x = textOffsetX, y = textOffsetY)) {
            // Function to calculate the offset of the text when the range is big enough to display
            // two dates
            fun sliderTextOffsetSolo(x: Float): Dp =
                (x * (sliderLength / (steps)) - (multiplyFactorHorizontalOffset * x).dp)
            // Function to calculate the offset of the text when the range is too close to display
            // two dates
            fun sliderTextOffsetCombine(x: Float, y: Float): Dp =
                // Check if the left side of the slider is too close to the start
                if (x < leftSideTooCloseToStart) {
                    sliderTextOffsetSolo(x)
                    // Check if the right side of the slider is too close to the end
                } else if (y > rightSideTooCloseToEnd) {
                    (sliderTextOffsetSolo(y) - combineOffsetEnd)
                    // In between
                } else {
                    (sliderTextOffsetSolo((x + y) / 2) - combineOffsetInBetween)
                }
            // Choose if we display two dates or one combination of them
            // For simplicity, if both dates are equal, we simply display one on the other
            // (they will have same offset)
            if (
                (filters.to - filters.from) > minDistanceJoinDates ||
                    (filters.to - filters.from) < minDistanceOneDate
            ) {
                Text(
                    floatToDate(filters.from).format(DateTimeFormatter.ofPattern("dd/MM")),
                    modifier =
                        Modifier.offset(sliderTextOffsetSolo(filters.from))
                            .testTag("search_menu_filter_from")
                )
                Text(
                    floatToDate(filters.to).format(DateTimeFormatter.ofPattern("dd/MM")) +
                        if (filters.to.roundToInt() == upperBoundSlider.roundToInt()) "+" else "",
                    modifier =
                        Modifier.offset(sliderTextOffsetSolo(filters.to))
                            .testTag("search_menu_filter_to")
                )
            } else {
                Text(
                    floatToDate(filters.from).format(DateTimeFormatter.ofPattern("dd/MM")) +
                        " - " +
                        floatToDate(filters.to).format(DateTimeFormatter.ofPattern("dd/MM")) +
                        if (filters.to.roundToInt() == upperBoundSlider.roundToInt()) "+" else "",
                    modifier =
                        Modifier.offset(sliderTextOffsetCombine(filters.from, filters.to))
                            .testTag("search_menu_filter_from_and_to")
                )
            }
        }
    }
}

@Composable
fun TwoBoxesDisplayer(firstBox: @Composable () -> Unit, secondBox: (@Composable () -> Unit)?) {
    // Define the padding around the boxes
    val padding = 12.dp

    // Start a box that fills the maximum width of the parent
    Box(modifier = Modifier.fillMaxWidth()) {
        // Start a box for the first composable, align it to the start (left) and add padding
        Box(modifier = Modifier.align(Alignment.TopStart).padding(start = padding)) {
            firstBox() // Call the first composable
        }
        // If a second composable is provided
        if (secondBox != null) {
            // Start a box for the second composable, align it to the end (right) and add padding
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(end = padding)) {
                secondBox() // Call the second composable
            }
        }
    }
}

// Function to convert a float from the slider into a date
fun floatToDate(value: Float): ZonedDateTime {
    val valueToFloat = value.roundToLong()
    return ZonedDateTime.now().plusDays(valueToFloat)
}
