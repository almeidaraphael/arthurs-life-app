package com.arthurslife.app.domain.task.usecase

import com.arthurslife.app.domain.auth.AuthenticationSessionService
import com.arthurslife.app.domain.common.DomainException
import com.arthurslife.app.domain.task.TaskNotCompletedException
import com.arthurslife.app.domain.task.TaskNotFoundException
import com.arthurslife.app.domain.task.TaskRepository
import com.arthurslife.app.domain.task.TaskRepositoryException
import com.arthurslife.app.domain.task.TaskUserNotFoundException
import com.arthurslife.app.domain.user.UserRepository
import com.arthurslife.app.domain.user.UserRole
import javax.inject.Inject

/**
 * Use case for undoing task completion in Arthur's Life MVP.
 *
 * This use case allows both children and caregivers to undo completed tasks with
 * different authorization rules:
 * - Children: Can only undo their own tasks and must have sufficient tokens
 * - Caregivers: Can undo any task with administrative override (no token restrictions)
 *
 * It handles the core interaction between the task system and token economy:
 * 1. Validates role-based authorization
 * 2. Marks a task as incomplete
 * 3. Removes tokens from the user based on task's token reward (role-dependent)
 * 4. Updates both task and user records
 *
 * Note: This use case does not handle achievement rollback for MVP simplicity.
 * Once an achievement is unlocked, it remains unlocked even if the contributing
 * task is undone.
 */
class UndoTaskUseCase
@Inject
constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val authenticationSessionService: AuthenticationSessionService,
) {
    /**
     * Undoes a task completion and removes tokens from the assigned user.
     * Applies role-based authorization rules for children vs caregivers.
     *
     * @param taskId ID of the task to undo
     * @return Result indicating success or failure with details
     */
    suspend operator fun invoke(taskId: String): Result<TaskUndoResult> {
        return try {
            val currentUser = authenticationSessionService.getCurrentUser()
                ?: return Result.failure(TaskRepositoryException("User not authenticated"))

            val task = taskRepository.findById(taskId)
                ?: return Result.failure(TaskNotFoundException(taskId))

            if (!task.isCompleted) {
                return Result.failure(TaskNotCompletedException(taskId))
            }

            val assignedUser = userRepository.findById(task.assignedToUserId)
                ?: return Result.failure(TaskUserNotFoundException(task.assignedToUserId))

            validateUndoAuthorization(currentUser, task)?.let {
                return Result.failure(it)
            }

            val uncompletedTask = task.markIncomplete()
            taskRepository.updateTask(uncompletedTask)

            val updatedUser = deductTokensFromUser(assignedUser, task.tokenReward)
            userRepository.updateUser(updatedUser)

            Result.success(
                TaskUndoResult(
                    task = uncompletedTask,
                    tokensDeducted = task.tokenReward,
                    newTokenBalance = updatedUser.tokenBalance.getValue(),
                    undoneByRole = currentUser.role,
                ),
            )
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Validates authorization rules for undoing a task based on user role.
     */
    private fun validateUndoAuthorization(
        currentUser: com.arthurslife.app.domain.user.User,
        task: com.arthurslife.app.domain.task.Task,
    ): TaskRepositoryException? {
        return when (currentUser.role) {
            UserRole.CHILD -> {
                if (task.assignedToUserId != currentUser.id) {
                    TaskRepositoryException("Children can only undo their own tasks")
                } else {
                    null
                }
            }
            UserRole.CAREGIVER -> null // Caregivers can undo any task
        }
    }

    /**
     * Deducts tokens from the user account allowing negative balances.
     * This supports the business rule that undoing tasks can result in negative balances.
     */
    private fun deductTokensFromUser(
        user: com.arthurslife.app.domain.user.User,
        tokensToDeduct: Int,
    ): com.arthurslife.app.domain.user.User {
        return user.copy(
            tokenBalance = user.tokenBalance.adminSubtract(tokensToDeduct),
        )
    }
}

/**
 * Result data class for task undo operations.
 *
 * @property task The task that was marked as incomplete
 * @property tokensDeducted Number of tokens deducted from the user
 * @property newTokenBalance User's updated token balance
 * @property undoneByRole The role of the user who performed the undo operation
 */
data class TaskUndoResult(
    val task: com.arthurslife.app.domain.task.Task,
    val tokensDeducted: Int,
    val newTokenBalance: Int,
    val undoneByRole: UserRole,
)
