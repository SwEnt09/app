package com.github.swent.echo.compose.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.compose.components.searchmenu.FiltersContainer
import com.github.swent.echo.compose.components.searchmenu.SortBy
import java.time.ZonedDateTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchMenuSheetTest {
    @get:Rule val composeTestRule = createComposeRule()

    private var dismissed = 0
    private var extended = 0
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

    @Before
    fun setUp() {
        composeTestRule.setContent {
            dismissed = 0
            extended = 0
            filters.tagId = remember { mutableStateOf("") }
            filters.epflChecked = remember { mutableStateOf(true) }
            filters.sectionChecked = remember { mutableStateOf(true) }
            filters.classChecked = remember { mutableStateOf(true) }
            filters.pendingChecked = remember { mutableStateOf(true) }
            filters.confirmedChecked = remember { mutableStateOf(true) }
            filters.fullChecked = remember { mutableStateOf(true) }
            filters.from = remember { mutableStateOf(ZonedDateTime.now()) }
            filters.to = remember { mutableStateOf(ZonedDateTime.now()) }
            filters.sortBy = remember { mutableStateOf(SortBy.NONE) }

            SearchMenuSheet(filters, onDismiss = { dismissed++ }, onFullyExtended = { extended++ })
        }
    }

    @Test
    fun shouldShowSearchMenuSheet() {
        composeTestRule.onNodeWithTag("search_menu_sheet").assertExists()
    }

    @Test
    fun shouldShowSearchMenuSheetContent() {
        composeTestRule.onNodeWithTag("search_menu_sheet_content").assertExists()
    }

    @Test
    fun shouldShowSearchMenuFirstLayer() {
        composeTestRule.onNodeWithTag("search_menu_first_layer").assertExists()
    }

    @Test
    fun shouldShowSearchMenuSheetContentSearchBarTags() {
        composeTestRule.onNodeWithTag("search_menu_search_bar_tags").assertExists()
    }

    @Test
    fun shouldShowSearchMenuSheetContentSwitchSearchModeButton() {
        composeTestRule.onNodeWithTag("search_menu_switch_mode_button").assertExists()
    }

    @Test
    fun shouldSwitchSearchModeWhenSwitchModeButtonClicked() {
        composeTestRule.onNodeWithTag("search_menu_switch_mode_button").performClick()
        composeTestRule.onNodeWithTag("search_menu_filters_content").assertDoesNotExist()
        composeTestRule.onNodeWithTag("search_menu_switch_mode_button").performClick()
        composeTestRule.onNodeWithTag("search_menu_filters_content").assertExists()
    }

    @Test
    fun shouldShowSearchMenuSecondLayer() {
        composeTestRule.onNodeWithTag("search_menu_second_layer").assertExists()
    }

    @Test
    fun shouldShowSearchMenuThirdLayer() {
        composeTestRule.onNodeWithTag("search_menu_third_layer").assertExists()
    }

    @Test
    fun shouldShowSearchMenuSheetContentResetFiltersButton() {
        composeTestRule.onNodeWithTag("search_menu_reset_filters_button").assertExists()
    }
}