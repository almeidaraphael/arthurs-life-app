package com.lemonqwest.app.testutils

import com.lemonqwest.app.infrastructure.database.LemonQwestDatabase

/**
 * Database cleanup utilities.
 */
object DatabaseCleanupUtils {
    /**
     * Clears all data from the database.
     */
    suspend fun clearDatabase(database: LemonQwestDatabase) {
        database.clearAllTables()
    }
}
