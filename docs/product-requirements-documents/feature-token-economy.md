---
post_title: "Feature: Token Economy System"
author1: "Memory Bank (AI)"
post_slug: "feature-token-economy"
microsoft_alias: "pheiow"
featured_image: ""
categories: ["Product Requirements"]
tags: ["token-economy", "gamification", "rewards", "tasks"]
ai_note: "This PRD was generated based on an analysis of the existing project structure and the need to centralize the token economy logic. It establishes the core rules for earning, spending, and managing tokens within the app."
summary: "This document outlines the requirements for the Token Economy system, which serves as the core incentive mechanism in Arthur's Life App. It defines how users earn tokens by completing tasks and redeem them for rewards, creating a balanced and motivating in-app economic system."
post_date: "2025-07-15"
---

# PRD: Token Economy System

## 1. Product overview

### 1.1 Document title and version
* PRD: Token Economy System
* Version: 1.0

### 1.2 Product summary
The Token Economy is a foundational feature of Arthur's Life App, designed to motivate and reward children for completing tasks and achieving goals. It functions as a simple, closed-loop economic system where tokens are the primary currency. Children earn tokens by completing tasks assigned by their caregivers and can then spend these tokens to redeem pre-approved rewards.

This system provides a tangible and immediate sense of accomplishment, teaching valuable concepts like earning, saving, and goal-setting in a controlled and safe environment. The economy is designed to be transparent and easy for both children and caregivers to understand and manage, ensuring it remains a positive and effective tool for motivation.

## 2. Goals

### 2.1 Business goals
*   Increase user engagement by providing a clear and consistent incentive structure.
*   Reinforce positive behavior by linking effort (completing tasks) directly to rewards.
*   Create a scalable system that can accommodate future gamification features (e.g., special achievements, bonuses).
*   Provide caregivers with an effective tool to motivate children and manage household responsibilities.

### 2.2 User goals
*   **For Children**: To have a fun and clear way to earn rewards for their efforts. To understand how much they need to save to get a desired reward.
*   **For Caregivers**: To have a simple, manageable system for incentivizing tasks without using real money. To track and approve token transactions easily.

### 2.3 Non-goals
*   This is not a real-world financial system. Tokens have no cash value and cannot be transferred out of the app.
*   The system will not involve complex economic concepts like interest, debt, or investment.
*   The system will not allow children to trade or transfer tokens between themselves.

## 3. User personas

### 3.1 Key user types
*   Child
*   Caregiver

### 3.2 Basic persona details
*   **Child**: The primary earner and spender of tokens. Interacts with the system by viewing their balance, seeing tokens awarded for tasks, and redeeming rewards.
*   **Caregiver**: The administrator of the economy. Sets the token value for tasks and the token cost for rewards, and approves all transactions.

### 3.3 Role-based access
*   **Child**: Can view their token balance, earn tokens from completed tasks (after caregiver approval), and request to redeem rewards. Cannot set token values or approve transactions.
*   **Caregiver**: Can view the child's token balance, define token amounts for tasks and rewards, approve task completion to award tokens, and approve reward redemptions to deduct tokens. Can also manually add or remove tokens with a clear reason for auditing purposes.

## 4. Requirements

### 4.1 Functional requirements (FR)
*   **FR-TE-01**: Token Balance: The system must maintain a persistent token balance for each child user. The balance should be clearly visible on the child's main dashboard.
*   **FR-TE-02**: Earning Tokens: Children are awarded tokens when a caregiver approves a completed task. The number of tokens awarded is determined by the value set for that task.
*   **FR-TE-03**: Spending Tokens: Children can redeem rewards by spending tokens. The cost of the reward is deducted from their balance upon caregiver approval.
*   **FR-TE-04**: Transaction History: The system must keep a simple, viewable log of all token transactions (earnings and spending) for both child and caregiver.
*   **FR-TE-05**: Insufficient Funds: The system must prevent a child from redeeming a reward if their token balance is insufficient.
*   **FR-TE-06**: Caregiver Controls: Caregivers must have a dedicated interface to set the token value for tasks and the cost for rewards.
*   **FR-TE-07**: Manual Adjustment: Caregivers must be able to manually add or subtract tokens from a child's balance, providing a mandatory reason for the adjustment (e.g., "Bonus for good behavior," "Correction for mistake").

### 4.2 Non-functional requirements (NFR)
*   **NFR-TE-01**: Performance: Token balance updates must be reflected in the UI in near real-time (<500ms) after a transaction is approved.
*   **NFR-TE-02**: Security: All token transactions must be processed authoritatively by the backend or a secure local data store to prevent unauthorized modification by the client.
*   **NFR-TE-03**: Reliability: The token balance must be accurately maintained and backed up to prevent data loss.
*   **NFR-TE-04**: Usability: The process of earning and spending tokens must be intuitive and visually clear for a young child.

## 5. User experience

