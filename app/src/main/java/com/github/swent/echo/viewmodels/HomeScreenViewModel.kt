package com.github.swent.echo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.compose.components.searchmenu.FiltersContainer
import com.github.swent.echo.compose.components.searchmenu.SortBy
import com.github.swent.echo.data.SAMPLE_EVENTS
import com.github.swent.echo.data.SAMPLE_TAGS
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.ZonedDateTime
import javax.inject.Inject
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
    private lateinit var filterTagSet: Set<Tag>
    private lateinit var allEventsList: List<Event>
    private lateinit var allTagSet: Set<Tag>
    private val _displayEventList = MutableStateFlow<List<Event>>(listOf())
    val displayEventList = _displayEventList.asStateFlow()
    private val _displayEventInfo = MutableStateFlow<Event?>(null)
    val displayEventInfo = _displayEventInfo.asStateFlow()
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
                from = ZonedDateTime.now(),
                to = ZonedDateTime.now(),
                sortBy = SortBy.NONE
            )
        )
    val filtersContainer = _filtersContainer.asStateFlow()

    private val _profileName =
        MutableStateFlow<String>("John Doe") // placeholder until we have user profiles
    val profileName = _profileName.asStateFlow()
    private val _profileClass =
        MutableStateFlow<String>("IN - BA6") // placeholder until we have user profiles
    val profileClass = _profileClass.asStateFlow()

    init {
        viewModelScope.launch {
            val userid = authenticationService.getCurrentUserID()
            filterTagSet =
                if (userid == null) {
                    // the user is not logged in, display the default events on map
                    // TODO add a default tag list
                    repository.getAllTags().toSet()
                } else {
                    // the user is logged in, display the events based on the users tags on map
                    // repository.getUserProfile(userid).tags
                    setOf(SAMPLE_TAGS.first())
                    // SAMPLE_TAGS
                }
            allEventsList = SAMPLE_EVENTS // repository.getAllEvents()
            allTagSet = SAMPLE_TAGS // repository.getAllTags()
            filterEvents()
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

    fun onFromChanged(from: ZonedDateTime) {
        _filtersContainer.value = _filtersContainer.value.copy(from = from)
        refreshFiltersContainer()
    }

    fun onToChanged(to: ZonedDateTime) {
        _filtersContainer.value = _filtersContainer.value.copy(to = to)
        refreshFiltersContainer()
    }

    fun onSortByChanged(sortBy: SortBy) {
        _filtersContainer.value = _filtersContainer.value.copy(sortBy = sortBy)
        refreshFiltersContainer()
    }

    fun signOut() {
        viewModelScope.launch { authenticationService.signOut() }
    }

    fun refreshFiltersContainer() {
        filterTagSet =
            setOf(
                SAMPLE_TAGS.find { it.name == filtersContainer.value.searchEntry } ?: Tag("0", "0")
            ) // replace with the repository call
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
                from = ZonedDateTime.now(),
                to = ZonedDateTime.now(),
                sortBy = SortBy.NONE
            )
        refreshFiltersContainer()
    }

    // End of methods to set the filters container values

    private fun filterEvents() {
        _displayEventList.value =
            allEventsList.filter { event ->
                event.tags.any { tag -> filterTagSet.any { tag2 -> tag.tagId == tag2.tagId } }
            }
    }

    /** Displays the event info sheet for the given event. Set the overlay to EVENT_INFO_SHEET. */
    fun onEventSelected(event: Event) {
        _displayEventInfo.value = event
        setOverlay(Overlay.EVENT_INFO_SHEET)
    }

    /** Add the given tag to the filter tag list. */
    fun addTag(tag: Tag) {
        filterTagSet = filterTagSet.plus(tag)
        filterEvents()
    }

    /** Remove the given tag from the filter tag list. */
    fun removeTag(tag: Tag) {
        filterTagSet = filterTagSet.minus(tag)
        filterEvents()
    }

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
}
