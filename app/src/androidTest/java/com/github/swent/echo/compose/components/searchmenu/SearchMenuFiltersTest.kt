package com.github.swent.echo.compose.components.searchmenu

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import java.time.ZonedDateTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchMenuFiltersTest {
    @get:Rule val composeTestRule = createComposeRule()

    private var filters =
        FiltersContainer(
            tagId = mutableStateOf(""),
            epflChecked = mutableStateOf(true),
            sectionChecked = mutableStateOf(true),
            classChecked = mutableStateOf(true),
            pendingChecked = mutableStateOf(true),
            confirmedChecked = mutableStateOf(true),
            fullChecked = mutableStateOf(true),
            from = mutableStateOf(ZonedDateTime.now()),
            to = mutableStateOf(ZonedDateTime.now()),
            sortBy = mutableStateOf(SortBy.NONE)
        )
    private var checkboxes = listOf("EPFL", "Section", "Class", "Pending", "Confirmed", "Full")

    @Before
    fun setUp() {
        composeTestRule.setContent {
            filters =
                FiltersContainer(
                    tagId = remember { mutableStateOf("") },
                    epflChecked = remember { mutableStateOf(true) },
                    sectionChecked = remember { mutableStateOf(true) },
                    classChecked = remember { mutableStateOf(true) },
                    pendingChecked = remember { mutableStateOf(true) },
                    confirmedChecked = remember { mutableStateOf(true) },
                    fullChecked = remember { mutableStateOf(true) },
                    from = remember { mutableStateOf(ZonedDateTime.now()) },
                    to = remember { mutableStateOf(ZonedDateTime.now()) },
                    sortBy = remember { mutableStateOf(SortBy.NONE) }
                )
            SearchMenuFilters(filters)
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
        SortBy.entries.forEach { composeTestRule.onNodeWithTag(it.value).assertExists() }
        composeTestRule.onNodeWithTag("sort_by_button").performClick()
        SortBy.entries.forEach { composeTestRule.onNodeWithTag(it.value).assertDoesNotExist() }
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
        assert(!filters.epflChecked.value)
        assert(!filters.sectionChecked.value)
        assert(!filters.classChecked.value)
        assert(!filters.pendingChecked.value)
        assert(!filters.confirmedChecked.value)
        assert(!filters.fullChecked.value)
        checkboxes.forEach { composeTestRule.onNodeWithTag("${it}_checkbox").performClick() }
        assert(filters.epflChecked.value)
        assert(filters.sectionChecked.value)
        assert(filters.classChecked.value)
        assert(filters.pendingChecked.value)
        assert(filters.confirmedChecked.value)
        assert(filters.fullChecked.value)
    }
}
