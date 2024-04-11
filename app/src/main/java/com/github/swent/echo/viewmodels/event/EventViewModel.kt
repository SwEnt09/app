package com.github.swent.echo.viewmodels.event

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import java.time.Instant
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** represents an event, used in the event screens */
class EventViewModel() : ViewModel() {
    private val emptyEvent =
        Event(
            "",
            "",
            "",
            "",
            Location("", 0.0, 0.0),
            Date.from(Instant.now()),
            Date.from(Instant.now()),
            setOf()
        )
    private val _event = MutableStateFlow<Event>(emptyEvent)
    private val _status = MutableStateFlow<EventStatus>(EventStatus.New)

    // placeholder for the list of all tags
    private val allTagsList = listOf(Tag("1", "tag1"), Tag("2", "tag2"), Tag("3", "tag3"))

    // return the event
    fun getEvent(): Event {
        return _event.asStateFlow().value
    }

    // return the organizer name of the event
    fun getOrganizerName(): String {
        // TODO: get organizer name from repository
        return ""
    }

    // return the list of possible organizer for the user
    fun getOrganizerList(): List<String> {
        // placeholder until we get organizer list from repository
        return listOf("testOrganizer", "anotherTestOrganizer")
    }

    // set the organizer of the event
    fun setOrganizer(organizer: String) {
        // TODO: set organizer of the event (need repository)
    }

    // update the event in the ViewModel
    fun setEvent(newEvent: Event) {
        if (_status.value == EventStatus.Saving) {
            Log.w("set event", "trying to update the event but it's not saved yet")
        } else if (_status.value !is EventStatus.Error) {
            _event.value = newEvent
            if (_status.value != EventStatus.New) {
                _status.value = EventStatus.Modified
            }
        }
    }

    /**
     * if the tagName is in the global tag list, add it as a tag to the event tag list return a tag
     * if the string match its name or null otherwise
     */
    fun getAndAddTagFromString(tagName: String): Tag? {
        val tag = allTagsList.filter { (_, name) -> name == tagName }
        if (tag.isNotEmpty()) {
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
            if(eventIsValid()){
                _status.value = EventStatus.Saving
                // TODO: save in repository
            }
        }
    }

    // return the status of the event
    fun getStatus(): EventStatus {
        return _status.asStateFlow().value
    }

    /**
     * check the current event has valid data
     * if not return false and set _status to Error
     */
    private fun eventIsValid(): Boolean {
        val event  = _event.value
        if(event.startDate.after(event.endDate)){
            _status.value = EventStatus.Error("end date before start date")
        }
        if(event.title.isBlank()){
            _status.value = EventStatus.Error("title is empty")
        }
        return _status.value is EventStatus.Error
    }
}

/** the different status of an event */
sealed class EventStatus {
    // new event not in the repository
    data object New : EventStatus()
    // same as in the repository
    data object Saved : EventStatus()
    // different from the version in the repository
    data object Modified : EventStatus()
    // syncing with the version in the repository
    data object Saving : EventStatus()

    data class Error(val error: String) : EventStatus()
}
