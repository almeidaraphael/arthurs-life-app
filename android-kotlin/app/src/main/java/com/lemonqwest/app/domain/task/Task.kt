package com.lemonqwest.app.domain.task

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Core domain entity representing a task in the LemonQwest MVP task management system.
 *
 * This simplified entity focuses on essential task properties for the MVP:
 * - Basic task identification and description
 * - Simple category classification (3 categories only)
 * - Fixed token rewards per category
 * - Completion status tracking
 * - Child assignment for single-child families
 *
 * @property id Unique identifier for the task
 * @property title Task name displayed to users
 * @property category Task category determining token reward amount
 * @property tokenReward Number of tokens awarded upon completion
 * @property isCompleted Whether the task has been completed
 * @property assignedToUserId ID of the child assigned to this task
 * @property createdAt Timestamp when task was created
 * @property completedAt Timestamp when task was completed (null if not completed)
 */
@Serializable
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: TaskCategory,
    val tokenReward: Int,
    val isCompleted: Boolean = false,
    val assignedToUserId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val customIconName: String? = null,
    val customColorHex: String? = null,
) {
    /**
     * Creates a completed copy of this task with tokens awarded.
     *
     * @return A new Task instance marked as completed
     */
    fun markCompleted(): Task = copy(
        isCompleted = true,
        completedAt = System.currentTimeMillis(),
    )

    /**
     * Creates an uncompleted copy of this task.
     *
     * @return A new Task instance marked as not completed
     */
    fun markIncomplete(): Task = copy(
        isCompleted = false,
        completedAt = null,
    )

    /**
     * Checks if this task can be completed (not already completed).
     *
     * @return true if task can be completed, false if already completed
     */
    fun canBeCompleted(): Boolean = !isCompleted

    /**
     * Gets the display icon name for this task, falling back to category default.
     *
     * @return Custom icon name if set, otherwise category default icon
     */
    fun getDisplayIcon(): String = customIconName ?: category.defaultIconName

    /**
     * Gets the display color hex for this task, falling back to category default.
     *
     * @return Custom color hex if set, otherwise category default color
     */
    fun getDisplayColor(): String = customColorHex ?: category.defaultColorHex

    /**
     * Creates a copy of this task with custom visual attributes.
     *
     * @param iconName Custom icon name
     * @param colorHex Custom color hex code
     * @return New Task instance with updated visual attributes
     */
    fun withCustomVisuals(iconName: String?, colorHex: String?): Task = copy(
        customIconName = iconName,
        customColorHex = colorHex,
    )

    companion object {
        /**
         * Creates a new task with appropriate token reward based on category.
         *
         * @param title Task title
         * @param category Task category
         * @param assignedToUserId ID of assigned child
         * @return New Task instance with category-appropriate token reward
         */
        fun create(
            title: String,
            category: TaskCategory,
            assignedToUserId: String,
            customIconName: String? = null,
            customColorHex: String? = null,
        ): Task =
            Task(
                title = title,
                category = category,
                tokenReward = category.defaultTokenReward,
                assignedToUserId = assignedToUserId,
                customIconName = customIconName,
                customColorHex = customColorHex,
            )
    }
}
