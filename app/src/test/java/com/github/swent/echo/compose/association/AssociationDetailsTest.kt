package com.github.swent.echo.compose.association

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.AssociationHeader
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import java.time.ZonedDateTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssociationDetailsTest {

    @get:Rule val composeTestRule = createComposeRule()

    private val testAssociation =
        Association(
            "id 1",
            "name 1",
            "description 1",
            "url1",
            setOf(Tag("tagId 1", "tag description"))
        )
    private var isFollowed = mutableStateOf(false)
    private val testEvent =
        Event(
            "event 1",
            EventCreator.EMPTY,
            AssociationHeader.fromAssociation(testAssociation),
            "title 1",
            "",
            Location.EMPTY,
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            emptySet(),
            0,
            0,
            0
        )

    @Before
    fun setUp() {
        composeTestRule.setContent {
            AssociationDetails(
                testAssociation,
                isFollowed.value,
                { isFollowed.value = !isFollowed.value },
                listOf(testEvent),
                true,
                {}
            )
        }
    }

    @Test
    fun associationDetailsExists() {
        composeTestRule.onNodeWithTag("association_details").assertExists()
    }

    @Test
    fun followButtonWorks() {
        composeTestRule.onNodeWithTag("association_details_follow_button").performClick()
        assert(isFollowed.value)
        composeTestRule
            .onNodeWithTag("association_details_follow_button_text", useUnmergedTree = true)
            .assertTextEquals("Unfollow")
        composeTestRule.onNodeWithTag("association_details_follow_button").performClick()
        assert(!isFollowed.value)
        composeTestRule
            .onNodeWithTag("association_details_follow_button_text", useUnmergedTree = true)
            .assertTextEquals("Follow")
    }

    @Test
    fun pagerExists() {
        composeTestRule.onNodeWithTag("pager").assertExists()
    }
}
