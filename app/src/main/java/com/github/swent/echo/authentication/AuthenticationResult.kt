package com.github.swent.echo.authentication

import java.lang.Exception

/** Represents the result of an authentication operation. */
sealed class AuthenticationResult {
    /** The operation was successful. */
    data object Success : AuthenticationResult()

    /**
     * The operation failed.
     *
     * @param message Human-readable error message. Can be displayed to the user.
     * @param exception The exception that caused the error, if any.
     */
    data class Error(val message: String, val exception: Exception? = null) :
        AuthenticationResult()
}
