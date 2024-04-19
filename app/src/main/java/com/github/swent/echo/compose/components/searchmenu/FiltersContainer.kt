package com.github.swent.echo.compose.components.searchmenu

import androidx.compose.runtime.MutableState
import java.time.ZonedDateTime
import kotlinx.serialization.Contextual

data class FiltersContainer(
    var tagId: MutableState<String>,
    var epflChecked: MutableState<Boolean>,
    var sectionChecked: MutableState<Boolean>,
    var classChecked: MutableState<Boolean>,
    var pendingChecked: MutableState<Boolean>,
    var confirmedChecked: MutableState<Boolean>,
    var fullChecked: MutableState<Boolean>,
    @Contextual var from: MutableState<ZonedDateTime>,
    @Contextual var to: MutableState<ZonedDateTime>,
    var sortBy: MutableState<SortBy>? = null,
)

// Enum class for the different states of the sort by filter
enum class SortBy(val value: String) {
    NONE("---"),
    DATE_ASC("Date (Asc)"),
    DATE_DESC("Date (Desc)"),
    DISTANCE_ASC("Distance (Asc)"),
    DISTANCE_DESC("Distance (Desc)"),
}
