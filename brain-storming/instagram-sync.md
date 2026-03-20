To make this user story clear and actionable for an AI like Claude, we should structure it with specific technical boundaries and clear **Acceptance Criteria**. This ensures the code generated handles the authentication flow and the data mapping correctly.

Here is a refined version of your story with the added technical details needed for a smooth integration.

---

## User Story: Instagram Activity Sharing & Media Upload

### **Description**
As a user, I want to share my synchronized Strava activities directly to Instagram from the application. I need the ability to supplement the activity data with photos from my local device before posting, so that my social feed is personalized and visually engaging.

### **Technical Context**
* **Source Data:** Strava Activity Object (Distance, Time, Elevation, Map Polyline).
* **Media Handling:** Support for both Strava-sourced images and local file uploads (JPG/PNG).
* **Integration:** Instagram Graph API (specifically the **Content Publishing API** for professional/business accounts or the **Instagram Basic Display API** if restricted to profile data).
* **Naming Convention:** All backend objects, fields, and variables must be in **English** (following platform best practices).

### **Acceptance Criteria**
1.  **Sync Trigger:** The system must identify a successfully synchronized Strava activity as "eligible for sharing."
2.  **Media Management:**
    * Provide a UI component to preview images fetched from the Strava activity.
    * Include a "Upload Local Photo" button to allow users to add files from their local machine to the post queue.
3.  **Content Composition:** * Automatically generate a default description (caption) containing key stats (e.g., *"Morning Run: 10km in 50:00"*).
    * Allow the user to edit this description before pushing to Instagram.
4.  **API Integration:**
    * Implement an OAuth2 flow to authenticate the user’s Instagram/Facebook profile.
    * Use the Instagram Profile/Media API to POST the selected images and the description.
5.  **Error Handling:** Provide feedback if the Instagram API returns an error (e.g., expired tokens, unsupported image formats, or API rate limits).

---

### **Refinement Tips for Claude**
* **State Management:** If you are building this in a framework like React or Vue, ask Claude to manage the "upload queue" in the component state before the final POST.
* **Security:** Remind Claude to handle the Instagram Access Tokens securely (e.g., stored in a secure server-side session or encrypted database field).

Would you like me to generate the **Bootstrap-based UI code** for the image upload and preview section of this story?