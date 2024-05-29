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

@Composable
fun Dropdown(
    title: String,
    items: List<String>,
    selectedItem: Int,
    callback: (Int) -> Unit,
) {
    val width = 170.dp
    val itemsHeight = 40.dp
    var expanded by remember { mutableStateOf(false) }
    val verticalMaxSize = (LocalConfiguration.current.screenHeightDp / 4).dp
    val cornerRadius = 5.dp
    Box {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.width(width).height(itemsHeight).testTag("dropdown_button"),
            shape = RoundedCornerShape(cornerRadius),
        ) {
            Text(if (selectedItem < 0) title else items[selectedItem])
            Icon(
                if (!expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                contentDescription = "arrow"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier =
                Modifier.width(width).heightIn(max = verticalMaxSize).testTag("dropdown_menu")
        ) {
            DropdownMenuItem(
                onClick = {
                    callback(-1)
                    expanded = false
                },
                text = { Text("---") },
                modifier = Modifier.height(itemsHeight).testTag("dropdown_item_null")
            )
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
