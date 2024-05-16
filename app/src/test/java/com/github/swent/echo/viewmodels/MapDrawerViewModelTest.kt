package com.github.swent.echo.viewmodels

import android.content.Context
import android.view.View
import com.github.swent.echo.compose.map.IMapViewProvider
import com.github.swent.echo.data.model.Event
import com.mapbox.mapboxsdk.geometry.LatLng
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class MapDrawerViewModelTest {
    private lateinit var provider: IMapViewProvider<View>
    private lateinit var viewModel: MapDrawerViewModel

    @Before
    fun setUp() {
        provider = mockk(relaxed = true)
        viewModel = MapDrawerViewModel(provider)
    }

    @Test
    fun `factory should call provider factory`() {
        val context = mockk<Context>()
        val onCreate = {}
        val onLongPress: (LatLng) -> Unit = {}
        viewModel.factory(context, false, onCreate, onLongPress)
        verify { provider.factory(context, false, onCreate, onLongPress) }
    }

    @Test
    fun `update should call provider update`() {
        val view = mockk<View>()
        val events = emptyList<Event>()
        val callback = { _: Event -> }
        viewModel.update(view, events, callback, false)
        verify { provider.update(view, events, callback, false) }
    }
}
