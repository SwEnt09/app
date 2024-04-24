package com.github.swent.echo.compose.map

import android.view.View
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.data.SAMPLE_EVENTS
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.di.SimpleMapViewProvider
import com.github.swent.echo.viewmodels.MapDrawerViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapTest {

    private lateinit var provider: SimpleMapViewProvider
    private lateinit var viewModel: MapDrawerViewModel

    @get:Rule var composeTestRule = createComposeRule()

    private var clickedEvent: Event? = null

    @Before
    fun setUp() {
        clickedEvent = null
        provider = SimpleMapViewProvider()
        viewModel = MapDrawerViewModel(provider as IMapViewProvider<View>)
        composeTestRule.setContent {
            MapDrawer(
                events = SAMPLE_EVENTS,
                callback = { event -> clickedEvent = event },
                mapDrawerViewModel = viewModel
            )
        }
    }

    @Test
    fun shouldShowEventTitles() {
        SAMPLE_EVENTS.forEach { event ->
            composeTestRule.onNodeWithText(event.title).assertIsDisplayed()
        }
    }

    @Test
    fun shouldCallWithCorrectEventWhenMarkerClicked() {
        SAMPLE_EVENTS.forEach { event ->
            composeTestRule.onNodeWithText(event.title).performClick()
            assertEquals(event, clickedEvent)
        }
    }
}
