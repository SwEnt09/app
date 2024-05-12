package com.github.swent.echo.di

import com.github.swent.echo.authentication.AuthenticationService
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AuthenticationServiceModule::class]
)
abstract class FakeAuthenticationServiceModule {

    @Singleton
    @Binds
    abstract fun bindAuthenticationService(
        simpleAuthenticationService: SimpleAuthenticationService
    ): AuthenticationService
}
