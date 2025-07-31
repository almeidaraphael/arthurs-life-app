package com.lemonqwest.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/**
 * Test module that replaces the production CoroutineModule during testing.
 *
 * Provides test-friendly dispatchers that:
 * - Execute immediately without threading complexity
 * - Enable deterministic testing
 * - Support parallel test execution
 * - Eliminate dispatcher race conditions
 *
 * This module automatically replaces CoroutineModule in test environments
 * via Hilt's @TestInstallIn annotation.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CoroutineModule::class],
)
object TestDispatcherModule {

    /**
     * Provides UnconfinedTestDispatcher for IO operations in tests.
     * This dispatcher executes tasks immediately on the calling thread.
     */
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = UnconfinedTestDispatcher()

    /**
     * Provides UnconfinedTestDispatcher for Main operations in tests.
     * This dispatcher executes tasks immediately on the calling thread.
     */
    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = UnconfinedTestDispatcher()
}
