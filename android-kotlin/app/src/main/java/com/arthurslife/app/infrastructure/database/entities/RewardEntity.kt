package com.arthurslife.app.infrastructure.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arthurslife.app.domain.reward.Reward
import com.arthurslife.app.domain.reward.RewardCategory

/**
 * Room database entity for reward persistence in Arthur's Life reward system.
 *
 * This entity supports the complete reward system including custom rewards created
 * by caregivers, predefined rewards, availability management, and approval workflows.
 *
 * The entity provides conversion methods to and from the domain Reward model,
 * maintaining clean separation between persistence and domain layers.
 */
@Entity(tableName = "rewards")
data class RewardEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val tokenCost: Int,
    val isAvailable: Boolean,
    val isCustom: Boolean,
    val requiresApproval: Boolean,
    val createdByUserId: String?,
    val createdAt: Long,
    val imageUrl: String?,
) {
    /**
     * Converts this database entity to a domain Reward model.
     *
     * @return Domain Reward instance with converted category enum
     */
    fun toDomain(): Reward =
        Reward(
            id = id,
            title = title,
            description = description,
            category = RewardCategory.valueOf(category),
            tokenCost = tokenCost,
            isAvailable = isAvailable,
            isCustom = isCustom,
            requiresApproval = requiresApproval,
            createdByUserId = createdByUserId,
            createdAt = createdAt,
            imageUrl = imageUrl,
        )

    companion object {
        /**
         * Converts a domain Reward model to a database entity.
         *
         * @param reward Domain Reward instance to convert
         * @return RewardEntity ready for database persistence
         */
        fun fromDomain(reward: Reward): RewardEntity =
            RewardEntity(
                id = reward.id,
                title = reward.title,
                description = reward.description,
                category = reward.category.name,
                tokenCost = reward.tokenCost,
                isAvailable = reward.isAvailable,
                isCustom = reward.isCustom,
                requiresApproval = reward.requiresApproval,
                createdByUserId = reward.createdByUserId,
                createdAt = reward.createdAt,
                imageUrl = reward.imageUrl,
            )
    }
}
