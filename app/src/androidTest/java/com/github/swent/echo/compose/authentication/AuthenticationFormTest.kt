package com.github.swent.echo.compose.authentication

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthenticationFormTest {

    companion object {
        const val ACTION = "Action Button"
        const val EMAIL = "test@test.com"
        const val PASSWORD = "password"
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

    @Before
    fun setUp() {
        authenticationCount = 0
        email = ""
        password = ""
    }

    @Test
    fun shouldDisplayLabelsAndActionButton() {
        composeTestRule.setContent { AuthenticationForm(ACTION, this::onAuthenticate) }
        composeTestRule.onNodeWithTag("action-button").assertExists()
        composeTestRule.onNodeWithText(ACTION).assertExists()
        composeTestRule.onNodeWithText("Email").assertExists()
        composeTestRule.onNodeWithText("Password").assertExists()
    }

    @Test
    fun shouldCallOnAuthenticateWithCorrectParametersWhenActionButtonIsPressed() {
        composeTestRule.setContent { AuthenticationForm(ACTION, this::onAuthenticate) }

        composeTestRule.onNodeWithTag("email-field").performTextInput(EMAIL)
        composeTestRule.onNodeWithTag("password-field").performTextInput(PASSWORD)
        assertEquals(0, authenticationCount)
        composeTestRule.onNodeWithTag("action-button").performClick()
        assertEquals(1, authenticationCount)
        assertEquals(EMAIL, email)
        assertEquals(PASSWORD, password)
    }
}
