package com.github.swent.echo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivityTest {

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

    // Create a test rule for the `MainActivity`
    @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun shouldDisplayRegisterRouteWhenAppStarts() {
        composeTestRule.onNodeWithTag("register-screen").assertIsDisplayed()
    }

    @Test
    fun shouldNavigateToLoginRouteWhenLoginButtonIsClicked() {
        // Navigate to the login screen
        composeTestRule.onNodeWithTag("login-button").performClick()
        composeTestRule.onNodeWithTag("login-screen").assertIsDisplayed()
    }

    @Test
    fun shouldNavigateBackToRegisterRouteWhenRegisterButtonIsClicked() {
        // Navigate to the login screen
        composeTestRule.onNodeWithTag("login-button").performClick()
        // Navigate back to the register screen
        composeTestRule.onNodeWithTag("register-button").performClick()
        composeTestRule.onNodeWithTag("register-screen").assertIsDisplayed()
    }

    @Test
    fun testRegistrationProcess() {
        val email = "test@test.com"
        val password = "password"

        // Perform the registration process
        composeTestRule.onNodeWithTag("register-screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("email-field").performTextInput(email)
        composeTestRule.onNodeWithTag("password-field").performTextInput(password)
        composeTestRule.onNodeWithTag("action-button").performClick()

        // Login with the registered user
        composeTestRule.onNodeWithTag("login-screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("email-field").performTextInput(email)
        composeTestRule.onNodeWithTag("password-field").performTextInput(password)
        composeTestRule.onNodeWithTag("action-button").performClick()

        // The map screen should be displayed
        composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    }

    @Test
    fun testGoogleSignInProcess() {
        // Perform the Google sign-in process
        composeTestRule.onNodeWithTag("register-screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("google-sign-in-button").performClick()

        // The map screen should be displayed
        composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    }
}
