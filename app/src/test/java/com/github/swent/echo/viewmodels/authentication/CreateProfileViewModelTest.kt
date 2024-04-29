package com.github.swent.echo.viewmodels.authentication

import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.model.SectionEPFL
import com.github.swent.echo.data.model.SemesterEPFL
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.Repository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CreateProfileViewModelTest {

    private val repository: Repository = mockk()
    private val authenticationService: AuthenticationService = mockk()
    private lateinit var viewModel: CreateProfileViewModel

    @Before
    fun setUp() {
        viewModel = CreateProfileViewModel(repository, authenticationService)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun loggedinErrorMessage() = runTest {
        coEvery { authenticationService.getCurrentUserID() } returns null

        viewModel.profilesave()

        assert(viewModel.errorMessage.value == "Profile creation error: Not logged in")
    }

    @Test
    fun loggedInUser() = runTest {
        val userId = "userId"
        val firstName = "John"
        val lastName = "Doe"
        val section = SectionEPFL.AR
        val semester = SemesterEPFL.BA2
        val tags = listOf(Tag("tag1", "Sports"), Tag("tag2", "Music"))
        val userProfile =
            UserProfile(userId, "$firstName $lastName", semester, section, tags.toSet())

        coEvery { authenticationService.getCurrentUserID() } returns userId
        coEvery { repository.setUserProfile(any()) } just runs
        viewModel.profilesave()
        /*
               assertEquals(viewModel.firstName.value,firstName)
               assertEquals(viewModel.lastName.value,lastName)
               assertEquals(viewModel.selectedSection.value,section)
               assertEquals(viewModel.selectedSemester.value,semester)
               assertEquals(viewModel.tagList.value,tags)
        */
        coVerify { repository.setUserProfile(userProfile) }
    }

    @Test
    fun addTagTest() {
        val tag = Tag("tag3", "Dance")
        val initialTags = listOf(Tag("tag1", "Sports"), Tag("tag2", "Music"))
        val viewModel = CreateProfileViewModel(repository, authenticationService)
        viewModel.addTag(tag)
        assert(viewModel.tagList.value == initialTags + tag)
    }
}
