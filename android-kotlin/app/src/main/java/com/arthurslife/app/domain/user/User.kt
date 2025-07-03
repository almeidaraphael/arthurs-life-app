package com.arthurslife.app.domain.user

import com.arthurslife.app.domain.auth.PIN
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: UserRole = UserRole.CHILD,
    val tokenBalance: TokenBalance = TokenBalance.zero(),
    val pin: PIN? = null,
)

@Serializable
enum class UserRole {
    CHILD,
    CAREGIVER,
}
