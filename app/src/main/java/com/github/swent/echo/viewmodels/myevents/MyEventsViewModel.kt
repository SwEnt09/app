package com.github.swent.echo.viewmodels.myevents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MyEventsViewModel
@Inject
constructor(
    private val repository: Repository,
    private val authenticationService: AuthenticationService,
) : ViewModel() {
    private lateinit var user: String
    private val _joinedEvents = MutableStateFlow<List<Event>>(listOf())
    val joinedEvents = _joinedEvents.asStateFlow()
    private val _createdEvents = MutableStateFlow<List<Event>>(listOf())
    val createdEvents = _createdEvents.asStateFlow()

    init {
        viewModelScope.launch {
            user = authenticationService.getCurrentUserID() ?: ""
            _joinedEvents.value = repository.getJoinedEvents(user)
            _createdEvents.value = repository.getAllEvents().filter { it.creator.userId == user }
        }
    }

    fun joinOrLeaveEvent(event: Event, onFinished: () -> Unit) {
        viewModelScope.launch {
            if (_joinedEvents.value.map { it.eventId }.contains(event.eventId)) {
                repository.leaveEvent(user, event)
            } else {
                repository.joinEvent(user, event)
            }
            _joinedEvents.value = repository.getJoinedEvents(user)
            onFinished()
        }
    }

    fun refreshEvents() {
        viewModelScope.launch {
            _joinedEvents.value = repository.getJoinedEvents(user)
            _createdEvents.value = repository.getAllEvents().filter { it.creator.userId == user }
        }
    }
}
