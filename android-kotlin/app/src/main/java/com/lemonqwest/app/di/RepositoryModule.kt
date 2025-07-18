package com.lemonqwest.app.di

import com.lemonqwest.app.data.theme.ThemeRepositoryImpl
import com.lemonqwest.app.domain.auth.AuthRepository
import com.lemonqwest.app.domain.common.DateProvider
import com.lemonqwest.app.domain.common.SystemDateProvider
import com.lemonqwest.app.domain.reward.RewardDataSource
import com.lemonqwest.app.domain.reward.RewardRepository
import com.lemonqwest.app.domain.theme.repository.ThemeRepository
import com.lemonqwest.app.domain.user.ChangeUserAvatarUseCase
import com.lemonqwest.app.domain.user.ChangeUserPinUseCase
import com.lemonqwest.app.domain.user.ProfileImageRepository
import com.lemonqwest.app.domain.user.UpdateUserProfileUseCase
import com.lemonqwest.app.infrastructure.auth.AuthRepositoryImpl
import com.lemonqwest.app.infrastructure.database.RoomRewardDataSource
import com.lemonqwest.app.infrastructure.reward.RewardRepositoryImpl
import com.lemonqwest.app.infrastructure.user.ProfileImageRepositoryImpl
import com.lemonqwest.app.presentation.viewmodels.UserProfileUseCases
import dagger.Binds
import dagger.Module
import dagger.Provides
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

    @Binds
    @Singleton
    abstract fun bindProfileImageRepository(
        profileImageRepositoryImpl: ProfileImageRepositoryImpl,
    ): ProfileImageRepository

    companion object {
        @Provides
        @Singleton
        fun provideUserProfileUseCases(
            updateUserProfileUseCase: UpdateUserProfileUseCase,
            changeUserAvatarUseCase: ChangeUserAvatarUseCase,
            changeUserPinUseCase: ChangeUserPinUseCase,
        ): UserProfileUseCases = UserProfileUseCases(
            updateUserProfileUseCase = updateUserProfileUseCase,
            changeUserAvatarUseCase = changeUserAvatarUseCase,
            changeUserPinUseCase = changeUserPinUseCase,
        )
    }
}
