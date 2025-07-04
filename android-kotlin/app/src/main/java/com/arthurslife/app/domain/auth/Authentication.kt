package com.arthurslife.app.domain.auth

import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRole

/**
 * Represents the current authentication state of the application.
 *
 * This data class tracks which user is currently active, their authentication status,
 * and their current role context. The authentication system supports role switching,
 * allowing caregivers to temporarily operate as children for demonstration purposes.
 *
 * @property currentUser The currently active user, null if no user is authenticated
 * @property isAuthenticated Whether a valid authentication session exists
 * @property currentRole The role context the user is operating under, may differ from user's primary role
 *
 * @sample
 * ```kotlin
 * // Unauthenticated state
 * val initial = AuthenticationState()
 *
 * // Authenticated child user
 * val childAuth = AuthenticationState(
 *     currentUser = childUser,
 *     isAuthenticated = true,
 *     currentRole = UserRole.CHILD
 * )
 *
 * // Caregiver operating in child role for demonstration
 * val demoMode = AuthenticationState(
 *     currentUser = caregiverUser,
 *     isAuthenticated = true,
 *     currentRole = UserRole.CHILD
 * )
 * ```
 */
data class AuthenticationState(
    val currentUser: User? = null,
    val isAuthenticated: Boolean = false,
    val currentRole: UserRole? = null,
)

/**
 * Sealed class hierarchy representing the possible outcomes of authentication operations.
 *
 * This provides type-safe handling of authentication results, ensuring all possible
 * outcomes are explicitly handled by calling code. The sealed nature prevents
 * external creation of result types, maintaining API integrity.
 */
sealed class AuthResult {
    /**
     * Successful authentication result.
     *
     * Contains the authenticated user and indicates the authentication operation
     * completed successfully. The user object includes all necessary information
     * for establishing the session context.
     *
     * @property user The successfully authenticated user
     */
    data class Success(
        val user: User,
    ) : AuthResult()

    /**
     * Authentication failed due to invalid PIN.
     *
     * Indicates the provided PIN does not match any user's stored PIN.
     * This should trigger appropriate error messaging and potentially
     * implement rate limiting for security.
     */
    object InvalidPin : AuthResult()

    /**
     * Authentication failed because the user was not found.
     *
     * This can occur when the authentication system references a user
     * that no longer exists in the system, typically during edge cases
     * or data consistency issues.
     */
    object UserNotFound : AuthResult()
}

/**
 * Repository interface defining authentication operations for Arthur's Life application.
 *
 * This interface abstracts the authentication data layer, allowing different
 * implementations (local storage, remote authentication, etc.) while maintaining
 * consistent business logic. All operations are suspending functions to support
 * both local database operations and potential network authentication.
 *
 * The repository handles:
 * - PIN-based authentication for role switching
 * - Session management and user retrieval
 * - Secure logout operations
 *
 * @sample
 * ```kotlin
 * class AuthenticationService(private val authRepo: AuthRepository) {
 *     suspend fun signIn(pin: String): AuthResult {
 *         return authRepo.authenticateWithPin(pin)
 *     }
 * }
 * ```
 */
interface AuthRepository {
    /**
     * Authenticates a user using their PIN.
     *
     * This method implements the core authentication logic for Arthur's Life,
     * allowing users to access the application or switch roles using their
     * configured PIN. The PIN system provides child-safe authentication without
     * requiring complex passwords.
     *
     * @param pin The PIN string to authenticate against
     * @return [AuthResult.Success] with user data if PIN is valid,
     *         [AuthResult.InvalidPin] if PIN doesn't match,
     *         [AuthResult.UserNotFound] if referenced user doesn't exist
     *
     * @sample
     * ```kotlin
     * when (val result = authRepo.authenticateWithPin("1234")) {
     *     is AuthResult.Success -> startSession(result.user)
     *     is AuthResult.InvalidPin -> showPinError()
     *     is AuthResult.UserNotFound -> handleDataInconsistency()
     * }
     * ```
     */
    suspend fun authenticateWithPin(pin: String): AuthResult

    /**
     * Switches the current user's active role context.
     *
     * This allows caregivers to temporarily operate the application as if they
     * were a child user, useful for demonstration or testing task completion
     * flows. Children cannot switch to caregiver roles.
     *
     * @param targetRole The role to switch to
     * @return [AuthResult.Success] if role switch is permitted and successful,
     *         appropriate error result otherwise
     *
     * @sample
     * ```kotlin
     * // Caregiver switching to child role for demonstration
     * val result = authRepo.switchRole(UserRole.CHILD)
     * ```
     */
    suspend fun switchRole(targetRole: UserRole): AuthResult

    /**
     * Retrieves the currently authenticated user.
     *
     * This method provides access to the current session's user data,
     * returning null if no user is currently authenticated. Used for
     * session validation and user context retrieval.
     *
     * @return The current user if authenticated, null otherwise
     */
    suspend fun getCurrentUser(): User?

    /**
     * Logs out the current user and clears the authentication session.
     *
     * This method securely terminates the current session, clearing all
     * authentication state and ensuring no sensitive data remains in memory.
     * Should be called when users explicitly log out or when security
     * requirements mandate session termination.
     */
    suspend fun logout()
}
