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
import com.github.swent.echo.data.SAMPLE_EVENTS
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.osmdroid.config.Configuration

@RunWith(AndroidJUnit4::class)
class MapViewAndroidTest {

    companion object {

        private val context = InstrumentationRegistry.getInstrumentation().targetContext

        @Composable
        private fun <T : View> DummyMapDrawer(p: IMapViewProvider<T>) {
            val e by remember { mutableStateOf(SAMPLE_EVENTS) }
            EchoAndroidView(
                factory = p::factory,
                update = p::update,
                events = e,
            )
        }
    }

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun osmdroidConfigShouldHaveCorrectValues() {
        val p = OsmdroidMapViewProvider()
        composeTestRule.setContent { DummyMapDrawer(p) }
        Configuration.getInstance().apply {
            assertEquals(osmdroidBasePath, context.cacheDir)
            assertEquals(userAgentValue, context.packageName)
        }
    }

    @Test
    fun osmdroidMapProviderShouldShowAndroidView() {
        val p = OsmdroidMapViewProvider()
        p.enableLocation()
        composeTestRule.setContent { DummyMapDrawer(p) }
        composeTestRule.onNodeWithTag("mapAndroidView").assertIsDisplayed()
    }

    @Test
    fun mapLibreMapProviderShouldShowAndroidView() {
        val p = MapLibreMapViewProvider()
        composeTestRule.setContent { DummyMapDrawer(p) }
        composeTestRule.onNodeWithTag("mapAndroidView").assertIsDisplayed()
    }
}
