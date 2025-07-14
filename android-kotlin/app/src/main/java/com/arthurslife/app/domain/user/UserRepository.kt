package com.arthurslife.app.domain.user

/**
 * Repository interface for user data persistence operations in Arthur's Life application.
 *
 * This interface abstracts the data access layer for user management, following the
 * Repository pattern from Domain-Driven Design. It provides a clean separation between
 * business logic and data storage, allowing different implementations (Room database,
 * remote API, in-memory storage) without affecting domain logic.
 *
 * All operations are suspending functions to support asynchronous data access patterns
 * commonly used with databases and network operations. The interface maintains data
 * consistency and supports the multi-user family structure of Arthur's Life.
 *
 * @sample
 * ```kotlin
 * class UserService(private val userRepo: UserRepository) {
 *     suspend fun createChildUser(name: String): User {
 *         val user = User(name = name, role = UserRole.CHILD)
 *         userRepo.saveUser(user)
 *         return user
 *     }
 * }
 * ```
 */
interface UserRepository {
    /**
     * Retrieves a user by their unique identifier.
     *
     * This method performs a primary key lookup to find a specific user.
     * Returns null if no user exists with the given ID, following null-safe
     * Kotlin patterns rather than throwing exceptions for missing data.
     *
     * @param id The unique identifier of the user to retrieve
     * @return The user if found, null if no user exists with the given ID
     *
     * @sample
     * ```kotlin
     * val user = userRepo.findById("user-123")
     * if (user != null) {
     *     // Process found user
     * } else {
     *     // Handle user not found
     * }
     * ```
     */
    suspend fun findById(id: String): User?

    /**
     * Retrieves a user by their unique identifier.
     *
     * This is an alias for findById to maintain compatibility with existing code.
     *
     * @param userId The unique identifier of the user to retrieve
     * @return The user if found, null if no user exists with the given ID
     */
    suspend fun getUserById(userId: String): User? = findById(userId)

    /**
     * Retrieves the first user with the specified role.
     *
     * This method is useful for finding role-specific users, particularly
     * in single-caregiver households where you need to locate the caregiver
     * user. For families with multiple children, this returns the first
     * child found (order depends on implementation).
     *
     * @param role The user role to search for
     * @return The first user with the specified role, null if none exists
     *
     * @sample
     * ```kotlin
     * val caregiver = userRepo.findByRole(UserRole.CAREGIVER)
     * val firstChild = userRepo.findByRole(UserRole.CHILD)
     * ```
     */
    suspend fun findByRole(role: UserRole): User?

    /**
     * Retrieves all users in the family system.
     *
     * This method returns a complete list of all users, including both
     * children and caregivers. Useful for family overview screens, user
     * selection interfaces, and administrative functions. The returned
     * list is always non-null but may be empty.
     *
     * @return List of all users in the system, empty list if no users exist
     *
     * @sample
     * ```kotlin
     * val allUsers = userRepo.getAllUsers()
     * val children = allUsers.filter { it.role == UserRole.CHILD }
     * val caregivers = allUsers.filter { it.role == UserRole.CAREGIVER }
     * ```
     */
    suspend fun getAllUsers(): List<User>

    /**
     * Persists a new user to the data store.
     *
     * This method handles the initial creation and storage of user entities.
     * The user ID should be generated before calling this method (typically
     * using UUID). If a user with the same ID already exists, behavior
     * depends on the implementation (may overwrite or throw exception).
     *
     * @param user The user entity to save
     *
     * @sample
     * ```kotlin
     * val newUser = User(
     *     name = "Arthur",
     *     role = UserRole.CHILD,
     *     tokenBalance = TokenBalance.zero()
     * )
     * userRepo.saveUser(newUser)
     * ```
     */
    suspend fun saveUser(user: User)

    /**
     * Persists multiple users to the data store in a single transaction.
     *
     * This method ensures all users are saved atomically - either all users
     * are saved successfully, or none are saved if any operation fails.
     * This is particularly useful during family setup/onboarding where
     * you want to ensure data consistency.
     *
     * @param users The list of user entities to save
     *
     * @sample
     * ```kotlin
     * val family = listOf(caregiver, child1, child2)
     * userRepo.saveUsers(family)
     * ```
     */
    suspend fun saveUsers(users: List<User>)

    /**
     * Updates an existing user's data in the data store.
     *
     * This method is used for modifying existing user information such as
     * token balances, PIN changes, or name updates. The user must already
     * exist in the system (typically verified by ID). Implementation should
     * handle the case where the user doesn't exist appropriately.
     *
     * @param user The user entity with updated data
     *
     * @sample
     * ```kotlin
     * val user = userRepo.findById("user-123")!!
     * val updatedUser = user.copy(
     *     tokenBalance = user.tokenBalance.add(10)
     * )
     * userRepo.updateUser(updatedUser)
     * ```
     */
    suspend fun updateUser(user: User)

    /**
     * Removes a user from the data store.
     *
     * This method permanently deletes a user and all associated data.
     * Should be used with caution as it may affect data integrity if the
     * user has related data (tasks, transaction history, etc.). Consider
     * implementing soft deletion for audit trails.
     *
     * @param userId The unique identifier of the user to delete
     *
     * @sample
     * ```kotlin
     * // Ensure user exists before deletion
     * val user = userRepo.findById("user-123")
     * if (user != null) {
     *     userRepo.deleteUser("user-123")
     * }
     * ```
     */
    suspend fun deleteUser(userId: String)
}
