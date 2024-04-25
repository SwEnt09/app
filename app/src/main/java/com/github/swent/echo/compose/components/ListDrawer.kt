package com.github.swent.echo.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.swent.echo.data.model.Event
import java.time.format.DateTimeFormatter

@Composable
fun ListDrawer(eventsList: List<Event>) {
    LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White).padding(5.dp)) {
        items(eventsList) { event ->
            EventListItem(event = event)
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
fun EventListItem(event: Event) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(
        modifier =
        Modifier.clip(RoundedCornerShape(5.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { isExpanded = !isExpanded }
    ) {
        Row(
            modifier = Modifier.height(60.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val paddingItems = 3.dp
            val widthSmallItems = 50.dp
            val widthLargeItems = 80.dp
            Text(
                text = event.eventId,
                modifier = Modifier.padding(horizontal = paddingItems).width(widthLargeItems),
                textAlign = TextAlign.Center
            )
            Text(
                text = event.title,
                modifier = Modifier.padding(horizontal = paddingItems).width(widthLargeItems),
                textAlign = TextAlign.Center
            )
            Text(
                text = event.startDate.format(DateTimeFormatter.ofPattern("E, dd/MM\nHH:mm")),
                modifier = Modifier.padding(horizontal = paddingItems).width(widthLargeItems),
                textAlign = TextAlign.Center
            )
            Text(
                text = "5km",
                modifier = Modifier.padding(horizontal = paddingItems).width(widthSmallItems),
                textAlign = TextAlign.Center
            )
            Text(
                text = "${event.participantCount}/${event.maxParticipants}",
                modifier = Modifier.padding(horizontal = paddingItems).width(widthSmallItems),
                textAlign = TextAlign.Center
            )
        }
        if (isExpanded) {
            Row {
                Text(
                    text = event.description,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.width(200.dp)
                )
                Spacer(modifier = Modifier.width(40.dp))
                Column {
                    val buttonWidth = 130.dp
                    Button(
                        onClick = { /*TODO*/},
                        modifier = Modifier.width(buttonWidth),
                    ) {
                        Text("View On Map")
                    }
                    Button(
                        onClick = { /*TODO*/},
                        modifier = Modifier.width(buttonWidth),
                    ) {
                        Text("Join Event")
                    }
                }
            }
        }
    }
}