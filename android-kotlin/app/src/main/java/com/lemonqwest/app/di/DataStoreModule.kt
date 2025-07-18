package com.lemonqwest.app.di

import android.content.Context
import com.lemonqwest.app.data.theme.ThemePreferencesDataStore
import com.lemonqwest.app.domain.auth.AuthRepository
import com.lemonqwest.app.domain.user.UserDataSource
import com.lemonqwest.app.infrastructure.auth.AuthRepositoryImpl
import com.lemonqwest.app.infrastructure.preferences.AuthPreferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideAuthPreferencesDataStore(
        @ApplicationContext context: Context,
    ): AuthPreferencesDataStore = AuthPreferencesDataStore(context)

    @Provides
    @Singleton
    fun provideThemePreferencesDataStore(
        @ApplicationContext context: Context,
    ): ThemePreferencesDataStore = ThemePreferencesDataStore(context)

    // Keep AuthRepository for backward compatibility during migration
    @Provides
    @Singleton
    fun provideAuthRepository(
        authPreferencesDataStore: AuthPreferencesDataStore,
        userDataSource: UserDataSource,
    ): AuthRepository = AuthRepositoryImpl(authPreferencesDataStore, userDataSource)
}
