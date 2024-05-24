package com.github.swent.echo.compose.components.searchmenu

import com.github.swent.echo.R

data class FiltersContainer(
    var searchEntry: String,
    var epflChecked: Boolean,
    var sectionChecked: Boolean,
    var classChecked: Boolean,
    var pendingChecked: Boolean,
    var confirmedChecked: Boolean,
    var fullChecked: Boolean,
    var from: Float,
    var to: Float,
)

// Enum class for the different states of the sort by filter
enum class SortBy(val stringKey: String, val ascending: Boolean) {
    NONE("filters_container_sort_by_none", false),
    DATE_ASC("filters_container_sort_by_date_asc", true),
    DATE_DESC("filters_container_sort_by_date_desc", false),
    DISTANCE_ASC("filters_container_sort_by_distance_asc", true),
    DISTANCE_DESC("filters_container_sort_by_distance_desc", false),
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

class EventComparator(val keyType: SortBy) {}
