package com.github.swent.echo.compose.map

import android.content.Context
import android.view.View
import com.github.swent.echo.data.model.Event

/**
 * A MapView provider is meant to be used by an
 * [EchoAndroidView][com.github.swent.echo.compose.map.EchoAndroidView]. The methods `factory` and
 * `update` directly correspond to the parameters of the same name in an
 * [EchoAndroidView][com.github.swent.echo.compose.map.EchoAndroidView].
 *
 * @author alejandrocalles
 */
interface IMapViewProvider<T : View> {
    fun factory(context: Context): T

    fun update(view: T, events: List<Event>, callback: (Event) -> Unit)

    fun enableLocation()

    fun disableLocation()
}
