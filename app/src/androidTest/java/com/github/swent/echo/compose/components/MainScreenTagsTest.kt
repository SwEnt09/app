package com.github.swent.echo.compose.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.data.model.Tag
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenTagsTest {
    @get:Rule val composeTestRule = createComposeRule()
    private var tagClicked: Tag? = null

    @Before
    fun setUp() {
        tagClicked = null
    }

    @Test
    fun showTagText() {
        val tagList = listOf(Tag("Tag1", "Sports"), Tag("Tag2", "Music"))
        composeTestRule.setContent { TagUI(tags = tagList, null) {} }
        composeTestRule.onNodeWithText("Sports").assertExists()
        composeTestRule.onNodeWithText("Music").assertExists()
    }

    @Test
    fun clickTag() {
        val tagList = listOf(Tag("Tag1", "Sports"), Tag("Tag2", "Music"))
        composeTestRule.setContent { TagUI(tags = tagList, null) { tagClicked = it } }
        composeTestRule.onNodeWithText("Sports").performClick()
        assert(tagClicked != null && tagClicked!!.name == "Sports")
        composeTestRule.onNodeWithText("Music").performClick()
        assert(tagClicked != null && tagClicked!!.name == "Music")
    }
}
