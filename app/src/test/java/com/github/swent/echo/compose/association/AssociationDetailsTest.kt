package com.github.swent.echo.compose.association

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import java.time.ZonedDateTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssociationDetailsTest {

    @get:Rule val composeTestRule = createComposeRule()

    private val testAssociation = Association("id 1", "name 1", "description 1")
    private var isFollowed = mutableStateOf(false)
    private val testEvent =
        Event(
            "event 1",
            EventCreator.EMPTY,
            testAssociation,
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
                { isFollowed.value = !isFollowed.value },
                testAssociation,
                isFollowed.value,
                listOf(testEvent),
                true
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
    fun associationDetailsTabsWork() {
        composeTestRule.onNodeWithTag("association_details_events_tab").performClick()
        composeTestRule.onNodeWithTag("association_details_underline_events").assertExists()
        composeTestRule.onNodeWithTag("list_drawer").assertExists()

        composeTestRule.onNodeWithTag("association_details_description_tab").performClick()
        composeTestRule.onNodeWithTag("association_details_underline_description").assertExists()
        composeTestRule
            .onNodeWithTag("association_details_description_text", useUnmergedTree = true)
            .assertTextEquals(testAssociation.description)
    }
}
