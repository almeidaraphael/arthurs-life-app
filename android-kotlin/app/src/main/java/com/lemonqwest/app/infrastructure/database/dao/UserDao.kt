package com.lemonqwest.app.infrastructure.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.lemonqwest.app.infrastructure.database.entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE pinHash = :pinHash")
    suspend fun getUserByPinHash(pinHash: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = :role")
    suspend fun getUsersByRole(role: String): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: String)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>
}
