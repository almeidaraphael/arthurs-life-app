package com.lemonqwest.app.testutils

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule

/**
 * Simple base class for Hilt instrumentation tests.
 *
 * Provides basic Hilt dependency injection setup without performance optimizations
 * or caching mechanisms that violate lean testing principles.
 */
@HiltAndroidTest
abstract class HiltTestBase {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    open fun setUpHilt() {
        hiltRule.inject()
    }
}
