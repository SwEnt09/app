package com.github.swent.echo.di

import android.app.Application
import com.github.swent.echo.connectivity.GPSService
import com.github.swent.echo.connectivity.GPSServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GPSServiceModule {

    @Singleton
    @Provides
    fun provideGPSService(application: Application): GPSService {
        val context = application.applicationContext
        return GPSServiceImpl(context)
    }
}
