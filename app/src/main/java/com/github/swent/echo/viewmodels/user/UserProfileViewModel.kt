package com.github.swent.echo.viewmodels.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// represent a user profile
@HiltViewModel
class UserProfileViewModel
@Inject
constructor(
    private val repository: Repository,
    private val authenticationService: AuthenticationService,
) : ViewModel() {
    private val _userProfile = MutableStateFlow(UserProfile.EMPTY)
    val userProfile = _userProfile.asStateFlow()

    init {
        viewModelScope.launch {
            assert(authenticationService.userIsLoggedIn())
            _userProfile.value =
                repository.getUserProfile(authenticationService.getCurrentUserID()!!)!!
        }
    }

    // change the user profile in the database
    fun setUserProfile(newUserProfile: UserProfile) {
        viewModelScope.launch {
            repository.setUserProfile(newUserProfile)
            _userProfile.value =
                repository.getUserProfile(authenticationService.getCurrentUserID()!!)!!
        }
    }
}
