package com.github.swent.echo.viewmodels.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CreateProfileViewModel
@Inject
constructor(
    private val authenticationService: AuthenticationService,
    private val repository: Repository,

    // private val navAction: NavigationActions,

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

    // TODO: Save profile button => go to the home screen after saving the profile.
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
            }
        }
    }

    // Add tag button
    fun addTag(tag: Tag) {
        _tagList.value += tag
    }

    /*
       // Back button
       fun navigateBack() {
           navAction.goBack()
       }
    */
}
