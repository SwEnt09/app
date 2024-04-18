package com.github.swent.echo.compose.components.searchmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import java.time.ZonedDateTime

/** Composable to display the filters sheet */
@Composable
fun SearchMenuFilters(filters: FiltersContainer) {
    // Content of the Events for filters
    val eventsForItems =
        listOf(
            CheckBoxItems(Icons.Filled.Face, "EPFL", filters.epflChecked),
            CheckBoxItems(Icons.Filled.Face, "Section", filters.sectionChecked),
            CheckBoxItems(Icons.Filled.Face, "Class", filters.classChecked)
        )

    // Content of the Events Status filters
    val eventsStatusItems =
        listOf(
            CheckBoxItems(Icons.Filled.Person, "Pending", filters.pendingChecked),
            CheckBoxItems(Icons.Filled.Person, "Confirmed", filters.confirmedChecked),
            CheckBoxItems(Icons.Filled.Person, "Full", filters.fullChecked)
        )

    Box(
        modifier =
            Modifier.fillMaxSize().background(Color.White).testTag("search_menu_filters_content")
    ) {
        // Sort by filter
        Row(
            modifier =
                Modifier.align(Alignment.TopStart)
                    .fillMaxWidth()
                    .zIndex(1f)
                    .testTag("sort_by_displayer_container")
        ) {
            SortByDisplayer()
        }
        // Checkbox filters
        Row(
            modifier =
                Modifier.align(Alignment.TopCenter)
                    .absoluteOffset(y = 50.dp)
                    .testTag("checkboxes_container")
        ) {
            // Events for Checkboxes
            CheckBoxesDisplayer("Events For:", checkBoxItems = eventsForItems)
            Spacer(modifier = Modifier.width(100.dp))
            // Events Status Checkboxes
            CheckBoxesDisplayer("Events Status:", checkBoxItems = eventsStatusItems)
        }
        Row(modifier = Modifier.align(Alignment.TopCenter).absoluteOffset(y = 170.dp)) {
            DateInputSample(filters.from)
            DateInputSample(filters.to)
        }
    }
}

// Enum class for the different states of the sort by filter
enum class SortBy(val value: String) {
    NONE("---"),
    DATE_ASC("Date (Asc)"),
    DATE_DESC("Date (Desc)"),
    DISTANCE_ASC("Distance (Asc)"),
    DISTANCE_DESC("Distance (Desc)"),
}

/** Data class for the checkboxes */
data class CheckBoxItems(
    val icon: ImageVector,
    val contentDescription: String,
    var checked: MutableState<Boolean>
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
                    checked = checkBoxItem.checked.value,
                    onCheckedChange = { checkBoxItem.checked.value = !checkBoxItem.checked.value },
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
fun SortByDisplayer() {
    var expanded by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf(SortBy.NONE) }

    Column {
        Button(
            onClick = { expanded = !expanded },
            shape = RoundedCornerShape(10),
            modifier = Modifier.width(170.dp).testTag("sort_by_button")
        ) {
            Text(if (sortBy == SortBy.NONE) "Sort by..." else sortBy.value)
            Icon(
                if (!expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                contentDescription = "Sort by"
            )
        }
        // Check if the sort by filter is expanded
        if (expanded) {
            SortBy.entries.forEach {
                Button(
                    onClick = {
                        sortBy = it
                        expanded = false
                    },
                    shape = RoundedCornerShape(5),
                    modifier = Modifier.width(170.dp).height(35.dp).testTag(it.value)
                ) {
                    Text(it.value)
                }
            }
        }
    }
}

/*
 * Composable to get a date from the user.
 * TODO : Implement it, but I spend a bit too much time
 *   on non-working solution so leaving it for next sprint
 */
@Composable fun DateInputSample(dateOutput: MutableState<ZonedDateTime>) {}
