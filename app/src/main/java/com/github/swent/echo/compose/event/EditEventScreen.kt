package com.github.swent.echo.compose.event

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.swent.echo.R
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.event.EventViewModel

/** This screen allow the user to edit an event. */
@Composable
fun EditEventScreen(eventViewModel: EventViewModel, navigationActions: NavigationActions) {
    EventScreen(
        title = stringResource(R.string.create_event_screen_title),
        canDelete = true,
        onEventBackButtonPressed = navigationActions::goBack,
        onEventSaved = { navigationActions.navigateTo(Routes.MAP) },
        onEventDeleted = { navigationActions.navigateTo(Routes.MAP) },
        eventViewModel = eventViewModel
    )
}
