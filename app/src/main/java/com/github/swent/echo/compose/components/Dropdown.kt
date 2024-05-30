package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * A Composable function that creates a dropdown menu. This function takes a title, a list of items,
 * a selected item index, and a callback function.
 */
@Composable
fun Dropdown(
    title: String, // The title of the dropdown
    items: List<String>, // The list of items to be displayed in the dropdown
    selectedItem: Int, // The index of the selected item
    callback: (Int) -> Unit, // The callback function to be called when an item is selected
) {
    // Define the dimensions and states for the dropdown
    val width = 170.dp
    val itemsHeight = 40.dp
    var expanded by remember {
        mutableStateOf(false)
    } // The state of the dropdown (expanded or not)
    val verticalMaxSize = (LocalConfiguration.current.screenHeightDp / 4).dp
    val cornerRadius = 5.dp

    // Create a Box layout to contain the dropdown
    Box {
        // Create a Button that expands or collapses the dropdown when clicked
        Button(
            onClick = { expanded = true },
            modifier = Modifier.width(width).height(itemsHeight).testTag("dropdown_button"),
            shape = RoundedCornerShape(cornerRadius),
        ) {
            // Display the selected item or the title if no item is selected
            Text(if (selectedItem < 0) title else items[selectedItem])
            // Display an arrow icon indicating the state of the dropdown
            Icon(
                if (!expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                contentDescription = "arrow"
            )
        }
        // Create a DropdownMenu that displays the items
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier =
                Modifier.width(width).heightIn(max = verticalMaxSize).testTag("dropdown_menu")
        ) {
            // Create a DropdownMenuItem for the null item
            DropdownMenuItem(
                onClick = {
                    callback(-1)
                    expanded = false
                },
                text = { Text("---") },
                modifier = Modifier.height(itemsHeight).testTag("dropdown_item_null")
            )
            // Create a DropdownMenuItem for each item in the list
            items.forEachIndexed { id, item ->
                DropdownMenuItem(
                    onClick = {
                        callback(id)
                        expanded = false
                    },
                    text = { Text(item) },
                    modifier = Modifier.height(itemsHeight).testTag("dropdown_item_$item")
                )
            }
        }
    }
}
