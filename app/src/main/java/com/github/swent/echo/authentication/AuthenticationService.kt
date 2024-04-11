package com.github.swent.echo.authentication

import io.github.jan.supabase.compose.auth.ComposeAuth

/** Service to handle user authentication. */
interface AuthenticationService {

    /** Supabase's compose auth plugin to make the sign in with Google easy. */
    val composeAuth: ComposeAuth

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
