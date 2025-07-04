package com.arthurslife.app.di

import com.arthurslife.app.data.theme.ThemeRepositoryImpl
import com.arthurslife.app.domain.auth.AuthRepository
import com.arthurslife.app.domain.theme.repository.ThemeRepository
import com.arthurslife.app.infrastructure.auth.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindThemeRepository(themeRepositoryImpl: ThemeRepositoryImpl): ThemeRepository
}
