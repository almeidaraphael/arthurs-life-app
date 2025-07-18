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
 * Tests for UserDao update operations.
 */
class UserDaoUpdateOperationsTest : DatabaseTestBase() {
    private lateinit var userDao: UserDao

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        userDao = database.userDao()
    }

    @Test
    fun shouldUpdateUser() = runTest {
        val originalUser = EntityTestFactory.createTestUserEntity(
            id = "user-1",
            name = "Original Name",
            tokenBalance = 10,
            role = UserRole.CHILD,
        )
        userDao.insertUser(originalUser)
        val updatedUser = originalUser.copy(name = "Updated Name", tokenBalance = 50)
        userDao.updateUser(updatedUser)
        val retrievedUser = userDao.getUserById("user-1")
        assertNotNull(retrievedUser)
        assertEquals("Updated Name", retrievedUser.name)
        assertEquals(50, retrievedUser.tokenBalance)
    }
    // ...other update operation tests...
}
