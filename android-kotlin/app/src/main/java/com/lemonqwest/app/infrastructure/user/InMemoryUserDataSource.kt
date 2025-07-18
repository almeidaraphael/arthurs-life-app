package com.lemonqwest.app.infrastructure.user

import com.lemonqwest.app.domain.user.User
import com.lemonqwest.app.domain.user.UserDataSource
import com.lemonqwest.app.domain.user.UserRole
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryUserDataSource
@Inject
constructor() : UserDataSource {
    private val mutex = Mutex()
    private val users = mutableListOf<User>()

    init {
        // InMemoryUserDataSource is now used only for testing - no hardcoded demo users
        // Production code should use RoomUserDataSource for persistent storage
    }

    override suspend fun findByPin(pin: String): User? =
        mutex.withLock {
            users.find { user ->
                user.pin?.verify(pin) == true
            }
        }

    override suspend fun findById(id: String): User? =
        mutex.withLock {
            users.find { it.id == id }
        }

    override suspend fun findByRole(role: UserRole): User? =
        mutex.withLock {
            users.find { it.role == role }
        }

    override suspend fun getAllUsers(): List<User> =
        mutex.withLock {
            users.toList()
        }

    override suspend fun saveUser(user: User): Unit =
        mutex.withLock {
            val existingIndex = users.indexOfFirst { it.id == user.id }
            if (existingIndex >= 0) {
                users[existingIndex] = user
            } else {
                users.add(user)
            }
        }

    override suspend fun saveUsers(users: List<User>): Unit =
        mutex.withLock {
            users.forEach { user ->
                val existingIndex = this.users.indexOfFirst { it.id == user.id }
                if (existingIndex >= 0) {
                    this.users[existingIndex] = user
                } else {
                    this.users.add(user)
                }
            }
        }
}
