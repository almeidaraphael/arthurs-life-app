package com.lemonqwest.app.database

import com.lemonqwest.app.infrastructure.database.dao.UserDao
import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Database integration tests for User entity persistence with complete test isolation.
 *
 * These tests verify that User entity operations work correctly
 * with fresh in-memory databases per test.
 */
class DatabaseIntegrationUserEntityTest : DatabaseTestBase() {
    private lateinit var userDao: UserDao

    @BeforeEach
    fun setupDao() {
        userDao = database.userDao()
    }

    @Test
    fun insertUser_retrieveUser_returnsCorrectUser() = runTest {
        val userEntity = EntityTestFactory.createTestUserEntity(
            id = "test-user-1",
            name = "Test User",
            role = "CHILD",
        )
        userDao.insertUser(userEntity)
        val retrievedUser = userDao.getUserById("test-user-1")
        assertNotNull(retrievedUser)
        assertEquals(userEntity.id, retrievedUser?.id)
        assertEquals(userEntity.name, retrievedUser?.name)
        assertEquals(userEntity.role, retrievedUser?.role)
        assertEquals(userEntity.tokenBalance, retrievedUser?.tokenBalance)
    }

    @Test
    fun updateUser_tokenBalance_updatesCorrectly() = runTest {
        val userEntity = EntityTestFactory.createTestUserEntity(
            id = "test-user-2",
            name = "Test User 2",
            role = "CHILD",
        ).copy(tokenBalance = 10)
        userDao.insertUser(userEntity)
        val updatedUser = userEntity.copy(tokenBalance = 20)
        userDao.updateUser(updatedUser)
        val retrievedUser = userDao.getUserById("test-user-2")
        assertNotNull(retrievedUser)
        assertEquals(20, retrievedUser?.tokenBalance)
    }

    @Test
    fun deleteUser_removesUser() = runTest {
        val userEntity = EntityTestFactory.createTestUserEntity(
            id = "test-user-4",
            name = "Test User 4",
            role = "CHILD",
        )
        userDao.insertUser(userEntity)
        userDao.deleteUser("test-user-4")
        val retrievedUser = userDao.getUserById("test-user-4")
        assertNull(retrievedUser)
    }
}
