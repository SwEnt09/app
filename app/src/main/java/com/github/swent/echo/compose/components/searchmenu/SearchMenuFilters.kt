package com.github.swent.echo.compose.components.searchmenu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

// Enum class for the different states of the sort by filter
enum class SortBy(val value: String) {
    NONE("---"),
    DATE_ASC("Date (Asc)"),
    DATE_DESC("Date (Desc)"),
    DISTANCE_ASC("Distance (Asc)"),
    DISTANCE_DESC("Distance (Desc)"),
}

@Composable
fun SearchMenuFilters() {
    // Content of the Events for filters
    var epflChecked by remember { mutableStateOf(false) }
    var sectionChecked by remember { mutableStateOf(false) }
    var classChecked by remember { mutableStateOf(false) }

    // Content of the Events Status filters
    var pendingChecked by remember { mutableStateOf(false) }
    var confirmedChecked by remember { mutableStateOf(false) }
    var fullChecked by remember { mutableStateOf(false) }

    // Content of the sort by filter
    var expanded by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf(SortBy.NONE) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Sort by filter
        Column(modifier = Modifier.align(Alignment.TopStart).padding(0.dp).zIndex(1f)) {
            Button(
                onClick = { expanded = !expanded },
                shape = RoundedCornerShape(10),
                modifier = Modifier.width(170.dp)
            ) {
                Text(if (sortBy == SortBy.NONE) "Sort by..." else sortBy.value)
                Icon(
                    if (!expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Sort by"
                )
            }
            // Check if the sort by filter is expanded
            if (expanded) {
                SortBy.entries.forEach {
                    Button(
                        onClick = {
                            sortBy = it
                            expanded = false
                        },
                        shape = RoundedCornerShape(5),
                        modifier = Modifier.width(170.dp).height(35.dp)
                    ) {
                        Text(it.value)
                    }
                }
            }
        }
        // Tickbox filters
        Row(modifier = Modifier.align(Alignment.TopCenter).absoluteOffset(y = 50.dp)) {
            // Events for Tickboxes
            Column {
                Text("Events for:")
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    Icon(Icons.Filled.Face, contentDescription = "EPFL")
                    Checkbox(
                        checked = epflChecked,
                        onCheckedChange = { epflChecked = !epflChecked },
                        modifier = Modifier.height(25.dp).width(25.dp)
                    )
                    Text("EPFL")
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row {
                    Icon(Icons.Filled.Face, contentDescription = "Section")
                    Checkbox(
                        checked = sectionChecked,
                        onCheckedChange = { sectionChecked = !sectionChecked },
                        modifier = Modifier.height(25.dp).width(25.dp)
                    )
                    Text("Section")
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row {
                    Icon(Icons.Filled.Face, contentDescription = "Class")
                    Checkbox(
                        checked = classChecked,
                        onCheckedChange = { classChecked = !classChecked },
                        modifier = Modifier.height(25.dp).width(25.dp)
                    )
                    Text("Class")
                }
            }
            Spacer(modifier = Modifier.width(100.dp))
            // Events Status Tickboxes
            Column {
                Text("Events Status:")
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    Icon(Icons.Filled.Person, contentDescription = "Pending")
                    Checkbox(
                        checked = pendingChecked,
                        onCheckedChange = { pendingChecked = !pendingChecked },
                        modifier = Modifier.height(25.dp).width(25.dp)
                    )
                    Text("Pending")
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row {
                    Icon(Icons.Filled.Person, contentDescription = "Confirmed")
                    Checkbox(
                        checked = confirmedChecked,
                        onCheckedChange = { confirmedChecked = !confirmedChecked },
                        modifier = Modifier.height(25.dp).width(25.dp)
                    )
                    Text("Confirmed")
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row {
                    Icon(Icons.Filled.Person, contentDescription = "Full")
                    Checkbox(
                        checked = fullChecked,
                        onCheckedChange = { fullChecked = !fullChecked },
                        modifier = Modifier.height(25.dp).width(25.dp)
                    )
                    Text("Full")
                }
            }
        }
        // TODO : Date selection, need to see with Yoan if already implemented
        // in create event because the one drawed on the figma is maybe not optimal
    }
}
