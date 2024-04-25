package com.github.swent.echo.compose.map

import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.osmdroid.views.MapView

class OsmdroidMapViewProviderTest {

    private lateinit var osmdroidMapViewProvider: OsmdroidMapViewProvider

    @Before
    fun setUp() {
        osmdroidMapViewProvider = OsmdroidMapViewProvider()
    }

    @Test
    fun `update should call invalidate`() {
        val mapView: MapView = mockk(relaxed = true)

        osmdroidMapViewProvider.update(mapView, emptyList(), {})
        verify { mapView.invalidate() }
    }
}
