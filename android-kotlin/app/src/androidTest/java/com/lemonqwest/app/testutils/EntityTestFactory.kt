package com.lemonqwest.app.testutils

import com.lemonqwest.app.domain.task.TaskCategory
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.infrastructure.database.entities.TaskEntity
import com.lemonqwest.app.infrastructure.database.entities.UserEntity

/**
 * Entity test factory for creating test data.
 */
object EntityTestFactory {
    fun createTestUserEntity(
        id: String = "test-user",
        name: String = "Test User",
        role: String = "CHILD",
        tokenBalance: Int = 0,
        pinHash: String? = null,
        displayName: String? = null,
        avatarType: String = "PREDEFINED",
        avatarData: String = "default",
        favoriteColor: String? = null,
        createdAt: Long = System.currentTimeMillis(),
    ): UserEntity {
        return UserEntity(
            id = id,
            name = name,
            role = role,
            tokenBalance = tokenBalance,
            pinHash = pinHash,
            displayName = displayName,
            avatarType = avatarType,
            avatarData = avatarData,
            favoriteColor = favoriteColor,
            createdAt = createdAt,
        )
    }
    fun createTestUserEntity(
        id: String = "test-user",
        name: String = "Test User",
        role: UserRole = UserRole.CHILD,
        tokenBalance: Int = 0,
        pinHash: String? = null,
        displayName: String? = null,
        avatarType: String = "PREDEFINED",
        avatarData: String = "default",
        favoriteColor: String? = null,
        createdAt: Long = System.currentTimeMillis(),
    ): UserEntity {
        return createTestUserEntity(
            id = id,
            name = name,
            role = role.name,
            tokenBalance = tokenBalance,
            pinHash = pinHash,
            displayName = displayName,
            avatarType = avatarType,
            avatarData = avatarData,
            favoriteColor = favoriteColor,
            createdAt = createdAt,
        )
    }
    fun createTestTaskEntity(
        id: String = "test-task",
        title: String = "Test Task",
        category: String = "HOUSEHOLD",
        tokenReward: Int = 5,
        isCompleted: Boolean = false,
        assignedToUserId: String = "test-user",
        createdAt: Long = System.currentTimeMillis(),
        completedAt: Long? = null,
        customIconName: String? = null,
        customColorHex: String? = null,
    ): TaskEntity {
        return TaskEntity(
            id = id,
            title = title,
            category = category,
            tokenReward = tokenReward,
            isCompleted = isCompleted,
            assignedToUserId = assignedToUserId,
            createdAt = createdAt,
            completedAt = completedAt,
            customIconName = customIconName,
            customColorHex = customColorHex,
        )
    }
    fun createTestTaskEntity(
        id: String = "test-task",
        title: String = "Test Task",
        category: TaskCategory = TaskCategory.HOUSEHOLD,
        tokenReward: Int = category.defaultTokenReward,
        isCompleted: Boolean = false,
        assignedToUserId: String = "test-user",
        createdAt: Long = System.currentTimeMillis(),
        completedAt: Long? = null,
        customIconName: String? = null,
        customColorHex: String? = null,
    ): TaskEntity {
        return createTestTaskEntity(
            id = id,
            title = title,
            category = category.name,
            tokenReward = tokenReward,
            isCompleted = isCompleted,
            assignedToUserId = assignedToUserId,
            createdAt = createdAt,
            completedAt = completedAt,
            customIconName = customIconName,
            customColorHex = customColorHex,
        )
    }
    fun createUserEntity(
        id: String = "test-user",
        name: String = "Test User",
        role: String = "CHILD",
        tokenBalance: Int = 0,
        createdAt: Long = System.currentTimeMillis(),
        pinHash: String? = null,
        displayName: String? = null,
        avatarType: String = "PREDEFINED",
        avatarData: String = "default",
        favoriteColor: String? = null,
    ): UserEntity = createTestUserEntity(
        id = id,
        name = name,
        role = role,
        tokenBalance = tokenBalance,
        pinHash = pinHash,
        displayName = displayName,
        avatarType = avatarType,
        avatarData = avatarData,
        favoriteColor = favoriteColor,
        createdAt = createdAt,
    )
    fun createTaskEntity(
        id: String = "test-task",
        title: String = "Test Task",
        category: String = "HOUSEHOLD",
        tokenReward: Int = 5,
        isCompleted: Boolean = false,
        assignedToUserId: String = "test-user",
        createdAt: Long = System.currentTimeMillis(),
        completedAt: Long? = null,
        customIconName: String? = null,
        customColorHex: String? = null,
    ): TaskEntity = createTestTaskEntity(
        id = id,
        title = title,
        category = category,
        tokenReward = tokenReward,
        isCompleted = isCompleted,
        assignedToUserId = assignedToUserId,
        createdAt = createdAt,
        completedAt = completedAt,
        customIconName = customIconName,
        customColorHex = customColorHex,
    )
    fun createTaskEntity(
        id: String = "test-task",
        title: String = "Test Task",
        category: TaskCategory = TaskCategory.HOUSEHOLD,
        tokenReward: Int = category.defaultTokenReward,
        isCompleted: Boolean = false,
        assignedToUserId: String = "test-user",
        createdAt: Long = System.currentTimeMillis(),
        completedAt: Long? = null,
        customIconName: String? = null,
        customColorHex: String? = null,
    ): TaskEntity = createTestTaskEntity(
        id = id,
        title = title,
        category = category,
        tokenReward = tokenReward,
        isCompleted = isCompleted,
        assignedToUserId = assignedToUserId,
        createdAt = createdAt,
        completedAt = completedAt,
        customIconName = customIconName,
        customColorHex = customColorHex,
    )
}
