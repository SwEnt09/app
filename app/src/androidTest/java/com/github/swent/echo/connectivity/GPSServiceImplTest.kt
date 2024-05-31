package com.github.swent.echo.connectivity

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mapbox.mapboxsdk.geometry.LatLng
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GPSServiceImplTest {
    private lateinit var gpsService: GPSService

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun shouldRefreshLocation() {
        lateinit var location: State<LatLng?>
        composeTestRule.setContent {
            gpsService = GPSServiceImpl(LocalContext.current)
            location = gpsService.userLocation.collectAsState()
        }
        composeTestRule.waitForIdle()
        assertNotNull(gpsService.userLocation)
    }
}
