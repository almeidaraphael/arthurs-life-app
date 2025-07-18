package com.lemonqwest.app.domain.reward

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Core domain entity representing a reward in the LemonQwest reward system.
 *
 * This entity encapsulates reward information, token cost, availability, and customization:
 * - Basic reward identification and description
 * - Category classification for organization
 * - Token cost for redemption
 * - Availability status and restrictions
 * - Custom vs predefined reward distinction
 * - Caregiver approval requirements
 *
 * @property id Unique identifier for the reward
 * @property title Reward name displayed to users
 * @property description Detailed description of the reward
 * @property category Reward category for organization and filtering
 * @property tokenCost Number of tokens required for redemption
 * @property isAvailable Whether the reward is currently available for redemption
 * @property isCustom Whether this is a custom reward created by caregivers
 * @property requiresApproval Whether redemption requires caregiver approval
 * @property createdByUserId ID of the user who created this reward (caregiver)
 * @property createdAt Timestamp when reward was created
 * @property imageUrl Optional URL for reward image
 */
@Serializable
data class Reward(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val category: RewardCategory,
    val tokenCost: Int,
    val isAvailable: Boolean = true,
    val isCustom: Boolean = false,
    val requiresApproval: Boolean = false,
    val createdByUserId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val imageUrl: String? = null,
) {
    init {
        require(title.isNotBlank()) { "Reward title cannot be blank" }
        require(description.isNotBlank()) { "Reward description cannot be blank" }
        require(tokenCost > 0) { "Token cost must be positive" }
        require(tokenCost >= category.minTokenCost) {
            "Token cost must be at least ${category.minTokenCost} for ${category.displayName}"
        }
        require(tokenCost <= category.maxTokenCost) {
            "Token cost must not exceed ${category.maxTokenCost} for ${category.displayName}"
        }
        if (isCustom) {
            require(createdByUserId != null) { "Custom rewards must have a creator" }
        }
    }

    /**
     * Creates a copy of this reward with updated availability status.
     *
     * @param available New availability status
     * @return A new Reward instance with updated availability
     */
    fun updateAvailability(available: Boolean): Reward = copy(isAvailable = available)

    /**
     * Creates a copy of this reward with updated token cost.
     *
     * @param newTokenCost New token cost (must be within category range)
     * @return A new Reward instance with updated token cost
     */
    fun updateTokenCost(newTokenCost: Int): Reward {
        require(newTokenCost > 0) { "Token cost must be positive" }
        require(newTokenCost >= category.minTokenCost) {
            "Token cost must be at least ${category.minTokenCost} for ${category.displayName}"
        }
        require(newTokenCost <= category.maxTokenCost) {
            "Token cost must not exceed ${category.maxTokenCost} for ${category.displayName}"
        }
        return copy(tokenCost = newTokenCost)
    }

    /**
     * Checks if this reward can be redeemed (is available and not requiring approval).
     *
     * @return true if reward can be immediately redeemed, false if unavailable or requires approval
     */
    fun canBeRedeemed(): Boolean = isAvailable && !requiresApproval

    /**
     * Checks if this reward requires caregiver approval for redemption.
     *
     * @return true if reward requires approval, false if can be redeemed immediately
     */
    fun needsApproval(): Boolean = requiresApproval

    /**
     * Checks if a user has sufficient tokens to afford this reward.
     *
     * @param userTokens Number of tokens the user has
     * @return true if user can afford this reward, false otherwise
     */
    fun isAffordableFor(userTokens: Int): Boolean = userTokens >= tokenCost

    companion object {
        /**
         * Creates a new predefined reward with recommended token cost.
         *
         * @param title Reward title
         * @param description Reward description
         * @param category Reward category
         * @param tokenCost Token cost (will be validated against category range)
         * @return New Reward instance as predefined reward
         */
        fun createPredefined(
            title: String,
            description: String,
            category: RewardCategory,
            tokenCost: Int,
        ): Reward = Reward(
            title = title,
            description = description,
            category = category,
            tokenCost = tokenCost,
            isCustom = false,
            requiresApproval = false,
        )

        /**
         * Creates a new custom reward created by a caregiver.
         *
         * @param title Reward title
         * @param description Reward description
         * @param category Reward category
         * @param tokenCost Token cost (will be validated against category range)
         * @param createdByUserId ID of the caregiver who created this reward
         * @param requiresApproval Whether this reward requires approval for redemption
         * @return New Reward instance as custom reward
         */
        fun createCustom(
            title: String,
            description: String,
            category: RewardCategory,
            tokenCost: Int,
            createdByUserId: String,
            requiresApproval: Boolean = false,
        ): Reward = Reward(
            title = title,
            description = description,
            category = category,
            tokenCost = tokenCost,
            isCustom = true,
            requiresApproval = requiresApproval,
            createdByUserId = createdByUserId,
        )
    }
}
