package com.github.swent.echo.compose.myevents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.github.swent.echo.R
import com.github.swent.echo.compose.components.ListDrawer
import com.github.swent.echo.compose.components.Pager
import com.github.swent.echo.compose.event.EventTitleAndBackButton
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.myevents.MyEventsViewModel

// This Composable function creates a screen for displaying the user's events.
@Composable
fun MyEventsScreen(
    myEventsViewModel: MyEventsViewModel, // ViewModel for managing the user's events
    navActions: NavigationActions // Actions for navigating between screens
) {
    // Create a scaffold, which is a Material Design layout structure.
    Scaffold(
        // The top bar of the scaffold is a title and a back button.
        topBar = {
            EventTitleAndBackButton(stringResource(R.string.hamburger_my_events)) {
                // When the back button is clicked, navigate to the map screen.
                navActions.navigateTo(Routes.MAP)
            }
        },
        modifier = Modifier.fillMaxSize().testTag("my_events_screen") // Fill the entire screen.
    ) {
        val userId = myEventsViewModel.user
        val modify = { e: Event -> navActions.navigateTo(Routes.EDIT_EVENT.build(e.eventId)) }
        val refresh = myEventsViewModel::refreshEvents

        // Get the list of events the user has joined.
        val joinedEvents by myEventsViewModel.joinedEvents.collectAsState()
        // Get the list of events the user has created.
        val createdEvents by myEventsViewModel.createdEvents.collectAsState()
        // Get whether the user is online.
        val isOnline by myEventsViewModel.isOnline.collectAsState()
        // Create a box with padding.
        Box(modifier = Modifier.padding(it)) {
            // Create a pager with two tabs: one for joined events and one for created events.
            Pager(
                listOf(
                    // The first tab is for joined events.
                    Pair(stringResource(R.string.my_events_joined_events)) {
                        // Display the joined events in a list.
                        ListDrawer(
                            joinedEvents,
                            isOnline,
                            refresh,
                            modify = modify,
                            userId = userId,
                        )
                    },
                    // The second tab is for created events.
                    Pair(stringResource(R.string.my_events_created_events)) {
                        // Display the created events in a list.
                        ListDrawer(
                            createdEvents,
                            isOnline,
                            refresh,
                            modify = modify,
                            userId = userId,
                        )
                    }
                )
            )
        }
    }
}
