package com.github.swent.echo.compose.map

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Location
import java.time.Instant
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint

@RunWith(AndroidJUnit4::class)
class MapViewAndroidTest {

    companion object {
        private const val ERROR_MARGIN_IN_METERS = 5.0

        private val events =
            listOf(
                Event(
                    eventId = "a",
                    creatorId = "a",
                    organizerId = "a",
                    title = "Bowling Event",
                    description = "",
                    location = Location("Location 1", MAP_CENTER.toGeoPoint()),
                    startDate = Date.from(Instant.now()),
                    endDate = Date.from(Instant.now()),
                    tags = emptySet(),
                ),
                Event(
                    eventId = "b",
                    creatorId = "a",
                    organizerId = "a",
                    title = "Swimming Event",
                    description = "",
                    location =
                        Location(
                            "Location 2",
                            MAP_CENTER.toGeoPoint().destinationPoint(1000.0, 90.0)
                        ),
                    startDate = Date.from(Instant.now()),
                    endDate = Date.from(Instant.now()),
                    tags = emptySet(),
                )
            )

        private val context = InstrumentationRegistry.getInstrumentation().targetContext

        fun closeEnough(p1: GeoPoint, p2: IGeoPoint) =
            p1.distanceToAsDouble(p2) < ERROR_MARGIN_IN_METERS

        @Composable
        private fun DummyMapDrawer() {
            val e by remember { mutableStateOf(events) }
            MapDrawer(events = e)
        }

        @Composable
        private fun <T : View> DummyMapDrawer(p: IMapViewProvider<T>) {
            val e by remember { mutableStateOf(events) }
            EchoAndroidView(
                factory = p::factory,
                update = p::update,
                events = e,
            )
        }
    }

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun osmdroidConfigurationShouldHaveTheCorrectValues() {
        composeTestRule.setContent { DummyMapDrawer() }
        Configuration.getInstance().apply {
            assertEquals(osmdroidBasePath, context.cacheDir)
            assertEquals(userAgentValue, context.packageName)
        }
    }

    @Test
    fun mapDrawerShouldDisplayAndroidView() {
        composeTestRule.setContent { DummyMapDrawer() }
        composeTestRule.onNodeWithTag("mapViewWrapper").assertIsDisplayed()
    }

    @Test
    fun mapViewProviderShouldCreateViewWithCorrectZoom() {
        val p = OsmdroidMapViewProvider()
        composeTestRule.setContent { DummyMapDrawer(p) }
        assertEquals(p.getZoom(), DEFAULT_ZOOM, 0.0)
    }

    @Test
    fun mapViewProviderShouldCreateViewWithCorrectCenter() {
        val p = OsmdroidMapViewProvider()
        composeTestRule.setContent { DummyMapDrawer(p) }
        assertTrue(closeEnough(MAP_CENTER.toGeoPoint(), p.getCenter()))
    }

    @Test
    fun mapViewProviderShouldCreateViewWithCorrectOutlineClip() {
        val p = OsmdroidMapViewProvider()
        composeTestRule.setContent { DummyMapDrawer(p) }
        assertTrue(p.getClipToOutline())
    }

    @Test
    fun mapLibreMapViewProviderShouldDisplayView() {
        val p = MapLibreMapViewProvider()
        composeTestRule.setContent { DummyMapDrawer(p) }
        composeTestRule.onNodeWithTag("mapViewWrapper").assertIsDisplayed()
    }
}
