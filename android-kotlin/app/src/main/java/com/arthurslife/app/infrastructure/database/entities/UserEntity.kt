package com.arthurslife.app.infrastructure.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arthurslife.app.domain.auth.PIN
import com.arthurslife.app.domain.user.AvatarType
import com.arthurslife.app.domain.user.TokenBalance
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val role: String,
    val tokenBalance: Int,
    val pinHash: String?,
    val displayName: String?,
    val avatarType: String,
    val avatarData: String,
    val favoriteColor: String?,
    val createdAt: Long,
) {
    fun toDomain(): User =
        User(
            id = id,
            name = name,
            role = UserRole.valueOf(role),
            tokenBalance = TokenBalance.create(tokenBalance),
            pin = pinHash?.let { PIN.fromHash(it) },
            displayName = displayName,
            avatarType = AvatarType.valueOf(avatarType),
            avatarData = avatarData,
            favoriteColor = favoriteColor,
        )

    companion object {
        fun fromDomain(user: User): UserEntity =
            UserEntity(
                id = user.id,
                name = user.name,
                role = user.role.name,
                tokenBalance = user.tokenBalance.getValue(),
                pinHash = user.pin?.getHash(),
                displayName = user.displayName,
                avatarType = user.avatarType.name,
                avatarData = user.avatarData,
                favoriteColor = user.favoriteColor,
                createdAt = System.currentTimeMillis(),
            )
    }
}
