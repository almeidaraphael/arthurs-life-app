package com.arthurslife.app.di

import com.arthurslife.app.data.theme.ThemeRepositoryImpl
import com.arthurslife.app.domain.auth.AuthRepository
import com.arthurslife.app.domain.common.DateProvider
import com.arthurslife.app.domain.common.SystemDateProvider
import com.arthurslife.app.domain.reward.RewardDataSource
import com.arthurslife.app.domain.reward.RewardRepository
import com.arthurslife.app.domain.theme.repository.ThemeRepository
import com.arthurslife.app.infrastructure.auth.AuthRepositoryImpl
import com.arthurslife.app.infrastructure.database.RoomRewardDataSource
import com.arthurslife.app.infrastructure.reward.RewardRepositoryImpl
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

    @Binds
    @Singleton
    abstract fun bindRewardRepository(rewardRepositoryImpl: RewardRepositoryImpl): RewardRepository

    @Binds
    @Singleton
    abstract fun bindRewardDataSource(
        roomRewardDataSource: RoomRewardDataSource,
    ): RewardDataSource

    @Binds
    @Singleton
    abstract fun bindDateProvider(systemDateProvider: SystemDateProvider): DateProvider
}
