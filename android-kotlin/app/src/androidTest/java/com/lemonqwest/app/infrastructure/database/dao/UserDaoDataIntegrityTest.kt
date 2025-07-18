package com.lemonqwest.app.infrastructure.database.dao

import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.infrastructure.database.LemonQwestDatabase
import com.lemonqwest.app.testutils.DatabaseCleanup
import com.lemonqwest.app.testutils.DatabaseTestFactory
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Tests for UserDao data integrity.
 */
class UserDaoDataIntegrityTest {
    private lateinit var database: LemonQwestDatabase
    private lateinit var userDao: UserDao

    @BeforeEach
    fun setup() {
        database = DatabaseTestFactory.createInMemoryDatabase()
        userDao = database.userDao()
    }

    @AfterEach
    fun teardown() = runTest {
        DatabaseCleanup.clearDatabase(database)
        database.close()
    }

    @Test
    fun shouldPreserveAllUserFields() = runTest {
        val currentTime = System.currentTimeMillis()
        val user = EntityTestFactory.createTestUserEntity(
            id = "test-user",
            name = "Test User",
            role = UserRole.CHILD.name,
        ).copy(
            tokenBalance = 42,
            createdAt = currentTime,
            displayName = "TestDisplayName",
            avatarType = "default",
            avatarData = "",
            favoriteColor = "#FFFFFF",
        )
        userDao.insertUser(user)
        val retrievedUser = userDao.getUserById("test-user")
        assertNotNull(retrievedUser)
        assertEquals(user.id, retrievedUser.id)
        assertEquals(user.name, retrievedUser.name)
        assertEquals(user.role, retrievedUser.role)
        assertEquals(user.tokenBalance, retrievedUser.tokenBalance)
        assertEquals(user.pinHash, retrievedUser.pinHash)
        assertEquals(user.createdAt, retrievedUser.createdAt)
    }
    // ...other data integrity tests...
}
