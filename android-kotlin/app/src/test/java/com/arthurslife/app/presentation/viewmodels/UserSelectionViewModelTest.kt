package com.arthurslife.app.presentation.viewmodels

import app.cash.turbine.test
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class UserSelectionViewModelTest {

    private lateinit var viewModel: UserSelectionViewModel
    private val mockUserRepository = mockk<UserRepository>()
    private val testDispatcher = StandardTestDispatcher()

    private val testUsers = listOf(
        User(
            id = "user1",
            name = "Arthur",
            role = UserRole.CHILD,
            tokenBalance = TokenBalance.create(50),
        ),
        User(
            id = "user2",
            name = "Parent",
            role = UserRole.CAREGIVER,
            tokenBalance = TokenBalance.zero(),
        ),
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { mockUserRepository.getAllUsers() } returns testUsers
        viewModel = UserSelectionViewModel(mockUserRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN repository returns users WHEN viewModel is initialized THEN users are loaded`() = runTest {
        // Given - setup in @Before

        // When - initialization happens in constructor
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val users = viewModel.users.first()
        assertEquals(testUsers, users)
    }

    @Test
    fun `GIVEN repository throws exception WHEN loading users THEN error is set`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { mockUserRepository.getAllUsers() } throws RuntimeException(errorMessage)

        // When
        val viewModel = UserSelectionViewModel(mockUserRepository)

        // Then
        testDispatcher.scheduler.advanceUntilIdle()

        val error = viewModel.errorMessage.first()
        assertTrue(error?.contains("Failed to load users") == true)

        val users = viewModel.users.first()
        assertTrue(users.isEmpty())
    }

    @Test
    fun `GIVEN user WHEN selectUser is called THEN selectedUser is updated`() = runTest {
        // Given
        val userToSelect = testUsers[0]

        // When
        viewModel.selectUser(userToSelect)

        // Then
        val selectedUser = viewModel.selectedUser.first()
        assertEquals(userToSelect, selectedUser)
    }

    @Test
    fun `GIVEN selected user WHEN clearSelection is called THEN selectedUser is null`() = runTest {
        // Given
        viewModel.selectUser(testUsers[0])

        // When
        viewModel.clearSelection()

        // Then
        val selectedUser = viewModel.selectedUser.first()
        assertNull(selectedUser)
    }

    @Test
    fun `GIVEN error message WHEN clearError is called THEN errorMessage is null`() = runTest {
        // Given
        coEvery { mockUserRepository.getAllUsers() } throws RuntimeException("Test error")
        val viewModel = UserSelectionViewModel(mockUserRepository)

        // When
        viewModel.clearError()

        // Then
        val error = viewModel.errorMessage.first()
        assertNull(error)
    }

    @Test
    fun `WHEN refreshUsers is called THEN repository is called again`() = runTest {
        // Given - initial load happens in constructor
        testDispatcher.scheduler.advanceUntilIdle()
        // When
        viewModel.refreshUsers()
        testDispatcher.scheduler.advanceUntilIdle()
        // Then
        coVerify(exactly = 2) { mockUserRepository.getAllUsers() }
    }

    @Test
    fun `GIVEN loading state WHEN users are being fetched THEN isLoading is true`() = runTest {
        coEvery { mockUserRepository.getAllUsers() } coAnswers {
            kotlinx.coroutines.delay(100)
            testUsers
        }
        val viewModel = UserSelectionViewModel(mockUserRepository)
        testDispatcher.scheduler.advanceTimeBy(1)
        viewModel.isLoading.test {
            val isLoading = awaitItem()
            assertTrue(isLoading)
        }
    }

    @Test
    fun `GIVEN successful load WHEN users are fetched THEN isLoading becomes false`() = runTest {
        // Given - setup in @Before with immediate return

        // When - initialization happens in constructor

        // Then
        testDispatcher.scheduler.advanceUntilIdle()

        val isLoading = viewModel.isLoading.first()
        assertEquals(false, isLoading)
    }

    @Test
    fun `GIVEN multiple caregivers WHEN users are loaded THEN all caregivers are available for selection`() = runTest {
        // Given
        val multipleUsers = listOf(
            User(
                id = "child1",
                name = "Arthur",
                role = UserRole.CHILD,
                tokenBalance = TokenBalance.create(50),
            ),
            User(
                id = "caregiver1",
                name = "Parent 1",
                role = UserRole.CAREGIVER,
                tokenBalance = TokenBalance.zero(),
            ),
            User(
                id = "caregiver2",
                name = "Parent 2",
                role = UserRole.CAREGIVER,
                tokenBalance = TokenBalance.zero(),
            ),
        )
        coEvery { mockUserRepository.getAllUsers() } returns multipleUsers
        val viewModel = UserSelectionViewModel(mockUserRepository)

        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val users = viewModel.users.first()
        assertEquals(multipleUsers, users)
        assertEquals(2, users.filter { it.role == UserRole.CAREGIVER }.size)
    }

    @Test
    fun `GIVEN multiple children WHEN users are loaded THEN all children are available for selection`() = runTest {
        // Given
        val multipleUsers = listOf(
            User(
                id = "child1",
                name = "Arthur",
                role = UserRole.CHILD,
                tokenBalance = TokenBalance.create(50),
            ),
            User(
                id = "child2",
                name = "Emma",
                role = UserRole.CHILD,
                tokenBalance = TokenBalance.create(30),
            ),
            User(
                id = "caregiver1",
                name = "Parent",
                role = UserRole.CAREGIVER,
                tokenBalance = TokenBalance.zero(),
            ),
        )
        coEvery { mockUserRepository.getAllUsers() } returns multipleUsers
        val viewModel = UserSelectionViewModel(mockUserRepository)

        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val users = viewModel.users.first()
        assertEquals(multipleUsers, users)
        assertEquals(2, users.filter { it.role == UserRole.CHILD }.size)
    }
}
