package com.github.swent.echo.viewmodels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.R
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.compose.components.searchmenu.FiltersContainer
import com.github.swent.echo.compose.components.searchmenu.floatToDate
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Overlay to display on top of the main screen. NONE: No overlay. EVENT_INFO_SHEET: Display the
 * event info sheet. SEARCH_SHEET: Display the search sheet.
 */
enum class Overlay {
    NONE,
    EVENT_INFO_SHEET,
    SEARCH_SHEET
}

/**
 * Mode to display the events. MAP: Display the events on the map. LIST: Display the events in a
 * list.
 */
enum class MapOrListMode {
    MAP,
    LIST
}

// Enum class for the different states of the sort by filter
enum class SortBy(val stringKey: Int) {
    DATE_ASC(R.string.filters_container_sort_by_date_asc),
    DATE_DESC(R.string.filters_container_sort_by_date_desc),
    DISTANCE_ASC(R.string.filters_container_sort_by_distance_asc),
    DISTANCE_DESC(R.string.filters_container_sort_by_distance_desc),
}

// Threshold for the status of an event to be considered full or pending
const val STATUS_THRESHOLD = 0.5
// default value for when a dropdown is not selected
const val DEFAULT_DROPDOWN_VALUE = -1

/**
 * ViewModel for the home screen. Contains the logic to display the events, filter them, and display
 * the event info sheet.
 */
