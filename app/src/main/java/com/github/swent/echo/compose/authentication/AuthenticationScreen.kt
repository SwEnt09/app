package com.github.swent.echo.compose.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
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
    isOnline: Boolean,
    onAuthenticate: (email: String, password: String) -> Unit,
    onStartGoogleSignIn: () -> Unit
) {
    Box(modifier = Modifier.padding(24.dp)) {
        when (state) {
            is AuthenticationState.SignedOut,
            is AuthenticationState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AuthenticationForm(
                        action = action,
                        isOnline = isOnline,
                        onAuthenticate = onAuthenticate,
                    )
                    AuthenticationMethodSeparator()
                    GoogleSignInButton(isOnline, onStartGoogleSignIn)
                    if (state is AuthenticationState.Error) {
                        Spacer(modifier = Modifier.padding(12.dp))
                        Text(
                            text = state.message,
                            modifier =
                                Modifier.fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .testTag("error-message"),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
            is AuthenticationState.SigningIn,
            is AuthenticationState.SignedIn -> SigningInScreen()
        }
    }
}

@Composable
fun GoogleSignInButton(isOnline: Boolean, onClick: () -> Unit) {
    return OutlinedButton(
        enabled = isOnline,
        onClick = onClick,
        modifier = Modifier.testTag("google-sign-in-button"),
    ) {
        Image(
            painter = painterResource(R.drawable.google_logo),
            contentDescription =
                stringResource(R.string.authentication_screen_google_logo_description),
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(stringResource(R.string.authentication_screen_sign_in_with_google))
    }
}

@Composable
fun AuthenticationMethodSeparator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Divider(modifier = Modifier.weight(1f), thickness = 1.dp)
        Text(
            stringResource(R.string.authentication_screen_sign_in_method_separator),
            modifier = Modifier.padding(vertical = 16.dp),
        )
        Divider(modifier = Modifier.weight(1f), thickness = 1.dp)
    }
}

@Composable
fun SigningInScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Signing in...")
    }
}
