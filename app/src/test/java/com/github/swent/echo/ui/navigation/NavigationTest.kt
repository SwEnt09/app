package com.github.swent.echo.ui.navigation

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.MainActivity
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.compose.navigation.AppNavigationHost
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.Repository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    companion object {
        private const val EMAIL = "test@example.com"
        private const val PASSWORD = "password"
    }

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

    // Create a test rule for the `MainActivity`
    @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject lateinit var authenticationService: AuthenticationService
    @Inject lateinit var repository: Repository

    private fun setUpValidUserId() = runBlocking {
        authenticationService.signUp(EMAIL, PASSWORD)
        authenticationService.signIn(EMAIL, PASSWORD)
    }

    private fun setUpValidUserProfile() = runBlocking {
        authenticationService.signUp(EMAIL, PASSWORD)
        authenticationService.signIn(EMAIL, PASSWORD)
        repository.setUserProfile(
            UserProfile(
                authenticationService.getCurrentUserID()!!,
                "John Doe",
                null,
                null,
                emptySet(),
                emptySet(),
                emptySet(),
            )
        )
    }

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        runBlocking { authenticationService.signOut() }
    }

    private fun assertHardwareBackButtonClosesApp() {
        // Press the back button
        composeTestRule.activity.onBackPressedDispatcher.onBackPressed()

        // Assert that the activity is finishing
        assertTrue(composeTestRule.activity.isFinishing)
    }

    @Test
    fun shouldShowRegisterScreenWhenTheUserIsNotLoggedIn() {
        composeTestRule.activity.setContent {
            AppNavigationHost(
                authenticationService = authenticationService,
                repository = repository
            )
        }

        composeTestRule.onNodeWithTag("register-screen").assertIsDisplayed()

        assertHardwareBackButtonClosesApp()
    }

    @Test
    fun shouldShowProfileCreationScreenWhenTheUserHasNoProfile() {
        setUpValidUserId()

        composeTestRule.activity.setContent {
            AppNavigationHost(
                authenticationService = authenticationService,
                repository = repository
            )
        }

        composeTestRule.onNodeWithTag("profile-creation").assertIsDisplayed()

        assertHardwareBackButtonClosesApp()
    }

    @Test
    fun shouldShowMapScreenWhenTheUserHasProfile() {
        setUpValidUserProfile()
        composeTestRule.activity.setContent {
            AppNavigationHost(
                authenticationService = authenticationService,
                repository = repository
            )
        }

        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()

        assertHardwareBackButtonClosesApp()
    }
}
