package com.github.swent.echo.viewmodels.authentication

/** The possible states of the authentication view models. */
sealed class AuthenticationState {
    data object SignedOut : AuthenticationState()

    data object SigningIn : AuthenticationState()

    data object SignedIn : AuthenticationState()

    data class Error(val message: String) : AuthenticationState()

    fun isSignedOutOrError(): Boolean {
        return this is SignedOut || this is Error
    }
}
