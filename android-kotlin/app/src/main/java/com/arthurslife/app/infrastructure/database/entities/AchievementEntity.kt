package com.arthurslife.app.infrastructure.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arthurslife.app.domain.achievement.Achievement
import com.arthurslife.app.domain.achievement.AchievementType

/**
 * Room database entity for storing achievement data.
 *
 * This entity maps achievement domain objects to database rows, handling persistence
 * and retrieval of achievement progress and status. It maintains a clear separation
 * between domain logic and data storage concerns.
 */
@Entity(
    tableName = "achievements",
)
data class AchievementEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "type")
    val type: String, // Stored as string representation of AchievementType

    @ColumnInfo(name = "progress")
    val progress: Int,

    @ColumnInfo(name = "is_unlocked")
    val isUnlocked: Boolean,

    @ColumnInfo(name = "unlocked_at")
    val unlockedAt: Long?, // Stored as timestamp, null if not unlocked
) {
    companion object {
        /**
         * Converts a domain Achievement to a database AchievementEntity.
         */
        fun fromDomain(achievement: Achievement): AchievementEntity = AchievementEntity(
            id = achievement.id,
            userId = achievement.userId,
            type = achievement.type.name,
            progress = achievement.progress,
            isUnlocked = achievement.isUnlocked,
            unlockedAt = achievement.unlockedAt,
        )
    }

    /**
     * Converts this database entity to a domain Achievement object.
     */
    fun toDomain(): Achievement {
        val achievementType = AchievementType.valueOf(type)
        return Achievement(
            id = id,
            userId = userId,
            type = achievementType,
            progress = progress,
            isUnlocked = isUnlocked,
            unlockedAt = unlockedAt,
        )
    }
}
