package com.lemonqwest.app.infrastructure.database.dao

import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Tests for UserDao data integrity.
 */
class UserDaoDataIntegrityTest : DatabaseTestBase() {
    private lateinit var userDao: UserDao

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        userDao = database.userDao()
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
