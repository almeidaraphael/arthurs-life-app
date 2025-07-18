package com.lemonqwest.app.testutils

import com.lemonqwest.app.infrastructure.database.LemonQwestDatabase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

/**
 * Enhanced base class for database tests with complete isolation.
 *
 * This class provides:
 * - In-memory database setup with fresh instances per test
 * - Complete resource cleanup between tests
 * - Thread-safe database instance management
 * - Inheritance from AndroidTestBase for full test isolation
 *
 * For tests that need both database and Hilt injection, extend AndroidTestBase directly
 * and use DatabaseTestFactory.createInMemoryDatabase() manually.
 *
 * Usage:
 * ```kotlin
 * class MyDaoTest : DatabaseTestBase() {
 *
 *     @Test
 *     fun `should test database operation`() = runTest {
 *         val dao = database.myDao()
 *         // Test implementation with isolated database
 *     }
 * }
 * ```
 */
@ExtendWith(DatabaseTestBase.DatabaseExtension::class)
abstract class DatabaseTestBase : AndroidTestBase() {

    /**
     * Database instance for this test.
     * Each test gets a fresh in-memory database instance.
     */
    protected lateinit var database: LemonQwestDatabase

    /**
     * Set up database before each test.
     * Called after AndroidTestBase.setUpAndroidTest().
     */
    @BeforeEach
    override fun setUpAndroidTest() {
        // Call parent setup first for complete isolation
        super.setUpAndroidTest()

        // Create fresh database instance for this test
        database = DatabaseTestFactory.createInMemoryDatabase()
    }

    /**
     * Clean up after each test.
     * Called before AndroidTestBase.tearDownAndroidTest().
     */
    @AfterEach
    override fun tearDownAndroidTest() {
        // Close database first
        if (::database.isInitialized) {
            database.close()
        }

        // Call parent cleanup for complete isolation
        super.tearDownAndroidTest()
    }

    /**
     * JUnit 5 extension to provide database instances as test parameters.
     * Creates fresh database instances for parameter injection.
     */
    class DatabaseExtension : ParameterResolver {
        override fun supportsParameter(
            parameterContext: ParameterContext,
            extensionContext: ExtensionContext,
        ): Boolean {
            return parameterContext.parameter.type == LemonQwestDatabase::class.java
        }

        override fun resolveParameter(
            parameterContext: ParameterContext,
            extensionContext: ExtensionContext,
        ): Any {
            return DatabaseTestFactory.createInMemoryDatabase()
        }
    }
}
