package com.arthurslife.app.domain

import com.arthurslife.app.domain.achievement.Achievement
import com.arthurslife.app.domain.achievement.AchievementType
import com.arthurslife.app.domain.auth.PIN
import com.arthurslife.app.domain.task.Task
import com.arthurslife.app.domain.task.TaskCategory
import com.arthurslife.app.domain.user.AvatarType
import com.arthurslife.app.domain.user.TokenBalance
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRole
import java.util.UUID
import kotlin.random.Random

/**
 * Test data factory providing consistent and realistic test data for Arthur's Life domain objects.
 *
 * This factory provides deterministic test data generation for reliable testing,
 * while also supporting randomization for property-based testing. All factory methods
 * use sensible defaults and allow customization through parameters.
 *
 * Key features:
 * - Consistent default values for reliable tests
 * - Randomization support for property-based testing
 * - Realistic data that reflects actual usage patterns
 * - Comprehensive coverage of all domain objects
 * - Helper methods for common test scenarios
 */
object TestDataFactory {

    // ==================================================
    // User Factory Methods
    // ==================================================

    /**
     * Creates a child user with sensible defaults.
     *
     * @param name Display name for the user
     * @param tokenBalance Initial token balance
     * @param id User ID (auto-generated if not provided)
     * @param displayName Custom display name
     * @param avatarType Type of avatar (PREDEFINED or CUSTOM)
     * @param avatarData Avatar data (avatar ID or custom image data)
     * @param favoriteColor User's favorite color
     * @return Child user instance
     */
    fun createChildUser(
        name: String = "Arthur",
        tokenBalance: TokenBalance = TokenBalance.create(25),
        id: String = UUID.randomUUID().toString(),
        displayName: String? = null,
        avatarType: AvatarType = AvatarType.PREDEFINED,
        avatarData: String = "default_child",
        favoriteColor: String? = null,
    ): User = User(
        id = id,
        name = name,
        role = UserRole.CHILD,
        tokenBalance = tokenBalance,
        pin = null,
        displayName = displayName,
        avatarType = avatarType,
        avatarData = avatarData,
        favoriteColor = favoriteColor,
    )

    /**
     * Creates a caregiver user with PIN authentication.
     *
     * @param name Display name for the caregiver
     * @param pin PIN for authentication (defaults to "1234", can be null)
     * @param tokenBalance Initial token balance
     * @param id User ID (auto-generated if not provided)
     * @param displayName Custom display name
     * @param avatarType Type of avatar (PREDEFINED or CUSTOM)
     * @param avatarData Avatar data (avatar ID or custom image data)
     * @param favoriteColor User's favorite color
     * @return Caregiver user instance
     */
    fun createCaregiverUser(
        name: String = "Parent",
        pin: PIN? = PIN.create("9176"), // Secure PIN not in weak list
        tokenBalance: TokenBalance = TokenBalance.zero(),
        id: String = UUID.randomUUID().toString(),
        displayName: String? = null,
        avatarType: AvatarType = AvatarType.PREDEFINED,
        avatarData: String = "default_caregiver",
        favoriteColor: String? = null,
    ): User = User(
        id = id,
        name = name,
        role = UserRole.CAREGIVER,
        tokenBalance = tokenBalance,
        pin = pin,
        displayName = displayName,
        avatarType = avatarType,
        avatarData = avatarData,
        favoriteColor = favoriteColor,
    )

    /**
     * Creates a user with random data for property-based testing.
     *
     * @param role User role (randomly selected if not provided)
     * @param includePin Whether to include PIN (only for caregivers)
     * @return User with randomized data
     */
    fun createRandomUser(
        role: UserRole = listOf(UserRole.CHILD, UserRole.CAREGIVER).random(),
        includePin: Boolean = role == UserRole.CAREGIVER,
    ): User {
        val names = listOf("Arthur", "Emma", "Liam", "Sophie", "Oliver", "Mia", "Parent", "Mom", "Dad")
        val tokenAmount = Random.nextInt(0, 100)

        return User(
            id = UUID.randomUUID().toString(),
            name = names.random(),
            role = role,
            tokenBalance = TokenBalance.create(tokenAmount),
            pin = if (includePin) PIN.create(Random.nextInt(1000, 9999).toString()) else null,
        )
    }

    // ==================================================
    // Task Factory Methods
    // ==================================================

