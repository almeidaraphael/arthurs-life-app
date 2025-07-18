package com.lemonqwest.app.testutils

import com.lemonqwest.app.infrastructure.database.LemonQwestDatabase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

/**
 * Base class for database tests.
 *
 * This class provides:
 * - In-memory database setup
 * - Automatic cleanup between tests
 */
@ExtendWith(DatabaseTestBase.DatabaseExtension::class)
abstract class DatabaseTestBase {

    /**
     * Database instance for this test.
     */
    protected lateinit var database: LemonQwestDatabase

    /**
     * Set up database before each test.
     */
    @BeforeEach
    open fun setUp() {
        database = DatabaseTestFactory.createInMemoryDatabase()
    }

    /**
     * Clean up after each test.
     */
    @AfterEach
    open fun tearDown() {
        database.close()
    }

    /**
     * JUnit 5 extension to provide database instances as test parameters.
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
