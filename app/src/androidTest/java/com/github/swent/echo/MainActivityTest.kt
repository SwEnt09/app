package com.github.swent.echo

import android.content.pm.ActivityInfo
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.requestFocus
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.SAMPLE_EVENTS
import com.github.swent.echo.data.model.SectionEPFL
import com.github.swent.echo.data.model.SemesterEPFL
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.Repository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivityTest {

    companion object {
        const val EMAIL = "test@test.com"
        const val PASSWORD = "password"
    }

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

    // Create a test rule for the `MainActivity`
    @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject lateinit var authenticationService: AuthenticationService

    @Inject lateinit var repository: Repository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun shouldDisplayRegisterRouteWhenAppStarts() {
        composeTestRule.onNodeWithTag("register-screen").assertIsDisplayed()
    }

    @Test
    fun shouldNavigateToLoginRouteWhenLoginButtonIsClicked() {
        // Navigate to the login screen
        composeTestRule.onNodeWithTag("login-button").performScrollTo()
        composeTestRule.onNodeWithTag("login-button").performClick()
        composeTestRule.onNodeWithTag("login-screen").assertIsDisplayed()
    }

    @Test
    fun shouldNavigateBackToRegisterRouteWhenRegisterButtonIsClicked() {
        // Navigate to the login screen
        composeTestRule.onNodeWithTag("login-button").performScrollTo()
        composeTestRule.onNodeWithTag("login-button").performClick()
        // Navigate back to the register screen
        composeTestRule.onNodeWithTag("register-button").performClick()
        composeTestRule.onNodeWithTag("register-screen").assertIsDisplayed()
    }

    @Test
    fun testRegistrationProcess() {
        // Perform the registration process
        composeTestRule.onNodeWithTag("register-screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("email-field").performTextInput(EMAIL)
        composeTestRule.onNodeWithTag("password-field").performTextInput(PASSWORD)
        composeTestRule.onNodeWithTag("confirm-password-field").performTextInput(PASSWORD)
        composeTestRule.onNodeWithTag("action-button").performClick()

        // Login with the registered user
        composeTestRule.onNodeWithTag("login-screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("email-field").performTextInput(EMAIL)
        composeTestRule.onNodeWithTag("password-field").performTextInput(PASSWORD)
        composeTestRule.onNodeWithTag("action-button").performClick()

        // The profile-creation should be displayed
        composeTestRule.onNodeWithTag("profile-creation").assertIsDisplayed()
    }

    @Test
    fun testGoogleSignInProcess() {
        // Perform the Google sign-in process
        composeTestRule.onNodeWithTag("register-screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("google-sign-in-button").performClick()

        // The profile creation screen should be displayed
        composeTestRule.onNodeWithTag("profile-creation").assertIsDisplayed()
    }

    @Test
    fun orientationIsPortraitOrientation() {
        assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            composeTestRule.activity.requestedOrientation
        )
    }

    @Test
    fun endToEndTestUserFlowExploration() {
        // Assume the the user has an account (already signed up previously) and an user profile
        runBlocking {
            authenticationService.signUp(EMAIL, PASSWORD)
            val userId = authenticationService.getCurrentUserID()!!
            repository.setUserProfile(
                UserProfile(
                    userId,
                    "John Doe",
                    SemesterEPFL.BA3,
                    SectionEPFL.IN,
                    emptySet(),
                    emptySet(),
                    emptySet(),
                )
            )
        }

        // Starting point is the register screen
        composeTestRule.onNodeWithTag("register-screen").assertIsDisplayed()

        // Go to the login screen
        composeTestRule.onNodeWithTag("login-button").performScrollTo()
        composeTestRule.onNodeWithTag("login-button").performClick()

        // Login with email and password
        composeTestRule.onNodeWithTag("email-field").performTextInput(EMAIL)
        composeTestRule.onNodeWithTag("password-field").performTextInput(PASSWORD)

        // Click on the login button
        composeTestRule.onNodeWithTag("action-button").performClick()

        // The home screen with the map should be displayed
        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()

        // Switch to the list view
        composeTestRule.onNodeWithTag("list_map_mode_button").performClick()

        // Check if the list view is displayed
        composeTestRule.onNodeWithTag("list_drawer").assertIsDisplayed()

        // Switch back to the map view
        composeTestRule.onNodeWithTag("list_map_mode_button").performClick()

        // Click on the event marker
        val event = SAMPLE_EVENTS.first()
        composeTestRule.onNodeWithTag("event_marker_${event.eventId}").performClick()

        // Check if the event info sheet is displayed
        composeTestRule.onNodeWithTag("event_info_sheet").assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("event_info_sheet_title")
            .assert(hasText(event.title, substring = true))

        // Join the event
        composeTestRule.onNodeWithTag("list_join_event_${event.eventId}").performClick()
    }

    @Test
    fun endToEndTestUserFlowEventCreation() {
        // Starting point is the register screen
        composeTestRule.onNodeWithTag("register-screen").assertIsDisplayed()

        // Registers with google sign in
        composeTestRule.onNodeWithTag("google-sign-in-button").performClick()

        // The profile creation screen should be displayed
        composeTestRule.onNodeWithTag("profile-creation").assertIsDisplayed()

        // Insert first and last name
        composeTestRule.onNodeWithTag("FirstName").performTextInput("John")
        composeTestRule.onNodeWithTag("LastName").performTextInput("Doe")

        // Click on the save button
        composeTestRule.onNodeWithTag("Save").performClick()

        // Redirected to the home screen
        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()

        // Click on the hamburger menu
        composeTestRule.onNodeWithTag("menu_button").performClick()

        // Click on the profile creation button
        composeTestRule.onNodeWithText("Create Event").performClick()

        // Fill the event creation form
        val title = "This is a test event title"
        val description = "This is a test event description"
        composeTestRule.onNodeWithTag("Title-field").performTextInput(title)
        composeTestRule.onNodeWithTag("Description-field").performTextInput(description)
        composeTestRule.onNodeWithTag("nb-participant-field").performTextReplacement("10")

        // Click outside of the text field
        composeTestRule.onNodeWithTag("Title-field").requestFocus()

        // Click on the save button
        composeTestRule
            .onNodeWithTag("Save-button")
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()

        // Should be redirected to the map screen
        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()

        // Check that the event was added to the repository
        val eventId = runBlocking {
            val event =
                repository.getAllEvents().find {
                    it.title == title && it.description == description
                }
            assertNotNull(event)

            event!!.eventId
        }

        // Click on the event marker
        composeTestRule.onNodeWithTag("event_marker_$eventId").performClick()

        // Check that the modify button is displayed
        composeTestRule.onNodeWithTag("modify_button").performClick()

        // Check that the title and description are correctly displayed
        composeTestRule.onNodeWithTag("Title-field").assert(hasText(title))
        composeTestRule.onNodeWithTag("Description-field").assert(hasText(description))

        // Modify the title
        val newTitle = "This is a new test event title"
        composeTestRule.onNodeWithTag("Title-field").performTextReplacement(newTitle)

        // Click on the save button
        composeTestRule.onNodeWithTag("Save-button").performScrollTo().performClick()

        // Should be redirected to the map screen
        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()

        // Check that the event was updated in the repository
        runBlocking {
            val event = repository.getEvent(eventId)
            assertEquals(newTitle, event!!.title)
        }
    }
}
