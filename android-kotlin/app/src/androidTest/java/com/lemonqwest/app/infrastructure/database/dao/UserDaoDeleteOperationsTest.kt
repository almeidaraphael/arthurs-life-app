package com.lemonqwest.app.infrastructure.database.dao

import com.lemonqwest.app.infrastructure.database.LemonQwestDatabase
import com.lemonqwest.app.testutils.DatabaseAssertions
import com.lemonqwest.app.testutils.DatabaseCleanup
import com.lemonqwest.app.testutils.DatabaseTestFactory
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for UserDao delete operations.
 */
class UserDaoDeleteOperationsTest {
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
    fun shouldDeleteUserById() = runTest {
        val user = EntityTestFactory.createTestUserEntity(id = "user-1")
        userDao.insertUser(user)
        DatabaseAssertions.verifyUserExists(userDao, "user-1")
        userDao.deleteUser("user-1")
        DatabaseAssertions.verifyUserNotExists(userDao, "user-1")
    }
    // ...other delete operation tests...
}
