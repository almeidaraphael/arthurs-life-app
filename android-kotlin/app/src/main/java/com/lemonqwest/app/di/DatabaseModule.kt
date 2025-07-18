package com.lemonqwest.app.di

import android.content.Context
import com.lemonqwest.app.infrastructure.database.LemonQwestDatabase
import com.lemonqwest.app.infrastructure.database.dao.AchievementDao
import com.lemonqwest.app.infrastructure.database.dao.RewardDao
import com.lemonqwest.app.infrastructure.database.dao.TaskDao
import com.lemonqwest.app.infrastructure.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger module for providing database-related dependencies.
 *
 * This module provides the Room database instance and its DAOs
 * for use throughout the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the singleton LemonQwestDatabase instance.
     *
     * @param context Application context
     * @return LemonQwestDatabase instance
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LemonQwestDatabase =
        LemonQwestDatabase.getDatabase(context)

    /**
     * Provides the TaskDao from the database.
     *
     * @param database LemonQwestDatabase instance
     * @return TaskDao instance
     */
    @Provides
    fun provideTaskDao(database: LemonQwestDatabase): TaskDao = database.taskDao()

    /**
     * Provides the UserDao from the database.
     *
     * @param database LemonQwestDatabase instance
     * @return UserDao instance
     */
    @Provides
    fun provideUserDao(database: LemonQwestDatabase): UserDao = database.userDao()

    /**
     * Provides the RewardDao from the database.
     *
     * @param database LemonQwestDatabase instance
     * @return RewardDao instance
     */
    @Provides
    fun provideRewardDao(database: LemonQwestDatabase): RewardDao = database.rewardDao()

    /**
     * Provides the AchievementDao from the database.
     *
     * @param database LemonQwestDatabase instance
     * @return AchievementDao instance
     */
    @Provides
    fun provideAchievementDao(
        database: LemonQwestDatabase,
    ): AchievementDao = database.achievementDao()
}
