package com.github.swent.echo.compose.association

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.data.model.Association
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssociationListScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    val testAssociation = Association("testid", "test name", "test description")

    @Test
    fun associationScreenDisplayCorrectStrings() {
        composeTestRule.setContent {
            AssociationListScreen(
                title = "test",
                {},
                associationList = listOf(testAssociation),
                hasActionButton = true,
                actionButtonName = "test action",
                onActionButtonClicked = {}
            )
        }
        composeTestRule
            .onNodeWithTag("${testAssociation.associationId}-name", true)
            .assertTextContains(testAssociation.name)
        composeTestRule
            .onNodeWithTag("${testAssociation.associationId}-action-button-text", true)
            .assertTextContains("test action")
    }

    @Test
    fun associationScreenActionAndBackButtonsTriggerCallback() {
        var backClicked = false
        var actionClicked = false
        composeTestRule.setContent {
            AssociationListScreen(
                title = "test",
                { backClicked = true },
                associationList = listOf(testAssociation),
                hasActionButton = true,
                actionButtonName = "test-action",
                onActionButtonClicked = { actionClicked = true }
            )
        }
        composeTestRule
            .onNodeWithTag("${testAssociation.associationId}-action-button", true)
            .performClick()
        assertTrue(actionClicked)
        composeTestRule.onNodeWithTag("Back-button").performClick()
        assertTrue(backClicked)
    }

    @Test
    fun associationScreenWithoutActionButton() {
        composeTestRule.setContent {
            AssociationListScreen(title = "test", {}, associationList = listOf(testAssociation))
        }
        composeTestRule
            .onNodeWithTag("${testAssociation.associationId}-action-button", true)
            .assertDoesNotExist()
        composeTestRule
            .onNodeWithTag("${testAssociation.associationId}-name", true)
            .assertIsDisplayed()
    }

    @Test
    fun associationSnackbarIsDisplayedWhenUnfollowClicked() {
        var undoClicked = false
        composeTestRule.setContent {
            AssociationListScreen(
                title = "test",
                {},
                associationList = listOf(testAssociation),
                hasActionButton = true,
                actionButtonName = "test-action",
                onActionButtonClicked = {},
                onUndoActionButtonClicked = { undoClicked = true }
            )
        }
        composeTestRule
            .onNodeWithTag("${testAssociation.associationId}-action-button", true)
            .performClick()
        composeTestRule.onNodeWithTag("snackbar").assertIsDisplayed()
    }
}
