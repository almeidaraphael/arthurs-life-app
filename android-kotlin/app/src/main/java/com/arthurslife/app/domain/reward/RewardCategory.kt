package com.arthurslife.app.domain.reward

import kotlinx.serialization.Serializable

/**
 * Defines the six core reward categories for Arthur's Life reward system.
 *
 * Each category has a suggested token cost range based on typical value and effort:
 * - Entertainment: Digital and screen-based activities (5-30 tokens)
 * - Treats: Snacks, desserts, and special foods (10-25 tokens)
 * - Activities: Outings, experiences, and family time (25-100 tokens)
 * - Privileges: Special permissions and benefits (15-50 tokens)
 * - Toys: Physical toys, craft supplies, and collectibles (20-150 tokens)
 * - Experiences: Movies, restaurants, events, and adventures (50-300 tokens)
 *
 * The token cost ranges provide guidance for caregivers while allowing flexibility
 * for family-specific customization.
 */
@Serializable
enum class RewardCategory(
    val displayName: String,
    val description: String,
    val minTokenCost: Int,
    val maxTokenCost: Int,
    val emoji: String,
) {
    /**
     * Entertainment and screen-based activities.
     *
     * Examples: Extra screen time, video games, movies, music privileges
     * Token Range: 5-30 tokens (quick, accessible rewards)
     */
    ENTERTAINMENT(
        displayName = "Entertainment",
        description = "Screen time, games, videos, and digital activities",
        minTokenCost = 5,
        maxTokenCost = 30,
        emoji = "📺",
    ),

    /**
     * Treats and special foods.
     *
     * Examples: Snacks, desserts, special drinks, favorite foods
     * Token Range: 10-25 tokens (consumable rewards)
     */
    TREATS(
        displayName = "Treats",
        description = "Snacks, desserts, and special foods",
        minTokenCost = 10,
        maxTokenCost = 25,
        emoji = "🍿",
    ),

    /**
     * Activities and experiences.
     *
     * Examples: Outings, playground visits, special activities, family time
     * Token Range: 25-100 tokens (experiential rewards)
     */
    ACTIVITIES(
        displayName = "Activities",
        description = "Outings, experiences, and special family time",
        minTokenCost = 25,
        maxTokenCost = 100,
        emoji = "🎯",
    ),

    /**
     * Privileges and special permissions.
     *
     * Examples: Later bedtime, friend visits, special permissions
     * Token Range: 15-50 tokens (behavioral rewards)
     */
    PRIVILEGES(
        displayName = "Privileges",
        description = "Special permissions and benefits",
        minTokenCost = 15,
        maxTokenCost = 50,
        emoji = "🔓",
    ),

    /**
     * Toys and physical items.
     *
     * Examples: Small toys, craft supplies, collectibles, books
     * Token Range: 20-150 tokens (tangible rewards)
     */
    TOYS(
        displayName = "Toys",
        description = "Physical toys, craft supplies, and collectibles",
        minTokenCost = 20,
        maxTokenCost = 150,
        emoji = "🧸",
    ),

    /**
     * Experiences and major activities.
     *
     * Examples: Movies, restaurants, events, adventures, special trips
     * Token Range: 50-300 tokens (premium experiences)
     */
    EXPERIENCES(
        displayName = "Experiences",
        description = "Movies, restaurants, events, and special adventures",
        minTokenCost = 50,
        maxTokenCost = 300,
        emoji = "🎪",
    ),
}
