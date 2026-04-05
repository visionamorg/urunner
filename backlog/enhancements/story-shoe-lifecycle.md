# Story: Shoe Lifecycle Tracker (Injury Prevention)

### Status: DONE

## 🎯 Goal
Protect runners by tracking the mileage of their gear and providing timely reminders to replace shoes before they lose their cushioning.

## 👤 User Story
`As a Runner, I want to assign my 'Nike Pegasus 40' to my runs so that I know exactly when they hit the 600km wear limit.`

## 🛠️ Acceptance Criteria
- [ ] Model: New `shoes` table linked to `users`.
- [ ] Activity Sync: Allow selecting a "Default Shoe" for all Strava/Garmin imports.
- [ ] UI: A "Gear" tab in the user profile showing a 3D-style progress bar for each shoe's lifespan.
- [ ] Notification: Push alert at 80% (reminder) and 100% (critical) lifespan milestones.

## 🚀 Powerful Addition: "The Injury Risk Correlation"
Analyze the user's "Efficiency Factor" over time. If their pace per heartbeat is decreasing *only* when wearing a specific old shoe, the AI tags that shoe as "High Injury Risk" and flags it for immediate replacement.

## 💡 Technical Strategy
1. Schema update: `ALTER TABLE running_activities ADD COLUMN shoe_id BIGINT;`.
2. Frontend: Create a `ShoeCard` component that changes color (Green → Yellow → Red) as mileage increases.
3. Integrate with the "Storefront" story: If a shoe is 100% worn, suggest buying a replacement in the Community Shop.
