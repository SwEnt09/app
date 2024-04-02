package com.github.swent.echo.di

import android.app.Application
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.data.repository.RepositoryImpl
import com.github.swent.echo.data.repository.datasources.RemoteDataSource
import com.github.swent.echo.data.repository.datasources.Supabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideRepository(application: Application, supabaseClient: SupabaseClient): Repository {
        val remoteDataSource: RemoteDataSource = Supabase(supabaseClient)

        return RepositoryImpl(remoteDataSource)
    }
}
