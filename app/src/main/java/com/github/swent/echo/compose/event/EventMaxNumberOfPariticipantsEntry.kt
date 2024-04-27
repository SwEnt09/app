package com.github.swent.echo.compose.event

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.github.swent.echo.R
import kotlin.math.max

@Composable
fun EventMaxNumberOfParticipantsEntry(
    maxNumberOfParticipants: Int,
    onMaxNbParticipantsChange: (newNumberOfParticipants: Int) -> Unit
) {
    var nbParticipants by remember { mutableStateOf(maxNumberOfParticipants.toString()) }
    Column(modifier = Modifier.fillMaxWidth().padding(EVENT_PADDING_BETWEEN_INPUTS)) {
        EventEntryName(stringResource(R.string.edit_event_screen_max_participants))
        TextField(
            value = nbParticipants,
            modifier =
                Modifier.fillMaxWidth(0.25F).onFocusChanged {
                    if (!it.isFocused) {
                        var newNumberOfParticipants = maxNumberOfParticipants
                        try {
                            newNumberOfParticipants = nbParticipants.toInt()
                        } catch (e: Exception) {
                            Log.w("edit event number of participants", e)
                        }
                        newNumberOfParticipants = max(0, newNumberOfParticipants)
                        nbParticipants = newNumberOfParticipants.toString()
                        onMaxNbParticipantsChange(newNumberOfParticipants)
                    }
                },
            onValueChange = { nbParticipants = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}
