package com.github.swent.echo.compose.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.viewmodels.tag.TagViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TagSelectionDialogTest {
    private val rootTagId = ""
    private val topTag = listOf(Tag("tag1", "tag1"))
    private val subTag1 = listOf(Tag("tag2", "tag2"), Tag("tag3", "tag3"))
    private val subTag3 = listOf(Tag("tag4", "tag4"))
    private var callBackTriggered = false

    @get:Rule val composeTestRule = createComposeRule()

    private val mockedRepository = mockk<Repository>(relaxed = true)
    private lateinit var tagViewModel: TagViewModel

    @Before
    fun init() {
        coEvery { mockedRepository.getSubTags(rootTagId) } returns topTag
        coEvery { mockedRepository.getSubTags("tag1") } returns subTag1
        coEvery { mockedRepository.getSubTags("tag3") } returns subTag3
        tagViewModel = TagViewModel(mockedRepository)
        composeTestRule.setContent {
            TagSelectionDialog(
                onDismissRequest = {},
                tagViewModel = tagViewModel,
                onTagSelected = { callBackTriggered = true }
            )
        }
    }

    @Test
    fun clickOnSubTagButtonShouldDisplaySubTags() {
        composeTestRule.onNodeWithTag("tag-dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tag1-subtag-button").performClick()
        composeTestRule.onNodeWithTag("tag3-subtag-button").performClick()
        composeTestRule.onNodeWithTag("tag4-select-button").assertIsDisplayed()
    }

    @Test
    fun clickOnTagBackButtonDisplaySuperTag() {
        composeTestRule.onNodeWithTag("tag-dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tag1-subtag-button").performClick()
        composeTestRule.onNodeWithTag("tag-back-button").performClick()
        composeTestRule.onNodeWithTag("tag1-select-button").assertIsDisplayed()
    }

    @Test
    fun clickOnTagTriggerCallBack() {
        composeTestRule.onNodeWithTag("tag-dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tag1-select-button").performClick()
        assertTrue(callBackTriggered)
    }
}
