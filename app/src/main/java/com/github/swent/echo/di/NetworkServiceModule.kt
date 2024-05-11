package com.github.swent.echo.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.connectivity.NetworkServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkServiceModule {

    @Singleton
    @Provides
    fun provideNetworkService(application: Application): NetworkService {
        val context = application.applicationContext
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
        return NetworkServiceImpl(connectivityManager as ConnectivityManager)
    }
}
