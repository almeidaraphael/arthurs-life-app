package com.arthurslife.app.presentation.viewmodels

/**
 * ViewModel-specific exceptions for presentation layer error handling.
 */

/**
 * Base exception for ViewModel operations.
 */
sealed class ViewModelException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

/**
 * Thrown when user data loading fails.
 */
class UserDataLoadException(
    message: String,
    cause: Throwable? = null,
) : ViewModelException(
    "User data error: $message",
    cause,
)

/**
 * Thrown when task operations fail at the presentation layer.
 */
class TaskOperationException(
    operation: String,
    cause: Throwable? = null,
) : ViewModelException(
    "Task $operation failed: ${cause?.message}",
    cause,
)
