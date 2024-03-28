package com.github.swent.echo.compose.authentication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.swent.echo.viewmodels.authentication.AuthenticationState

/**
 * A screen for authenticating a user. It displays a form for the user to enter their email and
 * password. The user can authenticate by clicking the action button.
 *
 * The purpose of this screen is to group into a single component what is common to the login and
 * registration screens.
 *
 * @param action The text to be displayed on the action button.
 * @param state The current state of the authentication process.
 * @param onAuthenticate The callback to be invoked when the user clicks the action button. It
 *   receives the user's email and password as parameters.
 */
@Composable
fun AuthenticationScreen(
    action: String,
    state: AuthenticationState,
    onAuthenticate: (email: String, password: String) -> Unit,
) {
    Box(modifier = Modifier.padding(24.dp)) {
        when (state) {
            is AuthenticationState.SignedOut ->
                AuthenticationForm(
                    action = action,
                    onAuthenticate = onAuthenticate,
                )
            is AuthenticationState.SigningIn -> Text("Signing in...")
            is AuthenticationState.SignedIn -> Text("Signed in")
            is AuthenticationState.Error ->
                AuthenticationForm(
                    action = action,
                    onAuthenticate = onAuthenticate,
                    error = state.message,
                )
        }
    }
}
