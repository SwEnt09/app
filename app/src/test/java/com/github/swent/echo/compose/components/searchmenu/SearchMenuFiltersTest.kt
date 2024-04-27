package com.github.swent.echo.compose.components.searchmenu

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import java.time.ZonedDateTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchMenuFiltersTest {
    @get:Rule val composeTestRule = createComposeRule()

    private var filters =
        FiltersContainer(
            searchEntry = "",
            epflChecked = true,
            sectionChecked = true,
            classChecked = true,
            pendingChecked = true,
            confirmedChecked = true,
            fullChecked = true,
            from = ZonedDateTime.now(),
            to = ZonedDateTime.now().plusDays(365),
            sortBy = SortBy.NONE
        )
    private var checkboxes = listOf("EPFL", "Section", "Class", "Pending", "Confirmed", "Full")
    private var callback = 0

    @Before
    fun setUp() {
        composeTestRule.setContent {


            SearchMenuFilters(filters, { callback++ }, { callback++ }, { callback++ }, { callback++ }, { callback++ }, { callback++ }, { callback++ })
        }
    }

    @Test
    fun testSearchMenuFiltersExists() {
        composeTestRule.onNodeWithTag("search_menu_filters_content").assertExists()
    }

    @Test
    fun testSortByDisplayerContainerExists() {
        composeTestRule.onNodeWithTag("sort_by_displayer_container").assertExists()
    }

    @Test
    fun testSortByButtonExists() {
        composeTestRule.onNodeWithTag("sort_by_button").assertExists()
    }

    @Test
    fun testSortByDisplayerDisplayAndHideItemsWhenClicked() {
        composeTestRule.onNodeWithTag("sort_by_button").performClick()
        SortBy.entries.forEach { composeTestRule.onNodeWithTag(it.stringKey).assertExists() }
        composeTestRule.onNodeWithTag("sort_by_button").performClick()
        SortBy.entries.forEach { composeTestRule.onNodeWithTag(it.stringKey).assertDoesNotExist() }
    }

    @Test
    fun testCheckBoxesContainerExists() {
        composeTestRule.onNodeWithTag("checkboxes_container").assertExists()
    }

    @Test
    fun testCheckBoxesAllExist() {
        checkboxes.forEach { composeTestRule.onNodeWithTag("${it}_checkbox_row").assertExists() }
    }

    @Test
    fun testCheckBoxesCheckedAndUncheckedWhenClicked() {
        checkboxes.forEach { composeTestRule.onNodeWithTag("${it}_checkbox").performClick() }
        assertEquals(callback, 6)
        checkboxes.forEach { composeTestRule.onNodeWithTag("${it}_checkbox").performClick() }
        assertEquals(callback, 12)
    }
}