    /**
     * Creates a basic task with default values.
     *
     * @param title Task title
     * @param category Task category
     * @param assignedToUserId User ID of assigned child
     * @param isCompleted Whether task is completed
     * @param id Task ID (auto-generated if not provided)
     * @return Task instance
     */
    fun createTask(
        title: String = "Brush teeth",
        category: TaskCategory = TaskCategory.PERSONAL_CARE,
        assignedToUserId: String = UUID.randomUUID().toString(),
        isCompleted: Boolean = false,
        id: String = UUID.randomUUID().toString(),
    ): Task = Task(
        id = id,
        title = title,
        category = category,
        tokenReward = category.defaultTokenReward,
        isCompleted = isCompleted,
        assignedToUserId = assignedToUserId,
        createdAt = System.currentTimeMillis(),
    )

    /**
     * Creates a completed task with tokens awarded.
     *
     * @param title Task title
     * @param category Task category
     * @param assignedToUserId User ID of assigned child
     * @return Completed task instance
     */
    fun createCompletedTask(
        title: String = "Clean room",
        category: TaskCategory = TaskCategory.HOUSEHOLD,
        assignedToUserId: String = UUID.randomUUID().toString(),
    ): Task = createTask(
        title = title,
        category = category,
        assignedToUserId = assignedToUserId,
        isCompleted = true,
    )

    /**
     * Creates a list of tasks for different categories.
     *
     * @param assignedToUserId User ID to assign all tasks to
     * @param includeCompleted Whether to include some completed tasks
     * @return List of diverse tasks
     */
    fun createTaskList(
        assignedToUserId: String = UUID.randomUUID().toString(),
        includeCompleted: Boolean = true,
    ): List<Task> {
        val tasks = mutableListOf<Task>()

        // Personal care tasks
        tasks.addAll(
            listOf(
                createTask("Brush teeth", TaskCategory.PERSONAL_CARE, assignedToUserId),
                createTask("Wash hands", TaskCategory.PERSONAL_CARE, assignedToUserId),
                createTask("Get dressed", TaskCategory.PERSONAL_CARE, assignedToUserId),
            ),
        )

        // Household tasks
        tasks.addAll(
            listOf(
                createTask("Make bed", TaskCategory.HOUSEHOLD, assignedToUserId),
                createTask("Clean room", TaskCategory.HOUSEHOLD, assignedToUserId),
                createTask("Help with dishes", TaskCategory.HOUSEHOLD, assignedToUserId),
            ),
        )

        // Homework tasks
        tasks.addAll(
            listOf(
                createTask("Math homework", TaskCategory.HOMEWORK, assignedToUserId),
                createTask("Read book", TaskCategory.HOMEWORK, assignedToUserId),
                createTask("Practice spelling", TaskCategory.HOMEWORK, assignedToUserId),
            ),
        )

        // Mark some as completed if requested
        if (includeCompleted) {
            tasks[0] = tasks[0].markCompleted()
            tasks[3] = tasks[3].markCompleted()
            tasks[6] = tasks[6].markCompleted()
        }

        return tasks
    }

    /**
     * Creates task statistics for testing.
     *
     * @param totalCompletedTasks Total completed tasks
     * @param totalTokensEarned Total tokens earned
     * @param incompleteTasks Number of incomplete tasks
     * @param completionRate Completion rate percentage
     * @return TaskStats instance
     */
    fun createTaskStats(
        totalCompletedTasks: Int = 5,
        totalTokensEarned: Int = 75,
        incompleteTasks: Int = 3,
        completionRate: Int = 62,
        currentStreak: Int = 3,
    ): com.arthurslife.app.domain.task.usecase.TaskStats =
        com.arthurslife.app.domain.task.usecase.TaskStats(
            totalCompletedTasks = totalCompletedTasks,
            totalTokensEarned = totalTokensEarned,
            incompleteTasks = incompleteTasks,
            completionRate = completionRate,
            currentStreak = currentStreak,
        )

    /**
     * Creates a random task for property-based testing.
     *
     * @param assignedToUserId User ID to assign task to
     * @return Task with randomized data
     */
    fun createRandomTask(
        assignedToUserId: String = UUID.randomUUID().toString(),
    ): Task {
        val titles = mapOf(
            TaskCategory.PERSONAL_CARE to listOf("Brush teeth", "Wash hands", "Comb hair", "Get dressed"),
            TaskCategory.HOUSEHOLD to listOf("Make bed", "Clean room", "Help with dishes", "Feed pets"),
            TaskCategory.HOMEWORK to listOf("Math homework", "Read book", "Practice spelling", "Study science"),
        )

        val category = TaskCategory.values().random()
        val title = titles[category]?.random() ?: "Random task"

        return createTask(
            title = title,
            category = category,
            assignedToUserId = assignedToUserId,
            isCompleted = Random.nextBoolean(),
        )
    }

