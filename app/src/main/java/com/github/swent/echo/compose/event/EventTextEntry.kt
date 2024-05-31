package com.github.swent.echo.compose.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A text entry with a name above.
 *
 * @param name the name to display above the text entry
 * @param value the value of the text field
 * @param modifier the modifier of the composable
 * @param onValueChange a callback called when the text field is modified
 */
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
            onValueChange = onValueChange
        )
    }
}

/**
 * The name above an entry field.
 *
 * @param name the text to display
 */
@Composable
fun EventEntryName(name: String) {
    Text(
        text = name,
        modifier = Modifier.height(30.dp),
        fontSize = 20.sp,
        style =
            TextStyle(
                fontWeight = FontWeight(400),
                textAlign = TextAlign.Justify,
            )
    )
}

/**
 * A text input field.
 *
 * @param value the value of the text field
 * @param modifier the modifier of the composable
 * @param onValueChange a callback called when the text field is modified
 */
@Composable
fun EventEntryField(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (newValue: String) -> Unit
) {
    TextField(value = value, modifier = modifier.fillMaxWidth(), onValueChange = onValueChange)
}
