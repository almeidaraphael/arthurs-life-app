package com.lemonqwest.app.infrastructure.database.dao

import com.lemonqwest.app.testutils.DatabaseAssertions
import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Performance tests for UserDao.
 */
class UserDaoPerformanceTest : DatabaseTestBase() {
    private lateinit var userDao: UserDao

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        userDao = database.userDao()
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
