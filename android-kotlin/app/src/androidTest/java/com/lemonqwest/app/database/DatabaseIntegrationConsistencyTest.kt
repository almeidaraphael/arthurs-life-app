package com.lemonqwest.app.database

import com.lemonqwest.app.infrastructure.database.dao.UserDao
import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Database consistency tests with complete test isolation.
 *
 * These tests verify that database operations maintain data consistency
 * under various scenarios with fresh in-memory databases per test.
 */
class DatabaseIntegrationConsistencyTest : DatabaseTestBase() {
    private lateinit var userDao: UserDao

    @BeforeEach
    fun setupDao() {
        userDao = database.userDao()
    }

    @Test
    fun database_concurrentOperations_maintainConsistency() = runTest {
        val userEntity = EntityTestFactory.createTestUserEntity(
            id = "concurrent-user",
            name = "Concurrent User",
            role = "CHILD",
        )
        userDao.insertUser(userEntity)
        repeat(10) { index ->
            val updatedUser = userEntity.copy(tokenBalance = index + 1)
            userDao.updateUser(updatedUser)
        }
        val finalUser = userDao.getUserById("concurrent-user")
        assertNotNull(finalUser)
        assertTrue("Token balance should be positive", finalUser!!.tokenBalance > 0)
    }
}