    // ==================================================
    // Achievement Factory Methods
    // ==================================================

    /**
     * Creates an achievement with default values.
     *
     * @param type Achievement type
     * @param userId User ID who owns the achievement
     * @param progress Current progress toward goal
     * @param isUnlocked Whether achievement is unlocked
     * @return Achievement instance
     */
    fun createAchievement(
        type: AchievementType = AchievementType.FIRST_STEPS,
        userId: String = UUID.randomUUID().toString(),
        progress: Int = 0,
        isUnlocked: Boolean = false,
    ): Achievement = Achievement(
        id = "${type.name.lowercase()}-$userId",
        type = type,
        isUnlocked = isUnlocked,
        progress = progress,
        unlockedAt = if (isUnlocked) System.currentTimeMillis() else null,
        userId = userId,
    )

    /**
     * Creates an unlocked achievement.
     *
     * @param type Achievement type
     * @param userId User ID who owns the achievement
     * @return Unlocked achievement instance
     */
    fun createUnlockedAchievement(
        type: AchievementType = AchievementType.FIRST_STEPS,
        userId: String = UUID.randomUUID().toString(),
    ): Achievement = createAchievement(
        type = type,
        userId = userId,
        progress = type.target,
        isUnlocked = true,
    )

    /**
     * Creates a partially progressed achievement.
     *
     * @param type Achievement type
     * @param userId User ID who owns the achievement
     * @param progressPercentage Progress percentage (0-99)
     * @return Achievement with partial progress
     */
    fun createPartialAchievement(
        type: AchievementType = AchievementType.CENTURY_CLUB,
        userId: String = UUID.randomUUID().toString(),
        progressPercentage: Int = 50,
    ): Achievement {
        val progress = (type.target * progressPercentage / 100).coerceAtMost(type.target - 1)
        return createAchievement(
            type = type,
            userId = userId,
            progress = progress,
            isUnlocked = false,
        )
    }

    /**
     * Creates a complete set of achievements for a user.
     *
     * @param userId User ID to create achievements for
     * @param includeProgress Whether to include some progress on achievements
     * @return List of all achievement types for the user
     */
    fun createAchievementSet(
        userId: String = UUID.randomUUID().toString(),
        includeProgress: Boolean = true,
    ): List<Achievement> = AchievementType.values().map { type ->
        when {
            !includeProgress -> createAchievement(type, userId)
            type == AchievementType.FIRST_STEPS -> createUnlockedAchievement(type, userId)
            type == AchievementType.CENTURY_CLUB -> createPartialAchievement(type, userId, 70)
            else -> createAchievement(type, userId, Random.nextInt(0, type.target))
        }
    }

    // ==================================================
    // Token Balance Factory Methods
    // ==================================================

    /**
     * Creates a token balance with specified amount.
     *
     * @param amount Token amount
     * @return TokenBalance instance
     */
    fun createTokenBalance(amount: Int = 25): TokenBalance = TokenBalance.create(amount)

    /**
     * Creates a zero token balance.
     *
     * @return TokenBalance with zero tokens
     */
    fun createZeroTokenBalance(): TokenBalance = TokenBalance.zero()

    /**
     * Creates a large token balance for testing high-value scenarios.
     *
     * @param amount Token amount (defaults to 1000)
     * @return TokenBalance with large amount
     */
    fun createLargeTokenBalance(amount: Int = 1000): TokenBalance = TokenBalance.create(amount)

    /**
     * Creates a negative token balance using administrative override.
     *
     * @param amount Negative token amount
     * @return TokenBalance with negative amount
     */
    fun createNegativeTokenBalance(amount: Int = -10): TokenBalance = TokenBalance.createAdmin(
        amount,
    )

    // ==================================================
    // PIN Factory Methods
    // ==================================================

    /**
     * Creates a PIN with default value.
     *
     * @param rawPin PIN string (defaults to "1234")
     * @return PIN instance
     */
    fun createPIN(rawPin: String = "1234"): PIN = PIN.create(rawPin)

