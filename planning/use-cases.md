# Use Cases & User Flows

Detailed use cases and user flows organized by role, covering all major user interactions and system capabilities.

## 🧒 CHILD ROLE - Core Use Cases

### UC-C1: Daily Task Completion Flow
**Primary Actor:** Child  
**Goal:** Complete daily tasks and earn tokens  
**Flow:**
1. Opens app → sees Home screen with today's tasks
2. Taps task → starts timer/activity tracker
3. Completes task → celebrates with animation
4. Earns tokens → sees balance update
5. **Achievement system checks for triggers:**
   - First task completion (First Steps achievement)
   - All daily tasks completed (Task Master achievement)  
   - Consecutive day streaks (3, 7, 14, 30, 60-day achievements)
   - Speed completions (Quick Starter, Speedy Gonzales)
   - Category-specific achievements (Helper, Creative)
6. Views progress bar fill up
7. Achievement celebration triggers if milestone reached

### UC-C2: Token Redemption & Wishlist Flow
**Primary Actor:** Child  
**Goal:** Exchange tokens for rewards or manage wishlist items  
**Flow:**
1. Rewards tab → browses available rewards
2. Selects reward → checks token balance
3. If affordable: confirms redemption → tokens deducted
4. If not affordable: adds to wishlist → saves for later
5. Views wishlist progress and savings goals

### UC-C3: Achievement Discovery & Celebration
**Primary Actor:** Child  
**Goal:** Explore and unlock achievements through task completion  
**Flow:**
1. Achievements tab → views locked/unlocked achievements
2. Discovers achievement requirements and progress
3. Completes tasks → triggers achievement celebration
4. Celebrates with animation and badge unlock
5. Shares achievements with family (if enabled)

### UC-C4: Theme & Accessibility Customization
**Primary Actor:** Child (with support)  
**Goal:** Personalize app appearance and accessibility needs  
**Flow:**
1. Profile tab → Settings
2. Selects preferred character/theme options
3. Adjusts accessibility settings (text size, contrast, TTS)
4. Tests changes with immediate feedback
5. Saves personalized configuration

### UC-C5: Achievement Detail Exploration
**Primary Actor:** Child  
**Goal:** Explore specific achievement requirements and track progress  
**Flow:**
1. Achievements tab → selects specific achievement badge
2. Views detailed requirements and current progress
3. Reads tips and suggestions for completion
4. Sets reminders for upcoming opportunities
5. Shares progress with family (optional)
6. Returns to achievement gallery with motivation

### UC-C6: Wishlist Item Management
**Primary Actor:** Child  
**Goal:** Add, prioritize, and manage personal wishlist items  
**Flow:**
1. Rewards tab → browses unaffordable rewards
2. Adds items to wishlist with priority ranking
3. Views progress tracking and savings goals
4. Adjusts priorities based on changing interests
5. Celebrates when items become affordable
6. Removes achieved or unwanted items

## 👨‍👩‍👧‍👦 CAREGIVER ROLE - Child Management Use Cases

### UC-CG1: Child Setup & Onboarding
**Primary Actor:** Caregiver  
**Goal:** Set up new child profile and initial tasks  
**Flow:**
1. Dashboard → "Add Child" (if authorized by admin)
2. Enters child details (name, age, preferences)
3. Selects task templates or creates custom tasks
4. Sets initial token values and rewards
5. Configures accessibility preferences

### UC-CG2: Daily Task Management
**Primary Actor:** Caregiver  
**Goal:** Adjust tasks for selected child  
**Flow:**
1. Dashboard → selects child from dropdown
2. Tasks screen → views child's current tasks
3. Adds/edits/removes tasks as needed
4. Sets schedules, reminders, token values
5. Previews child's view to validate

### UC-CG3: Progress Monitoring & Adjustment
**Primary Actor:** Caregiver  
**Goal:** Track child's progress and make adjustments  
**Flow:**
1. Progress screen → selects child and time period
2. Reviews completion rates, token earnings
3. Identifies patterns (struggling areas, streaks)
4. Adjusts task difficulty or rewards accordingly
5. Exports report if needed

