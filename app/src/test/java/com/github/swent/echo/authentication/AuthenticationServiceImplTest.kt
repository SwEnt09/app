package com.github.swent.echo.authentication

import android.util.Log
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.exceptions.UnknownRestException
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthenticationServiceImplTest {

    private val errorMessage = "error_message"
    private val expectedErrorMessage = "Error message"
    private val unknownRestException = UnknownRestException(errorMessage, mockk(relaxed = true))

    private lateinit var authMock: Auth
    private lateinit var composeAuthMock: ComposeAuth
    private lateinit var service: AuthenticationServiceImpl

    companion object {
        private const val EMAIL = "test@email.com"
        private const val PASSWORD = "password"
        private const val USER_ID = "123"
    }

    @Before
    fun setUp() {
        authMock = mockk(relaxed = true)
        composeAuthMock = mockk(relaxed = true)

        // Mocking error logging done by the `AuthenticationServiceImpl` class
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0

        service = AuthenticationServiceImpl(authMock, composeAuthMock)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `signIn should return success when successful`() {
        coEvery { authMock.signInWith(Email, config = any()) } returns Unit
        val result = runBlocking { service.signIn(EMAIL, PASSWORD) }
        assertEquals(AuthenticationResult.Success, result)
    }

    @Test
    fun `signIn should return error when failed`() {
        coEvery { authMock.signInWith(Email, config = any()) } throws Exception()
        val result = runBlocking { service.signIn(EMAIL, PASSWORD) }
        assertTrue(result is AuthenticationResult.Error)
    }

    @Test
    fun `signIn should return same error message when is a rest exception`() {
        coEvery { authMock.signInWith(Email, config = any()) } throws unknownRestException
        val result = runBlocking { service.signIn(EMAIL, PASSWORD) }
        assertEquals(expectedErrorMessage, (result as AuthenticationResult.Error).message)
    }

    @Test
    fun `signUp should return success when successful`() {
        coEvery { authMock.signUpWith(Email, config = any()) } returns mockk()
        val result = runBlocking { service.signUp(EMAIL, PASSWORD) }
        assertEquals(AuthenticationResult.Success, result)
    }

    @Test
    fun `signUp should return error when failed`() {
        coEvery { authMock.signUpWith(Email, config = any()) } throws Exception()
        val result = runBlocking { service.signUp(EMAIL, PASSWORD) }
        assertTrue(result is AuthenticationResult.Error)
    }

    @Test
    fun `signUp shuold return same error message when is a rest exception`() {
        coEvery { authMock.signUpWith(Email, config = any()) } throws unknownRestException
        val result = runBlocking { service.signUp(EMAIL, PASSWORD) }
        assertEquals(expectedErrorMessage, (result as AuthenticationResult.Error).message)
    }

    @Test
    fun `signOut should return success when successful`() {
        coEvery { authMock.signOut() } returns Unit
        val result = runBlocking { service.signOut() }
        coVerify { authMock.signOut() }
        assertEquals(AuthenticationResult.Success, result)
    }

    @Test
    fun `signOut should return error when failed`() {
        coEvery { authMock.signOut() } throws Exception()
        val result = runBlocking { service.signOut() }
        coVerify { authMock.signOut() }
        assertTrue(result is AuthenticationResult.Error)
    }

    @Test
    fun `signOut shuold return same error message when is a rest exception`() {
        coEvery { authMock.signOut() } throws unknownRestException
        val result = runBlocking { service.signOut() }
        assertEquals(expectedErrorMessage, (result as AuthenticationResult.Error).message)
    }

    @Test
    fun `getCurrentUserID should return user id when user is signed in`() {
        every { authMock.currentSessionOrNull() } returns
            mockk { every { user } returns mockk { every { id } returns USER_ID } }
        val result = service.getCurrentUserID()
        assertEquals(USER_ID, result)
    }
}
