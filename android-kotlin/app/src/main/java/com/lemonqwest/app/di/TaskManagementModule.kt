package com.lemonqwest.app.di

import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.auth.AuthenticationSessionService
import com.lemonqwest.app.domain.common.AchievementEventManager
import com.lemonqwest.app.domain.task.usecase.CalculateDailyProgressUseCase
import com.lemonqwest.app.domain.task.usecase.CompleteTaskUseCase
import com.lemonqwest.app.domain.task.usecase.TaskManagementUseCases
import com.lemonqwest.app.domain.task.usecase.UndoTaskUseCase
import com.lemonqwest.app.presentation.viewmodels.TaskManagementDependencies
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Container for task-related use cases to reduce parameter count.
 */
data class TaskUseCases(
    val taskManagementUseCases: TaskManagementUseCases,
    val completeTaskUseCase: CompleteTaskUseCase,
    val undoTaskUseCase: UndoTaskUseCase,
    val calculateDailyProgressUseCase: CalculateDailyProgressUseCase,
)

@Module
@InstallIn(SingletonComponent::class)
object TaskManagementModule {

    @Provides
    @Singleton
    fun provideTaskUseCases(
        taskManagementUseCases: TaskManagementUseCases,
        completeTaskUseCase: CompleteTaskUseCase,
        undoTaskUseCase: UndoTaskUseCase,
        calculateDailyProgressUseCase: CalculateDailyProgressUseCase,
    ): TaskUseCases = TaskUseCases(
        taskManagementUseCases = taskManagementUseCases,
        completeTaskUseCase = completeTaskUseCase,
        undoTaskUseCase = undoTaskUseCase,
        calculateDailyProgressUseCase = calculateDailyProgressUseCase,
    )

    @Provides
    @Singleton
    fun provideTaskManagementDependencies(
        taskUseCases: TaskUseCases,
        achievementTrackingUseCase: AchievementTrackingUseCase,
        authenticationSessionService: AuthenticationSessionService,
        achievementEventManager: AchievementEventManager,
    ): TaskManagementDependencies = TaskManagementDependencies(
        taskUseCases = taskUseCases,
        achievementTrackingUseCase = achievementTrackingUseCase,
        authenticationSessionService = authenticationSessionService,
        achievementEventManager = achievementEventManager,
    )
}
