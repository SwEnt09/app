package com.github.swent.echo.compose.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.authentication.AuthenticationState
import com.github.swent.echo.viewmodels.authentication.LoginViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var navActions: NavigationActions
    private lateinit var state: MutableStateFlow<AuthenticationState>

    @Before
    fun setUp() {
        loginViewModel = mockk(relaxed = true)
        navActions = mockk(relaxed = true)
        state = MutableStateFlow(AuthenticationState.SignedOut)
        every { loginViewModel.state } answers { state.asStateFlow() }
        composeTestRule.setContent { LoginScreen(loginViewModel, navActions) }
    }

    @Test
    fun shouldHaveLoginButtonAndInputFieldsWhenIsSignedOut() {
        state.value = AuthenticationState.SignedOut
        composeTestRule.onNodeWithTag("action-button").assertExists().assertHasClickAction()
    }

    @Test
    fun shouldCallLoginOnViewModelWithEmailPasswordWhenLoginButtonIsClickedInSignedOutState() {
        state.value = AuthenticationState.SignedOut
        composeTestRule.onNodeWithTag("email-field").performTextInput("test@test.test")
        composeTestRule.onNodeWithTag("password-field").performTextInput("password")
        composeTestRule.onNodeWithTag("action-button").performClick()
        verify { loginViewModel.login("test@test.test", "password") }
    }

    @Test
    fun shouldCallLoginOnViewModelWithEmailPasswordWhenLoginButtonIsClickedInErrorState() {
        state.value = AuthenticationState.Error("Error message")
        composeTestRule.onNodeWithTag("email-field").performTextInput("test@test.test")
        composeTestRule.onNodeWithTag("password-field").performTextInput("password")
        composeTestRule.onNodeWithTag("action-button").performClick()
        verify { loginViewModel.login("test@test.test", "password") }
    }

    @Test
    fun shouldCallNavigateToRegisterWhenRegisterButtonIsClickedInSignedOutState() {
        state.value = AuthenticationState.SignedOut
        composeTestRule.onNodeWithTag("register-button").performClick()
        verify { navActions.navigateTo(Routes.REGISTER) }
    }

    @Ignore
    @Test
    fun shouldCallNavigateToRegisterWhenRegisterButtonIsClickedInErrorState() {
        state.value = AuthenticationState.Error("Error message")
        composeTestRule.onNodeWithTag("register-button").performClick()
        verify { navActions.navigateTo(Routes.REGISTER) }
    }
}
