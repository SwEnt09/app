package com.github.swent.echo.compose.map

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.data.SAMPLE_EVENTS
import com.mapbox.mapboxsdk.geometry.LatLng
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapViewAndroidTest {

    companion object {
        @Composable
        private fun <T : View> DummyMapDrawer(
            p: MapViewProvider<T>,
            launchEventCreation: (LatLng) -> Unit = {}
        ) {
            val e by remember { mutableStateOf(SAMPLE_EVENTS) }
            EchoAndroidView(
                factory = p::factory,
                update = p::update,
                events = e,
                withLocation = true,
                launchEventCreation = launchEventCreation
            )
        }
    }

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun mapLibreMapProviderShouldShowAndroidView() {
        val p = MapViewProviderImpl()
        composeTestRule.setContent { DummyMapDrawer(p) }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("mapAndroidView").assertIsDisplayed()
    }
}
