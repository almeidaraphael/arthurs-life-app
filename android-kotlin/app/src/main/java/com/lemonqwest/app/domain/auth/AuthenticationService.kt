package com.lemonqwest.app.domain.auth

import com.lemonqwest.app.domain.user.User
import com.lemonqwest.app.domain.user.UserRole

interface AuthenticationService {
    suspend fun authenticateWithPin(pin: String): AuthResult

    suspend fun switchRole(targetRole: UserRole): AuthResult

    suspend fun authenticateAsChild(): AuthResult

    suspend fun logout()
}

interface AuthenticationSessionService {
    suspend fun getCurrentUser(): User?

    suspend fun isAuthenticated(): Boolean
}