    /**
     * Creates a PIN with random 4-digit value.
     *
     * @return PIN with random value
     */
    fun createRandomPIN(): PIN = PIN.create(Random.nextInt(1000, 9999).toString())

    /**
     * Creates multiple PINs for testing.
     *
     * @param count Number of PINs to create
     * @return List of unique PINs
     */
    fun createPINList(count: Int = 5): List<PIN> = (1..count).map {
        PIN.create(Random.nextInt(1000, 9999).toString())
    }

    // ==================================================
    // Complex Scenario Factory Methods
    // ==================================================

    /**
     * Creates a complete family scenario with child and caregiver.
     *
     * @param childName Child's name
     * @param caregiverName Caregiver's name
     * @param childTokens Initial tokens for child
     * @return Pair of (child, caregiver) users
     */
    fun createFamilyScenario(
        childName: String = "Arthur",
        caregiverName: String = "Parent",
        childTokens: Int = 25,
    ): Pair<User, User> {
        val child = createChildUser(childName, TokenBalance.create(childTokens))
        val caregiver = createCaregiverUser(caregiverName)
        return Pair(child, caregiver)
    }

    /**
     * Creates a task completion scenario with all related objects.
     *
     * @param taskTitle Task title
     * @param category Task category
     * @return Triple of (user, task, achievement) ready for completion testing
     */
    fun createTaskCompletionScenario(
        taskTitle: String = "Brush teeth",
        category: TaskCategory = TaskCategory.PERSONAL_CARE,
    ): Triple<User, Task, Achievement> {
        val user = createChildUser()
        val task = createTask(taskTitle, category, user.id)
        val achievement = createAchievement(AchievementType.FIRST_STEPS, user.id)
        return Triple(user, task, achievement)
    }

    /**
     * Creates a reward redemption scenario.
     *
     * @param userTokens Initial user tokens
     * @param rewardCost Cost of reward to redeem
     * @return Pair of (user, sufficient balance check)
     */
    fun createRewardScenario(
        userTokens: Int = 50,
        rewardCost: Int = 30,
    ): Pair<User, Boolean> {
        val user = createChildUser(tokenBalance = TokenBalance.create(userTokens))
        val canAfford = user.tokenBalance.canAfford(rewardCost)
        return Pair(user, canAfford)
    }

    // ==================================================
    // Use Case Result Factory Methods
    // ==================================================

    /**
     * Creates a task completion result for testing.
     *
     * @param taskId ID of the completed task (creates default task if not provided)
     * @param tokensAwarded Number of tokens awarded
     * @param newTokenBalance User's new token balance
     * @param newlyUnlockedAchievements List of newly unlocked achievements
     * @return TaskCompletionResult instance
     */
    fun createTaskCompletionResult(
        taskId: String = "test-task-1",
        tokensAwarded: Int = 10,
        newTokenBalance: Int = 35,
        newlyUnlockedAchievements: List<Achievement> = emptyList(),
        task: Task? = null,
    ): com.arthurslife.app.domain.task.usecase.TaskCompletionResult {
        val resultTask = task ?: createTask(id = taskId, isCompleted = true)
        return com.arthurslife.app.domain.task.usecase.TaskCompletionResult(
            task = resultTask,
            tokensAwarded = tokensAwarded,
            newTokenBalance = newTokenBalance,
            newlyUnlockedAchievements = newlyUnlockedAchievements,
        )
    }

    /**
     * Creates a task undo result for testing.
     *
     * @param taskId ID of the undone task (creates default task if not provided)
     * @param tokensDeducted Number of tokens deducted
     * @param newTokenBalance User's new token balance
     * @param undoneByRole Role of user who performed the undo
     * @param task Task that was undone
     * @return TaskUndoResult instance
     */
    fun createTaskUndoResult(
        taskId: String = "test-task-1",
        tokensDeducted: Int = 10,
        newTokenBalance: Int = 15,
        undoneByRole: UserRole = UserRole.CHILD,
        task: Task? = null,
    ): com.arthurslife.app.domain.task.usecase.TaskUndoResult {
        val resultTask = task ?: createTask(id = taskId, isCompleted = false)
        return com.arthurslife.app.domain.task.usecase.TaskUndoResult(
            task = resultTask,
            tokensDeducted = tokensDeducted,
            newTokenBalance = newTokenBalance,
            undoneByRole = undoneByRole,
        )
    }
}
