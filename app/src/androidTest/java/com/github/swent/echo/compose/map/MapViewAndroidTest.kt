package com.github.swent.echo.compose.map

import android.view.View
import androidx.compose.runtime.Composable
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
                    organizerId = "a",
                    title = "Bowling Event",
                    description = "",
                    location = Location("Location 1", OsmdroidMapViewProvider.LAUSANNE_GEO_POINT),
                    startDate = Date.from(Instant.now()),
                    endDate = Date.from(Instant.now()),
                    tags = emptySet(),
                ),
                Event(
                    eventId = "b",
                    organizerId = "a",
                    title = "Swimming Event",
                    description = "",
                    location =
                        Location(
                            "Location 2",
                            OsmdroidMapViewProvider.LAUSANNE_GEO_POINT.destinationPoint(
                                1000.0,
                                90.0
                            )
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
            val e = remember { mutableStateOf(events) }
            MapDrawer(context = context, events = e)
        }

        @Composable
        private fun <T : View> DummyMapDrawer(p: IMapViewProvider<T>) {
            val e = remember { mutableStateOf(events) }
            EchoAndroidView(factory = p::factory, update = p::update, events = e)
        }

        private fun provider() = OsmdroidMapViewProvider(context)
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
    fun mapViewCreatorShouldCreateViewWithCorrectZoom() {
        val p = provider()
        composeTestRule.setContent { DummyMapDrawer(p) }
        assertEquals(p.getZoom(), OsmdroidMapViewProvider.ZOOM_DEFAULT, 0.0)
    }

    @Test
    fun mapViewCreatorShouldCreateViewWithCorrectCenter() {
        val p = provider()
        composeTestRule.setContent { DummyMapDrawer(p) }
        assertTrue(closeEnough(OsmdroidMapViewProvider.LAUSANNE_GEO_POINT, p.getCenter()))
    }

    @Test
    fun mapViewCreatorShouldCreateViewWithCorrectOutlineClip() {
        val p = provider()
        composeTestRule.setContent { DummyMapDrawer(p) }
        assertTrue(p.getClipToOutline())
    }
}
