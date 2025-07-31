package com.lemonqwest.app.domain.common

import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simple date representation for API compatibility.
 */
data class SimpleDate(
    val year: Int,
    val month: Int, // 1-12
    val day: Int,
) {
    fun isBefore(other: SimpleDate): Boolean {
        return when {
            year < other.year -> true
            year > other.year -> false
            month < other.month -> true
            month > other.month -> false
            day < other.day -> true
            else -> false
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is SimpleDate &&
            year == other.year &&
            month == other.month &&
            day == other.day
    }

    override fun hashCode(): Int {
        return year * 10000 + month * 100 + day
    }
}

/**
 * Provides current date functionality with testable abstraction.
 *
 * This allows for proper unit testing by injecting mock dates
 * and ensures consistent date handling across the application.
 */
interface DateProvider {
    /**
     * Gets the current date in the system timezone.
     *
     * @return Current SimpleDate
     */
    fun getCurrentDate(): SimpleDate

    /**
     * Converts timestamp to SimpleDate in system timezone.
     *
     * @param timestamp Milliseconds since epoch
     * @return SimpleDate representation
     */
    fun timestampToDate(timestamp: Long): SimpleDate

    /**
     * Compares two SimpleDate instances safely for all API levels.
     *
     * @param date1 First date to compare
     * @param date2 Second date to compare
     * @return true if date1 is before date2, false otherwise
     */
    fun isBefore(date1: SimpleDate, date2: SimpleDate): Boolean
}

/**
 * Production implementation of DateProvider using system clock.
 */
@Singleton
class SystemDateProvider @Inject constructor() : DateProvider {

    override fun getCurrentDate(): SimpleDate {
        val calendar = Calendar.getInstance()
        return SimpleDate(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH) + 1, // Calendar.MONTH is 0-based
            day = calendar.get(Calendar.DAY_OF_MONTH),
        )
    }

    override fun timestampToDate(timestamp: Long): SimpleDate {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timestamp)
        return SimpleDate(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH) + 1, // Calendar.MONTH is 0-based
            day = calendar.get(Calendar.DAY_OF_MONTH),
        )
    }

    override fun isBefore(date1: SimpleDate, date2: SimpleDate): Boolean {
        return date1.isBefore(date2)
    }
}
