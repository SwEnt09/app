package com.github.swent.echo.compose.authentication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.data.model.SectionEPFL
import com.github.swent.echo.data.model.SemesterEPFL
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateProfileTest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun profileCreationScreen_Loads() {
        // Verify that the Profile Creation screen loads successfully
        composeTestRule.setContent {
            ProfileCreationUI(
                listOf(SectionEPFL.AR, SectionEPFL.IN, SectionEPFL.SC, SectionEPFL.GM),
                listOf(SemesterEPFL.BA1, SemesterEPFL.BA2),
                listOf(Tag("1", "Sports"), Tag("2", "Music")),
                navAction = NavigationActions(navController = rememberNavController())
            )
        }
        // Assert that certain elements are present on the screen
        composeTestRule.onNodeWithTag("Save").assertExists()
        composeTestRule.onNodeWithTag("Back").assertExists()
        composeTestRule.onNodeWithTag("AddTag").assertExists()
        composeTestRule.onNodeWithTag("FirstName").assertExists()
        composeTestRule.onNodeWithTag("LastName").assertExists()
        composeTestRule.onNodeWithTag("Music").assertExists()
        composeTestRule.onNodeWithTag("Sports").assertExists()
        composeTestRule.onNodeWithTag("Section").assertExists()
        composeTestRule.onNodeWithTag("Semester").assertExists()
        composeTestRule.onAllNodesWithContentDescription("list dropdown")[0].performClick()
        // Verify that the dropdown menu appears
        composeTestRule.onNodeWithTag("SC").assertExists()
        composeTestRule.onNodeWithTag("IN").assertExists()
        // composeTestRule.onNodeWithTag("Math").assertExists()
        composeTestRule.onNodeWithTag("GM").assertExists()
        composeTestRule.onAllNodesWithContentDescription("list dropdown")[1].performClick()
        composeTestRule.onNodeWithTag("BA1").assertExists()
        composeTestRule.onNodeWithTag("BA2").assertExists()
    }
    /*
       @Test
       fun profileCreationScreen_SaveButtonClicked() {
           var saveButtonClicked = false
           val saveOnClick: () -> Unit = { saveButtonClicked = true }

           composeTestRule.setContent {
               ProfileCreationUI(
                   emptyList(),
                   emptyList(),
                   emptyList(),
                   navAction = NavigationActions(navController = rememberNavController())
               )
           }
           // Simulate a click on the Save button
           composeTestRule.onNodeWithText("Save").performClick()
           assert(saveButtonClicked)
       }
    */
}
