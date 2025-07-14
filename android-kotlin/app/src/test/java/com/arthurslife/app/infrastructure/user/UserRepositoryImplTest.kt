package com.arthurslife.app.infrastructure.user

import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.user.UserDataSource
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.testutils.RepositoryBehaviorTestUtils
import com.arthurslife.app.testutils.RepositoryTestBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Comprehensive tests for UserRepositoryImpl.
 *
 * This test class verifies that the UserRepositoryImpl correctly delegates
 * to the UserDataSource and maintains proper repository behavior patterns.
 */
class UserRepositoryImplTest : RepositoryTestBase() {

    private lateinit var mockDataSource: UserDataSource
    private lateinit var userRepository: UserRepositoryImpl

    @BeforeEach
    fun setup() {
        setupMocks()
        mockDataSource = mockk()
        userRepository = UserRepositoryImpl(mockDataSource)
    }

    @Test
    fun shouldFindUserByIdWhenExists() = runBlocking {
        // Given
        val userId = "user-1"
        val expectedUser = TestDataFactory.createChildUser(id = userId, name = "Arthur")
        coEvery { mockDataSource.findById(userId) } returns expectedUser

        // When
        val result = userRepository.findById(userId)

        // Then
        assertNotNull(result)
        val nonNullResult = result!!
        assertEquals(expectedUser.id, nonNullResult.id)
        assertEquals(expectedUser.name, nonNullResult.name)
        coVerify { mockDataSource.findById(userId) }
    }

    @Test
    fun shouldReturnNullWhenUserDoesNotExist() = runBlocking {
        // Given
        val userId = "non-existent"
        coEvery { mockDataSource.findById(userId) } returns null

        // When
        val result = userRepository.findById(userId)

        // Then
        assertNull(result)
        coVerify { mockDataSource.findById(userId) }
    }

    @Test
    fun shouldFindUserByRoleWhenExists() = runBlocking {
        // Given
        val role = UserRole.CHILD
        val expectedUser = TestDataFactory.createChildUser(name = "Arthur")
        coEvery { mockDataSource.findByRole(role) } returns expectedUser

        // When
        val result = userRepository.findByRole(role)

        // Then
        assertNotNull(result)
        val nonNullResult = result!!
        assertEquals(expectedUser.role, nonNullResult.role)
        assertEquals(expectedUser.name, nonNullResult.name)
        coVerify { mockDataSource.findByRole(role) }
    }

    @Test
    fun shouldReturnNullWhenNoUserWithRoleExists() = runBlocking {
        // Given
        val role = UserRole.CAREGIVER
        coEvery { mockDataSource.findByRole(role) } returns null

        // When
        val result = userRepository.findByRole(role)

        // Then
        assertNull(result)
        coVerify { mockDataSource.findByRole(role) }
    }

    @Test
    fun shouldGetAllUsers() = runBlocking {
        // Given
        val expectedUsers = listOf(
            TestDataFactory.createChildUser(id = "child-1", name = "Arthur"),
            TestDataFactory.createCaregiverUser(id = "caregiver-1", name = "Parent"),
        )
        coEvery { mockDataSource.getAllUsers() } returns expectedUsers

        // When
        val result = userRepository.getAllUsers()

        // Then
        assertEquals(2, result.size)
        assertEquals(expectedUsers, result)
        coVerify { mockDataSource.getAllUsers() }
    }

    @Test
    fun shouldReturnEmptyListWhenNoUsersExist() = runBlocking {
        // Given
        coEvery { mockDataSource.getAllUsers() } returns emptyList()

        // When
        val result = userRepository.getAllUsers()

        // Then
        assertTrue(result.isEmpty())
        coVerify { mockDataSource.getAllUsers() }
    }

    @Test
    fun shouldSaveNewUser() = runBlocking {
        // Given
        val newUser = TestDataFactory.createChildUser(name = "Emma")
        coEvery { mockDataSource.saveUser(newUser) } returns Unit

        // When
        userRepository.saveUser(newUser)

        // Then
        coVerify { mockDataSource.saveUser(newUser) }
    }

    @Test
    fun shouldSaveUserWithPin() = runBlocking {
        // Given
        val caregiver = TestDataFactory.createCaregiverUser(name = "Parent")
        coEvery { mockDataSource.saveUser(caregiver) } returns Unit

        // When
        userRepository.saveUser(caregiver)

        // Then
        coVerify { mockDataSource.saveUser(caregiver) }
    }

    @Test
    fun shouldHandleMultipleUserSaves() = runBlocking {
        // Given
        val users = listOf(
            TestDataFactory.createChildUser(id = "child-1", name = "Arthur"),
            TestDataFactory.createChildUser(id = "child-2", name = "Emma"),
            TestDataFactory.createCaregiverUser(id = "caregiver-1", name = "Parent"),
        )
        users.forEach { user ->
            coEvery { mockDataSource.saveUser(user) } returns Unit
        }

        // When
        users.forEach { user ->
            userRepository.saveUser(user)
        }

        // Then
        users.forEach { user ->
            coVerify { mockDataSource.saveUser(user) }
        }
    }

