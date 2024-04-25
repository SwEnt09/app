package com.github.swent.echo.viewmodels.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.ui.navigation.NavigationActions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class createProfileViewModel
@Inject
constructor(
    private val repository: Repository,
    private val navAction: NavigationActions,
    private val authenticationService: AuthenticationService,
) : ViewModel() {

    private val _firstName = MutableStateFlow("")
    val firstName = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName = _lastName.asStateFlow()

    private val _selectedSection = MutableStateFlow(null)
    val selectedSection = _selectedSection.asStateFlow()

    private val _selectedSemester = MutableStateFlow(null)
    val selectedSemester = _selectedSemester.asStateFlow()

    private val _tagList = MutableStateFlow<List<Tag>>(listOf())
    val tagList = _tagList.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    //  private val _profileCreationStatus =
    // MutableStateFlow<ProfileCreationStatus>(ProfileCreationStatus.Initial)
    // val profileCreationStatus = _profileCreationStatus.asStateFlow()

    fun profilesave() {

        viewModelScope.launch {
            val userId = authenticationService.getCurrentUserID()
            if (userId == null) {
                _errorMessage.value = "Profile creation error: Not logged in"
                // TODO: handle error later on. Will this ever be the case?
            } else {

                // TODO: this is the logic for creating a userProfile, changes are required in the
                // getUserProfile function so that it returns null if a user has not yet created a
                // user profile

                val userProfile =
                    repository.setUserProfile(
                        UserProfile(
                            userId,
                            name = "$_firstName $_lastName",
                            semester = _selectedSemester.value,
                            section = _selectedSection.value,
                            tags = _tagList.value.toSet()
                        )
                    )
                // val userProfile = repository.getUserProfile(userId)
                // _tagList.value = userProfile.tags.toList()
            }
        }
    }

    // Add tag button
    fun addTag(tag: Tag) {
        val currentList = _tagList.value.toMutableList()
        currentList.add(tag)
        _tagList.value = currentList
    }

    // Back button
    fun navigateBack() {
        navAction.goBack()
    }
}
