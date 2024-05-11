package com.github.swent.echo.compose.association

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.viewmodels.user.UserProfileViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssociationCommitteeMemberScreen {

    @get:Rule val composeTestRule = createComposeRule()

    private val mockedNavActions = mockk<NavigationActions>(relaxed = true)
    private val userProfileViewModel = mockk<UserProfileViewModel>(relaxed = true)
    private val userProfile =
        UserProfile.EMPTY.copy(
            associationsSubscriptions = setOf(Association("testAI", "testAN", "testAD"))
        )

    @Before
    fun init() {
        composeTestRule.setContent {
            every { userProfileViewModel.userProfile } returns MutableStateFlow(userProfile)
            AssociationCommitteeMemberScreen(
                userProfileViewModel = userProfileViewModel,
                navigationActions = mockedNavActions
            )
        }
    }

    @Test
    fun goBackButtonShouldGoBack() {
        composeTestRule.onNodeWithTag("Back-button").performClick()
        verify { mockedNavActions.goBack() }
    }
}
