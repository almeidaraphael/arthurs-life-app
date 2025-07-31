package com.lemonqwest.app.testutils

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.lemonqwest.app.domain.task.TaskCategory
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.infrastructure.database.LemonQwestDatabase
import com.lemonqwest.app.infrastructure.database.entities.TaskEntity
import com.lemonqwest.app.infrastructure.database.entities.UserEntity

/**
 * Database testing utilities for LemonQwest app.
 *
 * Provides utilities for testing database operations including:
 * - In-memory database setup
 * - Basic test data creation
 */

/**
 * In-memory database factory for testing.
 */
object DatabaseTestFactory {

    /**
     * Creates an in-memory Room database for testing.
     *
     * @return Test database instance
     */
    fun createInMemoryDatabase(): LemonQwestDatabase {
        return Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LemonQwestDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
    }
}

/**
 * Database cleanup utilities.
 */
object DatabaseCleanup {

    /**
     * Clears all data from the database.
     */
    suspend fun clearDatabase(database: LemonQwestDatabase) {
        database.clearAllTables()
    }
}

/**
 * Entity test factory for creating test data.
 */
object EntityTestFactory {

    /**
     * Creates a test user entity.
     */
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

    /**
     * Creates a test user entity with UserRole enum.
     */
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

    /**
     * Creates a test task entity.
     */
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

    /**
     * Creates a test task entity with TaskCategory enum.
     */
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

    /**
     * Alias for createTestUserEntity for compatibility.
     */
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

    /**
     * Alias for createTestTaskEntity for compatibility.
     */
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

    /**
     * Alias for createTestTaskEntity with TaskCategory enum.
     */
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
