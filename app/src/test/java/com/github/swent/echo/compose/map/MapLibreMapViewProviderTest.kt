package com.github.swent.echo.compose.map

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.maplibre.android.maps.MapView

class MapLibreMapViewProviderTest {

    private lateinit var mapLibreMapViewProvider: MapLibreMapViewProvider

    @Before
    fun setUp() {
        mapLibreMapViewProvider = MapLibreMapViewProvider()
    }

    @Test
    fun `update should call getMapAsync`() {
        val mapView: MapView = mockk { every { getMapAsync(any()) } returns Unit }

        mapLibreMapViewProvider.update(mapView, emptyList(), {}, false)
        verify { mapView.getMapAsync(any()) }
    }
}
