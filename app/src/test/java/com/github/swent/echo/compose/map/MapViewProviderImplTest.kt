package com.github.swent.echo.compose.map

import com.mapbox.mapboxsdk.maps.MapView
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class MapViewProviderImplTest {

    private lateinit var mapLibreMapViewProvider: MapViewProviderImpl

    @Before
    fun setUp() {
        mapLibreMapViewProvider = MapViewProviderImpl()
    }

    @Test
    fun `update should call getMapAsync`() {
        val mapView: MapView = mockk { every { getMapAsync(any()) } returns Unit }

        mapLibreMapViewProvider.update(mapView, emptyList(), {}, false)
        verify { mapView.getMapAsync(any()) }
    }
}