@HiltViewModel
class HomeScreenViewModel
@Inject
constructor(
    private val repository: Repository,
    private val authenticationService: AuthenticationService,
    private val networkService: NetworkService
) : ViewModel() {
    // Flow to observe the overlay to display on top of the main screen
    private val _overlay = MutableStateFlow(Overlay.NONE)
    val overlay = _overlay.asStateFlow()
    // Flow to observe the mode to display the events
    private val _mode = MutableStateFlow(MapOrListMode.MAP)
    val mode = _mode.asStateFlow()
    // private variables to store the filter values
    private var filterTagSet: Set<Tag> = setOf()
    private var filterWordList = listOf<String>()
    // private variables to store the events and tags
    private lateinit var allEventsList: List<Event>
    private lateinit var allTagSet: Set<Tag>
    // Flow to observe the list of events to display
    private val _displayEventList = MutableStateFlow<List<Event>>(listOf())
    val displayEventList = _displayEventList.asStateFlow()
    // Flow to observe the event to display in the event info sheet
    private val _displayEventInfo = MutableStateFlow<Event?>(null)
    val displayEventInfo = _displayEventInfo.asStateFlow()
    // Flow to observe if the user can modify the event
    private val _canUserModifyEvent = MutableStateFlow(false)
    val canUserModifyEvent = _canUserModifyEvent.asStateFlow()
    // Flow to observe the filters container
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
                sortBy = SortBy.DATE_ASC
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
            sortBy = SortBy.DATE_ASC
        )
    val filtersContainer = _filtersContainer.asStateFlow()
    // Flow to observe the profile name
    private val _profileName = MutableStateFlow("")
    val profileName = _profileName.asStateFlow()
    // Flow to observe the profile class
    private val _profileClass = MutableStateFlow("")
    val profileClass = _profileClass.asStateFlow()
    // Flow to observe the section
    private val _section = MutableStateFlow("")
    val section = _section.asStateFlow()
    // Flow to observe the semester
    private val _semester = MutableStateFlow("")
    val semester = _semester.asStateFlow()
    // root tag ids for the section and semester
    private val sectionTagId = "30f27641-bd63-42e7-9d95-6117ad997554"
    private val semesterTagId = "319715cd-6210-4e62-a061-c533095bd098"
    // list of all tags that are considered section or semester tags
    private lateinit var sectionTags: List<Tag>
    private lateinit var semesterTags: List<Tag>
    // Flow to observe the followed tags
    private val _followedTags = MutableStateFlow<List<Tag>>(listOf())
    val followedTags = _followedTags.asStateFlow()
    // Flow to observe the selected tag ids
    private val _selectedTagIds = MutableStateFlow<List<String>>(listOf())
    val selectedTagIds = _selectedTagIds.asStateFlow()
    private var followedTagFilter = listOf<Tag>()
    // Flow to observe the search mode
    private var _searchMode = MutableStateFlow(false)
    val searchMode = _searchMode.asStateFlow()
    private val _initialPage = MutableStateFlow(0)
    val initialPage = _initialPage.asStateFlow()
    // Flow to observe the network status
    val isOnline = networkService.isOnline
    private val _profilePicture = MutableStateFlow<Bitmap?>(null)
    val profilePicture = _profilePicture.asStateFlow()
    // Flow to observe the followed associations
    private val _followedAssociations = MutableStateFlow<List<String>>(listOf())
    val followedAssociations = _followedAssociations.asStateFlow()
    // Flow to observe the selected association
    private val _selectedAssociation = MutableStateFlow(DEFAULT_DROPDOWN_VALUE)
    val selectedAssociation = _selectedAssociation.asStateFlow()

    // Initialize the view model
    init {
        viewModelScope.launch {
            val userId = authenticationService.getCurrentUserID() ?: ""
            allEventsList = repository.getAllEvents().sortedBy { it.startDate }
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
            followedTagFilter = getTagsAndSubTags(_followedTags.value).toList()
            sectionTags = repository.getSubTags(sectionTagId)
            semesterTags = repository.getSubTags(semesterTagId)
            val pictureByteArray = repository.getUserProfilePicture(userId)
            _profilePicture.value =
                if (pictureByteArray != null) {
                    BitmapFactory.decodeByteArray(pictureByteArray, 0, pictureByteArray.size)
                } else {
                    null
                }
            val followedAssociationIds =
                repository.getUserProfile(userId)?.associationsSubscriptions?.map {
                    it.associationId
                } ?: listOf()
            _followedAssociations.value =
                repository.getAssociations(followedAssociationIds).map { it.name }
            refreshFiltersContainer()
        }
    }

    /**
     * Change the selected association to display the events of the selected association.
     *
     * @param selectedAssociation the index of the selected association
     */
    fun onAssociationSelected(selectedAssociation: Int) {
        _selectedAssociation.value = selectedAssociation
        refreshFiltersContainer()
    }

    /**
     * change the selection of tags to display and filter the events accordingly
     *
     * @param tag the tag that was clicked
     */
    fun onFollowedTagClicked(tag: Tag) {
        if (_followedTags.value.contains(tag)) {
            if (_selectedTagIds.value.contains(tag.tagId)) {
                _selectedTagIds.value = _selectedTagIds.value.minus(tag.tagId)
            } else {
                _selectedTagIds.value = _selectedTagIds.value.plus(tag.tagId)
            }
            val empty = _selectedTagIds.value.isEmpty()
            followedTagFilter =
                getTagsAndSubTags(
                        followedTags.value.filter { ftag ->
                            empty || _selectedTagIds.value.contains(ftag.tagId)
                        }
                    )
                    .toList()
        }
        filterEvents()
    }

    /**
     * changes the filtered information based on the search entry
     *
     * @param searchEntry the search entry to filter the events
     */
    fun onSearchEntryChanged(searchEntry: String) {
        _filtersContainer.value = _filtersContainer.value.copy(searchEntry = searchEntry)
        refreshFiltersContainer()
    }

    // Methods to set the filters container values

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
            _filtersContainer.value.copy(
                pendingChecked = !_filtersContainer.value.pendingChecked,
                confirmedChecked = false,
                fullChecked = false
            )
        refreshFiltersContainer()
    }

    fun onConfirmedCheckedSwitch() {
        _filtersContainer.value =
            _filtersContainer.value.copy(
                confirmedChecked = !_filtersContainer.value.confirmedChecked,
                pendingChecked = false,
                fullChecked = false
            )
        refreshFiltersContainer()
    }

    fun onFullCheckedSwitch() {
        _filtersContainer.value =
            _filtersContainer.value.copy(
                fullChecked = !_filtersContainer.value.fullChecked,
                confirmedChecked = false,
                pendingChecked = false
            )
        refreshFiltersContainer()
    }

    fun onDateFilterChanged(from: Float, to: Float) {
        _filtersContainer.value = _filtersContainer.value.copy(from = from, to = to)
        refreshFiltersContainer()
    }

    fun onSortByChanged(sortBy: Int) {
        _filtersContainer.value =
            _filtersContainer.value.copy(
                sortBy = if (sortBy < 0) SortBy.DATE_ASC else SortBy.entries[sortBy]
            )
        refreshFiltersContainer()
    }

    /** Sign out the user. */
    fun signOut() {
        viewModelScope.launch { authenticationService.signOut() }
    }

    /** Refresh the filters container and filter the events accordingly. */
    private fun refreshFiltersContainer() {
        filterWordList = _filtersContainer.value.searchEntry.lowercase().split(" ")
        val temp = allTagSet.filter { tag -> areWordsInTag(tag, filterWordList) }
        filterTagSet = getTagsAndSubTags(temp)

        updateSearchMode()
        filterEvents()
    }

    /**
     * Get all the tags and their sub tags from the initial tag list.
     *
     * @param initialTagList the initial list of tags
     */
    private fun getTagsAndSubTags(initialTagList: List<Tag>): Set<Tag> {
        if (initialTagList.isEmpty()) return setOf()

        var ret = initialTagList.toSet()
        val subTags = mutableListOf<Tag>()
        for (tag in initialTagList) {
            subTags += getSubTagsLocal(tag.tagId)
        }
        ret = ret.plus(getTagsAndSubTags(subTags))

        return ret
    }

    /**
     * Method to do locally the recursive search of the sub tags instead of calling the repository.
     *
     * @param tagId the id of the tag to get the sub tags from
     */
    private fun getSubTagsLocal(tagId: String): List<Tag> {
        return allTagSet.filter { tag -> tag.parentId == tagId }
    }

    /**
     * Update the search mode based on the filters container values. If the filter container is not
     * the default one, the search mode is on.
     */
    private fun updateSearchMode() {
        _searchMode.value =
            _filtersContainer.value != defaultFiltersContainer ||
                _selectedAssociation.value != DEFAULT_DROPDOWN_VALUE
    }

    /**
     * Check if the tag contains any of the words in the list of words.
     *
     * @param tag the tag to check
     * @param listOfWords the list of words to check
     */
    private fun areWordsInTag(tag: Tag, listOfWords: List<String>): Boolean {
        return listOfWords.any { word -> tag.name.lowercase().contains(word) }
    }

    /**
     * Check if any of the words in the list of words are in the title of the event.
     *
     * @param event the event to check
     * @param listOfWords the list of words to check
     */
    private fun areWordsInTitle(event: Event, listOfWords: List<String>): Boolean {
        return listOfWords.any { word -> event.title.lowercase().contains(word) }
    }

    /**
     * Check if any of the words in the list of words are in the description of the event.
     *
     * @param event the event to check
     * @param listOfWords the list of words to check
     */
    private fun areWordsInDescription(event: Event, listOfWords: List<String>): Boolean {
        return listOfWords.any { word -> event.description.lowercase().contains(word) }
    }

    /** Reset the filters container to the default values. */
    fun resetFiltersContainer() {
        _filtersContainer.value = defaultFiltersContainer
        refreshFiltersContainer()
    }

    // End of methods to set the filters container values

    /**
     * Filter the events based on one of the two modes, search or follow. If the search mode is off,
     * the events are filtered based on the tags the user follows. If the search mode is on, the
     * events are filtered based on the search entry and the filters container values. The events
     * are always filtered by date to avoid displaying past events.
     */
    private fun filterEvents() {
        if (!_searchMode.value) {
            _displayEventList.value =
                // special case, if the user follows no tags, display everything
                if (_followedTags.value.isEmpty()) {
                    allEventsList.filter { event
                        -> // filter by time to avoid displaying past events
                        dateFilterConditions(event)
                    }
                } else {
                    allEventsList
                        .filter { event ->
                            event.tags.any { tag ->
                                followedTagFilter.any { tag2 -> tag.tagId == tag2.tagId }
                            }
                        }
                        .filter { event -> // filter by time to avoid displaying past events
                            dateFilterConditions(event)
                        }
                }
        } else {
            _displayEventList.value =
                allEventsList
                    .asSequence()
                    .filter { event ->
                        // filter by tags, title or description
                        _filtersContainer.value.searchEntry == "" ||
                            event.tags.any { tag ->
                                filterTagSet.any { tag2 -> tag.tagId == tag2.tagId }
                            } ||
                            areWordsInTitle(event, filterWordList) ||
                            areWordsInDescription(event, filterWordList)
                    }
                    .filter { event ->
                        // filter by time
                        dateFilterConditions(event)
                    }
                    .filter { event ->
                        // filter by confirmed check
                        !_filtersContainer.value.confirmedChecked ||
                            event.maxParticipants <= 0 ||
                            (event.participantCount >= event.maxParticipants * STATUS_THRESHOLD &&
                                event.participantCount < event.maxParticipants)
                    }
                    .filter { event ->
                        // filter by pending check
                        !_filtersContainer.value.pendingChecked ||
                            event.maxParticipants <= 0 ||
                            event.participantCount < event.maxParticipants * STATUS_THRESHOLD
                    }
                    .filter { event ->
                        // filter by full check
                        !_filtersContainer.value.fullChecked ||
                            event.maxParticipants <= 0 ||
                            event.participantCount == event.maxParticipants
                    }
                    .filter { event ->
                        // filter by epfl check
                        !_filtersContainer.value.epflChecked ||
                            (!event.tags.any { tag -> sectionTags.contains(tag) } &&
                                !event.tags.any { tag -> semesterTags.contains(tag) })
                    }
                    .filter { event ->
                        // filter by section
                        !_filtersContainer.value.sectionChecked ||
                            _section.value == "" ||
                            event.tags.any { tag ->
                                tag.name.lowercase() == _section.value.lowercase()
                            }
                    }
                    .filter { event ->
                        // filter by semester
                        !_filtersContainer.value.classChecked ||
                            _semester.value == "" ||
                            event.tags.any { tag ->
                                tag.name.lowercase() == _semester.value.lowercase()
                            }
                    }
                    .filter { event ->
                        // filter by association
                        _selectedAssociation.value == DEFAULT_DROPDOWN_VALUE ||
                            event.organizer?.name ==
                                _followedAssociations.value[_selectedAssociation.value]
                    }
                    .sortedBy { event ->
                        event.startDate
                        // when we can sort by distance, update this
                        /*when (_filtersContainer.value.sortBy) {
                            SortBy.DATE_ASC -> event.startDate
                            SortBy.DATE_DESC -> event.startDate
                            else ->
                        }*/
                    }
                    .toList()

            // reverse the list if the sort by is descending
            if (
                _filtersContainer.value.sortBy == SortBy.DATE_DESC
            ) { // when we can filter by distance, update this too
                _displayEventList.value = _displayEventList.value.reversed()
            }
        }
    }

    /**
     * Displays the event info sheet for the given event. Set the overlay to EVENT_INFO_SHEET.
     *
     * @param event the event to display
     */
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

    fun refreshEvents() {
        viewModelScope.launch {
            allEventsList = repository.getAllEvents()
            _displayEventInfo.value =
                allEventsList.find { it.eventId == displayEventInfo.value?.eventId }
            filterEvents()
        }
    }
}
