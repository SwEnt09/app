package com.github.swent.echo.viewmodels.authentication

import com.github.swent.echo.authentication.AuthenticationResult
import com.github.swent.echo.fakes.FakeAuthenticationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private lateinit var fakeAuthenticationService: FakeAuthenticationService
    private lateinit var viewModel: LoginViewModel

    companion object {
        private const val EMAIL = "test@email.com"
        private const val PASSWORD = "password"
    }

    @Before
    fun setUp() {
        fakeAuthenticationService = FakeAuthenticationService()
        viewModel = LoginViewModel(fakeAuthenticationService)
    }

    @Test
    fun `state should be signed out when created`() {
        assertEquals(AuthenticationState.SignedOut, viewModel.state.value)
    }

    @Test
    fun `login should return success when successful`() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        viewModel.login(EMAIL, PASSWORD)
        assertEquals(AuthenticationState.SigningIn, viewModel.state.value)
        fakeAuthenticationService.completeSigningOperation(AuthenticationResult.Success)
        assertEquals(AuthenticationState.SignedIn, viewModel.state.value)
    }

    @Test
    fun `login should return error when failed`() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        viewModel.login(EMAIL, PASSWORD)
        assertEquals(AuthenticationState.SigningIn, viewModel.state.value)
        fakeAuthenticationService.completeSigningOperation(
            AuthenticationResult.Error("Error message")
        )
        assertEquals(AuthenticationState.Error("Error message"), viewModel.state.value)
    }
}
