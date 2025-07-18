package com.lemonqwest.app.infrastructure.database.dao

import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.testutils.DatabaseAssertions
import com.lemonqwest.app.testutils.DatabaseTestBase
import com.lemonqwest.app.testutils.EntityTestFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Comprehensive tests for UserDao database operations.
 *
 * This test class covers all UserDao operations including:
 * - CRUD operations (Create, Read, Update, Delete)
 * - Query operations (by ID, PIN, role)
 * - Edge cases and error handling
 * - Performance and data integrity
 */
class UserDaoTest : DatabaseTestBase() {

    private lateinit var userDao: UserDao

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        userDao = database.userDao()
    }

    @Nested
    @DisplayName("Create Operations")
    inner class CreateOperations {

        @Test
        @DisplayName("Should insert user successfully")
        fun shouldInsertUser() = runTest {
            // Given
            val user = EntityTestFactory.createTestUserEntity(
                id = "user-1",
                name = "Lemmy",
                role = UserRole.CHILD,
                tokenBalance = 25,
            )

            // When
            userDao.insertUser(user)

            // Then
            val retrievedUser = userDao.getUserById("user-1")
            assertNotNull(retrievedUser)
            assertEquals(user.id, retrievedUser.id)
            assertEquals(user.name, retrievedUser.name)
            assertEquals(user.role, retrievedUser.role)
            assertEquals(user.tokenBalance, retrievedUser.tokenBalance)
        }

        @Test
        @DisplayName("Should insert user with PIN hash")
        fun shouldInsertUserWithPin() = runTest {
            // Given
            val user = EntityTestFactory.createTestUserEntity(
                id = "caregiver-1",
                name = "Parent",
                role = UserRole.CAREGIVER,
                pinHash = "hashed-pin-1234",
            )

            // When
            userDao.insertUser(user)

            // Then
            val retrievedUser = userDao.getUserById("caregiver-1")
            assertNotNull(retrievedUser)
            assertEquals("hashed-pin-1234", retrievedUser.pinHash)
        }

        @Test
        @DisplayName("Should replace user on conflict")
        fun shouldReplaceUserOnConflict() = runTest {
            // Given
            val originalUser = EntityTestFactory.createTestUserEntity(
                id = "user-1",
                name = "Original Name",
                tokenBalance = 10,
                role = UserRole.CHILD,
            )
            val updatedUser = EntityTestFactory.createTestUserEntity(
                id = "user-1",
                name = "Updated Name",
                tokenBalance = 20,
                role = UserRole.CHILD,
            )

            // When
            userDao.insertUser(originalUser)
            userDao.insertUser(updatedUser) // Should replace due to OnConflictStrategy.REPLACE

            // Then
            val retrievedUser = userDao.getUserById("user-1")
            assertNotNull(retrievedUser)
            assertEquals("Updated Name", retrievedUser.name)
            assertEquals(20, retrievedUser.tokenBalance)
        }

        @Test
        @DisplayName("Should handle multiple user insertions")
        fun shouldInsertMultipleUsers() = runTest {
            // Given
            val users = listOf(
                EntityTestFactory.createTestUserEntity(id = "user-1", name = "Lemmy"),
                EntityTestFactory.createTestUserEntity(id = "user-2", name = "Emma"),
                EntityTestFactory.createTestUserEntity(
                    id = "caregiver-1",
                    name = "Parent",
                    role = UserRole.CAREGIVER,
                ),
            )

            // When
            users.forEach { userDao.insertUser(it) }

            // Then
            DatabaseAssertions.verifyUserCount(userDao, 3)
            users.forEach { user ->
                DatabaseAssertions.verifyUserExists(userDao, user.id)
            }
        }
    }

    @Nested
    @DisplayName("Read Operations")
    inner class ReadOperations {

        @BeforeEach
        fun setupTestData() = runTest {
            val testUsers = listOf(
                EntityTestFactory.createTestUserEntity(
                    id = "child-1",
                    name = "Lemmy",
                    role = UserRole.CHILD,
                    tokenBalance = 25,
                ),
                EntityTestFactory.createTestUserEntity(
                    id = "child-2",
                    name = "Emma",
                    role = UserRole.CHILD,
                    tokenBalance = 15,
                ),
                EntityTestFactory.createTestUserEntity(
                    id = "caregiver-1",
                    name = "Parent",
                    role = UserRole.CAREGIVER,
                    pinHash = "hashed-pin-1234",
                ),
            )
            testUsers.forEach { userDao.insertUser(it) }
        }

        @Test
        @DisplayName("Should get user by ID")
        fun shouldGetUserById() = runTest {
            // When
            val user = userDao.getUserById("child-1")

            // Then
            assertNotNull(user)
            assertEquals("child-1", user.id)
            assertEquals("Lemmy", user.name)
            assertEquals("CHILD", user.role)
            assertEquals(25, user.tokenBalance)
        }

        @Test
        @DisplayName("Should return null for non-existent user ID")
        fun shouldReturnNullForNonExistentId() = runTest {
            // When
            val user = userDao.getUserById("non-existent")

            // Then
            assertNull(user)
        }

        @Test
        @DisplayName("Should get user by PIN")
        fun shouldGetUserByPin() = runTest {
            // When
            val user = userDao.getUserByPinHash("hashed-pin-1234")

            // Then
            assertNotNull(user)
            assertEquals("caregiver-1", user.id)
            assertEquals("Parent", user.name)
            assertEquals("CAREGIVER", user.role)
        }

        @Test
        @DisplayName("Should return null for non-existent PIN")
        fun shouldReturnNullForNonExistentPin() = runTest {
            // When
            val user = userDao.getUserByPinHash("non-existent-pin")

            // Then
            assertNull(user)
        }

        @Test
        @DisplayName("Should get users by role")
        fun shouldGetUsersByRole() = runTest {
            // When
            val children = userDao.getUsersByRole("CHILD")
            val caregivers = userDao.getUsersByRole("CAREGIVER")

            // Then
            assertEquals(2, children.size)
            assertEquals(1, caregivers.size)
            assertTrue(children.all { it.role == "CHILD" })
            assertTrue(caregivers.all { it.role == "CAREGIVER" })
        }

        @Test
        @DisplayName("Should return empty list for non-existent role")
        fun shouldReturnEmptyListForNonExistentRole() = runTest {
            // When
            val users = userDao.getUsersByRole("ADMIN")

            // Then
            assertTrue(users.isEmpty())
        }

        @Test
        @DisplayName("Should get all users")
        fun shouldGetAllUsers() = runTest {
            // When
            val allUsers = userDao.getAllUsers()

            // Then
            assertEquals(3, allUsers.size)
            val userIds = allUsers.map { it.id }
            assertTrue(userIds.contains("child-1"))
            assertTrue(userIds.contains("child-2"))
            assertTrue(userIds.contains("caregiver-1"))
        }
    }

    @Nested
    @DisplayName("Update Operations")
    inner class UpdateOperations {

        @Test
        @DisplayName("Should update user successfully")
        fun shouldUpdateUser() = runTest {
            // Given
            val originalUser = EntityTestFactory.createTestUserEntity(
                id = "user-1",
                name = "Original Name",
                tokenBalance = 10,
                role = UserRole.CHILD,
            )
            userDao.insertUser(originalUser)

            val updatedUser = originalUser.copy(
                name = "Updated Name",
                tokenBalance = 50,
            )

            // When
            userDao.updateUser(updatedUser)

            // Then
            val retrievedUser = userDao.getUserById("user-1")
            assertNotNull(retrievedUser)
            assertEquals("Updated Name", retrievedUser.name)
            assertEquals(50, retrievedUser.tokenBalance)
        }

        @Test
        @DisplayName("Should update user token balance")
        fun shouldUpdateUserTokenBalance() = runTest {
            // Given
            val user = EntityTestFactory.createTestUserEntity(
                id = "user-1",
                tokenBalance = 10,
            )
            userDao.insertUser(user)

            // When - Simulate earning tokens from task completion
            val updatedUser = user.copy(tokenBalance = user.tokenBalance + 15)
            userDao.updateUser(updatedUser)

            // Then
            DatabaseAssertions.verifyUserTokenBalance(userDao, "user-1", 25)
        }

        @Test
        @DisplayName("Should update user PIN hash")
        fun shouldUpdateUserPinHash() = runTest {
            // Given
            val user = EntityTestFactory.createTestUserEntity(
                id = "caregiver-1",
                role = UserRole.CAREGIVER,
                pinHash = "old-pin-hash",
            )
            userDao.insertUser(user)

            // When
            val updatedUser = user.copy(pinHash = "new-pin-hash")
            userDao.updateUser(updatedUser)

            // Then
            val retrievedUser = userDao.getUserById("caregiver-1")
            assertNotNull(retrievedUser)
            assertEquals("new-pin-hash", retrievedUser.pinHash)
        }

        @Test
        @DisplayName("Should handle multiple concurrent updates")
        fun shouldHandleConcurrentUpdates() = runTest {
            // Given
            val user = EntityTestFactory.createTestUserEntity(
                id = "user-1",
                tokenBalance = 0,
            )
            userDao.insertUser(user)

            // When - Simulate multiple token earnings
            repeat(5) { iteration ->
                val currentUser = userDao.getUserById("user-1")!!
                val updatedUser = currentUser.copy(
                    tokenBalance = currentUser.tokenBalance + (iteration + 1),
                )
                userDao.updateUser(updatedUser)
            }

            // Then
            DatabaseAssertions.verifyUserTokenBalance(userDao, "user-1", 15) // 1+2+3+4+5
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    inner class DeleteOperations {

        @Test
        @DisplayName("Should delete user by ID")
        fun shouldDeleteUserById() = runTest {
            // Given
            val user = EntityTestFactory.createTestUserEntity(id = "user-1")
            userDao.insertUser(user)
            DatabaseAssertions.verifyUserExists(userDao, "user-1")

            // When
            userDao.deleteUser("user-1")

            // Then
            DatabaseAssertions.verifyUserNotExists(userDao, "user-1")
        }

        @Test
        @DisplayName("Should handle deleting non-existent user")
        fun shouldHandleDeletingNonExistentUser() = runTest {
            // When - This should not throw an exception
            userDao.deleteUser("non-existent-user")

            // Then - Database should still be empty
            DatabaseAssertions.verifyUserCount(userDao, 0)
        }

        @Test
        @DisplayName("Should delete only specified user")
        fun shouldDeleteOnlySpecifiedUser() = runTest {
            // Given
            val users = listOf(
                EntityTestFactory.createTestUserEntity(id = "user-1"),
                EntityTestFactory.createTestUserEntity(id = "user-2"),
                EntityTestFactory.createTestUserEntity(id = "user-3"),
            )
            users.forEach { userDao.insertUser(it) }

            // When
            userDao.deleteUser("user-2")

            // Then
            DatabaseAssertions.verifyUserExists(userDao, "user-1")
            DatabaseAssertions.verifyUserNotExists(userDao, "user-2")
            DatabaseAssertions.verifyUserExists(userDao, "user-3")
            DatabaseAssertions.verifyUserCount(userDao, 2)
        }
    }

    @Nested
    @DisplayName("Data Integrity")
    inner class DataIntegrity {

        @Test
        @DisplayName("Should preserve all user fields correctly")
        fun shouldPreserveAllUserFields() = runTest {
            // Given
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

            // When
            userDao.insertUser(user)

            // Then
            val retrievedUser = userDao.getUserById("test-user")
            assertNotNull(retrievedUser)
            assertEquals(user.id, retrievedUser.id)
            assertEquals(user.name, retrievedUser.name)
            assertEquals(user.role, retrievedUser.role)
            assertEquals(user.tokenBalance, retrievedUser.tokenBalance)
            assertEquals(user.pinHash, retrievedUser.pinHash)
            assertEquals(user.createdAt, retrievedUser.createdAt)
        }

        @Test
        @DisplayName("Should handle user with null PIN hash")
        fun shouldHandleNullPinHash() = runTest {
            // Given
            val user = EntityTestFactory.createTestUserEntity(
                id = "child-user",
                role = UserRole.CHILD,
                pinHash = null,
            )

            // When
            userDao.insertUser(user)

            // Then
            val retrievedUser = userDao.getUserById("child-user")
            assertNotNull(retrievedUser)
            assertNull(retrievedUser.pinHash)
        }

        @Test
        @DisplayName("Should handle user with zero token balance")
        fun shouldHandleZeroTokenBalance() = runTest {
            // Given
            val user = EntityTestFactory.createTestUserEntity(
                id = "new-user",
                tokenBalance = 0,
            )

            // When
            userDao.insertUser(user)

            // Then
            DatabaseAssertions.verifyUserTokenBalance(userDao, "new-user", 0)
        }

        @Test
        @DisplayName("Should handle user with large token balance")
        fun shouldHandleLargeTokenBalance() = runTest {
            // Given
            val user = EntityTestFactory.createTestUserEntity(
                id = "rich-user",
                tokenBalance = 999999,
            )

            // When
            userDao.insertUser(user)

            // Then
            DatabaseAssertions.verifyUserTokenBalance(userDao, "rich-user", 999999)
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    inner class PerformanceTests {

        @Test
        @DisplayName("Should handle large number of user insertions efficiently")
        fun shouldHandleManyInsertions() = runTest {
            val startTime = System.currentTimeMillis()

            // Given
            val users = (1..1000).map { index ->
                EntityTestFactory.createTestUserEntity(
                    id = "user-$index",
                    name = "User $index",
                    tokenBalance = index,
                )
            }

            // When
            users.forEach { userDao.insertUser(it) }

            // Then
            val endTime = System.currentTimeMillis()
            val executionTime = endTime - startTime

            DatabaseAssertions.verifyUserCount(userDao, 1000)
            println("Inserted 1000 users in ${executionTime}ms")

            // Verify we can still query efficiently
            val queryStartTime = System.currentTimeMillis()
            val allUsers = userDao.getAllUsers()
            val queryEndTime = System.currentTimeMillis()
            val queryTime = queryEndTime - queryStartTime

            assertEquals(1000, allUsers.size)
            println("Queried 1000 users in ${queryTime}ms")
        }

        @Test
        @DisplayName("Should handle complex queries efficiently")
        fun shouldHandleComplexQueries() = runTest {
            // Given - Create users across different roles
            val children = (1..100).map { index ->
                EntityTestFactory.createTestUserEntity(
                    id = "child-$index",
                    name = "Child $index",
                    role = UserRole.CHILD,
                    tokenBalance = index * 5,
                )
            }
            val caregivers = (1..10).map { index ->
                EntityTestFactory.createTestUserEntity(
                    id = "caregiver-$index",
                    name = "Caregiver $index",
                    role = UserRole.CAREGIVER,
                    pinHash = "pin-hash-$index",
                )
            }

            (children + caregivers).forEach { userDao.insertUser(it) }

            // When - Execute various queries
            val startTime = System.currentTimeMillis()

            val allUsers = userDao.getAllUsers()
            val allChildren = userDao.getUsersByRole("CHILD")
            val allCaregivers = userDao.getUsersByRole("CAREGIVER")
            val specificUser = userDao.getUserById("child-50")
            val pinUser = userDao.getUserByPinHash("pin-hash-5")

            val endTime = System.currentTimeMillis()

            // Then
            assertEquals(110, allUsers.size)
            assertEquals(100, allChildren.size)
            assertEquals(10, allCaregivers.size)
            assertNotNull(specificUser)
            assertNotNull(pinUser)

            println("Executed complex queries in ${endTime - startTime}ms")
        }
    }

    @Nested
    @DisplayName("Domain Conversion")
    inner class DomainConversion {

        @Test
        @DisplayName("Should convert to domain model correctly")
        fun shouldConvertToDomainModel() = runTest {
            // Given
            val userEntity = EntityTestFactory.createTestUserEntity(
                id = "test-user",
                name = "Test User",
                role = UserRole.CHILD,
                tokenBalance = 25,
                pinHash = null,
            )
            userDao.insertUser(userEntity)

            // When
            val retrievedEntity = userDao.getUserById("test-user")!!
            val domainUser = retrievedEntity.toDomain()

            // Then
            assertEquals(userEntity.id, domainUser.id)
            assertEquals(userEntity.name, domainUser.name)
            assertEquals(userEntity.role, domainUser.role.name)
            assertEquals(userEntity.tokenBalance, domainUser.tokenBalance.getValue())
            assertNull(domainUser.pin)
        }

        @Test
        @DisplayName("Should convert from domain model correctly")
        fun shouldConvertFromDomainModel() = runTest {
            // Given
            val domainUser = com.lemonqwest.app.domain.TestDataFactory.createChildUser(
                id = "domain-user",
                name = "Domain User",
                tokenBalance = com.lemonqwest.app.domain.user.TokenBalance.create(30),
            )

            // When
            val userEntity = UserEntity.fromDomain(domainUser)
            userDao.insertUser(userEntity)

            // Then
            val retrievedEntity = userDao.getUserById("domain-user")
            assertNotNull(retrievedEntity)
            assertEquals(domainUser.id, retrievedEntity.id)
            assertEquals(domainUser.name, retrievedEntity.name)
            assertEquals(domainUser.role.name, retrievedEntity.role)
            assertEquals(domainUser.tokenBalance.getValue(), retrievedEntity.tokenBalance)
        }
    }
}
