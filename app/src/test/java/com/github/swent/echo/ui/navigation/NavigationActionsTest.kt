package com.github.swent.echo.ui.navigation

import androidx.navigation.NavHostController
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.Repository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class NavigationActionsTest {

    companion object {
        private const val USER_ID = "1234"
        private val USER_PROFILE =
            UserProfile(
                USER_ID,
                "John Doe",
                null,
                null,
                emptySet(),
                emptySet(),
                emptySet(),
            )
    }

    private lateinit var navController: NavHostController
    private lateinit var navigationActions: NavigationActions
    private lateinit var authService: AuthenticationService
    private lateinit var repository: Repository

    @Before
    fun setUp() {
        navController = mockk(relaxed = true)

        authService = mockk()
        repository = mockk()

        every { authService.getCurrentUserID() } returns USER_ID
        coEvery { repository.getUserProfile(USER_ID) } returns USER_PROFILE

        navigationActions = NavigationActions(navController, authService, repository)
    }

    @Test
    fun `should navigate to the correct route`() {
        val routes =
            listOf(
                Routes.LOGIN,
                Routes.REGISTER,
                Routes.MAP,
                Routes.CREATE_EVENT,
                Routes.EDIT_EVENT.build("1234")
            )
        for (route in routes) {
            navigationActions.navigateTo(route)
            verify { navController.navigate(route.name) }
        }
    }

    @Test
    fun `should go back to the previous screen`() {
        navigationActions.goBack()
        verify { navController.navigateUp() }
    }

    @Test
    fun `should redirect to the home screen when user id is null`() {
        every { authService.getCurrentUserID() } returns null
        navigationActions.navigateTo(Routes.MAP)
        verify { navController.navigate(Routes.LOGIN.name) }
    }

    @Test
    fun `should redirect to the create profile screen when user profile is null`() {
        coEvery { repository.getUserProfile(USER_ID) } returns null
        navigationActions.navigateTo(Routes.MAP)
        verify { navController.navigate(Routes.PROFILE_CREATION.name) }
    }
}
