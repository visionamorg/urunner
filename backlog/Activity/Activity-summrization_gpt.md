# Epic: AI-Driven Activity Intelligence
## Story: Personalized Activity Summarization, Insights & Conversational Coaching

**As a** runner tracking my training in the application,
**I want** to access a deep, AI-powered analysis of my individual running sessions,
**So that** I can understand not just the numbers (distance, pace, duration), but the *meaning* behind them, get actionable advice for my next run, and have a conversational AI partner to discuss my performance and progress.

---

### **Overview**
The current application allows users to record basic activity data (Title, Distance, Time, Date, Notes). This story aims to transform the static "Activity Detail" experience into a dynamic, intelligent hub where AI acts as a digital running coach.

### **Core Functionality**

#### **1. Enhanced Activity Detail View (Frontend)**
- **Detailed Summary Card**: A dedicated section displaying AI-generated insights (e.g., "Great recovery run", "Intense threshold session").
- **Visual Insights**: Use of micro-animations and status indicators (e.g., color-coded based on AI-determined intensity vs. user notes).
- **AI Action Bar**: Floating or sidebar actions for:
    - `Summarize This Run`: Detailed text summary of performance.
    - `Get Next-Run Suggestions`: AI advice on what the next session should look like based on this run and recent volume.

#### **2. Activity-Contextual AI Chat (Conversational AI)**
- **Interactive Sidebar/Overlay**: A chat interface accessible directly from the Activity Detail page.
- **Contextual Awareness**: The AI starts the conversation knowing the specifics of *this* specific run (distance, pace, user's private notes, date).
- **Example Queries**:
    - "Given my notes about knee pain in this run, should I skip tomorrow's intervals?"
    - "How does my pace in this 10k compare to my historical average?"
    - "Explain why my pace dropped significantly in the last 2km of this run."

#### **3. Advanced AI Intelligence (The "More" Functionality)**
- **Sentiment & Effort Correlation**: AI analyzes the user's "Notes" field against the raw data (distance/pace) to detect if the user is overreaching or under-motivated.
- **Injury Risk Assessment**: AI flags potential issues if multiple runs have notes about "pain", "stiffness", or "fatigue".
- **Social Sharing Content**: One-click "Generate Social Caption" that creates a witty or inspirational summary of the run for Instagram/Strava sharing.
- **Comparison Engine**: AI automatically identifies the most similar past run and provides a "Then vs. Now" comparison in plain English.

---

### **Implementation Strategy for Claude**

#### **Backend (Spring Boot & Stitch AI)**
- **New Service**: `ActivityAIService` to handle integration with `STITCH_API_KEY`.
- **New Entity**: `ActivityInsight` (ID, ActivityID, SummaryText, SuggestionsJSON, CreatedAt) to cache AI results and avoid redundant API calls.
- **Controller Extension**: `POST /api/activities/{id}/analyze` and `GET /api/activities/{id}/chat` endpoints.
- **Prompts**: Craft system prompts that instruct the AI to act as an "Expert Marathon Coach" with access to the user's historical `ActivityStatsDto`.

#### **Frontend (Angular & TailwindCSS)**
- **Components**:
    - `ActivityDetailComponent`: Update to include the AI Insight section.
    - `ActivityChatComponent`: A reusable chat bubble/sidebar using modern aesthetics (glassmorphism/gradients).
- **State Management**: Update `ActivityService` to handle fetching and persisting AI-generated content.

---

### **Acceptance Criteria** Done 
- [ ] User can navigate to a single activity and see a "Request AI Summary" button.
- [ ] AI Summary is generated using activity data and user notes, and is persisted in the database.
- [ ] A chat window allows the user to ask at least 3 follow-up questions about the specific activity.
- [ ] The AI suggests a specific workout for the "next" training day based on the analyzed run.
- [ ] UI reflects a "Premium" look with smooth transitions for the AI chat and insight loading states.

