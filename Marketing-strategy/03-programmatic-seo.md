# Programmatic SEO for RunHub

Since you are building the web dashboard in Angular, you can leverage SSR (Server-Side Rendering) via Angular Universal to create millions of highly-indexed organic search pages automatically.

## What is Programmatic SEO (pSEO)?
Instead of writing 100 blog posts by hand, you write 1 template and inject your database data to generate 10,000 targeted web pages.

### Tactic 1: The "Running Routes in [City]" Directory
Runners constantly Google "Best 5k running routes in London" or "Central Park running map".

1. **The Template:** Create a dynamic Angular route: `runhub.com/routes/:city/:segmentId`.
2. **The Data:** Every time a user runs a popular segment, aggregate that data anonymously.
3. **The Output:** Auto-generate thousands of pages titled logically:
   - "Top 10k Running Routes in Austin, Texas"
   - "Elevation Map for The Golden Gate Bridge Run"
4. **The CTA:** Every page should have a massive button: "Track your run on this exact route using RunHub. Download Free."

### Tactic 2: Event SEO
Runner's search for "Boston Marathon 2026 results" or "Paris Half Marathon Route Map".
- Build an index of major upcoming races.
- Automatically generate 3D Mapbox flyovers for those routes.
- When people google the race, your domain ranks, showing the beautiful 3D map, and prompts them to download the app to navigate it.
