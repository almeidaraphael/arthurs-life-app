package com.arthurslife.app.domain.auth

import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRole

data class AuthenticationState(
    val currentUser: User? = null,
    val isAuthenticated: Boolean = false,
    val currentRole: UserRole? = null,
)

sealed class AuthResult {
    data class Success(
        val user: User,
    ) : AuthResult()

    object InvalidPin : AuthResult()

    object UserNotFound : AuthResult()
}

interface AuthRepository {
    suspend fun authenticateWithPin(pin: String): AuthResult

    suspend fun switchRole(targetRole: UserRole): AuthResult

    suspend fun getCurrentUser(): User?

    suspend fun logout()
}
