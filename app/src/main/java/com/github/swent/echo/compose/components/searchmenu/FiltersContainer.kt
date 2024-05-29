package com.github.swent.echo.compose.components.searchmenu

import com.github.swent.echo.viewmodels.SortBy

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
    var sortBy: SortBy?
)
