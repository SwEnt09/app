package com.github.swent.echo.compose.association

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.viewmodels.association.AssociationPage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssociationSearchTest {

    @get:Rule val composeTestRule = createComposeRule()

    private var nextPage = AssociationPage.SEARCH
    private val testAssociation = Association("id 1", "name 1", "description 1")

    @Before
    fun setup() {
        nextPage = AssociationPage.SEARCH
        composeTestRule.setContent {
            AssociationSearch(
                {
                    nextPage = AssociationPage.DETAILS
                    nextPage.association = it
                },
                listOf(testAssociation)
            )
        }
    }

    @Test
    fun onRowClicked() {
        composeTestRule.onNodeWithTag("association_list_${testAssociation.name}").performClick()
        val expectedPage = AssociationPage.DETAILS
        expectedPage.association = testAssociation
        assert(nextPage == expectedPage)
    }

    @Test
    fun onNameAssociationClicked() {
        composeTestRule
            .onNodeWithTag("association_name_button_${testAssociation.name}")
            .performClick()
        val expectedPage = AssociationPage.DETAILS
        expectedPage.association = testAssociation
        assert(nextPage == expectedPage)
    }
}
