package com.github.swent.echo.compose.myevents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.compose.components.ListDrawer
import com.github.swent.echo.compose.event.EventTitleAndBackButton
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.myevents.MyEventsViewModel

enum class MyEventsTab {
    JOINED_EVENTS,
    CREATED_EVENTS
}

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
        // Will be adapted with the future viewModel
        val joinedEventsList by myEventsViewModel.joinedEvents.collectAsState()
        val createdEventsList by myEventsViewModel.createdEvents.collectAsState()
        var myEventsTab by remember { mutableStateOf(MyEventsTab.JOINED_EVENTS) }

        Column(modifier = Modifier.padding(it)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                val paddingItems = 2.dp
                Box(
                    modifier =
                        Modifier.weight(1f)
                            .padding(paddingItems)
                            .height(50.dp)
                            .clickable { myEventsTab = MyEventsTab.JOINED_EVENTS }
                            .testTag("my_events_joined_events_tab")
                ) {
                    Text(
                        stringResource(R.string.my_events_joined_events),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Box(
                    modifier =
                        Modifier.weight(1f)
                            .padding(paddingItems)
                            .height(50.dp)
                            .clickable { myEventsTab = MyEventsTab.CREATED_EVENTS }
                            .testTag("my_events_created_events_tab")
                ) {
                    Text(
                        stringResource(R.string.my_events_created_events),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                val underline = Modifier.height(1.dp).width(200.dp)
                when (myEventsTab) {
                    MyEventsTab.JOINED_EVENTS -> {
                        Box(
                            modifier =
                                underline
                                    .align(Alignment.CenterStart)
                                    .padding(start = 10.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .testTag("my_events_underline_joined_events")
                        )
                    }
                    MyEventsTab.CREATED_EVENTS -> {
                        Box(
                            modifier =
                                underline
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 10.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .testTag("my_events_underline_created_events")
                        )
                    }
                }
            }
            // Will be adapted with the future viewModel
            ListDrawer(
                if (myEventsTab == MyEventsTab.JOINED_EVENTS) joinedEventsList
                else createdEventsList,
                "",
                "",
                true
            )
        }
    }
}
