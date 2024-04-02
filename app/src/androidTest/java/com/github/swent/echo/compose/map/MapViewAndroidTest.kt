package com.github.swent.echo.compose.map


import android.content.Context
import android.os.Looper
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.ITileSource
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import kotlin.math.abs
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class MapViewAndroidTest {

    companion object {
        private const val ERROR_MARGIN_IN_METERS = 5.0
        fun closeEnough(p1: GeoPoint, p2: IGeoPoint) =
            p1.distanceToAsDouble(p2) < ERROR_MARGIN_IN_METERS
    }

    @get:Rule val composeTestRule = createComposeRule()
    private val tileSource: ITileSource = TileSourceFactory.MAPNIK
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        configureOsmdroid(context)
    }

    @Test
    fun osmdroidConfigurationShouldHaveTheCorrectValues() {

        assert(Configuration.getInstance().osmdroidBasePath == context.cacheDir)
        assert(Configuration.getInstance().userAgentValue == context.packageName)
    }

    @Test
    fun mapDrawerShouldDisplayAndroidView() {
        composeTestRule.setContent {
            MapDrawer()
        }
        composeTestRule.onNodeWithTag("mapViewWrapper").assertIsDisplayed()
    }


    @Test
    fun mapViewCreatorShouldCreateViewWithCorrectZoom() {
        lateinit var mapView: MapView
        composeTestRule.setContent {
            MapDrawer(
                mapViewFactory = {
                    mapView = createMapView(it, tileSource)
                    mapView
                }
            )
        }
        assert(mapView.zoomLevelDouble == ZOOM_DEFAULT)
    }

    @Test
    fun mapViewCreatorShouldCreateViewWithCorrectCenter() {
        lateinit var mapView: MapView
        composeTestRule.setContent {
            MapDrawer(
                mapViewFactory = {
                    mapView = createMapView(it, tileSource)
                    mapView
                }
            )
        }
        assert(closeEnough(LAUSANNE_GEO_POINT, mapView.mapCenter))
    }


    @Test
    fun mapViewCreatorShouldCreateViewWithCorrectOutlineClip() {
        lateinit var mapView : MapView
        composeTestRule.setContent {
            MapDrawer(
                mapViewFactory = {
                    mapView = createMapView(it, tileSource)
                    mapView
                }
            )
        }
        assert(mapView.clipToOutline)
    }
}