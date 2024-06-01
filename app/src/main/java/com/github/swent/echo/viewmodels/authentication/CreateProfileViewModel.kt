package com.github.swent.echo.viewmodels.authentication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.AssociationHeader
import com.github.swent.echo.data.model.Section
import com.github.swent.echo.data.model.Semester
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class CreateProfileState {
    EDIT,
    SAVING,
    SAVED,
}

@HiltViewModel
class CreateProfileViewModel
@Inject
constructor(
    private val authenticationService: AuthenticationService,
    private val repository: Repository,
    private val network: NetworkService
) : ViewModel() {

    // The state of the profile creation process.
    private val _state = MutableStateFlow(CreateProfileState.EDIT)
    val state = _state.asStateFlow()

    val isOnline = network.isOnline

    private val _isEditing = MutableStateFlow(false)
    val isEditing = _isEditing.asStateFlow()

    private val _firstName = MutableStateFlow("")
    val firstName = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName = _lastName.asStateFlow()

    private val _selectedSection = MutableStateFlow<Section?>(null)
    val selectedSection = _selectedSection.asStateFlow()

    private val _selectedSemester = MutableStateFlow<Semester?>(null)
    val selectedSemester = _selectedSemester.asStateFlow()

    private val _tagList = MutableStateFlow<Set<Tag>>(emptySet())
    val tagList = _tagList.asStateFlow()

    private val _committeeMember = MutableStateFlow<Set<AssociationHeader>>(emptySet())
    val committeeMember = _committeeMember.asStateFlow()

    private val _associationSubscriptions = MutableStateFlow<Set<AssociationHeader>>(emptySet())
    val associationSubscriptions = _associationSubscriptions.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _picture = MutableStateFlow<Bitmap?>(null)
    val picture = _picture.asStateFlow()

    // functions to change the name of the private variables from outside the class.  (setters)

    fun setFirstName(firstName: String) {
        _firstName.value = firstName
    }

    fun setLastName(lastName: String) {
        _lastName.value = lastName
    }

    fun setSelectedSection(section: Section?) {
        _selectedSection.value = section
    }

    fun setSelectedSemester(semester: Semester?) {
        _selectedSemester.value = semester
    }

    init {
        viewModelScope.launch {
            val userId = authenticationService.getCurrentUserID()
            if (userId != null) {
                val userProfile = repository.getUserProfile(userId)
                if (userProfile != null) {
                    _isEditing.value = true
                    val nameParts = userProfile.name.split(" ")
                    if (nameParts.size > 1) {
                        _firstName.value = nameParts[0]
                        _lastName.value = nameParts[1]
                    } else {
                        _firstName.value = nameParts[0]
                        _lastName.value = ""
                    }
                    _selectedSemester.value = userProfile.semester
                    _selectedSection.value = userProfile.section
                    _tagList.value = userProfile.tags
                    _committeeMember.value = userProfile.committeeMember
                    _associationSubscriptions.value = userProfile.associationsSubscriptions
                    val pictureByteArray = repository.getUserProfilePicture(userId)
                    if (pictureByteArray != null) {
                        _picture.value =
                            BitmapFactory.decodeByteArray(
                                pictureByteArray,
                                0,
                                pictureByteArray.size
                            )
                    }
                } else {
                    _isEditing.value = false
                }
            }
        }
    }

    fun profileSave(firstNameArg: String, lastNameArg: String) {

        val userId = authenticationService.getCurrentUserID()
        if (userId == null) {
            Log.d("nullUserId", "User ID is null")
            _errorMessage.value = "Profile creation error: Not logged in"
        } else {
            // TODO: show loading animation while saving the profile
            viewModelScope.launch {
                _state.value = CreateProfileState.SAVING
                println("User profile name: ${_firstName.value}\n")
                println("User profile section: ${_selectedSection.value}\n")
                println("User profile semester: ${_selectedSemester.value}\n")
                repository.setUserProfile(
                    UserProfile(
                        userId,
                        name = "$firstNameArg $lastNameArg",
                        semester = _selectedSemester.value,
                        section = _selectedSection.value,
                        tags = _tagList.value,
                        committeeMember = _committeeMember.value,
                        associationsSubscriptions = _associationSubscriptions.value
                    )
                )
                if (picture.value == null) {
                    repository.deleteUserProfilePicture(userId)
                } else {
                    val pictureStream = ByteArrayOutputStream()
                    picture.value!!.compress(Bitmap.CompressFormat.JPEG, 100, pictureStream)
                    repository.setUserProfilePicture(userId, pictureStream.toByteArray())
                }
                _state.value = CreateProfileState.SAVED
            }
        }
    }

    // Add tag button
    fun addTag(tag: Tag) {
        _tagList.value += tag
    }

    fun removeTag(tag: Tag) {
        _tagList.value -= tag
    }

    fun setPicture(picture: Bitmap?) {
        _picture.value = picture
    }
}
