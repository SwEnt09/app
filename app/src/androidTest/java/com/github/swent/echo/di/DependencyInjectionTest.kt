package com.github.swent.echo.di

import android.util.Log
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.authentication.AuthenticationServiceImpl
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// This test class demonstrates how to test dependency injection with Hilt and makes sure that we
// achieve 80% test coverage for the [SupabaseClientModule] and [AuthenticationServiceModule].
@HiltAndroidTest
class DependencyInjectionTest {

    companion object {
        private const val SUPABASE_URL = "ulejnivguxeiibkbpwnb.supabase.co"
        private const val SUPABASE_PUBLIC_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVsZWpuaXZndXhlaWlia2Jwd25iIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTA4MzgxODQsImV4cCI6MjAyNjQxNDE4NH0.9Hkj-Gox2XHcHfs_U2GyQFc9sZ_nu2Xs16-KYBri32g"
    }

    @get:Rule val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var supabaseClient: SupabaseClient

    @Inject lateinit var authenticationService: AuthenticationService

    @Before
    fun setUp() {
        // This tells Hilt to inject the [supabaseClient] and [authenticationService] fields.
        // Hilt also supports injecting fake implementations for testing. See the Hilt documentation
        // for more information.
        hiltRule.inject()
    }

    @Test
    fun testSupabaseClientInjection() {
        Log.d(
            "ExampleHiltTest",
            "URL: ${supabaseClient.supabaseUrl} KEY: ${supabaseClient.supabaseKey}"
        )
        assertEquals(SUPABASE_URL, supabaseClient.supabaseUrl)
        assertEquals(SUPABASE_PUBLIC_KEY, supabaseClient.supabaseKey)
        assertEquals(2, supabaseClient.pluginManager.installedPlugins.size)
    }

    @Test
    fun testAuthenticationServiceInjection() {
        assertEquals(AuthenticationServiceImpl::class.java, authenticationService::class.java)
    }
}