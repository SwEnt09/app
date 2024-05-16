package com.github.swent.echo.compose.association

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.viewmodels.association.AssociationPage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

enum class TestExpandableLists(val value: String) {
    FOLLOWED_ASSOCIATIONS("Followed Associations"),
    MY_ASSOCIATIONS("My Associations")
}

@RunWith(AndroidJUnit4::class)
class AssociationMainScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    private val testAssociations =
        listOf(
            Association("id 1", "name 1", "description 1"),
            Association("id 2", "name 2", "description 2"),
            Association("id 3", "name 3", "description 3")
        )
    private val testEvents =
        listOf(
            Event.EMPTY.copy(
                eventId = "event 1",
                organizer = testAssociations[0],
                title = "title 1"
            ),
            Event.EMPTY.copy(
                eventId = "event 2",
                organizer = testAssociations[1],
                title = "title 2"
            ),
            Event.EMPTY.copy(
                eventId = "event 3",
                organizer = testAssociations[2],
                title = "title 3"
            )
        )
    private var nextPage = AssociationPage.MAINSCREEN
    private var associationToFilter = listOf<Association>()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            AssociationMainScreen(
                events = testEvents,
                goTo = { nextPage = it },
                addAssociationToFilter = { associationToFilter += it },
                followedAssociations = testAssociations,
                committeeAssociations = listOf(testAssociations[0]),
                eventsFilter = associationToFilter,
                isOnline = true
            )
        }
    }

    @Test
    fun allExpandableListsExist() {
        TestExpandableLists.entries.forEach {
            composeTestRule.onNodeWithTag("association_expandable_list_${it.value}").assertExists()
        }
    }

    @Test
    fun allExpandableListsContainsCorrectAssociations() {
        TestExpandableLists.entries.forEach {
            val associationList =
                when (it) {
                    TestExpandableLists.FOLLOWED_ASSOCIATIONS -> testAssociations
                    TestExpandableLists.MY_ASSOCIATIONS -> listOf(testAssociations[0])
                }
            composeTestRule
                .onNodeWithTag("association_expandable_list_${it.value}_box")
                .performClick()
            associationList.forEach { association ->
                composeTestRule.onNodeWithTag("association_list_${association.name}").assertExists()
            }
            composeTestRule
                .onNodeWithTag("association_expandable_list_${it.value}_box")
                .performClick()
        }
    }

    @Test
    fun listDrawerExists() {
        composeTestRule.onNodeWithTag("list_drawer").assertExists()
    }
}
