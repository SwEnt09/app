package com.github.swent.echo.compose.authentication

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class AuthenticationFormTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun shouldNotDisplayErrorWhenErrorIsNull() {
        composeTestRule.setContent { AuthenticationForm("Action", { _, _ -> }, null) }
        composeTestRule.onNodeWithTag("error-message").assertDoesNotExist()
    }

    @Test
    fun shouldDisplayErrorMessageWhenErrorIsNotNull() {
        val error = "An error occurred"
        composeTestRule.setContent { AuthenticationForm("Action", { _, _ -> }, error) }
        composeTestRule.onNodeWithTag("error-message").assertExists()
    }

    @Test
    fun shouldDisplayLabels() {
        composeTestRule.setContent { AuthenticationForm("Action", { _, _ -> }) }
        composeTestRule.onNodeWithText("Email").assertExists()
        composeTestRule.onNodeWithText("Password").assertExists()
    }
}
