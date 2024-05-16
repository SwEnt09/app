package com.github.swent.echo.compose.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.compose.components.searchmenu.SearchMenuDiscover
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.viewmodels.tag.TagViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.TestCoroutineScheduler
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchMenuDiscoverTest {
    private val rootTagId = "1d253a7e-eb8c-4546-bc98-1d3adadcffe8"
    private val topTag = listOf(Tag("tag1", "tag1"))
    private val subTag1 = listOf(Tag("tag2", "tag2"), Tag("tag3", "tag3"))
    private val subTag3 = listOf(Tag("tag4", "tag4"))
    private val subTag2 = listOf(Tag("tag5", "tag5"))
    private var str = ""

    @get:Rule val composeTestRule = createComposeRule()

    private val mockedRepository = mockk<Repository>(relaxed = true)
    private lateinit var tagViewModel: TagViewModel
    private val scheduler = TestCoroutineScheduler()

    @Before
    fun init() {
        coEvery { mockedRepository.getSubTags(rootTagId) } returns topTag
        coEvery { mockedRepository.getSubTags("tag1") } returns subTag1
        coEvery { mockedRepository.getSubTags("tag3") } returns subTag3
        coEvery { mockedRepository.getSubTags("tag2") } returns subTag2
        tagViewModel = TagViewModel(mockedRepository, SavedStateHandle())
        scheduler.runCurrent()
        composeTestRule.setContent {
            SearchMenuDiscover(searchEntryCallback = { str = it }, tagViewModel = tagViewModel)
        }
    }

    @Test
    fun testMainComponentExists() {
        composeTestRule.onNodeWithTag("discover_main_component").assertExists()
    }

    @Test
    fun testCorrectExecutionDiscover() {
        // Test default state
        composeTestRule.onNodeWithTag("discover_select_category").assertExists()
        // Test if clicked tag now appears as parent
        composeTestRule.onNodeWithTag("discover_child_${topTag[0].name}").performClick()
        composeTestRule.onNodeWithTag("discover_select_category").assertDoesNotExist()
        composeTestRule.onNodeWithTag("discover_parent_${topTag[0].name}").assertExists()
        // Test if clicked tag now appears as parent + previous parent
        composeTestRule.onNodeWithTag("discover_child_${subTag1[0].name}").performClick()
        composeTestRule.onNodeWithTag("discover_select_category").assertDoesNotExist()
        composeTestRule.onNodeWithTag("discover_parent_${topTag[0].name}").assertExists()
        composeTestRule.onNodeWithTag("discover_parent_${subTag1[0].name}").assertExists()
        // Test if on parent tag click, it goes back to this parent
        composeTestRule.onNodeWithTag("discover_parent_${tagViewModel.rootTag.name}").performClick()
        composeTestRule.onNodeWithTag("discover_select_category").assertExists()
    }

    @Test
    fun testLazyGridExists() {
        composeTestRule.onNodeWithTag("discover_lazy_grid").assertExists()
    }
}
