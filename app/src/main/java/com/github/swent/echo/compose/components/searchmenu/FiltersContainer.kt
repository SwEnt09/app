package com.github.swent.echo.compose.components.searchmenu

import com.github.swent.echo.viewmodels.SortBy

// This data class is responsible for holding the state of various filters.
data class FiltersContainer(
    var searchEntry: String, // The search query entered by the user
    var epflChecked: Boolean, // Whether the EPFL filter is checked
    var sectionChecked: Boolean, // Whether the Section filter is checked
    var classChecked: Boolean, // Whether the Class filter is checked
    var pendingChecked: Boolean, // Whether the Pending filter is checked
    var confirmedChecked: Boolean, // Whether the Confirmed filter is checked
    var fullChecked: Boolean, // Whether the Full filter is checked
    var from: Float, // The lower bound of the range filter
    var to: Float, // The upper bound of the range filter
    var sortBy: SortBy // The selected sorting option
)
