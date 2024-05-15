package com.github.swent.echo.viewmodels.association

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AssociationPage(var association: Association) {
    MAINSCREEN(Association.EMPTY),
    DETAILS(Association.EMPTY),
    SEARCH(Association.EMPTY)
}

enum class AssociationOverlay {
    NONE,
    SEARCH
}

// represent associations
@HiltViewModel
class AssociationViewModel
@Inject
constructor(
    private val repository: Repository,
    private val authenticationService: AuthenticationService,
    private val networkService: NetworkService
) : ViewModel() {
    private lateinit var allAssociations: List<Association>
    private lateinit var allEvents: List<Event>
    private val _followedAssociations = MutableStateFlow<List<Association>>(listOf())
    val followedAssociations = _followedAssociations.asStateFlow()
    private val _committeeAssociations = MutableStateFlow<List<Association>>(listOf())
    val committeeAssociations = _committeeAssociations.asStateFlow()
    private val _filteredEvents = MutableStateFlow<List<Event>>(listOf())
    val filteredEvents = _filteredEvents.asStateFlow()
    private val _eventsFilter = MutableStateFlow<List<Association>>(listOf())
    val eventsFilter = _eventsFilter.asStateFlow()
    private val _lastAssociationPage = MutableStateFlow(AssociationPage.MAINSCREEN)
    private val _actualAssociationPage = MutableStateFlow(AssociationPage.MAINSCREEN)
    val actualAssociationPage = _actualAssociationPage.asStateFlow()
    private val _overlay = MutableStateFlow(AssociationOverlay.NONE)
    val overlay = _overlay.asStateFlow()
    private val _searched = MutableStateFlow("")
    val searched = _searched.asStateFlow()
    val isOnline = networkService.isOnline

    init {
        viewModelScope.launch {
            val user = authenticationService.getCurrentUserID() ?: ""
            allAssociations = repository.getAllAssociations()
            allEvents = repository.getAllEvents()
            _followedAssociations.value =
                repository.getUserProfile(user)?.associationsSubscriptions?.toList() ?: listOf()
            _committeeAssociations.value =
                repository.getUserProfile(user)?.committeeMember?.toList() ?: listOf()
            _filteredEvents.value = computeFilteredEvents()
        }
    }

    fun onFollowAssociationChanged(association: Association) {
        if (_followedAssociations.value.contains(association)) {
            _followedAssociations.value -= association
            _eventsFilter.value -= association
        } else {
            _followedAssociations.value += association
        }
        viewModelScope.launch {
            val userProfile = repository.getUserProfile(authenticationService.getCurrentUserID()!!)
            val updatedProfile =
                userProfile?.copy(associationsSubscriptions = _followedAssociations.value.toSet())
            repository.setUserProfile(updatedProfile!!)
        }
        _filteredEvents.value = computeFilteredEvents()
    }

    fun goBack() {
        if (
            (_actualAssociationPage.value == AssociationPage.DETAILS) &&
                (_lastAssociationPage.value == AssociationPage.SEARCH)
        ) {
            _actualAssociationPage.value = AssociationPage.SEARCH
        } else {
            _actualAssociationPage.value = AssociationPage.MAINSCREEN
        }
    }

    fun goTo(page: AssociationPage) {
        _lastAssociationPage.value = _actualAssociationPage.value
        _actualAssociationPage.value = page
    }

    fun setOverlay(overlay: AssociationOverlay) {
        _overlay.value = overlay
    }

    fun setSearched(searched: String) {
        _searched.value = searched
    }

    fun associationEvents(association: Association): List<Event> {
        return allEvents.filter { it.organizer == association }
    }

    fun filterAssociations(): List<Association> {
        return if (_searched.value.isEmpty()) {
            allAssociations
        } else {
            allAssociations.filter {
                it.name.lowercase().contains(_searched.value.lowercase())
                // || it.tags.lowercase().contains(_searched.value.lowercase())
            }
        }
    }

    fun onAssociationToFilterChanged(association: Association) {
        if (_eventsFilter.value.contains(association)) {
            _eventsFilter.value -= association
        } else {
            _eventsFilter.value += association
        }
        _filteredEvents.value = computeFilteredEvents()
    }

    private fun computeFilteredEvents(): List<Event> {
        return if (_eventsFilter.value.isEmpty()) {
            allEvents.filter {
                _followedAssociations.value.contains(it.organizer) ||
                    _committeeAssociations.value.contains(it.organizer)
            }
        } else {
            allEvents.filter { _eventsFilter.value.contains(it.organizer) }
        }
    }
}
