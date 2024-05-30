package com.github.swent.echo.compose.components.searchmenu

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.viewmodels.MapOrListMode
import com.github.swent.echo.viewmodels.SortBy
import org.junit.Assert.assertEquals
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
            from = 0f,
            to = 14f,
            sortBy = SortBy.DATE_ASC
        )
    private var checkboxes = listOf("EPFL", "Section", "Class", "Pending", "Confirmed", "Full")
    private var callback = 0

    @Before
    fun setUp() {
        composeTestRule.setContent {
            SearchMenuFilters(
                filters,
                { callback++ },
                { callback++ },
                { callback++ },
                { callback++ },
                { callback++ },
                { callback++ },
                { callback++ },
                { f, t ->
                    filters.from = f
                    filters.to = t
                },
                MapOrListMode.LIST,
                listOf(),
                -1,
                { callback++ }
            )
        }
    }

    @Test
    fun testSearchMenuFiltersExists() {
        composeTestRule.onNodeWithTag("search_menu_filters_content").assertExists()
    }

    @Test
    fun dropdownMenusExist() {
        composeTestRule.onAllNodesWithTag("dropdown_button").assertCountEquals(2)
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

    @Test
    fun testDateSliderExists() {
        composeTestRule.onNodeWithTag("search_menu_time_slider").assertExists()
    }

    @Test
    fun testDateSliderValuesChange() {
        composeTestRule.onNodeWithTag("search_menu_time_slider").performTouchInput {
            // 100f approximately the width of one step of the slider
            swipe(start = Offset(x = 0f, y = center.y), end = Offset(x = 100f, y = center.y))
        }
        assertEquals(filters.from.toInt(), 1)
    }
}
