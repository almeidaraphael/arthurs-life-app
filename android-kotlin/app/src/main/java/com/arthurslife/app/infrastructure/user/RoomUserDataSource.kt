package com.arthurslife.app.infrastructure.user

import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserDataSource
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.infrastructure.database.dao.UserDao
import com.arthurslife.app.infrastructure.database.entities.UserEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room database implementation of UserDataSource.
 *
 * This implementation provides persistent storage for users using Room database,
 * replacing the in-memory storage for production use. It maintains thread safety
 * through Room's built-in coroutine support and provides reliable data persistence
 * across app restarts.
 */
@Singleton
class RoomUserDataSource @Inject constructor(
    private val userDao: UserDao,
) : UserDataSource {

    override suspend fun findByPin(pin: String): User? {
        val allUsers = userDao.getAllUsers()
        return allUsers.find { userEntity ->
            userEntity.pinHash?.let { hash ->
                val storedPin = com.arthurslife.app.domain.auth.PIN.fromHash(hash)
                storedPin.verify(pin)
            } ?: false
        }?.toDomain()
    }

    override suspend fun findById(id: String): User? {
        return userDao.getUserById(id)?.toDomain()
    }

    override suspend fun findByRole(role: UserRole): User? {
        return userDao.getUsersByRole(role.name).firstOrNull()?.toDomain()
    }

    override suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers().map { it.toDomain() }
    }

    override suspend fun saveUser(user: User) {
        val entity = UserEntity.fromDomain(user)
        userDao.insertUser(entity)
    }

    override suspend fun saveUsers(users: List<User>) {
        val entities = users.map { UserEntity.fromDomain(it) }
        userDao.insertUsers(entities)
    }
}