### 5.1 Entry points & first-time user flow
*   The child's token balance is prominently displayed on their home screen/dashboard.
*   When a task is completed and approved, a clear visual confirmation (e.g., animation of tokens being added) is shown.
*   When viewing rewards, the cost is clearly displayed, and the child's ability to afford it is visually indicated.

### 5.2 Core experience
*   **Earning**: Child completes a task -> Caregiver approves it -> Child receives a notification and sees their token balance increase. This loop should feel rewarding and immediate.
*   **Spending**: Child browses rewards -> Selects a reward they can afford -> Confirms redemption -> Caregiver approves it -> Child receives the reward, and their balance decreases. This process should feel like a successful achievement.

### 5.3 Advanced features & edge cases
*   **Saving for a Goal**: The UI should help a child visualize their progress toward saving for an expensive reward.
*   **Editing Values**: If a caregiver changes the token value of a task or reward, it does not affect past transactions.

### 5.4 UI/UX highlights
*   Use playful animations and sound effects for earning and spending tokens to enhance engagement.
*   A visual progress bar could show how close a child is to affording a specific "goal" reward.

## 6. Narrative
A child, Arthur, wants a new toy that his parents have added as a reward in the app. He sees it costs 100 tokens. He opens his task list and sees that "Clean my room" is worth 20 tokens and "Feed the dog" is worth 10. He completes both tasks over two days. His caregiver approves the completed tasks, and he watches his token balance grow. After a few more days of completing tasks, he finally reaches 100 tokens. He excitedly redeems the toy reward, his caregiver approves it, and he gets his new toy, feeling a great sense of accomplishment.

## 7. Success metrics

### 7.1 User-centric metrics
*   Frequency of task completion per child.
*   Frequency of reward redemption per child.
*   Average time a child takes to save for a reward.

### 7.2 Business metrics
*   Daily/Monthly Active Users (DAU/MAU).
*   User retention rate.

### 7.3 Technical metrics
*   API response time for token transactions (<200ms).
*   Zero errors in token balance calculations.

## 8. Technical considerations

### 8.1 Integration points
*   **Task System**: The token economy will be triggered by the `TaskCompleted` domain event (or similar) after caregiver approval.
*   **Reward System**: The token economy will be triggered when a `RewardRedemption` is requested and approved.
*   **User Profile**: The token balance will be an attribute of the child's user profile.

### 8.2 Data storage & privacy
*   Token balances and transaction histories must be stored securely.
*   Data will be stored locally on the device, with backup/restore functionality as defined in the Data Management PRD. All data is subject to COPPA and other privacy regulations.

### 8.3 Scalability & performance
*   The system should be designed to handle frequent transactions without performance degradation.
*   All token calculations must be handled server-side or in a secure, isolated part of the application logic to prevent client-side manipulation.

### 8.4 Potential challenges
*   Ensuring the "economy" remains balanced (i.e., tasks are not too rewarding, and rewards are not too cheap/expensive). This is up to the caregiver, but the UI can provide guidance.
*   Preventing user confusion if token values are changed by the caregiver.

## 9. User stories

### 9.1. View Token Balance
*   **ID**: US-TE-001
*   **Description**: As a Child, I want to see my total token balance on my main screen so that I know how many tokens I have available.
*   **Acceptance criteria**:
    *   Given I am logged in as a Child,
    *   When I am on the home screen,
    *   Then I can see a clear visual display of my current token balance.

### 9.2. Earn Tokens from a Task
*   **ID**: US-TE-002
*   **Description**: As a Child, I want to receive tokens immediately after my caregiver approves a task I completed so that I feel rewarded for my work.
*   **Acceptance criteria**:
    *   Given a task is marked as "Completed" by me,
    *   When my caregiver approves the completion,
    *   Then the specified number of tokens for that task is added to my balance,
    *   And I see a notification or animation confirming I've earned tokens.

### 9.3. Redeem a Reward
*   **ID**: US-TE-003
*   **Description**: As a Child, I want to use my tokens to redeem a reward so that I can get the things I've been saving for.
*   **Acceptance criteria**:
    *   Given I have enough tokens for a specific reward,
    *   When I select "Redeem" on that reward,
    *   Then a request is sent to my caregiver for approval,
    *   And upon caregiver approval, the token cost is subtracted from my balance.

### 9.4. Set Task Token Value
*   **ID**: US-TE-004
*   **Description**: As a Caregiver, I want to set the number of tokens a task is worth so that I can control the economic balance of the system.
*   **Acceptance criteria**:
    *   Given I am creating or editing a task,
    *   When I am on the task configuration screen,
    *   Then I can input a numerical value for the tokens to be awarded upon completion.

### 9.5. Set Reward Token Cost
*   **ID**: US-TE-005
*   **Description**: As a Caregiver, I want to set the token cost of a reward so that I can define what my child needs to save for.
*   **Acceptance criteria**:
    *   Given I am creating or editing a reward,
    *   When I am on the reward configuration screen,
    *   Then I can input a numerical value for the token cost.
