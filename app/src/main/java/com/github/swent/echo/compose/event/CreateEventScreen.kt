package com.github.swent.echo.compose.event

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.swent.echo.R
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.event.EventViewModel

/** This screen allow the user to create an event. */
@Composable
fun CreateEventScreen(eventViewModel: EventViewModel, navigationActions: NavigationActions) {
    EventScreen(
        title = stringResource(R.string.create_event_screen_title),
        onEventBackButtonPressed = navigationActions::goBack,
        onEventSaveButtonPressed = {
            eventViewModel.saveEvent()
            navigationActions.navigateTo(Routes.MAP)
        },
        eventViewModel = eventViewModel
    )
}
