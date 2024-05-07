package com.github.swent.echo.compose.components

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.github.swent.echo.MainActivity
import com.github.swent.echo.compose.components.searchmenu.FiltersContainer
import com.github.swent.echo.compose.components.searchmenu.SortBy
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SearchMenuSheetTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

    private var dismissed = 0
    private var extended = 0
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
            sortBy = SortBy.NONE
        )
    private var callback = 0

    @Before
    fun setUp() {
        hiltRule.inject()

        composeTestRule.activity.setContent {
            dismissed = 0
            extended = 0

            SearchMenuSheet(
                filters,
                onDismiss = { dismissed++ },
                onFullyExtended = { extended++ },
                searchEntryCallback = { callback++ },
                epflCallback = { callback++ },
                sectionCallback = { callback++ },
                classCallback = { callback++ },
                pendingCallback = { callback++ },
                confirmedCallback = { callback++ },
                fullCallback = { callback++ },
                sortByCallback = { callback++ },
                resetFiltersCallback = { callback++ },
                timeFilterCallback = { _, _ -> callback++ },
            )
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
