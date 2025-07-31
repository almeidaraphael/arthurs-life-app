package com.lemonqwest.app.infrastructure.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lemonqwest.app.domain.task.Task
import com.lemonqwest.app.domain.task.TaskCategory

/**
 * Room database entity for task persistence in LemonQwest MVP.
 *
 * This entity follows the simplified MVP schema defined in the planning documentation,
 * focusing on essential task properties without complex features like recurring
 * patterns or difficulty multipliers.
 *
 * The entity provides conversion methods to and from the domain Task model,
 * maintaining clean separation between persistence and domain layers.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val tokenReward: Int,
    val isCompleted: Boolean,
    val assignedToUserId: String,
    val createdAt: Long,
    val completedAt: Long?,
    val customIconName: String?,
    val customColorHex: String?,
) {
    /**
     * Converts this database entity to a domain Task model.
     *
     * @return Domain Task instance with converted category enum
     */
    fun toDomain(): Task =
        Task(
            id = id,
            title = title,
            category = TaskCategory.valueOf(category),
            tokenReward = tokenReward,
            isCompleted = isCompleted,
            assignedToUserId = assignedToUserId,
            createdAt = createdAt,
            completedAt = completedAt,
            customIconName = customIconName,
            customColorHex = customColorHex,
        )

    companion object {
        /**
         * Converts a domain Task model to a database entity.
         *
         * @param task Domain Task instance to convert
         * @return TaskEntity ready for database persistence
         */
        fun fromDomain(task: Task): TaskEntity =
            TaskEntity(
                id = task.id,
                title = task.title,
                category = task.category.name,
                tokenReward = task.tokenReward,
                isCompleted = task.isCompleted,
                assignedToUserId = task.assignedToUserId,
                createdAt = task.createdAt,
                completedAt = task.completedAt,
                customIconName = task.customIconName,
                customColorHex = task.customColorHex,
            )
    }
}
