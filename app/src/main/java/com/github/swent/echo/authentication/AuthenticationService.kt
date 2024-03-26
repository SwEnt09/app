package com.github.swent.echo.authentication

/** Service to handle user authentication. */
interface AuthenticationService {
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
    fun getCurrentUserID(): String?
}
