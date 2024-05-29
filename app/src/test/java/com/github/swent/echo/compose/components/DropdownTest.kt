package com.github.swent.echo.compose.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DropdownTest {

    @get:Rule val composeTestRule = createComposeRule()

    private val title = "test"
    private val items = listOf("item1", "item2", "item3")
    private var selectedItem = -1

    @Before
    fun setup() {
        selectedItem = -1
        composeTestRule.setContent { Dropdown(title, items, selectedItem) { selectedItem = it } }
    }

    @Test
    fun openDropdownMenu() {
        composeTestRule.onNodeWithTag("dropdown_button").performClick()
        composeTestRule.onNodeWithTag("dropdown_menu").assertIsDisplayed()
    }

    @Test
    fun selectItems() {
        composeTestRule.onNodeWithTag("dropdown_button").performClick()
        composeTestRule.onNodeWithTag("dropdown_item_null").performClick()
        assert(selectedItem == -1)
        items.forEachIndexed { index, item ->
            composeTestRule.onNodeWithTag("dropdown_button").performClick()
            composeTestRule.onNodeWithTag("dropdown_item_$item").performClick()
            assert(selectedItem == index)
        }
    }
}
