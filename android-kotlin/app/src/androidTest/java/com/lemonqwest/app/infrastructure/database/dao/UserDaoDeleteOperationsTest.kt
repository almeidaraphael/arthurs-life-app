package com.lemonqwest.app.infrastructure.database.dao

import com.lemonqwest.app.testutils.DatabaseAssertions
import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for UserDao delete operations.
 */
class UserDaoDeleteOperationsTest : DatabaseTestBase() {
    private lateinit var userDao: UserDao

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        userDao = database.userDao()
    }

    @Test
    fun shouldDeleteUserById() = runTest {
        val user = EntityTestFactory.createTestUserEntity(id = "user-1")
        userDao.insertUser(user)
        DatabaseAssertions.verifyUserExists(userDao, "user-1")
        userDao.deleteUser("user-1")
        DatabaseAssertions.verifyUserNotExists(userDao, "user-1")
    }
    // ...other delete operation tests...
}
