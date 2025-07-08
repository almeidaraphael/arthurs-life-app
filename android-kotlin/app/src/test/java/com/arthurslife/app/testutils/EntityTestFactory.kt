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
    ): UserEntity = UserEntity(
        id = id,
        name = name,
        role = role.name,
        tokenBalance = tokenBalance,
        pinHash = pinHash,
        createdAt = createdAt,
    )

    fun createTaskEntity(
        id: String = UUID.randomUUID().toString(),
        title: String = "Test Task",
        category: TaskCategory = TaskCategory.PERSONAL_CARE,
        tokenReward: Int = category.defaultTokenReward,
        isCompleted: Boolean = false,
        assignedToUserId: String = UUID.randomUUID().toString(),
        createdAt: Long = System.currentTimeMillis(),
    ): TaskEntity = TaskEntity(
        id = id,
        title = title,
        category = category.name,
        tokenReward = tokenReward,
        isCompleted = isCompleted,
        assignedToUserId = assignedToUserId,
        createdAt = createdAt,
    )
}
