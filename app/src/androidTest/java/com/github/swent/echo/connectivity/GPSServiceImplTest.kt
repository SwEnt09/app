package com.github.swent.echo.connectivity

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GPSServiceImplTest {

    companion object {
        const val LOCATION_DELAY_MILLIS = 3000
    }

    private lateinit var gpsService: GPSService

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun shouldRefreshLocation() {
        lateinit var location: State<LatLng?>
        composeTestRule.setContent {
            gpsService = GPSServiceImpl(LocalContext.current)
            location = gpsService.userLocation.collectAsState()
        }
        runBlocking { delay(LOCATION_DELAY_MILLIS.toDuration(DurationUnit.MILLISECONDS)) }
        assertNotNull(location.value)
    }
}
