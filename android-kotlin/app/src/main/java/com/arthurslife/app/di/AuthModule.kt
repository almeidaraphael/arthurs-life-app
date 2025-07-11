package com.arthurslife.app.di

import com.arthurslife.app.domain.achievement.AchievementDataSource
import com.arthurslife.app.domain.achievement.AchievementRepository
import com.arthurslife.app.domain.auth.AuthenticationDomainService
import com.arthurslife.app.domain.auth.AuthenticationService
import com.arthurslife.app.domain.auth.AuthenticationSessionService
import com.arthurslife.app.domain.task.TaskDataSource
import com.arthurslife.app.domain.task.TaskRepository
import com.arthurslife.app.domain.user.UserDataSource
import com.arthurslife.app.domain.user.UserRepository
import com.arthurslife.app.infrastructure.achievement.AchievementRepositoryImpl
import com.arthurslife.app.infrastructure.achievement.RoomAchievementDataSource
import com.arthurslife.app.infrastructure.database.RoomTaskDataSource
import com.arthurslife.app.infrastructure.task.TaskRepositoryImpl
import com.arthurslife.app.infrastructure.user.RoomUserDataSource
import com.arthurslife.app.infrastructure.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    @Singleton
    abstract fun bindUserDataSource(impl: RoomUserDataSource): UserDataSource

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindTaskDataSource(impl: RoomTaskDataSource): TaskDataSource

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindAchievementDataSource(
        impl: RoomAchievementDataSource,
    ): AchievementDataSource

    @Binds
    @Singleton
    abstract fun bindAchievementRepository(impl: AchievementRepositoryImpl): AchievementRepository

    @Binds
    @Singleton
    abstract fun bindAuthenticationService(impl: AuthenticationDomainService): AuthenticationService

    @Binds
    @Singleton
    abstract fun bindAuthenticationSessionService(
        impl: AuthenticationDomainService,
    ): AuthenticationSessionService
}
