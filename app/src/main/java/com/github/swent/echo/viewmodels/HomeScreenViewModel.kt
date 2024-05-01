package com.github.swent.echo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.compose.components.searchmenu.FiltersContainer
import com.github.swent.echo.compose.components.searchmenu.SortBy
import com.github.swent.echo.compose.components.searchmenu.floatToDate
import com.github.swent.echo.data.SAMPLE_EVENTS
import com.github.swent.echo.data.SAMPLE_TAGS
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class Overlay {
    NONE,
    EVENT_INFO_SHEET,
    SEARCH_SHEET
}

enum class MapOrListMode {
    MAP,
    LIST
}

@HiltViewModel
class HomeScreenViewModel
@Inject
constructor(
    private val repository: Repository,
    private val authenticationService: AuthenticationService,
) : ViewModel() {

    private val _overlay = MutableStateFlow<Overlay>(Overlay.NONE)
    val overlay = _overlay.asStateFlow()
    private val _mode = MutableStateFlow<MapOrListMode>(MapOrListMode.MAP)
    val mode = _mode.asStateFlow()
    private var filterTagSet: Set<Tag> = setOf()
    private lateinit var allEventsList: List<Event>
    private lateinit var allTagSet: Set<Tag>
    private val _displayEventList = MutableStateFlow<List<Event>>(listOf())
    val displayEventList = _displayEventList.asStateFlow()
    private val _displayEventInfo = MutableStateFlow<Event?>(null)
    val displayEventInfo = _displayEventInfo.asStateFlow()
    private val _canUserModifyEvent = MutableStateFlow<Boolean>(false)
    val canUserModifyEvent = _canUserModifyEvent.asStateFlow()
    private val _filtersContainer =
        MutableStateFlow(
            FiltersContainer(
                searchEntry = "",
                epflChecked = true,
                sectionChecked = true,
                classChecked = true,
                pendingChecked = true,
                confirmedChecked = true,
                fullChecked = true,
                from = 0f,
                to = 14f,
                sortBy = SortBy.NONE
            )
        )
    val filtersContainer = _filtersContainer.asStateFlow()
    private val _profileName = MutableStateFlow<String>("")
    val profileName = _profileName.asStateFlow()
    private val _profileClass = MutableStateFlow<String>("")
    val profileClass = _profileClass.asStateFlow()
    private var section = ""
    private var semester = ""

    init {
        viewModelScope.launch {
            val userId = authenticationService.getCurrentUserID() ?: ""
            allEventsList = SAMPLE_EVENTS // repository.getAllEvents()
            allTagSet = SAMPLE_TAGS // repository.getAllTags().toSet()
            semester = repository.getUserProfile(userId)?.semester?.name ?: ""
            section = repository.getUserProfile(userId)?.section?.name ?: ""
            _profileClass.value =
                if (semester == "") section
                else if (section == "") semester else "$section - $semester"
            _profileName.value = repository.getUserProfile(userId)?.name ?: ""
            refreshFiltersContainer()
        }
    }

    fun onSearchEntryChanged(searchEntry: String) {
        _filtersContainer.value = _filtersContainer.value.copy(searchEntry = searchEntry)
        refreshFiltersContainer()
    }

    fun onEpflCheckedSwitch() {
        _filtersContainer.value =
            _filtersContainer.value.copy(epflChecked = !_filtersContainer.value.epflChecked)
        refreshFiltersContainer()
        print("epflChecked: ${_filtersContainer.value.epflChecked}\n")
    }

    fun onSectionCheckedSwitch() {
        _filtersContainer.value =
            _filtersContainer.value.copy(sectionChecked = !_filtersContainer.value.sectionChecked)
        refreshFiltersContainer()
    }

    fun onClassCheckedSwitch() {
        _filtersContainer.value =
            _filtersContainer.value.copy(classChecked = !_filtersContainer.value.classChecked)
        refreshFiltersContainer()
    }

    fun onPendingCheckedSwitch() {
        _filtersContainer.value =
            _filtersContainer.value.copy(pendingChecked = !_filtersContainer.value.pendingChecked)
        refreshFiltersContainer()
    }

    fun onConfirmedCheckedSwitch() {
        _filtersContainer.value =
            _filtersContainer.value.copy(
                confirmedChecked = !_filtersContainer.value.confirmedChecked
            )
        refreshFiltersContainer()
    }

    fun onFullCheckedSwitch() {
        _filtersContainer.value =
            _filtersContainer.value.copy(fullChecked = !_filtersContainer.value.fullChecked)
        refreshFiltersContainer()
    }

    fun onDateFilterChanged(from: Float, to: Float) {
        _filtersContainer.value = _filtersContainer.value.copy(from = from, to = to)
        refreshFiltersContainer()
    }

    fun onSortByChanged(sortBy: SortBy) {
        _filtersContainer.value = _filtersContainer.value.copy(sortBy = sortBy)
        refreshFiltersContainer()
    }

    fun signOut() {
        viewModelScope.launch { authenticationService.signOut() }
    }

    private fun refreshFiltersContainer() {
        val listOfWords = _filtersContainer.value.searchEntry.lowercase().split(" ")
        filterTagSet =
            allTagSet
                .filter { tag -> listOfWords.any { word -> tag.name.lowercase().contains(word) } }
                .toSet()
        filterEvents()
    }

    fun resetFiltersContainer() {
        _filtersContainer.value =
            FiltersContainer(
                searchEntry = "",
                epflChecked = true,
                sectionChecked = true,
                classChecked = true,
                pendingChecked = true,
                confirmedChecked = true,
                fullChecked = true,
                from = 0f,
                to = 14f,
                sortBy = SortBy.NONE
            )
        refreshFiltersContainer()
    }

    // End of methods to set the filters container values

    private fun filterEvents() {
        print("section : $section\n")
        print("semester : $semester\n")
        _displayEventList.value =
            allEventsList
                .filter { event ->
                    // filter by tags
                    event.tags.any { tag -> filterTagSet.any { tag2 -> tag.tagId == tag2.tagId } }
                    // filter by time
                    &&
                        dateFilterConditions(event)
                        // filter by scope of the event
                        &&
                        ((_filtersContainer.value.epflChecked &&
                            event.tags.any { tag -> tag.name.lowercase() == "epfl" }) ||
                            (_filtersContainer.value.sectionChecked &&
                                event.tags.any { tag ->
                                    tag.name.lowercase() == section.lowercase()
                                }) ||
                            (_filtersContainer.value.classChecked &&
                                event.tags.any { tag ->
                                    tag.name.lowercase() == semester.lowercase()
                                }))
                        // filter by status of the event (pending, confirmed, full)
                        &&
                        (_filtersContainer.value.pendingChecked &&
                            event.participantCount < event.maxParticipants * 0.5 ||
                            _filtersContainer.value.confirmedChecked &&
                                (event.participantCount >= event.maxParticipants * 0.5 &&
                                    event.participantCount < event.maxParticipants) ||
                            _filtersContainer.value.fullChecked &&
                                event.participantCount == event.maxParticipants)
                }
                .sortedBy { event ->
                    event.startDate
                    // when we can sort by distance, update this
                    /*when (_filtersContainer.value.sortBy) {
                        SortBy.DATE_ASC -> event.startDate
                        SortBy.DATE_DESC ->
                        else ->
                    }*/
                }

        // reverse the list if the sort by is descending
        if (_filtersContainer.value.sortBy == SortBy.DATE_DESC) {
            _displayEventList.value = _displayEventList.value.reversed()
        }
    }

    /** Displays the event info sheet for the given event. Set the overlay to EVENT_INFO_SHEET. */
    fun onEventSelected(event: Event) {
        _displayEventInfo.value = event
        _canUserModifyEvent.value = authenticationService.getCurrentUserID() == event.creator.userId
        setOverlay(Overlay.EVENT_INFO_SHEET)
    }

    /* not needed for now
    /** Add the given tag to the filter tag list. */
    fun addTag(tag: Tag) {
        filterTagSet = filterTagSet.plus(tag)
        filterEvents()
    }

    /** Remove the given tag from the filter tag list. */
    fun removeTag(tag: Tag) {
        filterTagSet = filterTagSet.minus(tag)
        filterEvents()
    }*/

    /**
     * Set the overlay to the given overlay. (EVENT_INFO_SHEET, SEARCH_SHEET or NONE)s
     *
     * @param overlay The overlay to set.
     */
    fun setOverlay(overlay: Overlay) {
        _overlay.value = overlay
    }

    /** Clear the overlay. (set it to NONE) */
    fun clearOverlay() {
        _overlay.value = Overlay.NONE
    }

    /** Switch between map and list mode. */
    fun switchMode() {
        _mode.value =
            if (_mode.value == MapOrListMode.MAP) MapOrListMode.LIST else MapOrListMode.MAP
    }

    // Allows the users to see events that are happening in the next 14 days, and more if
    // the slider is full on the right
    private fun dateFilterConditions(event: Event): Boolean {
        var result = event.startDate.isAfter(floatToDate(_filtersContainer.value.from - 1))
        if (_filtersContainer.value.to.roundToInt() != 14) {
            result = result && event.startDate.isBefore(floatToDate(_filtersContainer.value.to))
        }
        return result
    }
}
