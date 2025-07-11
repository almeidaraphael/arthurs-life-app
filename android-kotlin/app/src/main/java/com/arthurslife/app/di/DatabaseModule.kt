package com.arthurslife.app.di

import android.content.Context
import com.arthurslife.app.infrastructure.database.ArthursLifeDatabase
import com.arthurslife.app.infrastructure.database.dao.AchievementDao
import com.arthurslife.app.infrastructure.database.dao.RewardDao
import com.arthurslife.app.infrastructure.database.dao.TaskDao
import com.arthurslife.app.infrastructure.database.dao.UserDao
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
     * Provides the singleton ArthursLifeDatabase instance.
     *
     * @param context Application context
     * @return ArthursLifeDatabase instance
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ArthursLifeDatabase =
        ArthursLifeDatabase.getDatabase(context)

    /**
     * Provides the TaskDao from the database.
     *
     * @param database ArthursLifeDatabase instance
     * @return TaskDao instance
     */
    @Provides
    fun provideTaskDao(database: ArthursLifeDatabase): TaskDao = database.taskDao()

    /**
     * Provides the UserDao from the database.
     *
     * @param database ArthursLifeDatabase instance
     * @return UserDao instance
     */
    @Provides
    fun provideUserDao(database: ArthursLifeDatabase): UserDao = database.userDao()

    /**
     * Provides the RewardDao from the database.
     *
     * @param database ArthursLifeDatabase instance
     * @return RewardDao instance
     */
    @Provides
    fun provideRewardDao(database: ArthursLifeDatabase): RewardDao = database.rewardDao()

    /**
     * Provides the AchievementDao from the database.
     *
     * @param database ArthursLifeDatabase instance
     * @return AchievementDao instance
     */
    @Provides
    fun provideAchievementDao(
        database: ArthursLifeDatabase,
    ): AchievementDao = database.achievementDao()
}
