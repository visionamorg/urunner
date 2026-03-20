Story Title: Transform Static Dashboard into a Dynamic Performance Analytics Hub

Description
As an athlete, I want my dashboard to provide deep visual insights into my training trends beyond simple totals. I want to see interactive charts, heatmaps of my activity, and predictive goals that motivate me to keep running. The UI should maintain the current clean aesthetic but incorporate high-end data visualization components.

Technical Context
Charting Library: Use Chart.js, ApexCharts, or Recharts for rendering.

Visual Elements: Replace the simple stat cards with "Mini-Trend" Sparklines.

Design Language: Maintain the current Bootstrap grid and Aero typographic identity, ensuring the color palette (Orange/White/Gray) remains consistent.

Naming Conventions: All code, variables, and components must be in English.

Acceptance Criteria for Claude
1. The "Performance Pulse" (Main Chart Area):

Replace the top stat cards or the "This Week" section with a large, interactive Area Chart showing "Volume vs. Intensity."

The X-axis should be days/weeks, and the Y-axis should allow toggling between Distance (KM) and Average Pace.

Include a gradient fill under the line to match the theme.

2. Metric Sparklines (The "At-a-Glance" Upgrade):

Inside the 6 top summary cards (Total KM, Avg Pace, etc.), add a small, simplified Sparkline chart at the bottom of each card to show the trend over the last 7 days.

If the trend is positive (e.g., more distance than last week), color the line green; if negative, color it red/orange.

3. Activity Distribution Heatmap:

Add a new section for a "Consistency Heatmap" (GitHub style). This should show a 365-day grid where the intensity of the orange color indicates the distance run on that specific day.

4. Interactive "Recent Activities" List:

Upgrade the list to include a mini-map thumbnail for each run (using a static map API or polyline preview).

Add a "Progress Bar" inside the list item that shows how close that run was to the user's personal record (PR) for that distance.

5. Training Load & Goals Widget:

Create a "Circular Gauge" or "Radial Bar" chart that shows "Monthly Goal Progress" (e.g., "75% of your 200km goal reached").

Add a "Predicted Finish" stat: "At your current rate, you will hit 210km by the end of the month."

Creative Instruction for Claude
"Glassmorphism" Touches: Use subtle blurs and shadows on the chart containers to make them feel modern.

Animation: Ensure the charts "grow" or animate into place when the dashboard loads.

Empty States: Design a beautiful "No Data" state for the charts for new users.