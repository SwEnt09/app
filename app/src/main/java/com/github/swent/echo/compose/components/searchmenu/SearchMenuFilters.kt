package com.github.swent.echo.compose.components.searchmenu

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.github.swent.echo.R
import java.time.ZonedDateTime

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
    sortByCallback: (SortBy) -> Unit
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
            DateInputSample(filters.from)
            DateInputSample(filters.to)
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

/*
 * Composable to get a date from the user.
 * TODO : Implement it, but I spend a bit too much time
 *   on non-working solution so leaving it for next sprint
 */
@Composable fun DateInputSample(dateOutput: ZonedDateTime) {}
