package com.lemonqwest.app.di

import com.lemonqwest.app.domain.achievement.AchievementDataSource
import com.lemonqwest.app.domain.achievement.AchievementRepository
import com.lemonqwest.app.domain.auth.AuthenticationDomainService
import com.lemonqwest.app.domain.auth.AuthenticationService
import com.lemonqwest.app.domain.auth.AuthenticationSessionService
import com.lemonqwest.app.domain.task.TaskDataSource
import com.lemonqwest.app.domain.task.TaskRepository
import com.lemonqwest.app.domain.user.UserDataSource
import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.infrastructure.achievement.AchievementRepositoryImpl
import com.lemonqwest.app.infrastructure.achievement.RoomAchievementDataSource
import com.lemonqwest.app.infrastructure.database.RoomTaskDataSource
import com.lemonqwest.app.infrastructure.task.TaskRepositoryImpl
import com.lemonqwest.app.infrastructure.user.RoomUserDataSource
import com.lemonqwest.app.infrastructure.user.UserRepositoryImpl
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
