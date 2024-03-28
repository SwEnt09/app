package com.github.swent.echo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
class SupabaseClientModule {

    @Provides
    fun provideSupabaseClient(): SupabaseClient {
        val supabaseUrl = "https://ulejnivguxeiibkbpwnb.supabase.co"
        val supabaseKey =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVsZWpuaXZndXhlaWlia2Jwd25iIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTA4MzgxODQsImV4cCI6MjAyNjQxNDE4NH0.9Hkj-Gox2XHcHfs_U2GyQFc9sZ_nu2Xs16-KYBri32g"
        return createSupabaseClient(supabaseUrl, supabaseKey) {
            defaultSerializer = KotlinXSerializer(Json)
            install(Auth)
            install(Postgrest)
        }
    }
}
