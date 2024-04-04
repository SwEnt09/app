package com.github.swent.echo.compose.map

import android.content.Context
import android.view.View

interface IMapViewProvider<T : View> {
    fun factory(context: Context): T

    fun update(view: T)
}