### UC-CG4: Reward System & Wishlist Management
**Primary Actor:** Caregiver  
**Goal:** Configure meaningful rewards and monitor child's wishlist for motivation insights  
**Flow:**
1. Dashboard → "Manage Rewards" or "View Child Wishlist"
2. Views current reward catalog and child's wishlist items
3. Adds custom family rewards (ice cream, movie night)
4. Sets token costs based on effort/value
5. Reviews child's wishlist for gift ideas and motivation patterns
6. Optional: Adds bonus tokens toward specific wishlist items
7. Archives expired or inappropriate rewards

### UC-CG5: Multi-Child Management
**Primary Actor:** Caregiver (with multiple children)  
**Goal:** Efficiently manage tasks across children  
**Flow:**
1. Dashboard → switches between children via selector
2. Tasks screen → copies tasks from one child to another
3. Progress screen → compares children's progress
4. Adjusts individual approaches based on each child's needs

### UC-CG6: Wishlist Monitoring & Gift Planning
**Primary Actor:** Caregiver  
**Goal:** Monitor child's wishlist for motivation insights and gift planning opportunities  
**Flow:**
1. Dashboard → "View Wishlist" or Children → "View All Wishlists"
2. Reviews child's current wishlist items and progress percentages
3. Identifies items child is close to affording (motivation opportunities)
4. Notes items child has been saving for longest (gift ideas)
5. Optional: Adds bonus tokens toward specific wishlist items as rewards
6. Uses wishlist insights to understand child's interests and motivation patterns
7. Plans surprise gifts or special occasion purchases based on wishlist

### UC-CG7: Task Creation & Management
**Primary Actor:** Caregiver  
**Goal:** Create comprehensive tasks with scheduling and reward configuration  
**Flow:**
1. Tasks screen → "Add Task" button
2. Enters task details: name, category, difficulty level
3. Configures scheduling: daily, weekly, custom patterns
4. Sets token rewards based on difficulty multiplier
5. Adds instructions and estimated duration
6. Configures reminders and preferred completion times
7. Previews child view to validate task clarity
8. Saves task and monitors child response

### UC-CG8: Accessibility Configuration & Support
**Primary Actor:** Caregiver  
**Goal:** Configure accessibility settings to support child's specific needs  
**Flow:**
1. Child profile → Accessibility Settings
2. Assesses child's accessibility requirements
3. Configures visual settings: contrast, text size, colors
4. Sets up auditory support: TTS, speech rate, voice options
5. Adjusts motor accessibility: touch sensitivity, gesture alternatives
6. Tests settings with child for comfort and effectiveness
7. Documents preferences for consistent family device setup

## 👑 ADMIN ROLE - Family Administration Use Cases

### UC-A1: Family Member Management
**Primary Actor:** Admin  
**Goal:** Manage family structure and member access  
**Flow:**
1. Family screen → views all family members
2. Adds new caregivers (mother, father, teacher, etc.)
3. Adds new children to the family
4. Sets permissions for each caregiver
5. Removes or deactivates family members as needed

### UC-A2: Caregiver Permission Configuration
**Primary Actor:** Admin  
**Goal:** Control what caregivers can access and modify  
**Flow:**
1. Family screen → selects caregiver
2. Configures child access (which children they can manage)
3. Sets feature permissions (add/remove tasks, modify rewards)
4. Defines notification preferences for caregiver
5. Reviews and saves permission changes

### UC-A3: Family Settings & Configuration
**Primary Actor:** Admin  
**Goal:** Configure family-wide settings and preferences  
**Flow:**
1. Profile → Family Settings
2. Sets family notification preferences
3. Configures data sharing between caregivers
4. Manages family backup and security settings
5. Sets default accessibility and theme preferences

### UC-A4: Family Analytics & Oversight
**Primary Actor:** Admin  
**Goal:** Monitor overall family activity and progress  
**Flow:**
1. Progress screen → views family-wide analytics
2. Reviews all children's progress and trends
3. Monitors caregiver activity and engagement
4. Generates family reports and insights
5. Identifies areas needing attention or adjustment

### UC-A5: Family Setup & Onboarding
**Primary Actor:** Admin  
**Goal:** Set up new family account with all members and initial configuration  
**Flow:**
1. App installation → starts family setup wizard
2. Creates family account with admin credentials
3. Adds family members: children and caregivers
4. Configures initial permissions and access levels
5. Sets up family-wide settings and preferences
6. Initializes task templates and reward catalogs
7. Completes onboarding and activates family system

