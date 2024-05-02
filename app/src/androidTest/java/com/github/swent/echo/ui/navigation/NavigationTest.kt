package com.github.swent.echo.ui.navigation

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
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

    @Before
    fun setUp() {
        hiltRule.inject()

        // Perform the sign up, sign in, and set the user profile
        runBlocking {
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
    }

    @After
    fun tearDown() {
        runBlocking { authenticationService.signOut() }
    }

    private fun setUp(route: Routes) {
        composeTestRule.activity.setContent {
            val navController = rememberNavController()
            val navigationActions =
                NavigationActions(navController, authenticationService, repository)
            AppNavigationHost(
                userIsLoggedIn = false,
                navController = navController,
                authenticationService = authenticationService,
                repository = repository
            )
            navigationActions.navigateTo(route)
        }
    }

    @Test
    fun shouldShowRegisterScreenWhenTheUserIsNotLoggedIn() {
        composeTestRule.activity.setContent {
            AppNavigationHost(
                userIsLoggedIn = false,
                authenticationService = authenticationService,
                repository = repository
            )
        }

        composeTestRule.onNodeWithTag("register-screen").assertIsDisplayed()
    }

    @Test
    fun shouldShowMapScreenWhenTheUserIsLoggedIn() {
        composeTestRule.activity.setContent {
            AppNavigationHost(
                userIsLoggedIn = true,
                authenticationService = authenticationService,
                repository = repository
            )
        }

        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()
    }

    @Test
    fun shouldShowTheMapScreenWhenNavigatingToTheMapRoute() {
        setUp(Routes.MAP)
        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()
    }

    @Test
    fun shouldShowTheLoginScreenWhenNavigatingToTheLoginRoute() {
        setUp(Routes.LOGIN)
        composeTestRule.onNodeWithTag("login-screen").assertIsDisplayed()
    }

    @Test
    fun shouldShowTheRegisterScreenWhenNavigatingToTheRegisterRoute() {
        setUp(Routes.REGISTER)
        composeTestRule.onNodeWithTag("register-screen").assertIsDisplayed()
    }
}
