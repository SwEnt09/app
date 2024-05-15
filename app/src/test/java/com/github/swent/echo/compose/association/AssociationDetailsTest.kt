package com.github.swent.echo.compose.association

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZonedDateTime

@RunWith(AndroidJUnit4::class)
class AssociationDetailsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testAssociation = Association("id 1", "name 1", "description 1")
    private var isFollowed = false
    private val testEvent = Event(
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
                { isFollowed = !isFollowed },
                testAssociation,
                isFollowed,
                listOf(testEvent),
                true
            )
        }
    }
}