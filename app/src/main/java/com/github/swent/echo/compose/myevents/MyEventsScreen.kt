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
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.myevents.MyEventsViewModel

@Composable
fun MyEventsScreen(myEventsViewModel: MyEventsViewModel, navActions: NavigationActions) {
    Scaffold(
        topBar = {
            EventTitleAndBackButton(stringResource(R.string.hamburger_my_events)) {
                navActions.navigateTo(Routes.MAP)
            }
        },
        modifier = Modifier.fillMaxSize().testTag("my_events_screen")
    ) {
        val joinedEventsList by myEventsViewModel.joinedEvents.collectAsState()
        val createdEventsList by myEventsViewModel.createdEvents.collectAsState()
        val isOnline by myEventsViewModel.isOnline.collectAsState()
        Box(modifier = Modifier.padding(it)) {
            Pager(
                listOf(
                    Pair(stringResource(R.string.my_events_joined_events)) {
                        ListDrawer(
                            joinedEventsList,
                            "",
                            "",
                            isOnline,
                            null,
                            myEventsViewModel::refreshEvents
                        )
                    },
                    Pair(stringResource(R.string.my_events_created_events)) {
                        ListDrawer(
                            createdEventsList,
                            "",
                            "",
                            isOnline,
                            null,
                            myEventsViewModel::refreshEvents
                        )
                    }
                )
            )
        }
    }
}
