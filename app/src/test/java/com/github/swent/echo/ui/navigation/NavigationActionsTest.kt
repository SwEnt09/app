package com.github.swent.echo.ui.navigation

import androidx.navigation.NavHostController
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class NavigationActionsTest {
    private lateinit var navController: NavHostController
    private lateinit var navigationActions: NavigationActions

    @Before
    fun setUp() {
        navController = mockk(relaxed = true)

        navigationActions = NavigationActions(navController)
    }

    @Test
    fun `should navigate to the correct route`() {
        val routes =
            listOf(
                Routes.LOADING,
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
}
