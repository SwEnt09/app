package com.github.swent.echo.viewmodels

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.github.swent.echo.compose.map.IMapViewProvider
import com.github.swent.echo.data.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapDrawerViewModel @Inject constructor(private val provider: IMapViewProvider<View>) :
    ViewModel() {

    fun factory(context: Context, withLocation: Boolean, onCreate: () -> Unit): View =
        provider.factory(context, withLocation, onCreate)

    fun update(view: View, events: List<Event>, callback: (Event) -> Unit, withLocation: Boolean) =
        provider.update(view, events, callback, withLocation)
}
