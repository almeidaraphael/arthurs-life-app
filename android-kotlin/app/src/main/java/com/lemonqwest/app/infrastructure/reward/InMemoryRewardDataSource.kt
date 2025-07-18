package com.lemonqwest.app.infrastructure.reward

import com.lemonqwest.app.domain.reward.Reward
import com.lemonqwest.app.domain.reward.RewardCategory
import com.lemonqwest.app.domain.reward.RewardDataSource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory implementation of RewardDataSource for LemonQwest MVP.
 *
 * This implementation provides reward persistence using in-memory storage,
 * following the same pattern as InMemoryTaskDataSource. It includes
 * pre-populated sample rewards across all six categories to demonstrate
 * the reward system functionality.
 *
 * The implementation uses a Mutex to ensure thread-safe operations on
 * the reward collection, supporting concurrent access from multiple coroutines.
 */
@Singleton
class InMemoryRewardDataSource
@Inject
constructor() : RewardDataSource {

    private val mutex = Mutex()
    private val rewards = mutableListOf<Reward>()

    init {
        // Initialize with sample rewards across all categories
        rewards.addAll(createSampleRewards())
    }

    /**
     * Creates a comprehensive set of sample rewards across all six categories.
     * These rewards demonstrate various reward types and provide
     * immediate content for children and caregivers to work with.
     */
    private fun createSampleRewards(): List<Reward> =
        createEntertainmentRewards() +
            createTreatsRewards() +
            createActivitiesRewards() +
            createPrivilegesRewards() +
            createToysRewards() +
            createExperiencesRewards()

    /**
     * Creates sample entertainment rewards (5-30 tokens).
     */
    private fun createEntertainmentRewards(): List<Reward> = listOf(
        Reward.createPredefined(
            title = "Extra Screen Time",
            description = "30 minutes of additional screen time",
            category = RewardCategory.ENTERTAINMENT,
            tokenCost = 15,
        ),
        Reward.createPredefined(
            title = "Choose Tonight's Movie",
            description = "Pick the family movie for tonight",
            category = RewardCategory.ENTERTAINMENT,
            tokenCost = 20,
        ),
        Reward.createPredefined(
            title = "Video Game Session",
            description = "1 hour of video game time",
            category = RewardCategory.ENTERTAINMENT,
            tokenCost = 25,
        ),
        Reward.createPredefined(
            title = "YouTube Videos",
            description = "Watch 3 favorite YouTube videos",
            category = RewardCategory.ENTERTAINMENT,
            tokenCost = 10,
        ),
    )

    /**
     * Creates sample treats rewards (10-25 tokens).
     */
    private fun createTreatsRewards(): List<Reward> = listOf(
        Reward.createPredefined(
            title = "Ice Cream Scoop",
            description = "One scoop of your favorite ice cream",
            category = RewardCategory.TREATS,
            tokenCost = 15,
        ),
        Reward.createPredefined(
            title = "Special Snack",
            description = "Choose a special snack from the pantry",
            category = RewardCategory.TREATS,
            tokenCost = 12,
        ),
        Reward.createPredefined(
            title = "Candy Bar",
            description = "Pick your favorite candy bar",
            category = RewardCategory.TREATS,
            tokenCost = 18,
        ),
        Reward.createPredefined(
            title = "Hot Chocolate",
            description = "Warm cup of hot chocolate with marshmallows",
            category = RewardCategory.TREATS,
            tokenCost = 20,
        ),
    )

    /**
     * Creates sample activities rewards (25-100 tokens).
     */
    private fun createActivitiesRewards(): List<Reward> = listOf(
        Reward.createPredefined(
            title = "Playground Visit",
            description = "Special trip to your favorite playground",
            category = RewardCategory.ACTIVITIES,
            tokenCost = 40,
        ),
        Reward.createPredefined(
            title = "Art & Craft Time",
            description = "30 minutes of art and craft activities",
            category = RewardCategory.ACTIVITIES,
            tokenCost = 35,
        ),
        Reward.createPredefined(
            title = "Nature Walk",
            description = "Explore the neighborhood or nearby park",
            category = RewardCategory.ACTIVITIES,
            tokenCost = 30,
        ),
        Reward.createPredefined(
            title = "Cooking Together",
            description = "Help make your favorite meal or dessert",
            category = RewardCategory.ACTIVITIES,
            tokenCost = 50,
        ),
    )

    /**
     * Creates sample privileges rewards (15-50 tokens).
     */
    private fun createPrivilegesRewards(): List<Reward> = listOf(
        Reward.createPredefined(
            title = "Later Bedtime",
            description = "Stay up 30 minutes past bedtime",
            category = RewardCategory.PRIVILEGES,
            tokenCost = 25,
        ),
        Reward.createPredefined(
            title = "Friend Visit",
            description = "Have a friend over for a playdate",
            category = RewardCategory.PRIVILEGES,
            tokenCost = 45,
        ),
        Reward.createPredefined(
            title = "Skip One Chore",
            description = "Skip your least favorite chore today",
            category = RewardCategory.PRIVILEGES,
            tokenCost = 20,
        ),
        Reward.createPredefined(
            title = "Choose Family Activity",
            description = "Pick what the family does this weekend",
            category = RewardCategory.PRIVILEGES,
            tokenCost = 35,
        ),
    )

    /**
     * Creates sample toys rewards (20-150 tokens).
     */
    private fun createToysRewards(): List<Reward> = listOf(
        Reward.createPredefined(
            title = "Small Toy",
            description = "Pick a small toy from the toy store",
            category = RewardCategory.TOYS,
            tokenCost = 75,
        ),
        Reward.createPredefined(
            title = "Craft Supplies",
            description = "New art supplies or craft materials",
            category = RewardCategory.TOYS,
            tokenCost = 50,
        ),
        Reward.createPredefined(
            title = "New Book",
            description = "Choose a new book to read",
            category = RewardCategory.TOYS,
            tokenCost = 40,
        ),
        Reward.createPredefined(
            title = "Collectible Item",
            description = "Add to your collection (cards, stickers, etc.)",
            category = RewardCategory.TOYS,
            tokenCost = 60,
        ),
    )

    /**
     * Creates sample experiences rewards (50-300 tokens).
     */
    private fun createExperiencesRewards(): List<Reward> = listOf(
        Reward.createPredefined(
            title = "Movie Theater",
            description = "Go see a movie at the theater",
            category = RewardCategory.EXPERIENCES,
            tokenCost = 100,
        ),
        Reward.createPredefined(
            title = "Restaurant Dinner",
            description = "Choose where the family goes for dinner",
            category = RewardCategory.EXPERIENCES,
            tokenCost = 150,
        ),
        Reward.createPredefined(
            title = "Amusement Park",
            description = "Day trip to an amusement park or fair",
            category = RewardCategory.EXPERIENCES,
            tokenCost = 250,
        ),
        Reward.createPredefined(
            title = "Special Event",
            description = "Attend a concert, show, or sports event",
            category = RewardCategory.EXPERIENCES,
            tokenCost = 200,
        ),
    )

    override suspend fun findById(id: String): Reward? =
        mutex.withLock {
            rewards.find { it.id == id }
        }

    override suspend fun getAllRewards(): List<Reward> =
        mutex.withLock {
            rewards.toList()
        }

    override suspend fun getAvailableRewards(): List<Reward> =
        mutex.withLock {
            rewards.filter { it.isAvailable }
        }

    override suspend fun findByCategory(category: RewardCategory): List<Reward> =
        mutex.withLock {
            rewards.filter { it.category == category }
        }

    override suspend fun findAvailableByCategory(category: RewardCategory): List<Reward> =
        mutex.withLock {
            rewards.filter { it.category == category && it.isAvailable }
        }

    override suspend fun findCustomByCreator(createdByUserId: String): List<Reward> =
        mutex.withLock {
            rewards.filter { it.isCustom && it.createdByUserId == createdByUserId }
        }

    override suspend fun getPredefinedRewards(): List<Reward> =
        mutex.withLock {
            rewards.filter { !it.isCustom }
        }

    override suspend fun getCustomRewards(): List<Reward> =
        mutex.withLock {
            rewards.filter { it.isCustom }
        }

    override suspend fun getApprovalRequiredRewards(): List<Reward> =
        mutex.withLock {
            rewards.filter { it.requiresApproval }
        }

    override suspend fun findAffordableRewards(tokenAmount: Int): List<Reward> =
        mutex.withLock {
            rewards.filter { it.tokenCost <= tokenAmount }
        }

    override suspend fun findAvailableAffordableRewards(tokenAmount: Int): List<Reward> =
        mutex.withLock {
            rewards.filter { it.isAvailable && it.tokenCost <= tokenAmount }
        }

    override suspend fun saveReward(reward: Reward): Unit =
        mutex.withLock {
            val existingIndex = rewards.indexOfFirst { it.id == reward.id }
            if (existingIndex >= 0) {
                rewards[existingIndex] = reward
            } else {
                rewards.add(reward)
            }
        }

    override suspend fun deleteReward(rewardId: String): Unit =
        mutex.withLock {
            rewards.removeIf { it.id == rewardId }
        }

    override suspend fun updateRewardAvailability(rewardId: String, isAvailable: Boolean): Unit =
        mutex.withLock {
            val index = rewards.indexOfFirst { it.id == rewardId }
            if (index >= 0) {
                rewards[index] = rewards[index].updateAvailability(isAvailable)
            }
        }

    override suspend fun updateRewardTokenCost(rewardId: String, newTokenCost: Int): Unit =
        mutex.withLock {
            val index = rewards.indexOfFirst { it.id == rewardId }
            if (index >= 0) {
                rewards[index] = rewards[index].updateTokenCost(newTokenCost)
            }
        }

    override suspend fun countCustomRewardsByCreator(createdByUserId: String): Int =
        mutex.withLock {
            rewards.count { it.isCustom && it.createdByUserId == createdByUserId }
        }

    override suspend fun countRewardsByCategory(category: RewardCategory): Int =
        mutex.withLock {
            rewards.count { it.category == category }
        }
}
