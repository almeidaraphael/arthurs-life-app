package com.arthurslife.app.domain.theme.model

// Theme capabilities interface for extensibility
interface ThemeCapabilities {
    val hasCustomIcons: Boolean
    val hasCustomShapes: Boolean
    val hasCustomAvatars: Boolean
    val hasCustomProgressIndicators: Boolean
    val hasCustomMotivationalContent: Boolean
}

// Default Material Design capabilities
object MaterialThemeCapabilities : ThemeCapabilities {
    override val hasCustomIcons: Boolean = false
    override val hasCustomShapes: Boolean = false
    override val hasCustomAvatars: Boolean = false
    override val hasCustomProgressIndicators: Boolean = false
    override val hasCustomMotivationalContent: Boolean = false
}

// Mario theme capabilities
object MarioThemeCapabilities : ThemeCapabilities {
    override val hasCustomIcons: Boolean = true
    override val hasCustomShapes: Boolean = true
    override val hasCustomAvatars: Boolean = true
    override val hasCustomProgressIndicators: Boolean = true
    override val hasCustomMotivationalContent: Boolean = true
}

// Extension function to get capabilities for any theme
fun AppTheme.getCapabilities(): ThemeCapabilities {
    return when (this) {
        AppTheme.MARIO_CLASSIC -> MarioThemeCapabilities
        AppTheme.MATERIAL_LIGHT, AppTheme.MATERIAL_DARK -> MaterialThemeCapabilities
    }
}

// Theme-specific content mappings
object ThemeContentMappings {
    // Motivational content for different themes
    fun getMotivationalEmoji(theme: AppTheme): String {
        return when (theme) {
            AppTheme.MARIO_CLASSIC -> "⭐" // Mario star
            else -> "🔥" // Fire emoji for standard themes
        }
    }

    fun getMotivationalMessage(theme: AppTheme, streak: Int): String {
        return when (theme) {
            AppTheme.MARIO_CLASSIC -> "$streak-day adventure! Keep collecting stars!"
            else -> "$streak-day streak! Keep it up!"
        }
    }

    fun getMotivationalSubMessage(theme: AppTheme): String {
        return when (theme) {
            AppTheme.MARIO_CLASSIC -> "You're on a super quest! Complete tasks to power up and earn coins."
            else -> "You're doing amazing! Keep completing tasks to earn more tokens."
        }
    }

    // Token/Currency names
    fun getTokenName(theme: AppTheme): String {
        return when (theme) {
            AppTheme.MARIO_CLASSIC -> "Coins"
            else -> "Tokens"
        }
    }

    // Progress descriptions
    fun getProgressDescription(theme: AppTheme, percentage: Int): String {
        return when (theme) {
            AppTheme.MARIO_CLASSIC -> "$percentage% of your quest complete!"
            else -> "$percentage% Complete"
        }
    }
}
