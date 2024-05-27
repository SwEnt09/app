package com.github.swent.echo.compose.authentication

import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.authentication.AuthenticationState
import com.github.swent.echo.viewmodels.authentication.RegisterViewModel

/**
 * The register screen.
 *
 * @param registerViewModel The view model for the register screen.
 * @param navActions The navigation actions.
 */
@Composable
fun RegisterScreen(registerViewModel: RegisterViewModel, navActions: NavigationActions) {
    val state by registerViewModel.state.collectAsState()
    val isOnline by registerViewModel.isOnline.collectAsState()
    val context = LocalContext.current

    var usingGoogleAuthentication by remember { mutableStateOf(false) }
    val startGoogleSignInCallback = registerViewModel.startGoogleSignInCallback()

    if (state is AuthenticationState.SignedIn) {
        // Redirect to the appropriate screen if the user is signed in.
        // Note that the user is always redirected to the login screen after registering with email.
        LaunchedEffect(state) {
            if (usingGoogleAuthentication) {
                navActions.navigateTo((state as AuthenticationState.SignedIn).redirect)
            } else {
                val message = context.getString(R.string.register_screen_confirm_your_email)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                navActions.navigateTo(Routes.LOGIN)
            }
        }
    }

    // Make the screen scrollable for small devices.
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.testTag("register-screen").fillMaxHeight().verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if (state.isSignedOutOrError()) {
            AuthenticationScreenTitle(
                subtitle = stringResource(R.string.register_screen_action_button)
            )
        }
        AuthenticationScreen(
            action = stringResource(R.string.register_screen_action_button),
            isOnline = isOnline,
            confirmPassword = true,
            validate = true,
            state = state,
            onAuthenticate = registerViewModel::register,
            onStartGoogleSignIn = {
                usingGoogleAuthentication = true
                startGoogleSignInCallback()
            },
        )
        if (state.isSignedOutOrError()) {
            NavigateToLoginScreen(onClick = { navActions.navigateTo(Routes.LOGIN) })
        }
    }
}

/** A link to navigate to the login screen. */
@Composable
fun NavigateToLoginScreen(onClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(vertical = 24.dp),
    ) {
        Text(stringResource(R.string.register_screen_do_you_have_an_account) + " ")
        Text(
            text = stringResource(R.string.register_screen_login_link),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onClick).testTag("login-button")
        )
    }
}
