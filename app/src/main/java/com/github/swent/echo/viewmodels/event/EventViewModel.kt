package com.github.swent.echo.viewmodels.event

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.R
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.AssociationHeader
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.data.repository.RepositoryStoreWhileNoInternetException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * This class is the viewmodel of an event, it used in the event screens.
 *
 * @param repository a repository
 * @param authenticationService an authentication service
 * @param savedState a SavedStateHandle to pass some arguments (userId and location) to the
 *   viewmodel through the navigation
 * @param networkService a network service used to get the state of the network
 */
@HiltViewModel
class EventViewModel
@Inject
constructor(
    private val repository: Repository,
    private val authenticationService: AuthenticationService,
    private val savedState: SavedStateHandle,
    private val networkService: NetworkService
) : ViewModel() {

    private val _event = MutableStateFlow<Event>(Event.EMPTY)
    private val _status = MutableStateFlow<EventStatus>(EventStatus.Saving)
    val event = _event.asStateFlow()
    val status = _status.asStateFlow()
    private lateinit var allTagsList: List<Tag>
    private val _isEventNew = MutableStateFlow(true)
    val isEventNew = _isEventNew.asStateFlow()
    private val _organizerList = MutableStateFlow<List<String>>(listOf())
    val organizerList = _organizerList.asStateFlow()
    val isOnline = networkService.isOnline

    /** Initialize async values */
    init {
        viewModelScope.launch {
            val userid = authenticationService.getCurrentUserID()
            if (userid == null) {
                _status.value = EventStatus.Error(R.string.event_creation_error_not_logged_in)
                Log.e("create event", "the user is not logged in")
            } else {
                val userProfile = repository.getUserProfile(userid)
                if (userProfile == null) {
                    throw Exception(
                        "Error: you can't edit or create an event with a null user profile"
                    )
                }
                val username = userProfile.name
                _organizerList.value =
                    userProfile.committeeMember.map { association -> association.name } + username
                if (savedState.contains("eventId")) {
                    val repositoryEvent = repository.getEvent(savedState.get<String>("eventId")!!)
                    if (repositoryEvent == null) {
                        _event.value =
                            _event.value.copy(
                                creator = userProfile.toEventCreator(),
                                organizer = null
                            )
                        _status.value = EventStatus.Modified
                    } else {
                        _isEventNew.value = false
                        _event.value = repositoryEvent
                        _status.value = EventStatus.Saved
                    }
                } else {
                    _event.value =
                        _event.value.copy(creator = userProfile.toEventCreator(), organizer = null)
                    if (savedState.contains("location")) {
                        val savedLocation =
                            Json.decodeFromString<Location>(savedState.get<String>("location")!!)
                        _event.value = _event.value.copy(location = savedLocation)
                    }
                    _status.value = EventStatus.Modified
                }
            }
            allTagsList = repository.getAllTags()
        }
    }

    /**
     * Set the organizer of the event.
     *
     * @param organizerName the name of the organizer
     */
    fun setOrganizer(organizerName: String) {
        viewModelScope.launch {
            if (organizerName == _event.value.creator.name) {
                setEvent(event.value.copy(organizer = null))
            } else {
                setEvent(
                    event.value.copy(
                        organizer =
                            AssociationHeader.fromAssociation(
                                repository.getAllAssociations().find { association ->
                                    association.name == organizerName
                                }
                            )
                    )
                )
            }
        }
    }

    /**
     * Update the event in the ViewModel.
     *
     * @param newEvent the new event
     */
    fun setEvent(newEvent: Event) {
        if (_status.value == EventStatus.Saving) {
            Log.w("set event", "trying to update the event but it's not saved yet")
        } else if (_status.value !is EventStatus.Error) {
            _event.value = newEvent
            _status.value = EventStatus.Modified
        }
    }

    /** Save the current event in the repository. */
    fun saveEvent() {
        if (_status.value == EventStatus.Saving) {
            Log.w("save event", "trying to save the event but it's already saving")
        } else if (_status.value == EventStatus.Saved) {
            Log.w("save event", "trying to save the event but it's already saved")
        } else {
            val eventStatusBeforeSaving = _status.value
            if (eventIsValid()) {
                _status.value = EventStatus.Saving
                viewModelScope.launch {
                    try {
                        if (isEventNew.value) {
                            val eventId = repository.createEvent(event.value)
                            _event.value = event.value.copy(eventId = eventId)
                        } else {
                            repository.setEvent(event.value)
                        }
                        _isEventNew.value = false
                        _status.value = EventStatus.Saved
                    } catch (e: RepositoryStoreWhileNoInternetException) {
                        _status.value =
                            EventStatus.Error(R.string.event_creation_error_network_failure)
                    }
                }
            }
        }
    }

    /** Change event status from error to modified. */
    fun dismissError() {
        if (_status.value is EventStatus.Error) {
            _status.value = EventStatus.Modified
        }
    }

    /** Check the current event has valid data if not return false and set _status to Error. */
    private fun eventIsValid(): Boolean {
        val event = _event.value
        if (event.startDate.isAfter(event.endDate)) {
            _status.value =
                EventStatus.Error(R.string.event_creation_error_end_date_before_start_date)
        }
        if (event.title.isBlank()) {
            _status.value = EventStatus.Error(R.string.event_creation_error_empty_title)
        }
        if (event.maxParticipants <= 0) {
            _status.value = EventStatus.Error(R.string.event_creation_error_max_participant)
        }
        if (event.creator == EventCreator.EMPTY) {
            _status.value = EventStatus.Error(R.string.event_creation_error_not_logged_in)
        }
        return _status.value !is EventStatus.Error
    }
}

/** The different status of an event. */
sealed class EventStatus {
    // same as in the repository
    data object Saved : EventStatus()
    // different from the version in the repository
    data object Modified : EventStatus()
    // syncing with the version in the repository
    data object Saving : EventStatus()

    data class Error(val errorRef: Int) : EventStatus()
}
