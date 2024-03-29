package com.github.swent.echo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

@Module
@InstallIn(SingletonComponent::class)
object SupabaseClientModule {

    private const val SUPABASE_URL = "https://ulejnivguxeiibkbpwnb.supabase.co"
    private const val SUPABASE_PUBLIC_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVsZWpuaXZndXhlaWlia2Jwd25iIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTA4MzgxODQsImV4cCI6MjAyNjQxNDE4NH0.9Hkj-Gox2XHcHfs_U2GyQFc9sZ_nu2Xs16-KYBri32g"

    @Provides
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(SUPABASE_URL, SUPABASE_PUBLIC_KEY) {
            install(Auth)
            install(Postgrest)
        }
    }
}
