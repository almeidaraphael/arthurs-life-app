package com.lemonqwest.app.domain

import com.lemonqwest.app.domain.achievement.Achievement
import com.lemonqwest.app.domain.achievement.AchievementType
import com.lemonqwest.app.domain.task.Task
import com.lemonqwest.app.domain.task.TaskCategory
import com.lemonqwest.app.domain.user.User
import com.lemonqwest.app.domain.user.UserRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue

/**
 * Essential domain-specific assertion extensions for LemonQwest testing.
 *
 * This file provides only the actively used assertion methods.
 * Reduced from 408 lines to comply with Lean Testing Manifesto principles.
 */

// ==================================================
// User Assertion Extensions (ACTIVELY USED)
// ==================================================

/**
 * Asserts that a user is a child with expected properties.
 */
fun User.shouldBeChild(
    expectedName: String = this.name,
    expectedTokens: Int = this.tokenBalance.getValue(),
) {
    assertEquals(UserRole.CHILD, this.role, "User should be a child")
    assertEquals(expectedName, this.name, "User name should match")
    assertEquals(expectedTokens, this.tokenBalance.getValue(), "Token balance should match")
    assertNull(this.pin, "Child users should not have a PIN")
}

/**
 * Asserts that a user is a caregiver with expected properties.
 */
fun User.shouldBeCaregiver(
    expectedName: String = this.name,
    shouldHavePin: Boolean = true,
) {
    assertEquals(UserRole.CAREGIVER, this.role, "User should be a caregiver")
    assertEquals(expectedName, this.name, "User name should match")
    if (shouldHavePin) {
        assertNotNull(this.pin, "Caregiver should have a PIN")
    } else {
        assertNull(this.pin, "Caregiver should not have a PIN")
    }
}

/**
 * Asserts that a user can afford a specific cost.
 */
fun User.shouldAfford(cost: Int) {
    assertTrue(
        this.tokenBalance.canAfford(cost),
        "User with ${this.tokenBalance.getValue()} tokens should afford $cost tokens",
    )
}

/**
 * Asserts that a user cannot afford a specific cost.
 */
fun User.shouldNotAfford(cost: Int) {
    assertFalse(
        this.tokenBalance.canAfford(cost),
        "User with ${this.tokenBalance.getValue()} tokens should not afford $cost tokens",
    )
}

// ==================================================
// Task Assertion Extensions (ACTIVELY USED)
// ==================================================

/**
 * Asserts that a task is completed.
 */
fun Task.shouldBeCompleted() {
    assertTrue(this.isCompleted, "Task '${this.title}' should be completed")
}

/**
 * Asserts that a task is not completed.
 */
fun Task.shouldNotBeCompleted() {
    assertFalse(this.isCompleted, "Task '${this.title}' should not be completed")
}

/**
 * Asserts that a task can be completed.
 */
fun Task.shouldBeCompletable() {
    assertTrue(this.canBeCompleted(), "Task '${this.title}' should be completable")
}

/**
 * Asserts that a task cannot be completed.
 */
fun Task.shouldNotBeCompletable() {
    assertFalse(this.canBeCompleted(), "Task '${this.title}' should not be completable")
}

/**
 * Asserts that a task has expected properties.
 */
fun Task.shouldHaveProperties(
    expectedTitle: String = this.title,
    expectedCategory: TaskCategory = this.category,
    expectedReward: Int = this.tokenReward,
) {
    assertEquals(expectedTitle, this.title, "Task title should match")
    assertEquals(expectedCategory, this.category, "Task category should match")
    assertEquals(expectedReward, this.tokenReward, "Task reward should match")
}

/**
 * Asserts that a task is assigned to a specific user.
 */
fun Task.shouldBeAssignedTo(expectedUserId: String) {
    assertEquals(expectedUserId, this.assignedToUserId, "Task should be assigned to expected user")
}

/**
 * Asserts that a task has the default reward for its category.
 */
fun Task.shouldHaveDefaultReward() {
    assertEquals(
        this.category.defaultTokenReward,
        this.tokenReward,
        "Task should have default reward for ${this.category.displayName}",
    )
}

// ==================================================
// Achievement Assertion Extensions (ACTIVELY USED)
// ==================================================

/**
 * Asserts that an achievement is unlocked.
 */
fun Achievement.shouldBeUnlocked() {
    assertTrue(this.isUnlocked, "Achievement '${this.name}' should be unlocked")
    assertNotNull(this.unlockedAt, "Unlocked achievement should have unlock timestamp")
    assertEquals(
        this.target,
        this.progress,
        "Unlocked achievement should have progress equal to target",
    )
}

/**
 * Asserts that an achievement is not unlocked.
 */
fun Achievement.shouldNotBeUnlocked() {
    assertFalse(this.isUnlocked, "Achievement '${this.name}' should not be unlocked")
    assertNull(this.unlockedAt, "Locked achievement should not have unlock timestamp")
}

/**
 * Asserts that an achievement can be unlocked.
 */
fun Achievement.shouldBeUnlockable() {
    assertTrue(this.canBeUnlocked(), "Achievement '${this.name}' should be unlockable")
}

/**
 * Asserts that an achievement cannot be unlocked.
 */
fun Achievement.shouldNotBeUnlockable() {
    assertFalse(this.canBeUnlocked(), "Achievement '${this.name}' should not be unlockable")
}

/**
 * Asserts that an achievement has expected progress.
 */
fun Achievement.shouldHaveProgress(
    expectedProgress: Int = this.progress,
    expectedPercentage: Int = this.progressPercentage,
) {
    assertEquals(expectedProgress, this.progress, "Achievement progress should match")
    assertEquals(
        expectedPercentage,
        this.progressPercentage,
        "Achievement progress percentage should match",
    )
}

/**
 * Asserts that an achievement has expected properties.
 */
fun Achievement.shouldHaveProperties(
    expectedType: AchievementType = this.type,
    expectedUserId: String = this.userId,
) {
    assertEquals(expectedType, this.type, "Achievement type should match")
    assertEquals(expectedUserId, this.userId, "Achievement user ID should match")
    assertEquals(expectedType.displayName, this.name, "Achievement name should match type")
    assertEquals(
        expectedType.description,
        this.description,
        "Achievement description should match type",
    )
    assertEquals(expectedType.target, this.target, "Achievement target should match type")
}

// ==================================================
// Collection Assertion Extensions (ACTIVELY USED)
// ==================================================

/**
 * Asserts that a collection of tasks has expected completion count.
 */
fun Collection<Task>.shouldHaveCompletedCount(expectedCompleted: Int) {
    val actualCompleted = this.count { it.isCompleted }
    assertEquals(
        expectedCompleted,
        actualCompleted,
        "Collection should have $expectedCompleted completed tasks",
    )
}

/**
 * Asserts that a collection of achievements has expected unlock count.
 */
fun Collection<Achievement>.shouldHaveUnlockedCount(expectedUnlocked: Int) {
    val actualUnlocked = this.count { it.isUnlocked }
    assertEquals(
        expectedUnlocked,
        actualUnlocked,
        "Collection should have $expectedUnlocked unlocked achievements",
    )
}

/**
 * Asserts that a collection of achievements has expected type distribution.
 */
fun Collection<Achievement>.shouldHaveTypeDistribution(
    expectedTypeCounts: Map<AchievementType, Int>,
) {
    val actualCounts = this.groupingBy { it.type }.eachCount()
    assertEquals(expectedTypeCounts, actualCounts, "Achievement type distribution should match")
}
