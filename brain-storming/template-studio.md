I absolutely remember that story, and the reference images you've shared provide the perfect inspiration. The current plan focuses on the *engine*—how to render the canvas. These images help us define the *designs*.

To implement this "Export Template Studio" effectively, we need to move from broad stylistic concepts to concrete, coded layouts that match these references. This will give users flexibility while maintaining strict brand consistency (using the "UR Community" logo).

Here is the updated and expanded User Story for Claude, designed to translate these diverse visual examples into functional code.

---

### **User Story: The "Activity Canvas" Template & Export Studio**

#### **Description**
As an athlete, I want an interactive studio within the application where I can customize how my activity data is rendered over my photos before exporting to social media. I need a library of professionally designed templates, ranging from clean data overlays to bold, typographic layouts, all featuring the "UR Community" logo.

#### **Technical Context**
* **Inspiration Sources:** Standardized overlays (`image_1.png`, `image_2.png`, `image_3.png`, `image_4.png`) and bold, multi-layout typographic grids (`image_5.png`).
* **Framework:** Standard HTML/CSS/JavaScript. Final export must utilize `html2canvas` (or `<canvas>`) to generate a single shareable PNG/JPG.
* **Aspect Ratio:** Default output is 9:16 (Instagram Story size).

#### **Acceptance Criteria for Claude**

**1. Template Selection UI:**
* Implement a sidebar or bottom-sheet carousel displaying previews of at least 4 predefined template options (corresponding to the coded designs below).

**2. Core Templates (The Designs):**
* Claude must generate the HTML and CSS for these 4 distinct layouts:
    * **The "Clear Info" Card (Ref: image_1.png / image_2.png):** A frosted-glass or semi-transparent rectangular card positioned over the background photo, displaying the runner's profile, distance, pace, and date in a clean list format. *Note: Ensure the language from the references (e.g., 'Masale, South Sulawesi' or 'Pace') is translated to English as a platform best practice.*
    * **The "Large Stat Overlay" (Ref: image_3.png):** Large, abstract, and bold numeric overlays (e.g., a massive '6' representing 6KM), paired with geometric background shapes that intersect with the runner's photo (similar to image_3.png).
    * **The "Aesthetic Text & Cloud" (Ref: image_4.png):** Minimalist text stats at the top (profile/distance/time) with a single bold, typographic title (like the "STRAVA" cloud font style) floating near the top. *Note: The title should be customizable (e.g., "Morning Run").*
    * **The "Typography Poster" (Ref: image_5.png):** Bold, heavy, sans-serif or custom brush-style typography (similar to the 'funRun 5K' example) used to spell out the activity type or date as a dominant design element, integrated directly with the background photo.

**3. Logo Implementation (Universal branding):**
* **The "UR Community" Logo:** Every single template must automatically include the standard "UR Community" brand logo. It must be positioned clearly but unobtrusively, following the placement logic from the Strava references (`image_1.png` uses it in the text card; `image_2.png` places it bottom-left).

**4. Advanced Data & Polyline Toggles:**
* Add controls to toggle the visibility of specific data points (e.g., Hide/Show Heart Rate, Hide/Show Spline Map).

---

### **Refinement for Claude**
* **State Management:** When a user selects a template, Claude must describe how the activity data (Distance=9.73km, Pace=5:57/km) is dynamically populated into the corresponding fields of the HTML structure of that template *before* the canvas is rendered.