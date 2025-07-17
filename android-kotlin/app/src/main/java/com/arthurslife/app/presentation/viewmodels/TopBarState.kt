package com.arthurslife.app.presentation.viewmodels

import com.arthurslife.app.domain.user.TokenBalance
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRole

/**
 * Data class representing the complete state of the top navigation bar.
 *
 * This state encapsulates all possible elements that can be displayed in the top bar
 * based on the current user role, screen context, and application state. The state
 * is immutable and reactive, updating automatically when underlying data changes.
 *
 * @property userAvatar Current user's avatar data for display
 * @property tokenBalance Current user's token balance (child mode only)
 * @property tasksProgress Progress information for tasks (child mode only)
 * @property totalTokensEarned Total tokens earned by the user (child tasks screen only)
 * @property rewardsAvailable Number of available rewards (child rewards screen only)
 * @property achievementsProgress Progress information for achievements (child achievements screen only)
 * @property selectedChild Currently selected child (caregiver mode only)
 * @property isSettingsVisible Whether settings button should be visible
 * @property currentScreen Current screen context for determining visible elements
 * @property userRole Current user's role
 * @property isChildMode Whether the user is currently in child mode
 * @property isCaregiverMode Whether the user is currently in caregiver mode
 */
data class TopBarState(
    val userAvatar: UserAvatar = UserAvatar.default(),
    val tokenBalance: TokenBalance? = null,
    val tasksProgress: TasksProgress? = null,
    val totalTokensEarned: TokenBalance? = null,
    val rewardsAvailable: Int? = null,
    val achievementsProgress: AchievementsProgress? = null,
    val selectedChild: SelectedChild? = null,
    val isSettingsVisible: Boolean = true,
    val currentScreen: TopBarScreen = TopBarScreen.HOME,
    val userRole: UserRole = UserRole.CHILD,
    val isChildMode: Boolean = true,
    val isCaregiverMode: Boolean = false,
) {
    companion object {
        /**
         * Creates an empty/default top bar state for unauthenticated users.
         */
        fun empty(): TopBarState = TopBarState(
            userAvatar = UserAvatar.default(),
            isSettingsVisible = false,
            isChildMode = false,
            isCaregiverMode = false,
        )

        /**
         * Creates a top bar state for child users based on current screen.
         */
        fun forChild(
            user: User,
            screen: TopBarScreen,
            tasksProgress: TasksProgress? = null,
            totalTokensEarned: TokenBalance? = null,
            rewardsAvailable: Int? = null,
            achievementsProgress: AchievementsProgress? = null,
        ): TopBarState = TopBarState(
            userAvatar = UserAvatar.from(user),
            tokenBalance = user.tokenBalance,
            tasksProgress = if (screen == TopBarScreen.HOME || screen == TopBarScreen.TASKS) tasksProgress else null,
            totalTokensEarned = if (screen == TopBarScreen.TASKS) totalTokensEarned else null,
            rewardsAvailable = if (screen == TopBarScreen.REWARDS) rewardsAvailable else null,
            achievementsProgress = if (screen == TopBarScreen.ACHIEVEMENTS) achievementsProgress else null,
            isSettingsVisible = true,
            currentScreen = screen,
            userRole = UserRole.CHILD,
            isChildMode = true,
            isCaregiverMode = false,
        )

        /**
         * Creates a top bar state for caregiver users based on current screen.
         */
        fun forCaregiver(
            user: User,
            screen: TopBarScreen,
            selectedChild: SelectedChild? = null,
        ): TopBarState = TopBarState(
            userAvatar = UserAvatar.from(user),
            selectedChild = if (screen == TopBarScreen.HOME) selectedChild else null,
            isSettingsVisible = true,
            currentScreen = screen,
            userRole = UserRole.CAREGIVER,
            isChildMode = false,
            isCaregiverMode = true,
        )
    }
}

/**
 * Represents user avatar information for display in the top bar.
 */
data class UserAvatar(
    val avatarData: String,
    val userName: String,
    val isClickable: Boolean = true,
) {
    companion object {
        fun default(): UserAvatar = UserAvatar(
            avatarData = "default_avatar",
            userName = "User",
            isClickable = false,
        )

        fun from(user: User): UserAvatar = UserAvatar(
            avatarData = user.avatarData,
            userName = user.displayName ?: user.name,
            isClickable = true,
        )
    }
}

/**
 * Represents tasks progress information for child users.
 */
data class TasksProgress(
    val completedTasks: Int,
    val totalTasks: Int,
    val percentage: Float = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f,
) {
    val progressText: String
        get() = "$completedTasks/$totalTasks"
}

/**
 * Represents achievements progress information for child users.
 */
data class AchievementsProgress(
    val unlockedAchievements: Int,
    val totalAchievements: Int,
    val percentage: Float = if (totalAchievements > 0) unlockedAchievements.toFloat() / totalAchievements else 0f,
) {
    val progressText: String
        get() = "$unlockedAchievements/$totalAchievements"
}

/**
 * Represents selected child information for caregiver users.
 */
data class SelectedChild(
    val childId: String,
    val childName: String,
    val childAvatar: String,
    val isClickable: Boolean = true,
)

/**
 * Enumeration of different screens that affect top bar content.
 * Based on PRD requirements for role-based and screen-specific elements.
 */
enum class TopBarScreen {
    /**
     * Home/Dashboard screen - shows different elements based on user role.
     */
    HOME,

    /**
     * Tasks screen - shows task-related progress and token information.
     */
    TASKS,

    /**
     * Rewards screen - shows reward-related information and token balance.
     */
    REWARDS,

    /**
     * Achievements screen - shows achievement progress information.
     */
    ACHIEVEMENTS,

    /**
     * Children Management screen (caregiver only) - shows basic user info and settings.
     */
    CHILDREN_MANAGEMENT,

    /**
     * Other screens that don't require specific top bar customization.
     */
    OTHER,
}
