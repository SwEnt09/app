package com.github.swent.echo.viewmodels.authentication

import app.cash.turbine.test
import com.github.swent.echo.authentication.AuthenticationResult
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.ui.navigation.Routes
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {
    private lateinit var authenticationService: AuthenticationService
    private lateinit var repository: Repository
    private lateinit var networkService: NetworkService
    private lateinit var viewModel: RegisterViewModel

    companion object {
        private const val EMAIL = "test@email.com"
        private const val PASSWORD = "password"
        private const val ERROR_MESSAGE = "Error message"

        private val USER_PROFILE =
            UserProfile("id", "John Doe", null, null, emptySet(), emptySet(), emptySet())
    }

    @Before
    fun setUp() {
        authenticationService = mockk()
        repository = mockk()
        networkService = mockk { every { isOnline } returns MutableStateFlow(true) }
        viewModel = RegisterViewModel(authenticationService, repository, networkService)
    }

    @Test
    fun `state should be signed out when created`() {
        assertEquals(AuthenticationState.SignedOut, viewModel.state.value)
    }

    @Test
    fun `register should return success with map route when successful and has a user profile`() =
        runTest {
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)

            every { authenticationService.getCurrentUserID() } returns USER_PROFILE.userId
            coEvery { authenticationService.signUp(EMAIL, PASSWORD) } returns
                AuthenticationResult.Success
            coEvery { repository.getUserProfile(any()) } returns USER_PROFILE

            viewModel.state.test {
                assertEquals(AuthenticationState.SignedOut, awaitItem())
                viewModel.register(EMAIL, PASSWORD)
                assertEquals(AuthenticationState.SigningIn, awaitItem())
                assertEquals(AuthenticationState.SignedIn(Routes.MAP), awaitItem())
            }
        }

    @Test
    fun `register should return success with profile creation route when successful and has no user profile`() =
        runTest {
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)

            every { authenticationService.getCurrentUserID() } returns USER_PROFILE.userId
            coEvery { authenticationService.signUp(EMAIL, PASSWORD) } returns
                AuthenticationResult.Success
            coEvery { repository.getUserProfile(any()) } returns null

            viewModel.state.test {
                assertEquals(AuthenticationState.SignedOut, awaitItem())
                viewModel.register(EMAIL, PASSWORD)
                assertEquals(AuthenticationState.SigningIn, awaitItem())
                assertEquals(AuthenticationState.SignedIn(Routes.PROFILE_CREATION), awaitItem())
            }
        }

    @Test
    fun `login should return error when failed`() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        coEvery { authenticationService.signUp(EMAIL, PASSWORD) } returns
            AuthenticationResult.Error(ERROR_MESSAGE)

        viewModel.state.test {
            assertEquals(AuthenticationState.SignedOut, awaitItem())
            viewModel.register(EMAIL, PASSWORD)
            assertEquals(AuthenticationState.SigningIn, awaitItem())
            assertEquals(AuthenticationState.Error(ERROR_MESSAGE), awaitItem())
        }
    }
}
