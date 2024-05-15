package com.github.swent.echo.viewmodels.authentication

import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.model.SectionEPFL
import com.github.swent.echo.data.model.SemesterEPFL
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.SimpleRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CreateProfileViewModelTest {

    private val authenticationService: AuthenticationService = mockk()
    private val repository: SimpleRepository = mockk()
    private lateinit var viewModel: CreateProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = CreateProfileViewModel(authenticationService, repository)
    }
    /*
       @Test
       fun loggedinErrorMessage() = runTest {
           coEvery { authenticationService.getCurrentUserID() } returns null

           viewModel.profileSave("John", "Doe")

           assert(viewModel.errorMessage.value == "Profile creation error: Not logged in")
       }
    */

    @Test
    fun loggedInUser() = runBlocking {
        val userId = "userId"
        // whenever(authenticationService.getCurrentUserID()).thenReturn(userId)
        coEvery { authenticationService.getCurrentUserID() } returns userId

        viewModel.setFirstName("John")
        viewModel.setLastName("Doe")
        viewModel.setSelectedSection(SectionEPFL.IN)
        viewModel.setSelectedSemester(SemesterEPFL.BA3)

        viewModel.addTag(Tag("tag1", "Sports"))
        viewModel.addTag(Tag("tag2", "Music"))

        val userProfile =
            UserProfile(
                userId,
                "John Doe",
                SemesterEPFL.BA3,
                SectionEPFL.IN,
                setOf(Tag("tag1", "Sports"), Tag("tag2", "Music")),
                setOf(),
                setOf()
            )

        coEvery { repository.getUserProfile(any()) } returns userProfile
        coEvery { repository.setUserProfile(userProfile) } returns Unit

        viewModel.profileSave("John", "Doe")

        val actualUserProfile = repository.getUserProfile(userId)

        assertEquals(userProfile.userId, actualUserProfile?.userId)
        assertEquals(userProfile.name, actualUserProfile?.name)
        assertEquals(userProfile.semester, actualUserProfile?.semester)
        assertEquals(userProfile.section, actualUserProfile?.section)
        assertEquals(userProfile.tags, actualUserProfile?.tags)

        assertEquals(actualUserProfile, userProfile)
        // println("Expected UserProfile: $userProfile")

    }

    @Test
    fun addTagTest() {
        val tag = Tag("tag3", "Dance")
        viewModel.addTag(tag)
        val tagList = viewModel.tagList.value
        assertTrue(tagList.contains(tag))
    }

    @Test
    fun removeTagTest() {
        val tag = Tag("tag3", "Dance")
        viewModel.addTag(tag)
        val tagList = viewModel.tagList.value
        assertTrue(tagList.contains(tag))
        viewModel.removeTag(tag)
        val tagList1 = viewModel.tagList.value
        assertFalse(tagList1.contains(tag))
    }
}
