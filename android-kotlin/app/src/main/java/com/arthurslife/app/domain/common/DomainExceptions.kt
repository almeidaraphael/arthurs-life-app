package com.arthurslife.app.domain.common

/**
 * Simple domain exception pattern to avoid generic exception catching.
 *
 * This provides a minimal solution to satisfy detekt while maintaining
 * simplicity and avoiding over-engineering.
 */

/**
 * Base exception for all domain operations.
 */
sealed class DomainException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {
    /**
     * Public constructor for testing purposes.
     */
    constructor(message: String) : this(message, null)
}

/**
 * Exception for task domain operations.
 */
class TaskDomainException(
    message: String,
    cause: Throwable? = null,
) : DomainException(message, cause)

/**
 * Exception for user domain operations.
 */
class UserDomainException(
    message: String,
    cause: Throwable? = null,
) : DomainException(message, cause)

/**
 * Exception for authentication domain operations.
 */
class AuthDomainException(
    message: String,
    cause: Throwable? = null,
) : DomainException(message, cause)

/**
 * Exception for presentation layer operations.
 */
class PresentationException(
    message: String,
    cause: Throwable? = null,
) : DomainException(message, cause)
