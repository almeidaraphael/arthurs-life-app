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
import kotlin.test.assertNull

/**
 * Domain conversion tests for UserDao.
 */
class UserDaoDomainConversionTest {
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
    fun shouldConvertToDomainModel() = runTest {
        val userEntity = EntityTestFactory.createTestUserEntity(
            id = "test-user",
            name = "Test User",
            role = UserRole.CHILD,
            tokenBalance = 25,
            pinHash = null,
        )
        userDao.insertUser(userEntity)
        val retrievedEntity = userDao.getUserById("test-user")!!
        val domainUser = retrievedEntity.toDomain()
        assertEquals(userEntity.id, domainUser.id)
        assertEquals(userEntity.name, domainUser.name)
        assertEquals(userEntity.role, domainUser.role.name)
        assertEquals(userEntity.tokenBalance, domainUser.tokenBalance.getValue())
        assertNull(domainUser.pin)
    }
    // ...other domain conversion tests...
}
