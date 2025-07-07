package com.arthurslife.app.domain.task

import kotlinx.serialization.Serializable

/**
 * Defines the three core task categories for the Arthur's Life MVP.
 *
 * Each category has a predefined token reward amount based on typical effort required:
 * - Personal Care: Basic daily hygiene and self-care tasks (5 tokens)
 * - Household: Helping with chores and family responsibilities (10 tokens)
 * - Homework: Educational and learning activities (15 tokens)
 *
 * The token rewards are fixed per category to keep the MVP simple while providing
 * appropriate incentives for different types of tasks.
 */
@Serializable
enum class TaskCategory(
    val displayName: String,
    val description: String,
    val defaultTokenReward: Int,
) {
    /**
     * Personal care and hygiene tasks.
     *
     * Examples: Brush teeth, wash hands, comb hair, get dressed
     * Token Reward: 5 tokens (basic daily activities)
     */
    PERSONAL_CARE(
        displayName = "Personal Care",
        description = "Daily hygiene and self-care tasks",
        defaultTokenReward = 5,
    ),

    /**
     * Household chores and family responsibilities.
     *
     * Examples: Make bed, clean room, help with dishes, feed pets
     * Token Reward: 10 tokens (contributing to family)
     */
    HOUSEHOLD(
        displayName = "Household",
        description = "Chores and family responsibilities",
        defaultTokenReward = 10,
    ),

    /**
     * Educational and learning activities.
     *
     * Examples: Complete homework, read book, practice math, study
     * Token Reward: 15 tokens (educational priority)
     */
    HOMEWORK(
        displayName = "Homework",
        description = "Educational and learning activities",
        defaultTokenReward = 15,
    ),
}
