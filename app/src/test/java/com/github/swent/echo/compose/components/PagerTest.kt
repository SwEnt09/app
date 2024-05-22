package com.github.swent.echo.compose.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PagerTest {

    @get:Rule val composeTestRule = createComposeRule()

    private lateinit var pagerContent: List<Pair<String, @Composable () -> Unit>>

    @Before
    fun setUp() {
        pagerContent =
            listOf(
                Pair("Title 1") { Text("Description 1", modifier = Modifier.testTag("page_0")) },
                Pair("Title 2") { Text("Description 2", modifier = Modifier.testTag("page_1")) }
            )
        composeTestRule.setContent { Pager(pagerContent) }
    }

    @Test
    fun pagerExists() {
        composeTestRule.onNodeWithTag("pager").assertExists()
    }

    @Test
    fun switchingBetweenPagesShouldWork() {
        // Initial state
        composeTestRule.onNodeWithTag("page_0").assertExists()
        composeTestRule.onNodeWithTag("underline_0").assertExists()
        composeTestRule.onNodeWithTag("page_1").assertDoesNotExist()
        composeTestRule.onNodeWithTag("underline_1").assertDoesNotExist()

        // Change page by clicking on title
        composeTestRule.onNodeWithTag("page_title_1").performClick()
        composeTestRule.onNodeWithTag("page_0").assertDoesNotExist()
        composeTestRule.onNodeWithTag("underline_0").assertDoesNotExist()
        composeTestRule.onNodeWithTag("page_1").assertExists()
        composeTestRule.onNodeWithTag("underline_1").assertExists()

        // Go back to the first page by swiping
        composeTestRule.onNodeWithTag("pager").performTouchInput {
            swipe(start = Offset(x = 50f, y = center.y), end = Offset(x = 300f, y = center.y))
        }
        composeTestRule.onNodeWithTag("page_0").assertExists()
        composeTestRule.onNodeWithTag("underline_0").assertExists()
        composeTestRule.onNodeWithTag("page_1").assertDoesNotExist()
        composeTestRule.onNodeWithTag("underline_1").assertDoesNotExist()
    }
}
