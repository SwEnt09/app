package com.github.swent.echo.compose.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.viewmodels.authentication.CreateProfileState
import com.github.swent.echo.viewmodels.authentication.CreateProfileViewModel
import com.github.swent.echo.viewmodels.tag.TagViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileCreationScreenTest {

    lateinit var viewModel: CreateProfileViewModel
    lateinit var navActions: NavigationActions
    lateinit var tagviewModel: TagViewModel

    @get:Rule val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        viewModel =
            mockk(relaxed = true) {
                every { state } returns MutableStateFlow(CreateProfileState.SAVING)
                every { isOnline } returns MutableStateFlow(true)
                every { isEditing } returns MutableStateFlow(false)
                every { firstName } returns MutableStateFlow("")
                every { lastName } returns MutableStateFlow("")
                every { selectedSection } returns MutableStateFlow(null)
                every { selectedSemester } returns MutableStateFlow(null)
                every { tagList } returns MutableStateFlow(emptySet())
                every { picture } returns MutableStateFlow(null)
            }
        navActions = mockk(relaxed = true)
        tagviewModel = mockk(relaxed = true)

        composeTestRule.setContent {
            ProfileCreationScreen(
                viewModel = viewModel,
                navAction = navActions,
                tagviewModel = tagviewModel,
            )
        }
    }

    @Test
    fun shouldShowSavingOverlay() {
        // Verify that the saving overlay is shown when the state is SAVING
        composeTestRule.onNodeWithTag("saving-overlay").assertIsDisplayed()
    }
}