### UC-A6: Data Management & Security
**Primary Actor:** Admin  
**Goal:** Manage family data backup, security, and privacy settings  
**Flow:**
1. Settings → Data Management section
2. Configures automatic backup schedule and preferences
3. Reviews backup status and storage usage
4. Manages data export for external records
5. Configures family privacy and security settings
6. Monitors data access logs and family activity
7. Handles data restoration when needed

## 🔄 CROSS-ROLE FLOWS - Shared Use Cases

### UC-X1: Mode Switching Flow
**Primary Actor:** Caregiver/Admin user  
**Goal:** Switch between child view and management view  
**Flow:**
1. Profile screen → "Switch Mode" button
2. PIN authentication (security)
3. Role verification → navigation updates to target mode
4. Dashboard opens with appropriate interface
5. Can switch between available modes anytime

### UC-X2: Emergency Task Override
**Primary Actor:** Caregiver (immediate), Child (affected)  
**Goal:** Handle unexpected schedule changes  
**Flow:**
1. Caregiver receives notification of completed/missed task
2. Opens app → navigates to child's Tasks
3. Marks task as complete/incomplete with reason
4. Adjusts token balance if needed
5. Child sees updated status with explanation

### UC-X3: Achievement Celebration Flow
**Primary Actor:** System (triggered), Child (recipient), Caregiver (notified)  
**Goal:** Celebrate milestones and maintain motivation through comprehensive achievement system  
**Flow:**
1. System detects achievement trigger (task completion, streak milestone, token milestone)
2. Real-time achievement validation against global achievement database
3. Child receives immediate celebration with:
   - Full-screen celebration animation with confetti
   - Achievement badge unlock with sound effects
   - Token reward automatically added to balance
   - Special character/theme unlocks (if applicable)
4. Achievement progress updated across all family devices
5. Caregivers receive push notification with achievement details
6. Achievement added to child's permanent badge collection
7. Family celebration mode triggered for major milestones
8. Achievement sharing options presented (if family sharing enabled)
9. New achievement hints revealed based on progress patterns

### UC-X4: Data Backup & Restore Flow
**Primary Actor:** Admin/Caregiver  
**Goal:** Protect family data and enable device migration  
**Flow:**
1. Settings → Data Management
2. Initiates backup to secure cloud storage
3. Verifies backup completion and integrity
4. On new device: Settings → Restore Data
5. Family data restored with all progress intact

## 🚨 EDGE CASES & ERROR FLOWS

### UC-E1: Network Connectivity & API Error Handling
**Goal:** Handle network issues and API failures gracefully  
**Flow:**
1. App detects network connectivity issues or API failures
2. Shows user-friendly error messages with retry options
3. Implements exponential backoff for API retry attempts
4. Caches critical data locally for immediate access
5. Queues user actions for retry when connectivity restored
6. Real-time reconnection and data synchronization

### UC-E2: Child Account Recovery
**Goal:** Restore access when child forgets password/PIN  
**Flow:**
1. Child taps "Forgot Password" on login
2. Caregiver receives notification
3. Caregiver verifies identity and resets credentials
4. Child regains access with new credentials
5. Security log updated with recovery event

### UC-E3: Task Conflict Resolution
**Goal:** Handle overlapping or conflicting tasks  
**Flow:**
1. System detects scheduling conflict
2. Caregiver notified with suggested resolutions
3. Caregiver chooses resolution (reschedule, remove, modify)
4. Child sees updated schedule with explanation
5. Notifications updated accordingly

### UC-E4: Offline Mode Handling
**Goal:** Maintain app functionality during network connectivity issues  
**Flow:**
1. App detects network connectivity loss
2. Switches to offline mode with clear user notification
3. Enables limited functionality: task completion, progress viewing
4. Disables network-dependent features: reward redemption, family sync
5. Queues user actions for sync when connection restored
6. Automatically syncs data when connectivity returns
7. Notifies user of successful synchronization

### UC-E5: Achievement System Error Handling
**Goal:** Handle achievement system failures and edge cases  
**Flow:**
1. Achievement trigger occurs during system unavailability
2. System queues achievement for later processing
3. User receives delayed achievement notification when system recovers
4. Achievement progress is recalculated and corrected if needed
5. Family members receive retroactive achievement notifications
6. System logs achievement processing issues for analysis

---

**Next:** [Navigation Structure](navigation.md) for information architecture and navigation patterns.
