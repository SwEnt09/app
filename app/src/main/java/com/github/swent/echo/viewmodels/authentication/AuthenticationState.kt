package com.github.swent.echo.viewmodels.authentication

import com.github.swent.echo.ui.navigation.Routes

/** The possible states of the authentication view models. */
sealed class AuthenticationState {
    data object SignedOut : AuthenticationState()

    data object SigningIn : AuthenticationState()

    data class SignedIn(val redirect: Routes) : AuthenticationState()

    data class Error(val message: String) : AuthenticationState()

    fun isSignedOutOrError(): Boolean {
        return this is SignedOut || this is Error
    }
}
