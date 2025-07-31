package com.lemonqwest.app.testutils

import com.lemonqwest.app.infrastructure.database.dao.TaskDao
import com.lemonqwest.app.infrastructure.database.dao.UserDao
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Lean database assertion utilities for LemonQwest tests.
 *
 * Provides essential verification methods for database operations following
 * Lean Testing Manifesto principles - simple, focused, business-value oriented.
 */
object DatabaseAssertions {

    /**
     * Verifies the total count of tasks in the database.
     */
    suspend fun verifyTaskCount(taskDao: TaskDao, expectedCount: Int) {
        val actualCount = taskDao.getAllTasks().size
        assertEquals(expectedCount, actualCount, "Task count mismatch")
    }

    /**
     * Verifies that a task with the given ID exists.
     */
    suspend fun verifyTaskExists(taskDao: TaskDao, taskId: String) {
        val task = taskDao.findById(taskId)
        assertNotNull(task, "Task with ID $taskId should exist")
    }

    /**
     * Verifies that a task with the given ID does not exist.
     */
    suspend fun verifyTaskNotExists(taskDao: TaskDao, taskId: String) {
        val task = taskDao.findById(taskId)
        assertNull(task, "Task with ID $taskId should not exist")
    }

    /**
     * Verifies the completion status of a task.
     */
    suspend fun verifyTaskCompletion(taskDao: TaskDao, taskId: String, expectedCompleted: Boolean) {
        val task = taskDao.findById(taskId)
        assertNotNull(task, "Task with ID $taskId should exist")
        assertEquals(expectedCompleted, task.isCompleted, "Task completion status mismatch")
    }

    /**
     * Verifies the total count of users in the database.
     */
    suspend fun verifyUserCount(userDao: UserDao, expectedCount: Int) {
        val actualCount = userDao.getAllUsers().size
        assertEquals(expectedCount, actualCount, "User count mismatch")
    }

    /**
     * Verifies that a user with the given ID exists.
     */
    suspend fun verifyUserExists(userDao: UserDao, userId: String) {
        val user = userDao.getUserById(userId)
        assertNotNull(user, "User with ID $userId should exist")
    }

    /**
     * Verifies that a user with the given ID does not exist.
     */
    suspend fun verifyUserNotExists(userDao: UserDao, userId: String) {
        val user = userDao.getUserById(userId)
        assertNull(user, "User with ID $userId should not exist")
    }

    /**
     * Verifies the token balance of a user.
     */
    suspend fun verifyUserTokenBalance(userDao: UserDao, userId: String, expectedBalance: Int) {
        val user = userDao.getUserById(userId)
        assertNotNull(user, "User with ID $userId should exist")
        assertEquals(expectedBalance, user.tokenBalance, "User token balance mismatch")
    }
}
