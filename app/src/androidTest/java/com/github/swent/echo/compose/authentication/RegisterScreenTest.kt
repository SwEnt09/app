package com.github.swent.echo.compose.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.authentication.AuthenticationState
import com.github.swent.echo.viewmodels.authentication.RegisterViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class RegisterScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var navActions: NavigationActions
    private lateinit var state: MutableStateFlow<AuthenticationState>

    @Before
    fun setUp() {
        registerViewModel = mockk(relaxed = true)
        navActions = mockk(relaxed = true)
        state = MutableStateFlow(AuthenticationState.SignedOut)
        every { registerViewModel.state } answers { state.asStateFlow() }
        composeTestRule.setContent { RegisterScreen(registerViewModel, navActions) }
    }

    @Test
    fun shouldHaveRegisterButtonAndInputFieldsWhenIsSignedOut() {
        state.value = AuthenticationState.SignedOut
        composeTestRule.onNodeWithTag("action-button").assertExists().assertHasClickAction()
    }

    @Test
    fun shouldCallRegisterOnViewModelWithEmailPasswordWhenRegisterButtonIsClickedInSignedOutState() {
        state.value = AuthenticationState.SignedOut
        composeTestRule.onNodeWithTag("email-field").performTextInput("test@test.test")
        composeTestRule.onNodeWithTag("password-field").performTextInput("password")
        composeTestRule.onNodeWithTag("action-button").performClick()
        verify { registerViewModel.register("test@test.test", "password") }
    }

    @Test
    fun shouldCallRegisterOnViewModelWithEmailPasswordWhenRegisterButtonIsClickedInErrorState() {
        state.value = AuthenticationState.Error("Error message")
        composeTestRule.onNodeWithTag("email-field").performTextInput("test@test.test")
        composeTestRule.onNodeWithTag("password-field").performTextInput("password")
        composeTestRule.onNodeWithTag("action-button").performClick()
        verify { registerViewModel.register("test@test.test", "password") }
    }

    @Test
    fun shouldCallNavigateToLoginWhenLoginButtonIsClickedInSignedOutState() {
        state.value = AuthenticationState.SignedOut
        composeTestRule.onNodeWithTag("login-button").performClick()
        verify { navActions.navigateTo(Routes.LOGIN) }
    }

    @Ignore
    @Test
    fun shouldCallNavigateToLoginWhenLoginButtonIsClickedInErrorState() {
        state.value = AuthenticationState.Error("Error message")
        composeTestRule.onNodeWithTag("login-button").performClick()
        verify { navActions.navigateTo(Routes.LOGIN) }
    }
}
