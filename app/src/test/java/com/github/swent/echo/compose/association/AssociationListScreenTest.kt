package com.github.swent.echo.compose.association

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.data.model.Association
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssociationListScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    private val testAssociations =
        listOf(
            Association("id 1", "name 1", "description 1"),
            Association("id 2", "name 2", "description 2"),
            Association("id 3", "name 3", "description 3")
        )
    private var onRowClicked = 0
    private var onAssociationClicked = 0

    @Before
    fun setUp() {
        onRowClicked = 0
        onAssociationClicked = 0
        composeTestRule.setContent {
            AssociationListScreen(
                associationList = testAssociations,
            ) {
                onRowClicked++
            }
        }
    }

    @Test
    fun associationListScreenExists() {
        composeTestRule.onNodeWithTag("association_list_screen").assertExists()
    }

    @Test
    fun allAssociationsDisplayed() {
        testAssociations.forEach {
            composeTestRule.onNodeWithTag("association_list_${it.name}").assertExists()
        }
    }

    @Test
    fun clickOnAssociationRowTrigger() {
        testAssociations.forEach {
            composeTestRule.onNodeWithTag("association_list_${it.name}").performClick()
            assertTrue(onRowClicked == 1)
            onRowClicked = 0
        }
    }
}
