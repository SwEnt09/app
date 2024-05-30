package com.github.swent.echo.viewmodels.association

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.toAssociationHeader
import com.github.swent.echo.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// This ViewModel is responsible for managing the data related to associations.
@HiltViewModel
class AssociationViewModel
@Inject
constructor(
    private val repository: Repository, // Repository for fetching data
    private val authenticationService: AuthenticationService, // Service for managing authentication
    private val networkService: NetworkService // Service for managing network
) : ViewModel() {
    // All associations
    private lateinit var allAssociations: List<Association>
    // All events
    private lateinit var allEvents: List<Event>
    // Followed associations
    private val _followedAssociations = MutableStateFlow<List<Association>>(listOf())
    val followedAssociations = _followedAssociations.asStateFlow()
    // Committee associations
    private val _committeeAssociations = MutableStateFlow<List<Association>>(listOf())
    val committeeAssociations = _committeeAssociations.asStateFlow()
    // All associations to show
    private val _showAllAssociations = MutableStateFlow<List<Association>>(listOf())
    val showAllAssociations = _showAllAssociations.asStateFlow()
    // Initial page
    private val _initialPage = MutableStateFlow(0)
    val initialPage = _initialPage.asStateFlow()
    // Current association page
    private val _currentAssociationPage = MutableStateFlow(Association.EMPTY)
    val currentAssociationPage = _currentAssociationPage.asStateFlow()
    // Searched term
    private val _searched = MutableStateFlow("")
    val searched = _searched.asStateFlow()
    // Online status
    val isOnline = networkService.isOnline

    // Initialize the ViewModel
    init {
        viewModelScope.launch {
            val user = authenticationService.getCurrentUserID() ?: ""
            allAssociations = repository.getAllAssociations()
            allEvents = repository.getAllEvents()
            val followedAssociationIds =
                repository.getUserProfile(user)?.associationsSubscriptions?.map { it.associationId }
                    ?: listOf()
            _followedAssociations.value = repository.getAssociations(followedAssociationIds)
            val committeeAssociationIds =
                repository.getUserProfile(user)?.committeeMember?.map { it.associationId }
                    ?: listOf()
            _committeeAssociations.value = repository.getAssociations(committeeAssociationIds)
            _showAllAssociations.value = allAssociations
        }
    }

    // Handle follow/unfollow association
    fun onFollowAssociationChanged(association: Association) {
        if (_followedAssociations.value.contains(association)) {
            _followedAssociations.value -= association
        } else {
            _followedAssociations.value += association
        }
        viewModelScope.launch {
            val userProfile = repository.getUserProfile(authenticationService.getCurrentUserID()!!)
            val updatedProfile =
                userProfile?.copy(
                    associationsSubscriptions =
                        _followedAssociations.value.toAssociationHeader().toSet()
                )
            repository.setUserProfile(updatedProfile!!)
        }
    }

    // Set searched term
    fun setSearched(searched: String) {
        _searched.value = searched
    }

    // Get events of an association
    fun associationEvents(association: Association): List<Event> {
        return allEvents.filter { it.organizer?.associationId == association.associationId }
    }

    // Filter associations based on searched term
    fun filterAssociations(associations: List<Association>): List<Association> {
        return if (_searched.value.isEmpty()) {
            associations
        } else {
            associations.filter {
                it.name.lowercase().contains(_searched.value.lowercase()) ||
                    it.relatedTags
                        .map { tag -> tag.name.lowercase() }
                        .contains(_searched.value.lowercase())
            }
        }
    }

    // Set current association page
    fun setCurrentAssociationPage(association: Association, initialPage: Int = _initialPage.value) {
        _initialPage.value = initialPage
        _currentAssociationPage.value = association
    }

    // Refresh events
    fun refreshEvents() {
        viewModelScope.launch { allEvents = repository.getAllEvents() }
    }
}
