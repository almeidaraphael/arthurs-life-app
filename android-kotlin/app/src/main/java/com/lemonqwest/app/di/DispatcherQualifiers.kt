package com.lemonqwest.app.di

import javax.inject.Qualifier

/**
 * Qualifier for IO dispatcher used for background tasks like:
 * - Database operations
 * - File I/O
 * - Network requests
 * - Repository operations
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/**
 * Qualifier for Main dispatcher used for:
 * - UI updates
 * - ViewModel operations
 * - LiveData/StateFlow emissions
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher
