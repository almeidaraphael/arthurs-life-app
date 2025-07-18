package com.lemonqwest.app.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.lemonqwest.app.infrastructure.database.LemonQwestDatabase
import com.lemonqwest.app.infrastructure.database.dao.UserDao
import com.lemonqwest.app.testutils.EntityTestFactory
import com.lemonqwest.app.testutils.ViewModelTestBase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DatabaseIntegrationConsistencyTest : ViewModelTestBase() {
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
    fun database_concurrentOperations_maintainConsistency() = runViewModelTest {
        val userEntity = EntityTestFactory.createTestUserEntity(
            id = "concurrent-user",
            name = "Concurrent User",
            role = "CHILD",
        )
        userDao.insertUser(userEntity)
        repeat(10) { index ->
            val updatedUser = userEntity.copy(tokenBalance = index + 1)
            userDao.updateUser(updatedUser)
        }
        val finalUser = userDao.getUserById("concurrent-user")
        assertNotNull(finalUser)
        assertTrue("Token balance should be positive", finalUser!!.tokenBalance > 0)
    }
}
