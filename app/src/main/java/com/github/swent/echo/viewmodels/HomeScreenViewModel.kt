package com.github.swent.echo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.compose.components.searchmenu.FiltersContainer
import com.github.swent.echo.compose.components.searchmenu.SortBy
import com.github.swent.echo.compose.components.searchmenu.floatToDate
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

const val STATUS_THRESHOLD = 0.5

@HiltViewModel
class HomeScreenViewModel
@Inject
constructor(
    private val repository: Repository,
    private val authenticationService: AuthenticationService,
) : ViewModel() {
    private val _overlay = MutableStateFlow(Overlay.NONE)
    val overlay = _overlay.asStateFlow()
    private val _mode = MutableStateFlow(MapOrListMode.MAP)
    val mode = _mode.asStateFlow()
    private var filterTagSet: Set<Tag> = setOf()
    private var filterWordList = listOf<String>()
    private lateinit var allEventsList: List<Event>
    private lateinit var allTagSet: Set<Tag>
    private val _displayEventList = MutableStateFlow<List<Event>>(listOf())
    val displayEventList = _displayEventList.asStateFlow()
    private val _displayEventInfo = MutableStateFlow<Event?>(null)
    val displayEventInfo = _displayEventInfo.asStateFlow()
    private val _canUserModifyEvent = MutableStateFlow(false)
    val canUserModifyEvent = _canUserModifyEvent.asStateFlow()
    private val _filtersContainer =
        MutableStateFlow(
            FiltersContainer(
                searchEntry = "",
                epflChecked = false,
                sectionChecked = false,
                classChecked = false,
                pendingChecked = false,
                confirmedChecked = false,
                fullChecked = false,
                from = 0f,
                to = 14f,
                sortBy = SortBy.NONE
            )
        )
    private val defaultFiltersContainer =
        FiltersContainer(
            searchEntry = "",
            epflChecked = false,
            sectionChecked = false,
            classChecked = false,
            pendingChecked = false,
            confirmedChecked = false,
            fullChecked = false,
            from = 0f,
            to = 14f,
            sortBy = SortBy.NONE
        )
    val filtersContainer = _filtersContainer.asStateFlow()
    private val _profileName = MutableStateFlow("")
    val profileName = _profileName.asStateFlow()
    private val _profileClass = MutableStateFlow("")
    val profileClass = _profileClass.asStateFlow()
    private val _section = MutableStateFlow("")
    val section = _section.asStateFlow()
    private val _semester = MutableStateFlow("")
    val semester = _semester.asStateFlow()
    private val sectionTagId = "30f27641-bd63-42e7-9d95-6117ad997554"
    private val semesterTagId = "319715cd-6210-4e62-a061-c533095bd098"
    private lateinit var sectionTags: List<Tag>
    private lateinit var semesterTags: List<Tag>
    private val _followedTags = MutableStateFlow<List<Tag>>(listOf())
    val followedTags = _followedTags.asStateFlow()
    private val _selectedTagId = MutableStateFlow<String?>(null)
    val selectedTagId = _selectedTagId.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = authenticationService.getCurrentUserID() ?: ""
            allEventsList = repository.getAllEvents()
            allTagSet = repository.getAllTags().toSet()
            _semester.value = repository.getUserProfile(userId)?.semester?.name ?: ""
            _section.value = repository.getUserProfile(userId)?.section?.name ?: ""
            _profileClass.value =
                if (_semester.value == "") _section.value
                else if (_section.value == "") _semester.value
                else "${section.value} - ${semester.value}"
            _profileName.value = repository.getUserProfile(userId)?.name ?: ""
            _followedTags.value =
                repository.getUserProfile(userId)?.tags?.toList() ?: allTagSet.toList()
            sectionTags = repository.getSubTags(sectionTagId)
            semesterTags = repository.getSubTags(semesterTagId)
            refreshFiltersContainer()
        }
    }

    fun onFollowedTagClicked(tag: Tag) {
        if (_followedTags.value.contains(tag)) {
            if (_selectedTagId.value == tag.tagId) {
                _selectedTagId.value = null
                println("tagId: ${_selectedTagId.value}\n")
            } else {
                _selectedTagId.value = tag.tagId
            }
        }
        filterEvents()
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
        filterWordList = _filtersContainer.value.searchEntry.lowercase().split(" ")
        filterTagSet = allTagSet.filter { tag -> areWordsInTag(tag, filterWordList) }.toSet()
        filterEvents()
    }

    private fun areWordsInTag(tag: Tag, listOfWords: List<String>): Boolean {
        return listOfWords.any { word -> tag.name.lowercase().contains(word) }
    }

    private fun areWordsInTitle(event: Event, listOfWords: List<String>): Boolean {
        return listOfWords.any { word -> event.title.lowercase().contains(word) }
    }

    private fun areWordsInDescription(event: Event, listOfWords: List<String>): Boolean {
        return listOfWords.any { word -> event.description.lowercase().contains(word) }
    }

    fun resetFiltersContainer() {
        _filtersContainer.value = defaultFiltersContainer
        refreshFiltersContainer()
    }

    // End of methods to set the filters container values

    private fun filterEvents() {
        if (_filtersContainer.value == defaultFiltersContainer) {
            _displayEventList.value =
                // special case, if the user follows no tags, display everything
                if (_followedTags.value.isEmpty()) {
                    allEventsList
                } else if (selectedTagId.value == null) {
                    allEventsList.filter { event ->
                        event.tags.any { tag ->
                            _followedTags.value.any { tag2 -> tag.tagId == tag2.tagId }
                        }
                    }
                } else {
                    allEventsList.filter { event ->
                        event.tags.any { tag -> tag.tagId == _selectedTagId.value!! }
                    }
                }
        } else {
            _displayEventList.value =
                allEventsList
                    .asSequence()
                    .filter { event -> // filter by tags, title or description
                        _filtersContainer.value.searchEntry == "" ||
                            event.tags.any { tag ->
                                filterTagSet.any { tag2 -> tag.tagId == tag2.tagId }
                            }
                                || areWordsInTitle(event, filterWordList)
                                || areWordsInDescription(event, filterWordList)
                    }
                    .filter { event -> // filter by time
                        dateFilterConditions(event)
                    }
                    // TODO : later add a radio button to filter only by one, and rewrite this like
                    // the
                    // scope
                    .filter { event -> // filter by status of the event (pending, confirmed, full)
                        (!_filtersContainer.value.confirmedChecked &&
                            !_filtersContainer.value.pendingChecked &&
                            !_filtersContainer.value.fullChecked) ||
                            (_filtersContainer.value.pendingChecked &&
                                event.participantCount < event.maxParticipants * STATUS_THRESHOLD ||
                                _filtersContainer.value.confirmedChecked &&
                                    (event.participantCount >=
                                        event.maxParticipants * STATUS_THRESHOLD &&
                                        event.participantCount < event.maxParticipants) ||
                                _filtersContainer.value.fullChecked &&
                                    event.participantCount == event.maxParticipants)
                    }
                    .filter { event ->
                        !_filtersContainer.value.epflChecked ||
                                (!event.tags.any { tag -> sectionTags.contains(tag) } &&
                                        !event.tags.any { tag -> semesterTags.contains(tag) })
                    }
                    .filter { event ->
                        !_filtersContainer.value.sectionChecked ||
                            _section.value == "" ||
                            event.tags.any { tag ->
                                tag.name.lowercase() == _section.value.lowercase()
                            }
                    }
                    .filter { event ->
                        !_filtersContainer.value.classChecked ||
                            _semester.value == "" ||
                            event.tags.any { tag ->
                                tag.name.lowercase() == _semester.value.lowercase()
                            }
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
                    .toList()

            // reverse the list if the sort by is descending
            if (_filtersContainer.value.sortBy == SortBy.DATE_DESC) {
                _displayEventList.value = _displayEventList.value.reversed()
            }
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
