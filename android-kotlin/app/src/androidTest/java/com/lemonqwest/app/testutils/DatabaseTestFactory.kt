package com.lemonqwest.app.testutils

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.lemonqwest.app.infrastructure.database.LemonQwestDatabase

/**
 * In-memory database factory for testing.
 */
object DatabaseTestFactory {
    /**
     * Creates an in-memory Room database for testing.
     *
     * @return Test database instance
     */
    fun createInMemoryDatabase(): LemonQwestDatabase {
        return Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LemonQwestDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
    }
}
