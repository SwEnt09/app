package com.github.swent.echo.authentication

import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthenticationServiceImplTest {

    private lateinit var authMock: Auth

    companion object {
        private const val EMAIL = "test@email.com"
        private const val PASSWORD = "password"
        private const val USER_ID = "123"
    }

    @Before
    fun setUp() {
        authMock = mockk(relaxed = true)
    }

    @Test
    fun `signIn should return success when successful`() {
        val service = AuthenticationServiceImpl(authMock)
        coEvery { authMock.signInWith(Email, config = any()) } returns Unit
        val result = runBlocking { service.signIn(EMAIL, PASSWORD) }
        assertEquals(AuthenticationResult.Success, result)
    }

    @Test
    fun `signIn should return error when failed`() {
        val service = AuthenticationServiceImpl(authMock)
        coEvery { authMock.signInWith(Email, config = any()) } throws Exception()
        val result = runBlocking { service.signIn(EMAIL, PASSWORD) }
        assertTrue(result is AuthenticationResult.Error)
    }

    @Test
    fun `signUp should return success when successful`() {
        val service = AuthenticationServiceImpl(authMock)
        coEvery { authMock.signUpWith(Email, config = any()) } returns mockk()
        val result = runBlocking { service.signUp(EMAIL, PASSWORD) }
        assertEquals(AuthenticationResult.Success, result)
    }

    @Test
    fun `signUp should return error when failed`() {
        val service = AuthenticationServiceImpl(authMock)
        coEvery { authMock.signUpWith(Email, config = any()) } throws Exception()
        val result = runBlocking { service.signUp(EMAIL, PASSWORD) }
        assertTrue(result is AuthenticationResult.Error)
    }

    @Test
    fun `signOut should return success when successful`() {
        val service = AuthenticationServiceImpl(authMock)
        coEvery { authMock.signOut() } returns Unit
        val result = runBlocking { service.signOut() }
        coVerify { authMock.signOut() }
        assertEquals(AuthenticationResult.Success, result)
    }

    @Test
    fun `signOut should return error when failed`() {
        val service = AuthenticationServiceImpl(authMock)
        coEvery { authMock.signOut() } throws Exception()
        val result = runBlocking { service.signOut() }
        coVerify { authMock.signOut() }
        assertTrue(result is AuthenticationResult.Error)
    }

    @Test
    fun `getCurrentUserID should return user id when user is signed in`() {
        val service = AuthenticationServiceImpl(authMock)
        every { authMock.currentSessionOrNull() } returns
            mockk { every { user } returns mockk { every { id } returns USER_ID } }
        val result = runBlocking { service.getCurrentUserID() }
        assertEquals(USER_ID, result)
    }
}
