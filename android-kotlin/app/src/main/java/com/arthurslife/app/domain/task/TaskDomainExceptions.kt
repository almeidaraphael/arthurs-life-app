package com.arthurslife.app.domain.task

/**
 * Domain-specific exceptions for task operations.
 */

/**
 * Thrown when a task operation fails.
 */
sealed class TaskException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Thrown when a task is not found.
 */
class TaskNotFoundException(val taskId: String) : TaskException("Task not found: $taskId")

/**
 * Thrown when attempting to operate on an already completed task.
 */
class TaskAlreadyCompletedException(
    val taskId: String,
) : TaskException("Task is already completed: $taskId")

/**
 * Thrown when a task operation fails due to invalid data.
 */
class InvalidTaskDataException(message: String) : TaskException("Invalid task data: $message")

/**
 * Thrown when a task repository operation fails.
 */
class TaskRepositoryException(
    message: String,
    cause: Throwable? = null,
) : TaskException("Repository error: $message", cause)

/**
 * Thrown when attempting to undo a task that is not completed.
 */
class TaskNotCompletedException(
    val taskId: String,
) : TaskException("Task is not completed, cannot undo: $taskId")

/**
 * Thrown when user associated with task is not found.
 */
class TaskUserNotFoundException(val userId: String) : TaskException(
    "User not found for task: $userId",
)
