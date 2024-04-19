package com.github.swent.echo.viewmodels.event

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.R
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** represents an event, used in the event screens */
@HiltViewModel
class EventViewModel
@Inject
constructor(
    private val repository: Repository,
    private val authenticationService: AuthenticationService,
    private val savedEventId: SavedStateHandle
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

    // initialize async values
    init {
        viewModelScope.launch {
            val userid = authenticationService.getCurrentUserID()
            if (userid == null) {
                _status.value = EventStatus.Error(R.string.event_creation_error_not_logged_in)
                Log.e("create event", "the user is not logged in")
            } else {
                val username = repository.getUserProfile(userid).name
                if (savedEventId.contains("eventId")) {
                    _isEventNew.value = false
                    _event.value = repository.getEvent(savedEventId.get<String>("eventId")!!)
                    _status.value = EventStatus.Saved
                } else {
                    _event.value =
                        _event.value.copy(
                            creator = repository.getUserProfile(userid),
                            organizer = null
                        )
                    _status.value = EventStatus.Modified
                }
                // TODO: also add the associations linked to the user to the organizerList (not
                // in repository yet)
                _organizerList.value = listOf(username)
            }
            allTagsList = repository.getAllTags()
        }
    }

    // set the organizer of the event
    fun setOrganizer(organizerName: String) {
        viewModelScope.launch {
            if (organizerName == _event.value.creator.name) {
                setEvent(event.value.copy(organizer = null))
            } else {
                // TODO: check if it's an association linked to the user (no method or attribute
                // available)
                TODO("Set organizer as association not implemented")
            }
        }
    }

    // update the event in the ViewModel
    fun setEvent(newEvent: Event) {
        if (_status.value == EventStatus.Saving) {
            Log.w("set event", "trying to update the event but it's not saved yet")
        } else if (_status.value !is EventStatus.Error) {
            _event.value = newEvent
            _status.value = EventStatus.Modified
        }
    }

    /**
     * if the tagName is in the global tag list, add it as a tag to the event tag list return a tag
     * if the string match its name or null otherwise
     */
    fun getAndAddTagFromString(tagName: String): Tag? {
        if (allTagsList.any { (_, name) -> name == tagName }) {
            val tag = allTagsList.filter { (_, name) -> name == tagName }
            setEvent(_event.value.copy(tags = _event.value.tags + tag.first()))
            return tag.first()
        }
        return null
    }

    // delete a tag from the tags of the event if present
    fun deleteTag(tag: Tag) {
        setEvent(_event.value.copy(tags = _event.value.tags.filter { t -> t != tag }.toSet()))
    }

    // save the current event in the repository
    fun saveEvent() {
        if (_status.value == EventStatus.Saving) {
            Log.w("save event", "trying to save the event but it's already saving")
        } else if (_status.value == EventStatus.Saved) {
            Log.w("save event", "trying to save the event but it's already saved")
        } else {
            if (eventIsValid()) {
                _status.value = EventStatus.Saving
                viewModelScope.launch {
                    if (isEventNew.value) {
                        // TODO: get new eventId for new events (not implemented yet in the
                        // repository)
                    } else {
                        repository.setEvent(_event.value)
                    }
                    _isEventNew.value = false
                    _status.value = EventStatus.Saved
                }
            }
        }
    }

    /** check the current event has valid data if not return false and set _status to Error */
    private fun eventIsValid(): Boolean {
        val event = _event.value
        if (event.startDate.isAfter(event.endDate)) {
            _status.value =
                EventStatus.Error(R.string.event_creation_error_end_date_before_start_date)
        }
        if (event.title.isBlank()) {
            _status.value = EventStatus.Error(R.string.event_creation_error_empty_title)
        }
        return _status.value !is EventStatus.Error
    }
}

/** the different status of an event */
sealed class EventStatus {
    // same as in the repository
    data object Saved : EventStatus()
    // different from the version in the repository
    data object Modified : EventStatus()
    // syncing with the version in the repository
    data object Saving : EventStatus()

    data class Error(val errorRef: Int) : EventStatus()
}
