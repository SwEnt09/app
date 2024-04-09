package com.github.swent.echo.compose.authentication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R

/**
 * A form for authenticating a user. It contains fields for the user's email and password.
 *
 * @param action The text to be displayed on the action button.
 * @param onAuthenticate The callback to be invoked when the user clicks the action button. It
 *   receives the user's email and password as parameters.
 * @param error The error message to be displayed, if any.
 */
@Composable
fun AuthenticationForm(
    action: String,
    onAuthenticate: (email: String, password: String) -> Unit,
    error: String? = null,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().testTag("email-field"),
            label = { Text(stringResource(R.string.authentication_form_email_label)) },
            value = email,
            onValueChange = { email = it },
        )
        Spacer(modifier = Modifier.padding(16.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().testTag("password-field"),
            label = { Text(stringResource(R.string.authentication_form_password_label)) },
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                ),
        )
        Spacer(modifier = Modifier.padding(16.dp))
        ElevatedButton(
            modifier = Modifier.fillMaxWidth().testTag("action-button"),
            onClick = { onAuthenticate(email, password) },
        ) {
            Text(action)
        }
        Spacer(modifier = Modifier.padding(12.dp))
        error?.let {
            Text(
                text = it,
                modifier =
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp).testTag("error-message"),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
