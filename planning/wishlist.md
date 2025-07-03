# Wishlist System

Comprehensive wishlist system design for savings goals, family integration, and motivation enhancement.

## Wishlist Features

### Wishlist Management
- Children can add any reward to their wishlist regardless of current token balance
- Visual progress indicators show how close child is to affording each wishlist item
- Automatic suggestions for similar or lower-cost alternatives
- Wishlist items persist across app sessions and device changes

### Savings Goals
- Children can set specific wishlist items as "savings goals"
- Progress tracking shows tokens needed and estimated completion time
- Optional "savings mode" can lock a portion of tokens toward specific goals
- Achievement unlocks when savings goals are reached

### Family Integration
- Caregivers can view child's complete wishlist with progress tracking and gift planning insights
- Caregiver dashboard shows child's top wishlist item and percentage progress
- Quick actions allow caregivers to add bonus tokens toward specific wishlist items
- Gift planning recommendations highlight items child has wanted longest or is closest to affording
- Admin can see family-wide wishlist trends and popular items across all children
- Special family events can contribute bonus tokens toward wishlist items
- Wishlist sharing allows siblings to support each other's goals
- Caregiver notifications when child is close to affording a wishlist item
- Integration with reward management system for creating wishlist-based custom rewards

### Motivation Features
- Wishlist items show progress percentages and visual progress bars
- "Almost there!" notifications when child is within 10% of affording an item
- Celebration animations when wishlist items become affordable
- Wishlist achievements for reaching certain milestones

## Wishlist User Flows

### Adding to Wishlist (Child)
1. Rewards tab → browses available rewards
2. Finds desired but unaffordable item
3. Taps "Add to Wishlist" button
4. Optional: Set as savings goal
5. Item appears in wishlist section with progress tracking

### Purchasing from Wishlist (Child)
1. Rewards tab → wishlist section
2. Views progress on wishlist items
3. Selects affordable wishlist item
4. Confirms purchase → item removed from wishlist
5. Celebrates achievement of reaching savings goal

### Viewing Child's Wishlist (Caregiver)
1. Dashboard → Child overview
2. Taps "View Wishlist" in child summary
3. Sees child's current wishlist items and progress
4. Optional: Add bonus tokens toward specific wishlist items
5. Uses wishlist for gift planning and motivation strategies

### Gift Planning Flow (Caregiver)
1. Children tab → "View All Wishlists"
2. Reviews all children's wishlist items and progress
3. Identifies gift opportunities:
   - Items child is very close to affording (motivation boost)
   - Items child has wanted longest (gift ideas)
   - Items approaching special occasions
4. Optional actions:
   - Add bonus tokens toward specific items
   - Mark items as potential gifts
   - Set reminders for gift planning
5. Export wishlist data for gift shopping

## Wishlist Integration Points

### Rewards Screen Integration
- Dedicated wishlist section below available rewards
- Toggle between "Available Now" and "Wishlist" views
- Progress bars showing completion percentage for each wishlist item
- Quick action buttons for moving items between available and wishlist

### Achievement System Integration
- **Wishlist Warrior** - Add 5 items to wishlist
- **Saver** - Reach 50% progress on a wishlist item
- **Goal Achiever** - Purchase an item that was on wishlist for 7+ days
- **Dream Big** - Add an item worth 500+ tokens to wishlist
- **Patient Saver** - Save for a wishlist item for 30+ days
- **Wishlist Champion** - Complete 10 wishlist purchases

### Caregiver Dashboard Integration
- Child overview cards show top wishlist item and progress
- Wishlist insights in progress reports and analytics
- Quick actions to add bonus tokens toward wishlist goals
- Wishlist trends help understand child motivation patterns
- Gift planning recommendations based on wishlist age and priority
- Multi-child wishlist comparison for fair reward distribution
- Integration with reward management for creating wishlist-based rewards
- Wishlist progress alerts for caregivers when children are close to goals

