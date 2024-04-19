package com.github.swent.echo.compose.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** A text entry with a name above */
@Composable
fun EventTextEntry(
    name: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (newValue: String) -> Unit
) {
    Column(modifier = modifier.padding(EVENT_PADDING_BETWEEN_INPUTS)) {
        EventEntryName(name = name)
        EventEntryField(
            value = value,
            modifier = modifier.testTag("$name-field"),
            onValueChange = { onValueChange(it) }
        )
    }
}

/** name above an entry field */
@Composable
fun EventEntryName(name: String) {
    Text(
        text = name,
        modifier = Modifier.width(210.dp).height(30.dp),
        fontSize = 20.sp,
        style =
            TextStyle(
                fontWeight = FontWeight(400),
                color = Color(0xFF000000),
                textAlign = TextAlign.Justify,
            )
    )
}

/** a text input field */
@Composable
fun EventEntryField(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (newValue: String) -> Unit
) {
    TextField(
        value = value,
        modifier = modifier.fillMaxWidth(),
        onValueChange = { onValueChange(it) }
    )
}
