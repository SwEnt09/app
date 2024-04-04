package com.github.swent.echo.compose.map

import androidx.compose.runtime.getValue
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

        fun provider(): OsmdroidMapViewProvider = OsmdroidMapViewProvider(context, events)
    }

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun osmdroidConfigurationShouldHaveTheCorrectValues() {
        val p = provider()
        composeTestRule.setContent { MapDrawer(provider = p) }
        assert(Configuration.getInstance().osmdroidBasePath == context.cacheDir)
        assert(Configuration.getInstance().userAgentValue == context.packageName)
    }

    @Test
    fun mapDrawerShouldDisplayAndroidView() {
        val p = provider()
        composeTestRule.setContent { MapDrawer(provider = p) }
        composeTestRule.onNodeWithTag("mapViewWrapper").assertIsDisplayed()
    }

    @Test
    fun mapViewCreatorShouldCreateViewWithCorrectZoom() {
        val p = provider()
        composeTestRule.setContent { MapDrawer(provider = p) }
        assert(p.getZoom() == OsmdroidMapViewProvider.ZOOM_DEFAULT)
    }

    @Test
    fun mapViewCreatorShouldCreateViewWithCorrectCenter() {
        val p = provider()
        composeTestRule.setContent { MapDrawer(provider = p) }
        assert(closeEnough(OsmdroidMapViewProvider.LAUSANNE_GEO_POINT, p.getCenter()))
    }

    @Test
    fun mapViewCreatorShouldCreateViewWithCorrectOutlineClip() {
        val p = provider()
        composeTestRule.setContent { MapDrawer(provider = p) }
        assert(p.getClipToOutline())
    }
}
