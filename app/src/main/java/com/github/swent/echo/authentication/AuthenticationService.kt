package com.github.swent.echo.authentication

import androidx.compose.runtime.Composable
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult

/** Service to handle user authentication. */
interface AuthenticationService {

    /**
     * Creates a callback to start the Google sign in flow.
     *
     * @param onResult The callback to be invoked when the sign in flow finishes. It receives the
     *  result of the sign in flow.
     */
    @Composable
    fun startGoogleSignInCallback(onResult: (NativeSignInResult) -> Unit): () -> Unit

    /**
     * Sign in with email and password.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     */
    suspend fun signIn(email: String, password: String): AuthenticationResult

    /**
     * Sign up with email and password.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     */
    suspend fun signUp(email: String, password: String): AuthenticationResult

    /** Sign out the current user. */
    suspend fun signOut(): AuthenticationResult

    /**
     * Get the current user's ID.
     *
     * @return The current user's ID, or null if the user is not signed in.
     */
    suspend fun getCurrentUserID(): String?
}
