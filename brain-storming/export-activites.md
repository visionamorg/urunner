Act as an expert Full-Stack Developer and UI/UX Designer. I am building a running community application that synchronizes user activities from platforms like Strava and Garmin.

I need you to write the code and provide the technical architecture for a new feature called the "Activity Canvas". Standard apps just slap a colored polyline over a map with basic text. I want my users to generate high-fidelity, highly personalized, and aesthetic digital art from their run data to export to Instagram and other social media.

Please review the requirements below and generate the necessary frontend code, logic structure, and styling.

1. Feature Overview & Creative Concepts
The Activity Canvas must allow users to transform their raw running data (Polyline, Distance, Elevation, Split Times, Heart Rate) into premium social exports. It should include these advanced layout options:

The Storyteller Mode: Automatically extract "Highlights" (e.g., Fastest KM, Max Elevation, Max HR) and place dynamic markers/icons directly on the route line to tell the story of the run.

Topographic & Minimalist Art: Options to strip away the standard street map. Users can render their route as neon lines on a dark background, minimalistic brush strokes, or a 3D isometric topographic mountain.

Magazine Cover Typography: Premium typographic layouts where stats dictate the design (e.g., fast runs use sleek, italicized fonts; long endurance runs use bold, heavy fonts).

2. UI/UX & Layout Requirements
Framework: Use responsive Bootstrap CSS. The studio must work flawlessly on mobile (for quick post-run sharing) and desktop (for deep customization).

Canvas Controls: Provide a clean sidebar or bottom sheet with toggles for:

Layout Theme Selection (Minimalist, Data Heavy, Magazine).

Background Map Visibility (Street, Satellite, Dark Mode, Transparent).

Data Overlay Toggles (Show/Hide highlight markers).

Unit toggles (KM/Miles, Pace/Speed).

Custom Media: Include a file upload input allowing users to upload a local photo as the background, complete with a CSS opacity/blur slider so the data overlays remain readable.

Export Component: A button that utilizes <canvas> (or libraries like html2canvas/dom-to-image) to generate a high-resolution PNG/JPG tailored for social media aspect ratios (1080x1080 for square, 1080x1920 for stories).

UI Exclusions: Do not include any top navigation search bars, and do not include any white "Welcome back" or greeting containers in this view. Keep the interface strictly focused on the canvas studio.

3. Technical & Architecture Constraints
Naming Conventions: All backend variables, database fields, API endpoints, function names, and CSS classes must strictly use English technical naming conventions (standard platform best practice).

State Management: Define how the application should hold the selected Strava data object and the uploaded local image in the state before rendering the final canvas.

4. Desired Output
HTML/Bootstrap Structure: The complete responsive markup for the Activity Canvas UI.

CSS Styling: The custom CSS required for the advanced typographic themes and canvas layout.

JavaScript Logic: The core functions needed to handle the local image upload, toggle the data markers on the polyline, and execute the final image export.