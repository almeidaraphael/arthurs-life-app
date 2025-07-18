package com.lemonqwest.app.domain.user

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.auth.PIN
import com.lemonqwest.app.domain.shouldBeCaregiver
import com.lemonqwest.app.domain.shouldBeChild
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

/**
 * Focused test suite for User role-based access control.
 *
 * Tests cover:
 * - Role assignment validation
 * - CHILD vs CAREGIVER constraints
 * - PIN requirements per role
 * - Role-based business rules
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("User Role Tests")
class UserRoleTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @ParameterizedTest
    @EnumSource(UserRole::class)
    @DisplayName("Should properly assign user roles")
    fun shouldAssignUserRoles(role: UserRole) {
        val user = User(
            name = "Test User",
            role = role,
            tokenBalance = TokenBalance.create(10),
            pin = if (role == UserRole.CAREGIVER) PIN.create("1234") else null,
        )

        assertEquals(role, user.role, "User should have assigned role")

        when (role) {
            UserRole.CHILD -> {
                assertNull(user.pin, "Child users should not have PIN")
            }
            UserRole.CAREGIVER -> {
                assertNotNull(user.pin, "Caregiver users should have PIN")
            }
        }
    }

    @Test
    @DisplayName("Should validate child user constraints")
    fun shouldValidateChildUserConstraints() {
        val child = TestDataFactory.createChildUser()

        child.shouldBeChild()
        assertNull(child.pin, "Child should not have PIN for security")
    }

    @Test
    @DisplayName("Should validate caregiver user constraints")
    fun shouldValidateCaregiverUserConstraints() {
        val caregiver = TestDataFactory.createCaregiverUser()

        caregiver.shouldBeCaregiver()
        assertNotNull(caregiver.pin, "Caregiver should have PIN for authentication")
    }

    @Test
    @DisplayName("Should allow caregiver without PIN for specific scenarios")
    fun shouldAllowCaregiverWithoutPin() {
        val caregiver = User(
            name = "Guest Caregiver",
            role = UserRole.CAREGIVER,
            tokenBalance = TokenBalance.zero(),
            pin = null,
        )

        caregiver.shouldBeCaregiver(shouldHavePin = false)
    }
}
