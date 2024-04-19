package com.github.swent.echo.compose.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.viewmodels.authentication.AuthenticationState
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthenticationScreenTest {

    companion object {
        const val ACTION = "Action Button"
    }

    @get:Rule val composeTestRule = createComposeRule()

    private var authenticationCount = 0
    private var email = ""
    private var password = ""

    private fun onAuthenticate(email: String, password: String) {
        this.email = email
        this.password = password
        authenticationCount++
    }

    private fun onStartGoogleSignIn() {
        // Not implemented
    }

    @Before
    fun setUp() {
        authenticationCount = 0
        email = ""
        password = ""
    }

    @Test
    fun shouldHaveLoginButtonAndInputFieldsWhenIsSignedOut() {
        composeTestRule.setContent {
            AuthenticationScreen(
                ACTION,
                AuthenticationState.SignedOut,
                this::onAuthenticate,
                this::onStartGoogleSignIn
            )
        }
        composeTestRule.onNodeWithText(ACTION).assertExists().assertHasClickAction()
        composeTestRule.onNodeWithTag("email-field").assertExists()
        composeTestRule.onNodeWithTag("password-field").assertExists()
        composeTestRule.onNodeWithTag("error-message").assertDoesNotExist()
    }

    @Test
    fun shouldCallOnAuthenticateWithCorrectParametersWhenActionButtonIsPressedInSignedOutState() {
        composeTestRule.setContent {
            AuthenticationScreen(
                ACTION,
                AuthenticationState.SignedOut,
                this::onAuthenticate,
                this::onStartGoogleSignIn
            )
        }
        composeTestRule.onNodeWithTag("email-field").performTextInput("test@test.test")
        composeTestRule.onNodeWithTag("password-field").performTextInput("password")
        assertEquals(0, authenticationCount)
        composeTestRule.onNodeWithTag("action-button").performClick()
        assertEquals(1, authenticationCount)
        assertEquals("test@test.test", email)
        assertEquals("password", password)
    }

    @Test
    fun shouldHaveSigningInTextWhenIsSigningIn() {
        composeTestRule.setContent {
            AuthenticationScreen(
                ACTION,
                AuthenticationState.SigningIn,
                this::onAuthenticate,
                this::shouldHaveSignedInTextWhenIsSignedIn
            )
        }
        composeTestRule.onNodeWithText("Signing in...").assertExists()
        assertEquals(0, authenticationCount)
    }

    @Test
    fun shouldHaveSignedInTextWhenIsSignedIn() {
        composeTestRule.setContent {
            AuthenticationScreen(
                ACTION,
                AuthenticationState.SignedIn,
                this::onAuthenticate,
                this::shouldHaveSigningInTextWhenIsSigningIn
            )
        }
        composeTestRule.onNodeWithText("Signing in...").assertExists()
        assertEquals(0, authenticationCount)
    }

    @Test
    fun shouldHaveErrorTextWhenIsError() {
        val message = "Error message"
        composeTestRule.setContent {
            AuthenticationScreen(
                ACTION,
                AuthenticationState.Error(message),
                this::onAuthenticate,
                this::onStartGoogleSignIn
            )
        }
        composeTestRule.onNodeWithText(message).assertExists()
    }

    @Test
    fun shouldHaveLoginButtonAndInputFieldsWhenIsError() {
        val message = "Error message"
        composeTestRule.setContent {
            AuthenticationScreen(
                ACTION,
                AuthenticationState.Error(message),
                this::onAuthenticate,
                this::onStartGoogleSignIn
            )
        }
        composeTestRule.onNodeWithText(ACTION).assertExists().assertHasClickAction()
        composeTestRule.onNodeWithTag("email-field").assertExists()
        composeTestRule.onNodeWithTag("password-field").assertExists()
        composeTestRule.onNodeWithTag("error-message").assertExists()
    }

    @Test
    fun shouldCallOnAuthenticateWithCorrectParametersWhenActionButtonIsPressedInErrorState() {
        composeTestRule.setContent {
            AuthenticationScreen(
                ACTION,
                AuthenticationState.Error("Error"),
                this::onAuthenticate,
                this::onStartGoogleSignIn
            )
        }
        composeTestRule.onNodeWithTag("email-field").performTextInput("test@test.test")
        composeTestRule.onNodeWithTag("password-field").performTextInput("password")
        composeTestRule.onNodeWithTag("action-button").performClick()
        assertEquals(1, authenticationCount)
        assertEquals("test@test.test", email)
        assertEquals("password", password)
    }
}