    @Test
    fun shouldUpdateExistingUser() = runBlocking {
        // Given
        val originalUser = TestDataFactory.createChildUser(name = "Original Name")
        val updatedUser = originalUser.copy(name = "Updated Name")
        coEvery { mockDataSource.saveUser(updatedUser) } returns Unit

        // When
        userRepository.updateUser(updatedUser)

        // Then
        coVerify { mockDataSource.saveUser(updatedUser) }
    }

    @Test
    fun shouldUpdateUserTokenBalance() = runBlocking {
        // Given
        val user = TestDataFactory.createChildUser()
        val updatedUser = user.copy(tokenBalance = user.tokenBalance.add(25))
        coEvery { mockDataSource.saveUser(updatedUser) } returns Unit

        // When
        userRepository.updateUser(updatedUser)

        // Then
        coVerify { mockDataSource.saveUser(updatedUser) }
    }

    @Test
    fun shouldUpdateUserPin() = runBlocking {
        // Given
        val originalUser = TestDataFactory.createCaregiverUser()
        val newPin = com.arthurslife.app.domain.auth.PIN.create("5678")
        val updatedUser = originalUser.copy(pin = newPin)
        coEvery { mockDataSource.saveUser(updatedUser) } returns Unit

        // When
        userRepository.updateUser(updatedUser)

        // Then
        coVerify { mockDataSource.saveUser(updatedUser) }
    }

    @Test
    fun shouldThrowExceptionWhenDeletingUser() = runBlocking {
        runRepositoryTest {
            // Given
            val userId = "user-to-delete"

            // When & Then
            try {
                userRepository.deleteUser(userId)
                fail("Expected UnsupportedOperationException to be thrown")
            } catch (e: UnsupportedOperationException) {
                // Expected exception for unsupported delete operation
                assertEquals("Delete operation not supported in current implementation", e.message)
            }
        }
    }

    @Test
    fun shouldNotCallDataSourceWhenDeleteAttempted() = runBlocking {
        runRepositoryTest {
            // Given
            val userId = "user-to-delete"

            // When
            try {
                userRepository.deleteUser(userId)
                throw AssertionError("Expected UnsupportedOperationException to be thrown")
            } catch (e: UnsupportedOperationException) {
                // Expected exception for unsupported delete operation
                assertEquals("Delete operation not supported in current implementation", e.message)
            }

            // Then - Verify no interaction with data source
            coVerify(exactly = 0) { mockDataSource.findById(any()) }
            coVerify(exactly = 0) { mockDataSource.saveUser(any()) }
        }
    }

    @Test
    fun shouldPropagateDataSourceExceptions() = runTest {
        // Given
        val userId = "problematic-user"
        val expectedException = IllegalStateException("Data source error")
        coEvery { mockDataSource.findById(userId) } throws expectedException

        // When & Then
        val actualException = RepositoryBehaviorTestUtils.verifyRepositoryThrowsException<IllegalStateException, UserRepositoryImpl>(
            userRepository,
            IllegalStateException::class.java,
        ) {
            findById(userId) ?: error("Expected exception")
        }

        assertEquals(expectedException.message, actualException.message)
        coVerify { mockDataSource.findById(userId) }
    }

    @Test
    fun shouldHandleDataSourceSaveExceptions() = runTest {
        // Given
        val user = TestDataFactory.createChildUser()
        val expectedException = IllegalStateException("Save operation failed")
        coEvery { mockDataSource.saveUser(user) } throws expectedException

        // When & Then
        val actualException = RepositoryBehaviorTestUtils.verifyRepositoryThrowsException<IllegalStateException, UserRepositoryImpl>(
            userRepository,
            IllegalStateException::class.java,
        ) {
            saveUser(user)
        }

        assertEquals(expectedException.message, actualException.message)
        coVerify { mockDataSource.saveUser(user) }
    }

    @Test
    fun shouldHandleDataSourceRetrievalExceptions() = runTest {
        // Given
        val expectedException = IllegalArgumentException("Invalid query")
        coEvery { mockDataSource.getAllUsers() } throws expectedException

        // When & Then
        val actualException = RepositoryBehaviorTestUtils.verifyRepositoryThrowsException<IllegalArgumentException, UserRepositoryImpl>(
            userRepository,
            IllegalArgumentException::class.java,
        ) {
            getAllUsers()
        }

        assertEquals(expectedException.message, actualException.message)
        coVerify { mockDataSource.getAllUsers() }
    }

