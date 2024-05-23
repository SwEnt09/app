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
    private val _showAllAssociations = MutableStateFlow<List<Association>>(listOf())
    val showAllAssociations = _showAllAssociations.asStateFlow()
    private val _initialPage = MutableStateFlow(0)
    val initialPage = _initialPage.asStateFlow()
    private val _currentAssociationPage = MutableStateFlow(Association.EMPTY)
    val currentAssociationPage = _currentAssociationPage.asStateFlow()
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
            _showAllAssociations.value = allAssociations
        }
    }

    fun onFollowAssociationChanged(association: Association) {
        if (_followedAssociations.value.contains(association)) {
            _followedAssociations.value -= association
        } else {
            _followedAssociations.value += association
        }
        viewModelScope.launch {
            val userProfile = repository.getUserProfile(authenticationService.getCurrentUserID()!!)
            val updatedProfile =
                userProfile?.copy(associationsSubscriptions = _followedAssociations.value.toSet())
            repository.setUserProfile(updatedProfile!!)
        }
    }

    fun setSearched(searched: String) {
        _searched.value = searched
    }

    fun associationEvents(association: Association): List<Event> {
        return allEvents.filter { it.organizer == association }
    }

    fun filterAssociations(associations: List<Association>): List<Association> {
        return if (_searched.value.isEmpty()) {
            associations
        } else {
            associations.filter {
                it.name.lowercase().contains(_searched.value.lowercase())
                // || it.tags.lowercase().contains(_searched.value.lowercase())
            }
        }
    }

    fun setCurrentAssociationPage(association: Association, initialPage: Int = _initialPage.value) {
        _initialPage.value = initialPage
        _currentAssociationPage.value = association
    }

    fun refreshEvents() {
        viewModelScope.launch { allEvents = repository.getAllEvents() }
    }
}
