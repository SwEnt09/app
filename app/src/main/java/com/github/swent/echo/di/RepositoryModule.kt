package com.github.swent.echo.di

import android.app.Application
import androidx.room.Room
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.data.repository.RepositoryImpl
import com.github.swent.echo.data.repository.datasources.LocalDataSource
import com.github.swent.echo.data.repository.datasources.RemoteDataSource
import com.github.swent.echo.data.room.AppDatabase
import com.github.swent.echo.data.room.RoomLocalDataSource
import com.github.swent.echo.data.supabase.SupabaseDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        supabaseClient: SupabaseClient,
        application: Application,
        networkService: NetworkService
    ): Repository {
        val remoteDataSource: RemoteDataSource = SupabaseDataSource(supabaseClient)
        val localDataSource: LocalDataSource =
            RoomLocalDataSource(
                Room.databaseBuilder(
                        application.applicationContext,
                        AppDatabase::class.java,
                        AppDatabase.APP_DATABASE_NAME
                    )
                    .build()
            )

        return RepositoryImpl(remoteDataSource, localDataSource, networkService)
    }

    /*
       @Singleton
       @Provides
       fun provideRepository(authenticationService: AuthenticationService): Repository {
           return SimpleRepository(authenticationService)
       }
    */
}
