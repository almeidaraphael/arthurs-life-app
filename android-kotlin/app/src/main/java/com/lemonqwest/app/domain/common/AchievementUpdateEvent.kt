package com.lemonqwest.app.domain.common

import com.lemonqwest.app.domain.achievement.Achievement
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Event that indicates achievements have been updated.
 * Used to notify achievement screens to refresh their data.
 */
data class AchievementUpdateEvent(
    val userId: String,
    val newlyUnlockedAchievements: List<Achievement> = emptyList(),
)

/**
 * Singleton event manager for achievement updates.
 * Provides a centralized way to notify when achievements are updated.
 */
@Singleton
class AchievementEventManager @Inject constructor() {
    private val _achievementUpdates = MutableSharedFlow<AchievementUpdateEvent>()
    val achievementUpdates: SharedFlow<AchievementUpdateEvent> = _achievementUpdates.asSharedFlow()

    /**
     * Emits an achievement update event.
     */
    suspend fun emitAchievementUpdate(event: AchievementUpdateEvent) {
        _achievementUpdates.emit(event)
    }

    /**
     * Convenience method to emit an update event for a user.
     */
    suspend fun emitAchievementUpdate(
        userId: String,
        newlyUnlockedAchievements: List<Achievement> = emptyList(),
    ) {
        emitAchievementUpdate(AchievementUpdateEvent(userId, newlyUnlockedAchievements))
    }
}
