package com.github.swent.echo.di

import android.app.Application
import com.github.swent.echo.data.repository.FileCacheImpl
import com.github.swent.echo.data.repository.datasources.FileCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object FileCacheModule {

    @Singleton
    @Provides
    fun provideFileCache(application: Application): FileCache {
        return FileCacheImpl(application.cacheDir, Dispatchers.IO)
    }
}
