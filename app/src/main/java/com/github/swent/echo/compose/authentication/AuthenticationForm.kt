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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R

/**
 * A form for authenticating a user. It contains fields for the user's email and password.
 *
 * @param action The text to be displayed on the action button.
 * @param onAuthenticate The callback to be invoked when the user clicks the action button. It
 *   receives the user's email and password as parameters.
 * @param isOnline A boolean indicating whether the user is online.
 * @param confirmPassword A boolean indicating whether the form should contain a field for
 *   confirming the password.
 * @param validate A boolean indicating whether the form should validate the input before calling
 *   the [onAuthenticate] callback.
 */
@Composable
fun AuthenticationForm(
    action: String,
    onAuthenticate: (email: String, password: String) -> Unit,
    isOnline: Boolean,
    confirmPassword: Boolean = false,
    validate: Boolean = false,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AuthenticationTextField(
            modifier = Modifier.fillMaxWidth().testTag("email-field"),
            label = stringResource(R.string.authentication_form_email_label),
            value = email,
            onValueChange = {
                emailError = ""
                email = it
            },
            errorText = emailError,
        )
        Spacer(modifier = Modifier.padding(8.dp))
        AuthenticationTextField(
            modifier = Modifier.fillMaxWidth().testTag("password-field"),
            label = stringResource(R.string.authentication_form_password_label),
            value = password,
            onValueChange = {
                passwordError = ""
                password = it
            },
            errorText = passwordError,
            isPasswordField = true,
        )
        if (confirmPassword) {
            Spacer(modifier = Modifier.padding(8.dp))
            AuthenticationTextField(
                modifier = Modifier.fillMaxWidth().testTag("confirm-password-field"),
                label = stringResource(R.string.authentication_form_confirm_password_label),
                value = confirm,
                onValueChange = {
                    confirmError = ""
                    confirm = it
                },
                errorText = confirmError,
                isPasswordField = true,
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        ElevatedButton(
            enabled = isOnline,
            modifier = Modifier.fillMaxWidth().testTag("action-button"),
            onClick = {
                if (validate) {
                    if (email.isEmpty()) {
                        emailError = "Email is required"
                    }

                    if (password.length < 8) {
                        passwordError = "Password must be at least 8 characters"
                    }

                    if (confirmPassword && password != confirm && passwordError.isEmpty()) {
                        confirmError = "Passwords do not match"
                    }

                    if (emailError.isEmpty() && passwordError.isEmpty() && confirmError.isEmpty()) {
                        onAuthenticate(email, password)
                    }
                } else {
                    onAuthenticate(email, password)
                }
            },
        ) {
            Text(action)
        }
    }
}

@Composable
fun AuthenticationTextField(
    modifier: Modifier,
    label: String,
    value: String,
    errorText: String,
    onValueChange: (String) -> Unit,
    isPasswordField: Boolean = false,
) {
    Column {
        OutlinedTextField(
            modifier = modifier,
            label = { Text(label) },
            value = value,
            onValueChange = onValueChange,
            isError = errorText.isNotEmpty(),
            visualTransformation =
                if (isPasswordField) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions =
                if (isPasswordField)
                    KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                else KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            singleLine = true,
        )
        if (errorText.isNotEmpty()) {
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
