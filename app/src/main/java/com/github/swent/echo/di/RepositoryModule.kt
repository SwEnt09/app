package com.github.swent.echo.di

import com.github.swent.echo.data.repository.IRepository
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.data.repository.datasources.Cache
import com.github.swent.echo.data.repository.datasources.LocalDataSource
import com.github.swent.echo.data.repository.datasources.RemoteDataSource
import com.github.swent.echo.data.repository.datasources.Supabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideRepository(): IRepository {
        val localDataSource: LocalDataSource = Cache()
        val remoteDataSource: RemoteDataSource = Supabase()

        return Repository(localDataSource, remoteDataSource)
    }
}
