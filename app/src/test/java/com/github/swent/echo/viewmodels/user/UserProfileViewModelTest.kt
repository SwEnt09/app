package com.github.swent.echo.viewmodels.user

import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.Repository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class UserProfileViewModelTest {

    private val mockedAuthenticationService = mockk<AuthenticationService>(relaxed = true)
    private val mockedRepository = mockk<Repository>(relaxed = true)
    private val userId = "user-id"
    private val userProfile =
        UserProfile(userId, "test name", null, null, setOf(), setOf(), setOf())
    private val scheduler = TestCoroutineScheduler()
    private lateinit var userProfileViewModel: UserProfileViewModel

    @Before
    fun init() {
        every { mockedAuthenticationService.userIsLoggedIn() } returns true
        every { mockedAuthenticationService.getCurrentUserID() } returns userId
        coEvery { mockedRepository.getUserProfile(any()) } returns userProfile
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        runBlocking {
            userProfileViewModel =
                UserProfileViewModel(mockedRepository, mockedAuthenticationService)
        }
        scheduler.runCurrent()
    }

    @Test
    fun userProfileIsCorrect() {
        assertEquals(userProfile, userProfileViewModel.userProfile.value)
    }

    @Test
    fun changedUserProfileIsChanged() {
        val newUserProfile = userProfile.copy(name = "changed name")
        userProfileViewModel.setUserProfile(newUserProfile)
        scheduler.runCurrent()
        coVerify { mockedRepository.setUserProfile(any()) }
        assertEquals(userProfile, userProfileViewModel.userProfile.value)
    }
}
