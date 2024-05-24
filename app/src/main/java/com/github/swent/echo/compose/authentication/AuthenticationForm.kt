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

enum class AuthenticationFormError(val id: Int) {
    EMPTY_EMAIL(R.string.authentication_error_email_is_required),
    PASSWORD_TOO_SHORT(R.string.authentication_password_too_short),
    PASSWORD_DO_NOT_MATCH(R.string.authentication_error_passwords_do_not_match),
}

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

    val minPasswordLength = 8
    val spaceBetweenTextFields = 8.dp

    val emailErrorText = stringResource(AuthenticationFormError.EMPTY_EMAIL.id)
    val passwordTooShort = stringResource(AuthenticationFormError.PASSWORD_TOO_SHORT.id)
    val passwordDoNotMatch = stringResource(AuthenticationFormError.PASSWORD_DO_NOT_MATCH.id)

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
        Spacer(modifier = Modifier.padding(spaceBetweenTextFields))
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
            Spacer(modifier = Modifier.padding(spaceBetweenTextFields))
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
        Spacer(modifier = Modifier.padding(spaceBetweenTextFields))
        ElevatedButton(
            enabled = isOnline,
            modifier = Modifier.fillMaxWidth().testTag("action-button"),
            onClick = {
                if (validate) {
                    var hasError = false

                    if (email.isEmpty()) {
                        emailError = emailErrorText
                        hasError = true
                    }

                    if (password.length < minPasswordLength) {
                        passwordError = passwordTooShort
                        hasError = true
                    }

                    if (confirmPassword && password != confirm && passwordError.isEmpty()) {
                        confirmError = passwordDoNotMatch
                        hasError = true
                    }

                    if (!hasError) {
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
    val spaceBetweenErrorText = 4.dp

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
            Spacer(modifier = Modifier.padding(spaceBetweenErrorText))
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
