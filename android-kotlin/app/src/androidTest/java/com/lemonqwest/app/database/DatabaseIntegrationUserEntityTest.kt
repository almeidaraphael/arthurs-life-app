package com.lemonqwest.app.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.lemonqwest.app.infrastructure.database.LemonQwestDatabase
import com.lemonqwest.app.infrastructure.database.dao.UserDao
import com.lemonqwest.app.testutils.EntityTestFactory
import com.lemonqwest.app.testutils.ViewModelTestBase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DatabaseIntegrationUserEntityTest : ViewModelTestBase() {
    private lateinit var database: LemonQwestDatabase
    private lateinit var userDao: UserDao

    @BeforeEach
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LemonQwestDatabase::class.java,
        ).allowMainThreadQueries().build()
        userDao = database.userDao()
    }

    @AfterEach
    fun teardown() {
        database.close()
    }

    @Test
    fun insertUser_retrieveUser_returnsCorrectUser() = runViewModelTest {
        val userEntity = EntityTestFactory.createTestUserEntity(
            id = "test-user-1",
            name = "Test User",
            role = "CHILD",
        )
        userDao.insertUser(userEntity)
        val retrievedUser = userDao.getUserById("test-user-1")
        assertNotNull(retrievedUser)
        assertEquals(userEntity.id, retrievedUser?.id)
        assertEquals(userEntity.name, retrievedUser?.name)
        assertEquals(userEntity.role, retrievedUser?.role)
        assertEquals(userEntity.tokenBalance, retrievedUser?.tokenBalance)
    }

    @Test
    fun updateUser_tokenBalance_updatesCorrectly() = runViewModelTest {
        val userEntity = EntityTestFactory.createTestUserEntity(
            id = "test-user-2",
            name = "Test User 2",
            role = "CHILD",
        ).copy(tokenBalance = 10)
        userDao.insertUser(userEntity)
        val updatedUser = userEntity.copy(tokenBalance = 20)
        userDao.updateUser(updatedUser)
        val retrievedUser = userDao.getUserById("test-user-2")
        assertNotNull(retrievedUser)
        assertEquals(20, retrievedUser?.tokenBalance)
    }

    @Test
    fun deleteUser_removesUser() = runViewModelTest {
        val userEntity = EntityTestFactory.createTestUserEntity(
            id = "test-user-4",
            name = "Test User 4",
            role = "CHILD",
        )
        userDao.insertUser(userEntity)
        userDao.deleteUser("test-user-4")
        val retrievedUser = userDao.getUserById("test-user-4")
        assertNull(retrievedUser)
    }
}
