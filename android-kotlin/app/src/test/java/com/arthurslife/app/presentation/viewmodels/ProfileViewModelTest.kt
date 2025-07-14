
package com.arthurslife.app.presentation.viewmodels

import app.cash.turbine.test
import com.arthurslife.app.domain.auth.PIN
import com.arthurslife.app.domain.user.TokenBalance
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRepository
import com.arthurslife.app.domain.user.UserRole
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel
    private val mockUserRepository = mockk<UserRepository>()
    private val testDispatcher = StandardTestDispatcher()

    private val testChildUser = User(
        id = "child1",
        name = "Arthur",
        role = UserRole.CHILD,
        tokenBalance = TokenBalance.create(50),
        displayName = "Little Arthur",
        favoriteColor = "Blue",
    )

    private val testCaregiverUser = User(
        id = "caregiver1",
        name = "Parent",
        role = UserRole.CAREGIVER,
        tokenBalance = TokenBalance.zero(),
        pin = PIN.create("1234"),
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProfileViewModel(mockUserRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN user WHEN setSelectedUser is called THEN selectedUser is updated`() = runTest {
        // Given - testChildUser

        // When
        viewModel.setSelectedUser(testChildUser)

        // Then
        viewModel.selectedUser.test {
            val selectedUser = awaitItem()
            assertEquals(testChildUser, selectedUser)
        }
    }

    @Test
    fun `GIVEN valid user ID WHEN loadUserById is called THEN user is loaded`() = runTest {
        // Given
        val userId = "child1"
        coEvery { mockUserRepository.findById(userId) } returns testChildUser

        // When
        viewModel.loadUserById(userId)

        // Then
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectedUser.test {
            val selectedUser = awaitItem()
            assertEquals(testChildUser, selectedUser)
        }

        coVerify { mockUserRepository.findById(userId) }
    }

    @Test
    fun `GIVEN invalid user ID WHEN loadUserById is called THEN error is set`() = runTest {
        // Given
        val userId = "invalid"
        coEvery { mockUserRepository.findById(userId) } returns null

        // When
        viewModel.loadUserById(userId)

        // Then
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.errorMessage.test {
            val error = awaitItem()
            assertEquals("User not found", error)
        }

        viewModel.selectedUser.test {
            val selectedUser = awaitItem()
            assertNull(selectedUser)
        }
    }

    @Test
    fun `GIVEN repository throws exception WHEN loadUserById is called THEN error is set`() = runTest {
        // Given
        val userId = "child1"
        val errorMessage = "Database error"
        coEvery { mockUserRepository.findById(userId) } throws RuntimeException(errorMessage)

        // When
        viewModel.loadUserById(userId)

        // Then
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.errorMessage.test {
            val error = awaitItem()
            assertTrue(error?.contains("Failed to load user") == true)
        }
    }

    @Test
    fun `GIVEN selected user WHEN refreshSelectedUser is called THEN user data is refreshed`() = runTest {
        // Given
        viewModel.setSelectedUser(testChildUser)
        val updatedUser = testChildUser.copy(tokenBalance = TokenBalance.create(75))
        coEvery { mockUserRepository.findById(testChildUser.id) } returns updatedUser

        // When
        viewModel.refreshSelectedUser()

        // Then
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectedUser.test {
            val selectedUser = awaitItem()
            assertEquals(updatedUser, selectedUser)
        }

        coVerify { mockUserRepository.findById(testChildUser.id) }
    }

    @Test
    fun `GIVEN no selected user WHEN refreshSelectedUser is called THEN nothing happens`() = runTest {
        // Given - no selected user

        // When
        viewModel.refreshSelectedUser()

        // Then
        coVerify(exactly = 0) { mockUserRepository.findById(any()) }
    }

    @Test
    fun `GIVEN selected user WHEN clearSelectedUser is called THEN selectedUser is null`() = runTest {
        // Given
        viewModel.setSelectedUser(testChildUser)

        // When
        viewModel.clearSelectedUser()

        // Then
        viewModel.selectedUser.test {
            val selectedUser = awaitItem()
            assertNull(selectedUser)
        }
    }

    @Test
    fun `GIVEN error message WHEN clearError is called THEN errorMessage is null`() = runTest {
        // Given
        coEvery { mockUserRepository.findById(any()) } returns null
        viewModel.loadUserById("invalid")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearError()

        // Then
        viewModel.errorMessage.test {
            val error = awaitItem()
            assertNull(error)
        }
    }

    @Test
    fun `GIVEN user with display name WHEN getDisplayName is called THEN display name is returned`() = runTest {
        // Given
        viewModel.setSelectedUser(testChildUser)

        // When
        val displayName = viewModel.getDisplayName()

        // Then
        assertEquals("Little Arthur", displayName)
    }

    @Test
    fun `GIVEN user without display name WHEN getDisplayName is called THEN name is returned`() = runTest {
        // Given
        val userWithoutDisplayName = testChildUser.copy(displayName = null)
        viewModel.setSelectedUser(userWithoutDisplayName)

        // When
        val displayName = viewModel.getDisplayName()

        // Then
        assertEquals("Arthur", displayName)
    }

    @Test
    fun `GIVEN no selected user WHEN getDisplayName is called THEN null is returned`() = runTest {
        // Given - no selected user

        // When
        val displayName = viewModel.getDisplayName()

        // Then
        assertNull(displayName)
    }

    @Test
    fun `GIVEN user with tokens WHEN getTokenBalance is called THEN token value is returned`() = runTest {
        // Given
        viewModel.setSelectedUser(testChildUser)

        // When
        val tokenBalance = viewModel.getTokenBalance()

        // Then
        assertEquals(50, tokenBalance)
    }

    @Test
    fun `GIVEN no selected user WHEN getTokenBalance is called THEN null is returned`() = runTest {
        // Given - no selected user

        // When
        val tokenBalance = viewModel.getTokenBalance()

        // Then
        assertNull(tokenBalance)
    }

    @Test
    fun `GIVEN user with PIN WHEN hasPin is called THEN true is returned`() = runTest {
        // Given
        viewModel.setSelectedUser(testCaregiverUser)

        // When
        val hasPin = viewModel.hasPin()

        // Then
        assertTrue(hasPin)
    }

    @Test
    fun `GIVEN user without PIN WHEN hasPin is called THEN false is returned`() = runTest {
        // Given
        viewModel.setSelectedUser(testChildUser)

        // When
        val hasPin = viewModel.hasPin()

        // Then
        assertFalse(hasPin)
    }

    @Test
    fun `GIVEN loading state WHEN user is being fetched THEN isLoading is true`() = runTest {
        coEvery { mockUserRepository.findById(any()) } coAnswers {
            kotlinx.coroutines.delay(100)
            testChildUser
        }
        viewModel.loadUserById("child1")
        testDispatcher.scheduler.advanceTimeBy(1)
        viewModel.isLoading.test {
            val isLoading = awaitItem()
            assertTrue(isLoading)
        }
    }

    @Test
    fun `GIVEN successful load WHEN user is fetched THEN isLoading becomes false`() = runTest {
        // Given
        coEvery { mockUserRepository.findById(any()) } returns testChildUser

        // When
        viewModel.loadUserById("child1")

        // Then
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.isLoading.test {
            val isLoading = awaitItem()
            assertFalse(isLoading)
        }
    }
}
