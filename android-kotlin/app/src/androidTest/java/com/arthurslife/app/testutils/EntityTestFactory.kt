package com.arthurslife.app.testutils

import com.arthurslife.app.domain.task.TaskCategory
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.infrastructure.database.entities.TaskEntity
import com.arthurslife.app.infrastructure.database.entities.UserEntity
import java.util.UUID

/**
 * Test entity factory for Room database entities and domain objects.
 * Provides methods to create UserEntity and TaskEntity for integration tests.
 */
object EntityTestFactory {
    fun createUserEntity(
        id: String = UUID.randomUUID().toString(),
        name: String = "Test User",
        role: UserRole = UserRole.CHILD,
        tokenBalance: Int = 0,
        createdAt: Long = System.currentTimeMillis(),
        pinHash: String? = null,
        displayName: String = "TestDisplayName",
        avatarType: String = "default",
        avatarData: String = "default_avatar",
        favoriteColor: String = "#FFFFFF",
    ): UserEntity = UserEntity(
        id = id,
        name = name,
        role = role.name,
        tokenBalance = tokenBalance,
        pinHash = pinHash,
        createdAt = createdAt,
        displayName = displayName,
        avatarType = avatarType,
        avatarData = avatarData,
        favoriteColor = favoriteColor,
    )

    fun createTaskEntity(
        id: String = UUID.randomUUID().toString(),
        title: String = "Test Task",
        category: TaskCategory = TaskCategory.PERSONAL_CARE,
        tokenReward: Int = category.defaultTokenReward,
        isCompleted: Boolean = false,
        assignedToUserId: String = UUID.randomUUID().toString(),
        createdAt: Long = System.currentTimeMillis(),
        completedAt: Long? = null,
        customIconName: String = "default_icon",
        customColorHex: String = "#FFFFFF",
    ): TaskEntity = TaskEntity(
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