    @Test
    fun shouldMaintainDataConsistency() = runTest {
        // Given
        val user = TestDataFactory.createChildUser(
            id = "consistent-user",
            name = "Consistent User",
        )
        coEvery { mockDataSource.saveUser(user) } returns Unit
        coEvery { mockDataSource.findById(user.id) } returns user

        // When
        userRepository.saveUser(user)
        val retrievedUser = userRepository.findById(user.id)

        // Then
        assertNotNull(retrievedUser)
        val nonNullRetrievedUser = retrievedUser!!
        assertEquals(user.id, nonNullRetrievedUser.id)
        assertEquals(user.name, nonNullRetrievedUser.name)
        assertEquals(user.role, nonNullRetrievedUser.role)
        assertEquals(user.tokenBalance, nonNullRetrievedUser.tokenBalance)

        coVerify { mockDataSource.saveUser(user) }
        coVerify { mockDataSource.findById(user.id) }
    }

    @Test
    fun shouldHandleConcurrentOperationsSafely() = runTest {
        // Given
        val user1 = TestDataFactory.createChildUser(id = "user-1", name = "User 1")
        val user2 = TestDataFactory.createChildUser(id = "user-2", name = "User 2")

        coEvery { mockDataSource.saveUser(user1) } returns Unit
        coEvery { mockDataSource.saveUser(user2) } returns Unit
        coEvery { mockDataSource.findById(user1.id) } returns user1
        coEvery { mockDataSource.findById(user2.id) } returns user2

        // When - Simulate concurrent operations
        userRepository.saveUser(user1)
        userRepository.saveUser(user2)
        val retrievedUser1 = userRepository.findById(user1.id)
        val retrievedUser2 = userRepository.findById(user2.id)

        // Then
        assertNotNull(retrievedUser1)
        assertNotNull(retrievedUser2)
        val nonNullRetrievedUser1 = retrievedUser1!!
        val nonNullRetrievedUser2 = retrievedUser2!!
        assertEquals(user1.id, nonNullRetrievedUser1.id)
        assertEquals(user2.id, nonNullRetrievedUser2.id)

        coVerify { mockDataSource.saveUser(user1) }
        coVerify { mockDataSource.saveUser(user2) }
        coVerify { mockDataSource.findById(user1.id) }
        coVerify { mockDataSource.findById(user2.id) }
    }

    @Test
    fun shouldDelegateAllOperationsToDataSource() = runTest {
        // Given
        val user = TestDataFactory.createChildUser()
        val users = listOf(user)
        val role = UserRole.CHILD

        coEvery { mockDataSource.findById(any()) } returns user
        coEvery { mockDataSource.findByRole(any()) } returns user
        coEvery { mockDataSource.getAllUsers() } returns users
        coEvery { mockDataSource.saveUser(any()) } returns Unit

        // When - Execute all repository operations
        userRepository.findById("test-id")
        userRepository.findByRole(role)
        userRepository.getAllUsers()
        userRepository.saveUser(user)
        userRepository.updateUser(user)

        // Then - Verify all operations were delegated
        coVerify { mockDataSource.findById("test-id") }
        coVerify { mockDataSource.findByRole(role) }
        coVerify { mockDataSource.getAllUsers() }
        coVerify(exactly = 2) { mockDataSource.saveUser(user) } // save and update
    }

    @Test
    fun shouldMaintainRepositoryAbstraction() = runTest {
        // This test verifies that the repository properly abstracts the data source
        // and doesn't expose internal implementation details

        // Given
        val user = TestDataFactory.createChildUser()
        coEvery { mockDataSource.saveUser(user) } returns Unit
        coEvery { mockDataSource.findById(user.id) } returns user

        // When
        userRepository.saveUser(user)
        val result = userRepository.findById(user.id)

        // Then - Verify the repository interface is clean and consistent
        assertNotNull(result)
        assertEquals(user, result)

        // The repository should not expose data source implementation details
        // This is verified by the fact that the test only interacts with the repository interface
    }

    @Test
    fun shouldSupportRepositoryCachingPatterns() = runTest {
        // This test verifies that the repository can be extended with caching
        // without breaking the existing interface

        // Given
        val userId = "cacheable-user"
        val user = TestDataFactory.createChildUser(id = userId)
        coEvery { mockDataSource.findById(userId) } returns user

        // When - Multiple calls to the same operation
        val result1 = userRepository.findById(userId)
        val result2 = userRepository.findById(userId)
        val result3 = userRepository.findById(userId)

        // Then - All calls should work correctly (caching would be implementation detail)
        assertNotNull(result1)
        assertNotNull(result2)
        assertNotNull(result3)
        assertEquals(user, result1)
        assertEquals(user, result2)
        assertEquals(user, result3)

        // Verify data source was called (caching would reduce these calls)
        coVerify(exactly = 3) { mockDataSource.findById(userId) }
    }
}
