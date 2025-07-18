package com.lemonqwest.app.domain.task

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.UUID

/**
 * Test suite for Task random data testing scenarios with complete test isolation.
 *
 * Tests cover:
 * - Diverse random task generation with test isolation
 * - Random task property validation with parallel safety
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Task Random Data Testing Tests")
class TaskRandomDataTestingTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should generate diverse random tasks")
    fun shouldGenerateDiverseRandomTasks() = runTest {
        val randomTasks = (1..50).map { TestDataFactory.createRandomTask() }

        // Verify diversity
        val categories = randomTasks.map { it.category }.toSet()
        val completionStates = randomTasks.map { it.isCompleted }.toSet()

        assertTrue(categories.size > 1, "Should generate tasks with different categories")
        assertTrue(
            completionStates.size > 1,
            "Should generate tasks with different completion states",
        )

        // Verify all tasks have valid properties
        randomTasks.forEach { task ->
            assertNotNull(task.id, "Random task should have ID")
            assertTrue(task.title.isNotBlank(), "Random task should have non-blank title")
            assertTrue(task.tokenReward >= 0, "Random task should have non-negative reward")
            assertNotNull(
                UUID.fromString(task.assignedToUserId),
                "Random task should have valid user ID",
            )
        }
    }
}
