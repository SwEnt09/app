package com.github.swent.echo.viewmodels.authentication

import com.github.swent.echo.ui.navigation.Routes

/** The possible states of the authentication view models. */
sealed class AuthenticationState {
    /** The user is signed out. */
    data object SignedOut : AuthenticationState()

    /** The user is signing in. */
    data object SigningIn : AuthenticationState()

    /**
     * The user is signed in.
     *
     * @param redirect The route to redirect to after signing in.
     */
    data class SignedIn(val redirect: Routes) : AuthenticationState()

    /**
     * An error occurred during the authentication process.
     *
     * @param message The error message.
     */
    data class Error(val message: String) : AuthenticationState()

    /**
     * Returns true if the user is signed out or an error occurred.
     *
     * @return True if the user is signed out or an error occurred.
     */
    fun isSignedOutOrError(): Boolean {
        return this is SignedOut || this is Error
    }
}
