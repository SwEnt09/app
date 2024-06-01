package com.github.swent.echo.viewmodels

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.github.swent.echo.compose.map.MapViewProvider
import com.github.swent.echo.data.model.Event
import com.mapbox.mapboxsdk.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapDrawerViewModel @Inject constructor(private val provider: MapViewProvider<View>) :
    ViewModel() {

    fun factory(
        context: Context,
        withLocation: Boolean,
        onCreate: () -> Unit,
        onLongPress: (LatLng) -> Unit
    ): View = provider.factory(context, withLocation, onCreate, onLongPress)

    fun update(view: View, events: List<Event>, callback: (Event) -> Unit, withLocation: Boolean) =
        provider.update(view, events, callback, withLocation)

    fun setSavedCameraPosition(newPosition: LatLng, zoomLevel: Double) {
        provider.setSavedCameraPosition(newPosition, zoomLevel)
    }
}
