# Epic: Activity Canvas & Export Studio
## Story: Advanced 3D / Cloud Typography Tool

**As a** creative user building a custom export canvas,
**I want a** dedicated tool to generate massive, realistic 3D or "Cloud" style text that floats in the background of my photo,
**So that** I can write custom phrases (like "SUNDAY RUN" or my own name) in the sky, exactly like the viral Strava cloud trend.

### Background / Reference:
This story is directly inspired by the "STRAVA" cloud typography trend, but democratizes it so the user can write **any string of text** and apply custom colors and textures.

**Status: Done**

### Acceptance Criteria:

1. **The Custom Text Input & Color Picker:**
   - *Given* I am in the Export Studio builder, *when* I open the "Effects" tab, *then* there is a "Massive Floating Text" module.
   - *Then* I can type a custom string (Max 10 characters) into an input field.
   - *Then* I am presented with a Color Picker module allowing me to tint the text (e.g., Sunset Orange, Neon Pink, or standard Cloud White).

2. **Texture Rendering Engine:**
   - *Given* the text input, *when* rendered on the canvas, *then* I can toggle between 3 distinct CSS/WebGL texture styles:
     - **"Realistic Cloud":** Uses a repeating SVG cloud noise texture clipped to the text (`background-clip: text`) with fuzzy `text-shadow` layers.
     - **"Solid 3D Block":** Uses layered `text-shadows` to create a profound 3D extrusion effect.
     - **"Neon Glow":** Uses high-saturation colors with massive outer blur shadows.

3. **Advanced Tool: Smart Subject Segmentation (Foreground Overlay):**
   - *Given* the 3D text is placed over my photo, *when* I toggle "Put Behind Subject," *then* the application runs a lightweight client-side AI foreground segmentation (e.g., using `body-segmentation` from TensorFlow.js).
   - *Then* the tool automatically masks out the runner so the massive Cloud text looks like it is floating in the sky *behind* the runner's head, drastically increasing the realism and "wow factor" of the image.

4. **Data Grid Layering:**
   - *Given* the massive text is dominating the sky, *then* the standard run statistics (Distance, Pace, Time) render automatically layered beneath it on the horizon line in small, crisp, high-contrast typography.
