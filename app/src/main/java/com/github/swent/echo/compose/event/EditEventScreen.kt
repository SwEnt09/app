package com.github.swent.echo.compose.event

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.swent.echo.R
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.event.EventViewModel

/**
 * This screen allow the user to edit an event.
 *
 * @param eventViewModel the event viewmodel
 * @param navigationActions the navigation actions
 */
@Composable
fun EditEventScreen(eventViewModel: EventViewModel, navigationActions: NavigationActions) {
    EventScreen(
        title = stringResource(R.string.edit_event_screen_name),
        canDelete = true,
        onEventBackButtonPressed = navigationActions::goBack,
        onEventSaved = { navigationActions.navigateTo(Routes.MAP) },
        onEventDeleted = { navigationActions.navigateTo(Routes.MAP) },
        eventViewModel = eventViewModel
    )
}
