package com.github.swent.echo.di

import android.content.Context
import com.github.swent.echo.ThemePreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideThemePreferenceManager(
        @ApplicationContext context: Context
    ): ThemePreferenceManager {
        return ThemePreferenceManager(context)
    }
}
