# Epic: Activity Canvas & Export Studio
## Story: Implementation of 4 Premium Visual Reference Templates

**As a** user generating an export canvas,
**I want to** select from 4 highly premium, distinct design layouts based on professional graphic design references,
**So that** my exported run looks like an editorial magazine cover or aesthetic social media post.

### Technical Context & Setup for Implementation
This story describes exactly how to construct the HTML/CSS template layers to match the provided visual references. Each template will receive standard data props: `{distance, pace, duration, date, title, backdropImage}`.

---

### Template 1: The Cloud Sky Typography (Reference: "URC" Cloud Image)
**Design Goal:** A surreal, floating text effect in the upper half of the canvas, resembling clouds or 3D objects, with crisp data resting securely below it.
- **Top Third (Title):** Use a heavy, textured, or stylized font with a soft outer glow/drop-shadow to mimic the Cloud text effect. This area displays the `{title}` (e.g., "MORNING RUN").
- **Mid-Line (Data Grid):** Directly below the sky text, float a clean, horizontal CSS grid with 3 columns (Distance, Pace, Time). 
- **Typography:** The data labels ("Distance") should be small, uppercase, and mid-opacity (rgba(255,255,255,0.7)). The values ("16.01 km") should be bold, crisp, and 100% opacity solid white.

### Template 2: The Minimalist Editorial Layout (Reference: "Activity On tuesday")
**Design Goal:** A sleek, trendy, editorial style often used in fashion/lifestyle magazines.
- **Header:** Mix font families directly. For example, "Activity" in a bold, modern Sans-Serif, followed by an italicized, elegant Serif font for "On", followed by bold Sans-Serif for the `{dayOfWeek}`.
- **Backdrop Styling:** Apply a very subtle, dark linear-gradient from the top edge bleeding down 30% to ensure the white text pops regardless of the background photo.
- **Data Placement:** Place the `{distance}` and `{pace}` cleanly below the title using standard, modern typography (no background cards, just raw text with subtle text-shadows). 
- **Watermark:** Anchor a small, clean "URC" (Urbain Running Club) logo and runner icon in the absolute bottom left/right corners.

### Template 3: The Geometric Mega-Stat (Reference: "6KM EASY RUN" Hexagon)
**Design Goal:** Dominant geometric shapes and massive, overlapping typography.
- **Background Layer 1 (The Mega Number):** Extract the floor integer of `{distance}` (e.g., "6" from "6.11 KM") and render it incredibly large (e.g., `font-size: 80vh`), positioned absolutely behind all other text. Set opacity to 20% or use CSS `mix-blend-mode: overlay` to make it blend into the photo.
- **Foreground Layer 2 (The Hexagon):** Inject an orange/vibrant SVG hexagon shape overlapping the right side of the mega number. Inside the hexagon, place stack text (e.g., "KM \n EASY \n RUN").
- **Data Layer:** To the left of the hexagon, stack the precise `{distance}` and `{pace}` cleanly.

### Template 4: The Typography Poster (Reference: "funRun" Grid)
**Design Goal:** Edgy, hand-drawn, "Zine" or indie race poster style.
- **Primary Title:** Utilize a wavy, distorted, or heavily stylized "Brush" or "Marker" web font (like the "funRun" or "Trail Run" examples). Center it aggressively on the Y and X axis.
- **Data Elements:** Treat the data like a minimalist event flyer. Place the date, time, and distance in extremely small, mono-spaced fonts anchored to the extreme corners of the canvas (Top Left, Top Right, Bottom Left).
- **Decorations:** Add subtle CSS shapes (like 3 starbursts `***` or grid lines) to break up the empty space and emphasize the poster graphic style.
