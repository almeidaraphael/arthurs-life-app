package com.arthurslife.app.domain.task.usecase

import com.arthurslife.app.domain.auth.AuthenticationSessionService
import com.arthurslife.app.domain.auth.PIN
import com.arthurslife.app.domain.task.Task
import com.arthurslife.app.domain.task.TaskCategory
import com.arthurslife.app.domain.task.TaskNotCompletedException
import com.arthurslife.app.domain.task.TaskNotFoundException
import com.arthurslife.app.domain.task.TaskRepository
import com.arthurslife.app.domain.task.TaskRepositoryException
import com.arthurslife.app.domain.task.TaskUserNotFoundException
import com.arthurslife.app.domain.user.TokenBalance
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRepository
import com.arthurslife.app.domain.user.UserRole
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UndoTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var userRepository: UserRepository
    private lateinit var authenticationSessionService: AuthenticationSessionService
    private lateinit var undoTaskUseCase: UndoTaskUseCase

    private val childUserId = "child123"
    private val caregiverUserId = "caregiver456"
    private val taskId = "task789"

    private val childUser = User(
        id = childUserId,
        name = "Test Child",
        role = UserRole.CHILD,
        pin = null, // Children don't have PINs
        tokenBalance = TokenBalance.create(25),
    )

    private val caregiverUser = User(
        id = caregiverUserId,
        name = "Test Caregiver",
        role = UserRole.CAREGIVER,
        pin = PIN.create("5678"),
        tokenBalance = TokenBalance.create(100),
    )

    private val completedTask = Task(
        id = taskId,
        title = "Test Task",
        category = TaskCategory.HOUSEHOLD,
        assignedToUserId = childUserId,
        tokenReward = 10,
        isCompleted = true,
    )

    private val incompleteTask = Task(
        id = taskId,
        title = "Test Task",
        category = TaskCategory.HOUSEHOLD,
        assignedToUserId = childUserId,
        tokenReward = 10,
        isCompleted = false,
    )

    @BeforeEach
    fun setup() {
        taskRepository = mockk()
        userRepository = mockk()
        authenticationSessionService = mockk()
        undoTaskUseCase = UndoTaskUseCase(
            taskRepository = taskRepository,
            userRepository = userRepository,
            authenticationSessionService = authenticationSessionService,
        )
    }

    @Test
    fun `child can undo their own completed task with sufficient balance`() = runTest {
        // Arrange
        coEvery { authenticationSessionService.getCurrentUser() } returns childUser
        coEvery { taskRepository.findById(taskId) } returns completedTask
        coEvery { userRepository.findById(childUserId) } returns childUser
        coEvery { taskRepository.updateTask(any()) } returns Unit
        coEvery { userRepository.updateUser(any()) } returns Unit

        // Act
        val result = undoTaskUseCase(taskId)

        // Assert
        assertTrue(result.isSuccess)
        val undoResult = result.getOrNull()!!
        assertEquals(taskId, undoResult.task.id)
        assertEquals(10, undoResult.tokensDeducted)
        assertEquals(15, undoResult.newTokenBalance) // 25 - 10 = 15
        assertEquals(UserRole.CHILD, undoResult.undoneByRole)

        coVerify { taskRepository.updateTask(any()) }
        coVerify { userRepository.updateUser(any()) }
    }

    @Test
    fun `child can undo their own task even with insufficient balance`() = runTest {
        // Arrange - child has only 5 tokens but task rewards 10
        val childWithLowBalance = childUser.copy(tokenBalance = TokenBalance.create(5))
        coEvery { authenticationSessionService.getCurrentUser() } returns childWithLowBalance
        coEvery { taskRepository.findById(taskId) } returns completedTask
        coEvery { userRepository.findById(childUserId) } returns childWithLowBalance
        coEvery { taskRepository.updateTask(any()) } returns Unit
        coEvery { userRepository.updateUser(any()) } returns Unit

        // Act
        val result = undoTaskUseCase(taskId)

        // Assert
        assertTrue(result.isSuccess)
        val undoResult = result.getOrNull()!!
        assertEquals(-5, undoResult.newTokenBalance) // 5 - 10 = -5 (negative balance allowed)
        assertEquals(UserRole.CHILD, undoResult.undoneByRole)
    }

    @Test
    fun `child cannot undo another child's task`() = runTest {
        // Arrange
        val otherChildUser = User(
            id = "otherChild",
            name = "Other Child",
            role = UserRole.CHILD,
            pin = null,
            tokenBalance = TokenBalance.create(15),
        )
        val otherChildTask = completedTask.copy(assignedToUserId = "otherChild")
        coEvery { authenticationSessionService.getCurrentUser() } returns childUser
        coEvery { taskRepository.findById(taskId) } returns otherChildTask
        coEvery { userRepository.findById("otherChild") } returns otherChildUser

        // Act
        val result = undoTaskUseCase(taskId)

        // Assert
        assertTrue(
            result.isFailure,
            "Expected operation to fail when child tries to undo another child's task",
        )
        assertTrue(result.exceptionOrNull() is TaskRepositoryException)
        assertEquals(
            "Repository error: Children can only undo their own tasks",
            result.exceptionOrNull()?.message,
        )

        coVerify(exactly = 0) { taskRepository.updateTask(any()) }
        coVerify(exactly = 0) { userRepository.updateUser(any()) }
    }

    @Test
    fun `caregiver can undo any child's task`() = runTest {
        // Arrange
        coEvery { authenticationSessionService.getCurrentUser() } returns caregiverUser
        coEvery { taskRepository.findById(taskId) } returns completedTask
        coEvery { userRepository.findById(childUserId) } returns childUser
        coEvery { taskRepository.updateTask(any()) } returns Unit
        coEvery { userRepository.updateUser(any()) } returns Unit

        // Act
        val result = undoTaskUseCase(taskId)

        // Assert
        assertTrue(result.isSuccess)
        val undoResult = result.getOrNull()!!
        assertEquals(taskId, undoResult.task.id)
        assertEquals(10, undoResult.tokensDeducted)
        assertEquals(15, undoResult.newTokenBalance) // Child's balance: 25 - 10 = 15
        assertEquals(UserRole.CAREGIVER, undoResult.undoneByRole)
    }

    @Test
    fun `caregiver can undo task even when child has insufficient balance`() = runTest {
        // Arrange
        val childWithLowBalance = childUser.copy(tokenBalance = TokenBalance.create(3))
        coEvery { authenticationSessionService.getCurrentUser() } returns caregiverUser
        coEvery { taskRepository.findById(taskId) } returns completedTask
        coEvery { userRepository.findById(childUserId) } returns childWithLowBalance
        coEvery { taskRepository.updateTask(any()) } returns Unit
        coEvery { userRepository.updateUser(any()) } returns Unit

        // Act
        val result = undoTaskUseCase(taskId)

        // Assert
        assertTrue(result.isSuccess)
        val undoResult = result.getOrNull()!!
        assertEquals(-7, undoResult.newTokenBalance) // 3 - 10 = -7 (negative balance allowed)
        assertEquals(UserRole.CAREGIVER, undoResult.undoneByRole)
    }

    @Test
    fun `undo fails when user is not authenticated`() = runTest {
        // Arrange
        coEvery { authenticationSessionService.getCurrentUser() } returns null

        // Act
        val result = undoTaskUseCase(taskId)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is TaskRepositoryException)
        assertEquals("Repository error: User not authenticated", result.exceptionOrNull()?.message)
    }

    @Test
    fun `undo fails when task is not found`() = runTest {
        // Arrange
        coEvery { authenticationSessionService.getCurrentUser() } returns childUser
        coEvery { taskRepository.findById(taskId) } returns null

        // Act
        val result = undoTaskUseCase(taskId)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is TaskNotFoundException)
    }

    @Test
    fun `undo fails when task is not completed`() = runTest {
        // Arrange
        coEvery { authenticationSessionService.getCurrentUser() } returns childUser
        coEvery { taskRepository.findById(taskId) } returns incompleteTask

        // Act
        val result = undoTaskUseCase(taskId)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is TaskNotCompletedException)
    }

    @Test
    fun `undo fails when assigned user is not found`() = runTest {
        // Arrange
        coEvery { authenticationSessionService.getCurrentUser() } returns childUser
        coEvery { taskRepository.findById(taskId) } returns completedTask
        coEvery { userRepository.findById(childUserId) } returns null

        // Act
        val result = undoTaskUseCase(taskId)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is TaskUserNotFoundException)
    }
}
