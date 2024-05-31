package com.github.swent.echo.viewmodels.myevents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// This ViewModel is responsible for managing the data related to the user's events.
@HiltViewModel
class MyEventsViewModel
@Inject
constructor(
    private val repository: Repository, // Repository for fetching data
    private val authenticationService: AuthenticationService, // Service for managing authentication
    private val networkService: NetworkService // Service for managing network
) : ViewModel() {
    // User ID
    val user = authenticationService.getCurrentUserID() ?: ""
    // Joined events
    private val _joinedEvents = MutableStateFlow<List<Event>>(listOf())
    val joinedEvents = _joinedEvents.asStateFlow()
    // Created events
    private val _createdEvents = MutableStateFlow<List<Event>>(listOf())
    val createdEvents = _createdEvents.asStateFlow()
    // Online status
    val isOnline = networkService.isOnline

    // Initialize the ViewModel
    init {
        viewModelScope.launch {
            _joinedEvents.value = repository.getJoinedEvents(user)
            _createdEvents.value = repository.getAllEvents().filter { it.creator.userId == user }
        }
    }

    // Handle join/leave event
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

    // Refresh events
    fun refreshEvents() {
        viewModelScope.launch {
            _joinedEvents.value = repository.getJoinedEvents(user)
            _createdEvents.value = repository.getAllEvents().filter { it.creator.userId == user }
        }
    }
}
