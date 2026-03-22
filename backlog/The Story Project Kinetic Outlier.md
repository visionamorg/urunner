Role: You are an Expert Angular (v17+) and Tailwind UI/UX Engineer.

The Objective: I have a functional running dashboard. The data bindings, routing, and logic are 100% correct, but the visual design feels flat and sterile ("just code"). I want you to upgrade the UI/UX and visual design while strictly maintaining my existing component logic (@for, @if, routerLink, pipe usages) and my current theme (modern dashboard with primary, blue, purple, orange accents).

Please refactor the provided code to achieve a Premium SaaS Dashboard look using these UI/UX principles:

1. Visual Hierarchy & Micro-interactions
Stats Cards: Use soft background gradients and glow effects instead of flat borders. When hovered, use scale-105, shadow elevations, and icon shifts.

Typography: Use subtle tracking (e.g., tracking-wide or tracking-tight), distinct font weights, and text-muted-foreground for labels to make the actual data metrics pop.

2. Gamifying the Streaks & Points
Fire Streak: Right now, it's just a text box. Make it feel rewarding! Add a soft, pulsing orange glow. Use a sleek progress visual if possible.

RunPoints: Make this feel like a "currency." Give it a distinct card design that feels separate from standard metrics.

3. Section Elevators
Weekly View: Turn the 7-day grid into a sleek timeline. Use smooth circular rings for "Today" and distinct glass-morphism states for completed vs. rest days.

Activities & Leaderboards: Move away from standard rectangular boxes. Use subtle dividing lines, left-border accents for top-3 leaders (🥇🥈🥉), and clean avatars.

4. Modern UX Fallbacks (Empty States)
Make the empty states (no activities, no events) look beautiful using curated iconography and warm typography, rather than just text.

Here is my current code to refactor. Keep the Angular logic untouched, but overhaul the Tailwind layout and styling classes:

[PASTE YOUR ANGULAR HTML CODE HERE]

💡 What this story achieves for you:
Instead of Claude giving you a completely different sci-fi aesthetic, this tells Claude: "Keep my variables, keep my colors, but use shadows, gradients, flex-alignments, and glass effects to make it look like a high-end web app."