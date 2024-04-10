package com.github.swent.echo.compose.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchMenuSheetTest {

    @get:Rule val composeTestRule = createComposeRule()

    private var dismissed = 0
    private var extended = 0

    @Before
    fun setUp(): Unit {
        composeTestRule.setContent {
            dismissed = 0
            extended = 0
            SearchMenuSheet(onDismiss = { dismissed++ }, onFullyExtended = { extended++ })
        }
    }

    @Test
    fun shouldShowSearchMenuSheet(): Unit {
        composeTestRule.onNodeWithTag("search_menu_sheet").assertExists()
    }
}
