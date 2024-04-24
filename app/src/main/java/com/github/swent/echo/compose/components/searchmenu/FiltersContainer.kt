package com.github.swent.echo.compose.components.searchmenu

import androidx.compose.runtime.MutableState
import com.github.swent.echo.R
import java.time.ZonedDateTime

data class FiltersContainer(
    var tagId: MutableState<String>,
    var epflChecked: MutableState<Boolean>,
    var sectionChecked: MutableState<Boolean>,
    var classChecked: MutableState<Boolean>,
    var pendingChecked: MutableState<Boolean>,
    var confirmedChecked: MutableState<Boolean>,
    var fullChecked: MutableState<Boolean>,
    var from: MutableState<ZonedDateTime>,
    var to: MutableState<ZonedDateTime>,
    var sortBy: MutableState<SortBy>,
)

// Enum class for the different states of the sort by filter
enum class SortBy(val stringKey: String) {
    NONE("filters_container_sort_by_none"),
    DATE_ASC("filters_container_sort_by_date_asc"),
    DATE_DESC("filters_container_sort_by_date_desc"),
    DISTANCE_ASC("filters_container_sort_by_distance_asc"),
    DISTANCE_DESC("filters_container_sort_by_distance_desc"),
}

// Function to get the string resource for the sort by filter
fun stringResourceSortBy(key: String): Int {
    return when (key) {
        "filters_container_sort_by_none" -> R.string.filters_container_sort_by_none
        "filters_container_sort_by_date_asc" -> R.string.filters_container_sort_by_date_asc
        "filters_container_sort_by_date_desc" -> R.string.filters_container_sort_by_date_desc
        "filters_container_sort_by_distance_asc" -> R.string.filters_container_sort_by_distance_asc
        "filters_container_sort_by_distance_desc" ->
            R.string.filters_container_sort_by_distance_desc
        else -> throw IllegalArgumentException("Invalid string key")
    }
}