package com.github.swent.echo.compose.association

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.MainActivity
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.viewmodels.association.AssociationViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AssociationScreenTest {

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navActions: NavigationActions
    private lateinit var associationViewModel: AssociationViewModel
    private val testAssociation =
        Association("id 1", "name 1", "description 1", "url 1", setOf(Tag.EMPTY))

    @Before
    fun setUp() {
        hiltRule.inject()

        navActions = mockk(relaxed = true)
        composeTestRule.activity.setContent {
            associationViewModel = hiltViewModel()
            AssociationScreen(associationViewModel = associationViewModel, navActions = navActions)
        }
    }

    @Test
    fun associationScreenExists() {
        composeTestRule.onNodeWithTag("association_screen").assertExists()
    }

    @Test
    fun navigationWorks() {
        // Initial state
        composeTestRule.onNodeWithTag("search_bar_Associations/Categories").assertExists()
        composeTestRule.onNodeWithTag("pager").assertExists()

        // Navigate to an association details
        associationViewModel.setCurrentAssociationPage(testAssociation)
        composeTestRule.onNodeWithTag("association_details").assertExists()

        // Navigate back
        composeTestRule.onNodeWithTag("Back-button").performClick()
        composeTestRule.onNodeWithTag("search_bar_Associations/Categories").assertExists()
        composeTestRule.onNodeWithTag("pager").assertExists()
    }
}
