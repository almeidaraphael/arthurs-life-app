package com.arthurslife.app.domain.task.usecase

import com.arthurslife.app.domain.common.DateProvider
import com.arthurslife.app.domain.common.SimpleDate
import com.arthurslife.app.domain.task.Task
import com.arthurslife.app.domain.task.TaskRepository
import javax.inject.Inject

/**
 * Use case for calculating daily progress based on today's tasks.
 *
 * Daily progress is calculated as:
 * (Tasks completed today) / (Tasks assigned for today) * 100
 *
 * If no tasks are assigned for today, progress is 100%.
 */
class CalculateDailyProgressUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val dateProvider: DateProvider,
) {

    /**
     * Calculates daily progress for the specified user.
     *
     * @param userId User ID to calculate progress for
     * @return Progress as a float between 0.0 and 1.0
     */
    suspend operator fun invoke(userId: String): Float {
        val today = dateProvider.getCurrentDate()
        val userTasks = taskRepository.findByUserId(userId)

        // Filter tasks that were assigned today or before today but not completed yet
        val todaysTasks = filterTodaysTasks(userTasks, today)

        if (todaysTasks.isEmpty()) {
            return COMPLETE_PROGRESS
        }

        val completedTodayTasks = todaysTasks.count { it.isCompleted }

        return completedTodayTasks.toFloat() / todaysTasks.size.toFloat()
    }

    /**
     * Gets tasks that are relevant for today's progress calculation.
     *
     * This includes:
     * - Tasks created today
     * - Incomplete tasks created before today (carry-over tasks)
     *
     * @param tasks All tasks for the user
     * @param today Current date
     * @return List of tasks relevant for today's progress
     */
    private fun filterTodaysTasks(tasks: List<Task>, today: SimpleDate): List<Task> {
        return tasks.filter { task ->
            val taskCreatedDate = dateProvider.timestampToDate(task.createdAt)

            when {
                // Tasks created today are always included
                taskCreatedDate == today -> true
                // Incomplete tasks from previous days carry over
                dateProvider.isBefore(taskCreatedDate, today) && !task.isCompleted -> true
                // Completed tasks from previous days are not included
                else -> false
            }
        }
    }

    companion object {
        private const val COMPLETE_PROGRESS = 1.0f
    }
}
