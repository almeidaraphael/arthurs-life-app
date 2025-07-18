package com.lemonqwest.app.domain.reward

/**
 * Domain-specific exceptions for reward operations.
 */

/**
 * Thrown when a reward operation fails.
 */
sealed class RewardException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Thrown when a reward is not found.
 */
class RewardNotFoundException(val rewardId: String) : RewardException("Reward not found: $rewardId")

/**
 * Thrown when attempting to redeem an unavailable reward.
 */
class RewardUnavailableException(
    val rewardId: String,
) : RewardException("Reward is not available for redemption: $rewardId")

/**
 * Thrown when a reward operation fails due to invalid data.
 */
class InvalidRewardDataException(message: String) : RewardException("Invalid reward data: $message")

/**
 * Thrown when a reward repository operation fails.
 */
class RewardRepositoryException(
    message: String,
    cause: Throwable? = null,
) : RewardException("Repository error: $message", cause)

/**
 * Thrown when attempting to redeem a reward without sufficient tokens.
 */
class InsufficientTokensException(
    val rewardId: String,
    val requiredTokens: Int,
    val availableTokens: Int,
) : RewardException(
    "Insufficient tokens for reward $rewardId: required $requiredTokens, available $availableTokens",
)

/**
 * Thrown when attempting to redeem a reward that requires approval.
 */
class RewardRequiresApprovalException(
    val rewardId: String,
) : RewardException("Reward requires caregiver approval: $rewardId")

/**
 * Thrown when user associated with reward is not found.
 */
class RewardUserNotFoundException(val userId: String) : RewardException(
    "User not found for reward operation: $userId",
)

/**
 * Thrown when attempting to create a reward with invalid token cost.
 */
class InvalidTokenCostException(
    val tokenCost: Int,
    val categoryMinCost: Int,
    val categoryMaxCost: Int,
) : RewardException(
    "Invalid token cost $tokenCost: must be between $categoryMinCost and $categoryMaxCost",
)

/**
 * Thrown when attempting to delete a predefined reward.
 */
class CannotDeletePredefinedRewardException(
    val rewardId: String,
) : RewardException("Cannot delete predefined reward: $rewardId")
