package com.github.swent.echo.viewmodels.authentication

import androidx.lifecycle.viewModelScope
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.SectionEPFL
import com.github.swent.echo.data.model.SemesterEPFL
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.fakes.FakeAuthenticationService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CreateProfileViewModelTest {

    private val authenticationService = FakeAuthenticationService()
    private val repository = mockk<Repository>(relaxed = true)
    private lateinit var viewModel: CreateProfileViewModel
    private val mockedNetworkService = mockk<NetworkService>()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        authenticationService.userID = "test_user_id"
        every { mockedNetworkService.isOnline } returns MutableStateFlow(true)
        coEvery { repository.getUserProfilePicture(any()) } returns null
        viewModel = CreateProfileViewModel(authenticationService, repository, mockedNetworkService)
    }

    @Test
    fun initTest() {

        val userId = "test_user_id"
        val userProfile =
            UserProfile(
                userId,
                "John Doe",
                SemesterEPFL.BA1,
                SectionEPFL.IN,
                setOf(Tag("1", "Music")),
                emptySet(),
                emptySet()
            )
        coEvery { (repository.getUserProfile(userId)) } returns (userProfile)

        val viewModel =
            CreateProfileViewModel(authenticationService, repository, mockedNetworkService)

        // Force coroutine execution to test initial values
        runBlocking { viewModel.viewModelScope.launch {}.join() }

        // Assert that user profile data is set in the ViewModel
        assert(viewModel.firstName.value == "John")
        assert(viewModel.lastName.value == "Doe")
        assert(viewModel.selectedSemester.value == SemesterEPFL.BA1)
        assert(viewModel.selectedSection.value == SectionEPFL.IN)
        assert(viewModel.tagList.value.contains(Tag("1", "Music")))
    }

    @Test
    fun loggedInUser() = runBlocking {
        val userId = "test_user_id"

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
