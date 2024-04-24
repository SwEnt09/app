package com.github.swent.echo.di

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import com.github.swent.echo.compose.map.IMapViewProvider
import com.github.swent.echo.data.model.Event

/**
 * A simple map view provider that uses a [ComposeView] to display a list of events. No map is shown
 * in this implementation.
 */
class SimpleMapViewProvider : IMapViewProvider<ComposeView> {
    override fun factory(context: android.content.Context): ComposeView {
        return ComposeView(context)
    }

    override fun update(view: ComposeView, events: List<Event>, callback: (Event) -> Unit) {
        view.setContent {
            Column {
                events.forEach { event ->
                    Text(
                        text = event.title,
                        modifier = Modifier.clickable(onClick = { callback(event) }),
                    )
                }
            }
        }
    }
}
