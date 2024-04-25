package com.github.swent.echo.viewmodels

import android.content.Context
import android.view.View
import com.github.swent.echo.compose.map.IMapViewProvider
import com.github.swent.echo.data.model.Event
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
        viewModel.factory(context)
        verify { provider.factory(context) }
    }

    @Test
    fun `update should call provider update`() {
        val view = mockk<View>()
        val events = emptyList<Event>()
        val callback = { _: Event -> }
        viewModel.update(view, events, callback)
        verify { provider.update(view, events, callback) }
    }
}