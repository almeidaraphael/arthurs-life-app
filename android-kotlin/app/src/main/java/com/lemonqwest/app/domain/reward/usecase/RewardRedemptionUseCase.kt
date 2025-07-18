package com.lemonqwest.app.domain.reward.usecase

import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.common.AchievementEventManager
import com.lemonqwest.app.domain.common.DomainException
import com.lemonqwest.app.domain.reward.InsufficientTokensException
import com.lemonqwest.app.domain.reward.RewardNotFoundException
import com.lemonqwest.app.domain.reward.RewardRepository
import com.lemonqwest.app.domain.reward.RewardRepositoryException
import com.lemonqwest.app.domain.reward.RewardRequiresApprovalException
import com.lemonqwest.app.domain.reward.RewardUnavailableException
import com.lemonqwest.app.domain.reward.RewardUserNotFoundException
import com.lemonqwest.app.domain.user.User
import com.lemonqwest.app.domain.user.UserRepository
import javax.inject.Inject

/**
 * Use case for handling reward redemption operations for children.
 *
 * This use case encapsulates the business logic for redeeming rewards, including:
 * - Token balance validation
 * - Reward availability checking
 * - Approval requirements validation
 * - Token deduction and balance updates
 * - Achievement progress tracking for token spending
 */
class RewardRedemptionUseCase
@Inject
constructor(
    private val rewardRepository: RewardRepository,
    private val userRepository: UserRepository,
    private val achievementTrackingUseCase: AchievementTrackingUseCase,
    private val achievementEventManager: AchievementEventManager,
) {
    /**
     * Redeems a reward for a child user.
     *
     * This operation validates the redemption request, checks token balance,
     * and processes the redemption if all conditions are met.
     *
     * @param rewardId ID of the reward to redeem
     * @param userId ID of the child user redeeming the reward
     * @return Result with updated user (with new token balance) or error
     */
    suspend fun redeemReward(
        rewardId: String,
        userId: String,
    ): Result<User> {
        return try {
            // Find the reward
            val reward = rewardRepository.findById(rewardId)
                ?: return Result.failure(RewardNotFoundException(rewardId))

            // Find the user
            val user = userRepository.findById(userId)
                ?: return Result.failure(RewardUserNotFoundException(userId))

            // Validate reward can be redeemed
            if (!reward.isAvailable) {
                return Result.failure(RewardUnavailableException(rewardId))
            }

            if (reward.requiresApproval) {
                return Result.failure(RewardRequiresApprovalException(rewardId))
            }

            // Check if user has sufficient tokens
            if (!user.tokenBalance.canAfford(reward.tokenCost)) {
                return Result.failure(
                    InsufficientTokensException(
                        rewardId = rewardId,
                        requiredTokens = reward.tokenCost,
                        availableTokens = user.tokenBalance.getValue(),
                    ),
                )
            }

            // Deduct tokens from user balance
            val newTokenBalance = user.tokenBalance.subtract(reward.tokenCost)
            val updatedUser = user.copy(tokenBalance = newTokenBalance)

            // Update user in repository
            userRepository.updateUser(updatedUser)

            // Track achievements for token spending
            val newlyUnlockedAchievements = achievementTrackingUseCase.updateAchievementsAfterTokenSpending(
                userId = userId,
                tokensSpent = reward.tokenCost,
            )

            // Emit achievement update event if any achievements were unlocked
            if (newlyUnlockedAchievements.isNotEmpty()) {
                achievementEventManager.emitAchievementUpdate(
                    userId = userId,
                    newlyUnlockedAchievements = newlyUnlockedAchievements,
                )
            }

            Result.success(updatedUser)
        } catch (e: RewardNotFoundException) {
            Result.failure(e)
        } catch (e: RewardUserNotFoundException) {
            Result.failure(e)
        } catch (e: RewardUnavailableException) {
            Result.failure(e)
        } catch (e: RewardRequiresApprovalException) {
            Result.failure(e)
        } catch (e: InsufficientTokensException) {
            Result.failure(e)
        } catch (e: RewardRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Checks if a reward can be redeemed by a user.
     *
     * This operation validates redemption prerequisites without actually
     * performing the redemption.
     *
     * @param rewardId ID of the reward to check
     * @param userId ID of the user
     * @return Result with redemption validation result or error
     */
    suspend fun canRedeemReward(
        rewardId: String,
        userId: String,
    ): Result<RedemptionValidation> {
        return try {
            val reward = rewardRepository.findById(rewardId)
                ?: return Result.failure(RewardNotFoundException(rewardId))

            val user = userRepository.findById(userId)
                ?: return Result.failure(RewardUserNotFoundException(userId))

            val validation = RedemptionValidation(
                canRedeem = reward.isAvailable && !reward.requiresApproval && user.tokenBalance.canAfford(reward.tokenCost),
                isAvailable = reward.isAvailable,
                requiresApproval = reward.requiresApproval,
                hasEnoughTokens = user.tokenBalance.canAfford(reward.tokenCost),
                requiredTokens = reward.tokenCost,
                availableTokens = user.tokenBalance.getValue(),
            )

            Result.success(validation)
        } catch (e: RewardNotFoundException) {
            Result.failure(e)
        } catch (e: RewardUserNotFoundException) {
            Result.failure(e)
        } catch (e: RewardRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Gets rewards that are available and affordable for a user.
     *
     * @param userId ID of the user
     * @return Result with list of redeemable rewards or error
     */
    suspend fun getRedeemableRewards(
        userId: String,
    ): Result<List<com.lemonqwest.app.domain.reward.Reward>> {
        return try {
            val user = userRepository.findById(userId)
                ?: return Result.failure(RewardUserNotFoundException(userId))

            val affordableRewards = rewardRepository.findAvailableAffordableRewards(
                user.tokenBalance.getValue(),
            )

            // Filter out rewards that require approval
            val redeemableRewards = affordableRewards.filter { !it.requiresApproval }

            Result.success(redeemableRewards)
        } catch (e: RewardUserNotFoundException) {
            Result.failure(e)
        } catch (e: RewardRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }

    /**
     * Gets all rewards that require approval for redemption.
     *
     * @return Result with list of approval-required rewards or error
     */
    suspend fun getApprovalRequiredRewards(): Result<List<com.lemonqwest.app.domain.reward.Reward>> {
        return try {
            val rewards = rewardRepository.getApprovalRequiredRewards()
            Result.success(rewards)
        } catch (e: RewardRepositoryException) {
            Result.failure(e)
        } catch (e: DomainException) {
            Result.failure(e)
        }
    }
}

/**
 * Data class containing validation information for reward redemption.
 *
 * @property canRedeem Whether the reward can be redeemed immediately
 * @property isAvailable Whether the reward is available
 * @property requiresApproval Whether the reward requires approval
 * @property hasEnoughTokens Whether the user has enough tokens
 * @property requiredTokens Number of tokens required for the reward
 * @property availableTokens Number of tokens the user has
 */
data class RedemptionValidation(
    val canRedeem: Boolean,
    val isAvailable: Boolean,
    val requiresApproval: Boolean,
    val hasEnoughTokens: Boolean,
    val requiredTokens: Int,
    val availableTokens: Int,
)
