package com.lemonqwest.app.domain

import com.lemonqwest.app.domain.achievement.Achievement
import com.lemonqwest.app.domain.achievement.AchievementType
import com.lemonqwest.app.domain.auth.PIN
import com.lemonqwest.app.domain.task.Task
import com.lemonqwest.app.domain.task.TaskCategory
import com.lemonqwest.app.domain.task.usecase.TaskCompletionResult
import com.lemonqwest.app.domain.task.usecase.TaskUndoResult
import com.lemonqwest.app.domain.user.AvatarType
import com.lemonqwest.app.domain.user.TokenBalance
import com.lemonqwest.app.domain.user.User
import com.lemonqwest.app.domain.user.UserRole
import java.util.UUID

object TestDataFactory {

    // User Factory Methods
    fun createChildUser(
        name: String = "LemonQwest Kid",
        tokenBalance: TokenBalance = TokenBalance.create(25),
        id: String = UUID.randomUUID().toString(),
        displayName: String? = null,
        avatarType: AvatarType = AvatarType.PREDEFINED,
        avatarData: String = "default_child",
        favoriteColor: String? = null,
    ): User = User(
        id = id,
        name = name,
        role = UserRole.CHILD,
        tokenBalance = tokenBalance,
        pin = null,
        displayName = displayName,
        avatarType = avatarType,
        avatarData = avatarData,
        favoriteColor = favoriteColor,
    )

    fun createCaregiverUser(
        name: String = "Parent",
        pin: PIN? = PIN.create("9176"),
        id: String = UUID.randomUUID().toString(),
        displayName: String? = null,
        avatarType: AvatarType = AvatarType.PREDEFINED,
        avatarData: String = "default_caregiver",
        favoriteColor: String? = null,
    ): User = User(
        id = id,
        name = name,
        role = UserRole.CAREGIVER,
        tokenBalance = TokenBalance.zero(),
        pin = pin,
        displayName = displayName,
        avatarType = avatarType,
        avatarData = avatarData,
        favoriteColor = favoriteColor,
    )

    // Task Factory Methods
    fun createTask(
        title: String = "Brush teeth",
        category: TaskCategory = TaskCategory.PERSONAL_CARE,
        assignedToUserId: String = UUID.randomUUID().toString(),
        isCompleted: Boolean = false,
        id: String = UUID.randomUUID().toString(),
    ): Task = Task(
        id = id,
        title = title,
        category = category,
        tokenReward = category.defaultTokenReward,
        isCompleted = isCompleted,
        assignedToUserId = assignedToUserId,
        createdAt = System.currentTimeMillis(),
    )

    fun createCompletedTask(
        title: String = "Clean room",
        category: TaskCategory = TaskCategory.HOUSEHOLD,
        assignedToUserId: String = UUID.randomUUID().toString(),
    ): Task = createTask(
        title = title,
        category = category,
        assignedToUserId = assignedToUserId,
        isCompleted = true,
    )

    // Achievement Factory Methods
    fun createAchievement(
        type: AchievementType = AchievementType.FIRST_STEPS,
        userId: String = UUID.randomUUID().toString(),
        progress: Int = 0,
        isUnlocked: Boolean = false,
    ): Achievement = Achievement(
        id = "${type.name.lowercase()}-$userId",
        type = type,
        isUnlocked = isUnlocked,
        progress = progress,
        unlockedAt = if (isUnlocked) System.currentTimeMillis() else null,
        userId = userId,
    )

    fun createUnlockedAchievement(
        type: AchievementType = AchievementType.FIRST_STEPS,
        userId: String = UUID.randomUUID().toString(),
    ): Achievement = createAchievement(
        type = type,
        userId = userId,
        progress = type.target,
        isUnlocked = true,
    )

    // PIN Factory Method
    fun createPIN(rawPin: String = "1234"): PIN = PIN.create(rawPin)

    // Task Use Case Result Factory Methods
    fun createTaskCompletionResult(
        taskId: String = UUID.randomUUID().toString(),
        tokensAwarded: Int = 10,
        newTokenBalance: Int = 60,
        newlyUnlockedAchievements: List<Achievement> = emptyList(),
    ): TaskCompletionResult = TaskCompletionResult(
        task = createTask(id = taskId, isCompleted = true),
        tokensAwarded = tokensAwarded,
        newTokenBalance = newTokenBalance,
        newlyUnlockedAchievements = newlyUnlockedAchievements,
    )

    fun createTaskUndoResult(
        taskId: String = UUID.randomUUID().toString(),
        tokensDeducted: Int = 10,
        newTokenBalance: Int = 40,
        undoneByRole: UserRole = UserRole.CHILD,
    ): TaskUndoResult = TaskUndoResult(
        task = createTask(id = taskId, isCompleted = false),
        tokensDeducted = tokensDeducted,
        newTokenBalance = newTokenBalance,
        undoneByRole = undoneByRole,
    )

    // Achievement Factory Methods (Extended)
    fun createPartialAchievement(
        type: AchievementType = AchievementType.FIRST_STEPS,
        progressPercentage: Int = 50,
        userId: String = UUID.randomUUID().toString(),
    ): Achievement = Achievement(
        id = "${type.name.lowercase()}-$userId",
        type = type,
        isUnlocked = false,
        progress = (type.target * progressPercentage) / 100,
        unlockedAt = null,
        userId = userId,
    )

    fun createAchievementSet(
        userId: String = UUID.randomUUID().toString(),
        includeProgress: Boolean = true,
    ): List<Achievement> = AchievementType.values().map { type ->
        val progressMap = mapOf(
            AchievementType.FIRST_STEPS to type.target, AchievementType.TASK_MASTER to 0,
            AchievementType.THREE_DAY_STREAK to 1, AchievementType.CENTURY_CLUB to 7,
            AchievementType.TOKEN_COLLECTOR to 25, AchievementType.PERFECT_WEEK to 3,
            AchievementType.SPEED_DEMON to 2, AchievementType.BIG_SPENDER to 45,
            AchievementType.TASK_CHAMPION to 15, AchievementType.EARLY_BIRD to 1,
        )
        createAchievement(
            type = type,
            userId = userId,
            progress = if (includeProgress) progressMap[type] ?: 0 else 0,
            isUnlocked = includeProgress && type == AchievementType.FIRST_STEPS,
        )
    }

    // Additional missing methods
    fun createTaskCompletionScenario(
        userId: String = UUID.randomUUID().toString(),
    ): Triple<Task, User, Achievement> {
        val task = createTask(assignedToUserId = userId, isCompleted = true)
        val user = createChildUser(id = userId)
        val achievement = createAchievement(userId = userId)
        return Triple(task, user, achievement)
    }

    fun createTaskList(
        count: Int = 9,
        assignedToUserId: String = UUID.randomUUID().toString(),
        includeCompleted: Boolean = false,
    ): List<Task> {
        val categories = TaskCategory.values()
        return (1..count).map { index ->
            val category = categories[(index - 1) % categories.size]
            createTask(
                id = "task-$index",
                title = "Task $index",
                category = category,
                assignedToUserId = assignedToUserId,
                isCompleted = if (includeCompleted && index % 3 == 0) true else false,
            )
        }
    }

    fun createRandomTask(): Task {
        val categories = TaskCategory.values()
        val randomCategory = categories.random()
        val randomCompleted = listOf(true, false).random()

        return createTask(
            id = UUID.randomUUID().toString(),
            title = "Random Task ${(1..1000).random()}",
            category = randomCategory,
            isCompleted = randomCompleted,
        )
    }
}
