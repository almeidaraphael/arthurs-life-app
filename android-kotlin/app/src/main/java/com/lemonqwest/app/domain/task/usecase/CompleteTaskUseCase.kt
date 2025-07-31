package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.common.DomainException
import com.lemonqwest.app.domain.task.TaskAlreadyCompletedException
import com.lemonqwest.app.domain.task.TaskNotFoundException
import com.lemonqwest.app.domain.task.TaskRepository
import com.lemonqwest.app.domain.task.TaskRepositoryException
import com.lemonqwest.app.domain.task.TaskUserNotFoundException
import com.lemonqwest.app.domain.user.UserRepository
import javax.inject.Inject

/**
 * Use case for completing tasks and awarding tokens in LemonQwest MVP.
 *
 * This use case handles the core interaction between the task system and token economy:
 * 1. Marks a task as completed
 * 2. Awards tokens to the user based on task's token reward
 * 3. Updates both task and user records
 *
 * The use case ensures data consistency by updating both the task completion status
 * and the user's token balance in a coordinated manner.
 */
class CompleteTaskUseCase
@Inject
constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val achievementTrackingUseCase: AchievementTrackingUseCase,
) {
    /**
     * Completes a task and awards tokens to the assigned user.
     *
     * @param taskId ID of the task to complete
     * @return Result indicating success or failure with details
     */
    suspend operator fun invoke(taskId: String): Result<TaskCompletionResult> {
        return try {
            // Find the task to complete
            val task = taskRepository.findById(taskId)
                ?: return Result.failure(TaskNotFoundException(taskId))

            // Check if task is already completed
            if (task.isCompleted) {
                return Result.failure(TaskAlreadyCompletedException(taskId))
            }

            // Find the assigned user
            val user = userRepository.findById(task.assignedToUserId)
                ?: return Result.failure(TaskUserNotFoundException(task.assignedToUserId))

            // Mark task as completed
            val completedTask = task.markCompleted()
            taskRepository.updateTask(completedTask)

            // Award tokens to user
            val updatedUser = user.copy(
                tokenBalance = user.tokenBalance.add(task.tokenReward),
            )
            userRepository.updateUser(updatedUser)

            // Update achievements after task completion
            val newlyUnlockedAchievements = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(
                task.assignedToUserId,
            )

            Result.success(
                TaskCompletionResult(
                    task = completedTask,
                    tokensAwarded = task.tokenReward,
                    newTokenBalance = updatedUser.tokenBalance.getValue(),
                    newlyUnlockedAchievements = newlyUnlockedAchievements,
                ),
            )
        } catch (e: TaskNotFoundException) {
            Result.failure(e)
        } catch (e: TaskAlreadyCompletedException) {
            Result.failure(e)
        } catch (e: TaskUserNotFoundException) {
            Result.failure(e)
        } catch (e: TaskRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }
}

/**
 * Result data class for task completion operations.
 *
 * @property task The completed task
 * @property tokensAwarded Number of tokens awarded for completion
 * @property newTokenBalance User's updated token balance
 * @property newlyUnlockedAchievements List of achievements unlocked by this task completion
 */
data class TaskCompletionResult(
    val task: com.lemonqwest.app.domain.task.Task,
    val tokensAwarded: Int,
    val newTokenBalance: Int,
    val newlyUnlockedAchievements: List<com.lemonqwest.app.domain.achievement.Achievement> = emptyList(),
)
