package com.github.swent.echo.viewmodels.authentication

import androidx.lifecycle.ViewModel
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.SectionEPFL
import com.github.swent.echo.data.model.SemesterEPFL
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking

@HiltViewModel
class CreateProfileViewModel
@Inject
constructor(
    private val authenticationService: AuthenticationService,
    private val repository: Repository,
) : ViewModel() {

    private val _firstName = MutableStateFlow("")
    val firstName = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName = _lastName.asStateFlow()

    private val _selectedSection = MutableStateFlow<SectionEPFL?>(null)
    val selectedSection = _selectedSection.asStateFlow()

    private val _selectedSemester = MutableStateFlow<SemesterEPFL?>(null)
    val selectedSemester = _selectedSemester.asStateFlow()

    private val _tagList = MutableStateFlow<Set<Tag>>(setOf())
    val tagList = _tagList.asStateFlow()

    private val _committeeMember = MutableStateFlow<Set<Association>>(setOf())
    val committeeMember = _committeeMember.asStateFlow()

    private val _associationSubscriptions = MutableStateFlow<Set<Association>>(setOf())
    val associationSubscriptions = _associationSubscriptions.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // functions to change the name of the private variables from outside the class.  (setters)

    fun setFirstName(firstName: String) {
        _firstName.value = firstName
    }

    fun setLastName(lastName: String) {
        _lastName.value = lastName
    }

    fun setSelectedSection(section: SectionEPFL) {
        _selectedSection.value = section
    }

    fun setSelectedSemester(semester: SemesterEPFL) {
        _selectedSemester.value = semester
    }

    //TO ASK: how does it work for the associations and committee members? Of course they cannot be set by the user, but how do they get set?
    //TO ASK: how to display the associations a user is subscribe to?

    // TODO: Save profile button => go to the home screen after saving the profile.
    fun profilesave() {

        // viewModelScope.launch {
        val userId = authenticationService.getCurrentUserID()
        if (userId == null) {
            _errorMessage.value = "Profile creation error: Not logged in"
            // TODO: handle error later on. Will this ever be the case?
        } else {
            // TODO: this is the logic for creating a userProfile, changes are required in the
            // getUserProfile function so that it returns null if a user has not yet created a
            // profile.
            // TODO: show loading animation while saving the profile

            runBlocking {
                val userProfile = repository.getUserProfile(userId)
                if (userProfile != null) {
                    _firstName.value = userProfile.name.split(" ")[0]
                    _lastName.value = userProfile.name.split(" ")[1]
                    _selectedSemester.value = userProfile.semester as SemesterEPFL?
                    _selectedSection.value = userProfile.section as SectionEPFL?
                    _tagList.value = userProfile.tags
                    _committeeMember.value = userProfile.committeeMember
                    _associationSubscriptions.value = userProfile.associationsSubscriptions
                }
                repository.setUserProfile(
                    UserProfile(
                        userId,
                        name = "${_firstName.value} ${_lastName.value}",
                        semester = _selectedSemester.value,
                        section = _selectedSection.value,
                        tags = _tagList.value,
                        committeeMember = _committeeMember.value,
                        associationsSubscriptions = _associationSubscriptions.value
                    )
                )
            }
        }
        // }
    }

    // Add tag button
    fun addTag(tag: Tag) {
        _tagList.value += tag
    }

    fun removeTag(tag: Tag) {
        _tagList.value -= tag
    }
}
