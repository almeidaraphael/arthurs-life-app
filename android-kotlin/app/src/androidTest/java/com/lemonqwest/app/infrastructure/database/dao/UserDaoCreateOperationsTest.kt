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
 * Tests for UserDao create operations.
 */
class UserDaoCreateOperationsTest {
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
    fun shouldInsertUser() = runTest {
        val user = EntityTestFactory.createTestUserEntity(
            id = "user-1",
            name = "Lemmy",
            role = UserRole.CHILD,
            tokenBalance = 25,
        )
        userDao.insertUser(user)
        val retrievedUser = userDao.getUserById("user-1")
        assertNotNull(retrievedUser)
        assertEquals(user.id, retrievedUser.id)
        assertEquals(user.name, retrievedUser.name)
        assertEquals(user.role, retrievedUser.role)
        assertEquals(user.tokenBalance, retrievedUser.tokenBalance)
    }
    // ...other create operation tests...
}
