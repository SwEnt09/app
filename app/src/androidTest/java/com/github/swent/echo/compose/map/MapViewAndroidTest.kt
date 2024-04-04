package com.github.swent.echo.compose.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
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

        fun closeEnough(p1: GeoPoint, p2: IGeoPoint) =
            p1.distanceToAsDouble(p2) < ERROR_MARGIN_IN_METERS
    }

    @get:Rule val composeTestRule = createComposeRule()
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun osmdroidConfigurationShouldHaveTheCorrectValues() {
        val p = OsmdroidMapViewProvider(context)
        composeTestRule.setContent { MapDrawer(provider = p) }
        assert(Configuration.getInstance().osmdroidBasePath == context.cacheDir)
        assert(Configuration.getInstance().userAgentValue == context.packageName)
    }

    @Test
    fun mapDrawerShouldDisplayAndroidView() {
        val p = OsmdroidMapViewProvider(context)
        composeTestRule.setContent { MapDrawer(provider = p) }
        composeTestRule.onNodeWithTag("mapViewWrapper").assertIsDisplayed()
    }

    @Test
    fun mapViewCreatorShouldCreateViewWithCorrectZoom() {
        val p = OsmdroidMapViewProvider(context)
        composeTestRule.setContent { MapDrawer(provider = p) }
        assert(p.getZoom() == OsmdroidMapViewProvider.ZOOM_DEFAULT)
    }

    @Test
    fun mapViewCreatorShouldCreateViewWithCorrectCenter() {
        val p = OsmdroidMapViewProvider(context)
        composeTestRule.setContent { MapDrawer(provider = p) }
        assert(closeEnough(OsmdroidMapViewProvider.LAUSANNE_GEO_POINT, p.getCenter()))
    }

    @Test
    fun mapViewCreatorShouldCreateViewWithCorrectOutlineClip() {
        val p = OsmdroidMapViewProvider(context)
        composeTestRule.setContent { MapDrawer(provider = p) }
        assert(p.getClipToOutline())
    }
}
