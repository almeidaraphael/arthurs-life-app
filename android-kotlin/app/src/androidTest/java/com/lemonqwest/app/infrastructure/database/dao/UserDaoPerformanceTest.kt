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
 * Performance tests for UserDao.
 */
class UserDaoPerformanceTest {
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
    fun shouldHandleManyInsertions() = runTest {
        val users = (1..1000).map { index ->
            EntityTestFactory.createTestUserEntity(
                id = "user-$index",
                name = "User $index",
                tokenBalance = index,
            )
        }
        users.forEach { userDao.insertUser(it) }
        DatabaseAssertions.verifyUserCount(userDao, 1000)
    }
    // ...other performance tests...
}
