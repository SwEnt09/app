package com.github.swent.echo.compose.components

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.MainActivity
import com.github.swent.echo.compose.components.searchmenu.FiltersContainer
import com.github.swent.echo.viewmodels.MapOrListMode
import com.github.swent.echo.viewmodels.SortBy
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
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
            sortBy = SortBy.DATE_ASC
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
                initialPage = 0,
                mode = MapOrListMode.MAP,
                followedAssociations = listOf(),
                selectedAssociation = -1,
                associationCallback = { callback++ }
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
    fun searchBarExists() {
        composeTestRule.onNodeWithTag("search_bar_Search Interests").assertExists()
    }

    @Test
    fun pagerExists() {
        composeTestRule.onNodeWithTag("pager").assertExists()
    }

    @Test
    fun shouldShowSearchMenuSheetContentResetFiltersButton() {
        composeTestRule.onNodeWithTag("search_menu_reset_filters_button").assertExists()
    }
}
