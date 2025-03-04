package com.github.swent.echo.compose.authentication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.authentication.AuthenticationState
import com.github.swent.echo.viewmodels.authentication.LoginViewModel

/**
 * The login screen.
 *
 * @param loginViewModel The view model for the login screen.
 * @param navActions The navigation actions.
 */
@Composable
fun LoginScreen(loginViewModel: LoginViewModel, navActions: NavigationActions) {
    val state by loginViewModel.state.collectAsState()
    val isOnline by loginViewModel.isOnline.collectAsState()

    // Redirect to the appropriate screen if the user is signed in.
    if (state is AuthenticationState.SignedIn) {
        LaunchedEffect(state) {
            navActions.navigateTo((state as AuthenticationState.SignedIn).redirect)
        }
    }

    // Make the screen scrollable for small devices.
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.testTag("login-screen").fillMaxHeight().verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if (state.isSignedOutOrError()) {
            AuthenticationScreenTitle(subtitle = stringResource(R.string.login_button))
        }
        AuthenticationScreen(
            action = stringResource(R.string.login_button),
            isOnline = isOnline,
            state = state,
            onAuthenticate = loginViewModel::login,
            onStartGoogleSignIn = loginViewModel.startGoogleSignInCallback(),
        )
        if (state.isSignedOutOrError()) {
            NavigateToRegisterScreen(onClick = { navActions.navigateTo(Routes.REGISTER) })
        }
    }
}

/** A link to navigate to the register screen. */
@Composable
fun NavigateToRegisterScreen(onClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(vertical = 24.dp),
    ) {
        Text(stringResource(R.string.login_screen_don_t_have_an_account) + " ")
        Text(
            text = stringResource(R.string.register),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onClick).testTag("register-button")
        )
    }
}