### Notification Integration
- "Getting closer!" when child reaches 25%, 50%, 75% progress
- "Almost there!" when child reaches 90% progress on wishlist item
- "Wishlist item now affordable!" when child earns enough tokens
- Weekly wishlist progress summary for motivation
- Caregiver alerts when child is close to major wishlist goals

## Wishlist Analytics & Insights

### Child Motivation Patterns
- Track which types of rewards children add to wishlists most frequently
- Monitor how long children typically save for different value items
- Identify seasonal patterns in wishlist additions
- Analyze correlation between wishlist progress and task completion rates

### Family Gift Planning Insights
- **Gift Opportunity Alerts**: Items child is close to affording or has wanted longest
- **Seasonal Recommendations**: Wishlist items appropriate for upcoming holidays/birthdays
- **Budget Planning**: Cost analysis and savings timeline for family gift planning
- **Motivation Timing**: Optimal times to add bonus tokens for maximum motivation impact

### Caregiver Support Features
- **Wishlist Dashboard**: Overview of all children's wishlist progress and insights
- **Gift Planning Calendar**: Integration with family calendar for gift timing
- **Motivation Recommendations**: Suggested strategies based on wishlist patterns
- **Progress Sharing**: Family updates on major wishlist milestones

## Technical Implementation

### Data Structure
```kotlin
data class WishlistItem(
    val id: String,
    val childId: String,
    val rewardId: String,
    val addedAt: Timestamp,
    val isSavingsGoal: Boolean,
    val priority: Int,
    val progress: WishlistProgress,
    val isGiftIdea: Boolean = false,
    val caregiverNotes: String? = null
)

data class WishlistProgress(
    val currentTokens: Int,
    val requiredTokens: Int,
    val percentage: Float,
    val estimatedCompletionDays: Int?,
    val bonusTokensAdded: Int = 0
)

data class WishlistStats(
    val totalItems: Int,
    val averageItemCost: Int,
    val totalProgress: Float,
    val nearestAffordableItem: WishlistItem?,
    val longestWaitingItem: WishlistItem?
)

data class FamilyWishlistInsights(
    val upcomingAffordableItems: List<WishlistItem>,
    val giftOpportunities: List<WishlistItem>,
    val motivationOpportunities: List<WishlistItem>,
    val seasonalRecommendations: List<WishlistItem>
)
```

### Wishlist Operations
- Add/remove items from wishlist
- Set/unset savings goals for wishlist items
- Calculate progress and estimated completion times
- Automatic affordability checks and notifications
- Wishlist analytics for caregivers and progress tracking
- Bonus token allocation toward specific wishlist items
- Gift planning insights and recommendations

### Progress Calculation
```kotlin
fun calculateWishlistProgress(item: WishlistItem, currentTokens: Int): WishlistProgress {
    val tokensNeeded = item.reward.cost
    val progress = (currentTokens.toFloat() / tokensNeeded.toFloat()) * 100
    val estimatedDays = if (averageTokensPerDay > 0) {
        (tokensNeeded - currentTokens) / averageTokensPerDay
    } else null
    
    return WishlistProgress(
        currentTokens = currentTokens,
        requiredTokens = tokensNeeded,
        percentage = progress,
        estimatedCompletionDays = estimatedDays
    )
}
```

### Family Integration Features
- **Multi-Child Wishlist View**: Caregiver overview of all children's wishlists
- **Cross-Child Support**: Allow siblings to contribute tokens to each other's goals
- **Family Events**: Special bonus token contributions for birthdays, holidays
- **Gift Coordination**: Prevent duplicate gift purchases across family members
- **Motivation Strategies**: Caregiver tools for timing bonus tokens for maximum impact

### Privacy & Safety
- Children's wishlist items are only visible to assigned caregivers and admins
- Gift planning features include privacy controls to prevent spoiling surprises
- Age-appropriate content filtering for wishlist items
- Secure token transactions for wishlist purchases
- Family sharing controls for wishlist achievement celebrations

---

**Previous:** [Global Achievement System](achievements.md) | **Next:** [Back to Planning Overview](README.md)
