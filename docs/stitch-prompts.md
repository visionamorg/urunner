# Stitch Design Prompts for URC Website

> **Project:** urc-website (ID: `3640690512314944608`)
> **URL:** https://stitch.withgoogle.com
>
> Paste each prompt into Stitch to generate a screen. Each prompt describes one page of the RunHub app.
> After generating all screens, use Claude Code MCP to pull designs back:
> ```
> npx @_davideast/stitch-mcp screens -p 3640690512314944608
> ```

---

## Global Design Context (prepend to every prompt if needed)

```
Dark theme social running platform called "URC" (Urban Runners Community).
Background color: #050a18. Card backgrounds: rgba(255,255,255,0.05).
Border color: rgba(255,255,255,0.1). Text: white with muted gray secondary text.
Font: system sans-serif. Tailwind CSS styling. Material Icons for iconography.
Primary accent: blue-600. Destructive: red-500. Success: green-500.
```

---

## 1. Login Page

```
A dark-themed login page for a social running app called "URC".

DESKTOP LAYOUT (split screen):
- Left half: a 2-column × 4-row photo mosaic grid of running/community photos with rounded corners and gap-2 spacing. A dark gradient overlay fades from bottom. Over the photos at bottom-left, show the app name "URC" in large bold white text with a tagline "Join the running community" below it.
- Right half: centered login form on dark background #050a18, max-width 28rem.

LOGIN FORM contains:
1. App logo/name "RunHub" at top
2. Two OAuth buttons stacked full-width:
   - "Continue with Strava" (orange background #FC4C02, white text, Strava icon)
   - "Continue with Garmin" (blue background #007CC3, white text, Garmin icon)
3. A divider line with "OR" text centered
4. Email input field (dark background, rounded, border, placeholder "Email")
5. Password input field with eye toggle icon for show/hide
6. "Sign In" primary blue button, full-width, rounded
7. "Don't have an account? Create one" link below
8. Small hint box at bottom: "Demo: alice@example.com / password123"

MOBILE LAYOUT: single column, no photo mosaic, just the form centered with a small logo at top.

Background: #050a18. Cards/inputs: semi-transparent white backgrounds. White text.
```

---

## 2. Register Page

```
A dark-themed registration page for a social running app called "URC".

DESKTOP LAYOUT (split screen):
- Left half: same photo mosaic grid as login (2×4 grid of running photos with gradient overlay). Tagline: "Start your running journey today".
- Right half: centered registration form, max-width 32rem.

REGISTRATION FORM contains:
1. App logo "RunHub" at top
2. Two OAuth buttons:
   - "Continue with Strava" (orange #FC4C02)
   - "Continue with Garmin" (blue #007CC3)
3. Divider with "OR"
4. Two-column row: First Name + Last Name inputs
5. Username input
6. Email input
7. Password input with visibility toggle
8. "Create Account" primary blue button, full-width
9. "Already have an account? Sign in" link
10. Inline validation messages in red below fields when invalid

MOBILE: single column, all fields stacked vertically.
Background: #050a18. Input fields: dark with subtle borders.
```

---

## 3. Home / Landing Page

```
A dark-themed landing page for a social running app called "URC" (Urban Runners Community).

SECTIONS (top to bottom):

1. STICKY NAVBAR: dark transparent background, logo "URC" left, nav links (Features, Community, About) center, "Sign In" and "Get Started" buttons right. Mobile: hamburger menu.

2. HERO SECTION:
   - Desktop: left side has large heading "Track Your Runs. Join the Community." with gradient-highlighted keywords in blue. Subtitle text below. Two CTA buttons: "Get Started" (primary blue) and "Learn More" (outline).
   - Right side: 3×4 photo mosaic grid of runners with floating achievement badge overlays (e.g., "🏃 5K PR!", "🔥 30 Day Streak") that have subtle pulse animations.
   - Mobile: 2×2 photo grid above the text content.

3. FEATURES GRID: 2→4 columns responsively. 8 feature cards, each with:
   - Colored icon circle (blue, red, green, purple, cyan, amber, pink, orange)
   - Feature title (bold)
   - Short description (muted text)
   - Cards have dark background with hover scale effect

4. COMMUNITY GALLERY: horizontal row of 6 community photos with captions and member counts.

5. HOW IT WORKS: 3-step numbered cards (1. Create Account, 2. Track Runs, 3. Join Community) with connecting lines.

6. STATS CAROUSEL: rotating display showing platform stats (e.g., "10,000+ Runners", "50,000+ KM Tracked", "200+ Communities").

7. CTA FOOTER: "Ready to start running?" heading with "Join Now" button.

Background: #050a18 throughout. Subtle animated glow effects behind hero. White text, muted gray secondary.
```

---

## 4. Dashboard

```
A dark-themed runner dashboard for a social running app called "URC".

LAYOUT (top to bottom):

1. HEADER ROW: "Good morning, [Username]" greeting left, "Log Run" blue primary button right.

2. STATS GRID (6 cards in a row on desktop, 2 columns on mobile):
   - Total Distance (km) with blue route icon
   - Total Runs with red activity icon
   - Weekly Distance with blue trending-up icon
   - Monthly Distance with purple calendar icon
   - Average Pace with green speed icon
   - Total Duration with orange timer icon
   Each card: dark bg, colored icon circle, large number, label below.

3. STREAK + RUNPOINTS ROW (2 cards, equal width):
   - Run Streak card: large fire emoji 🔥, streak number in orange, "days" label, "Keep it going!" message
   - RunPoints card: large star emoji ⭐, points number in yellow, level progress bar, "Level [X]" label

4. TWO-COLUMN SECTION (2/3 + 1/3):
   - Left: WEEK CALENDAR - 7 day circles (Mon-Sun) in a row, current day highlighted in blue, days with activities have a small green dot below. Activity list for selected day below.
   - Right: TODAY'S SCHEDULE - list items with colored left borders (blue for activities, green for events, purple for programs). Each shows time + title.

5. TWO-COLUMN SECTION (2/3 + 1/3):
   - Left: RECENT ACTIVITIES - list of activity cards with run icon, title, date, distance badge (blue), duration badge (gray), pace badge (gray).
   - Right: UPCOMING EVENTS - 3 event cards with date box (day/month), event name, location, register button.

6. TWO-COLUMN SECTION (1/2 + 1/2):
   - Left: ACTIVE CHALLENGES - challenge cards with title, progress bar, percentage, deadline.
   - Right: TRAINING PROGRAMS - program cards with name, level badge, progress bar, session count.

7. MINI LEADERBOARD: top 5 ranked users with medal emojis (🥇🥈🥉), avatar, name, distance. Current user highlighted.

Background: #050a18. Cards: rgba(255,255,255,0.05) with subtle borders. Hover scale effects on cards.
```

---

## 5. Communities List

```
A dark-themed communities listing page for a social running app called "URC".

LAYOUT:

1. HEADER: "Communities" title left, "Create Community" blue button right.

2. CREATE FORM (collapsible, shown when button clicked):
   - Card with fields: Community Name, Description (textarea), Google Drive Folder ID, Icon URL, Cover URL
   - Create + Cancel buttons

3. COMMUNITIES GRID (3 columns on desktop, 2 on tablet, 1 on mobile):
   Each community card:
   - Cover image at top (height 96px) or gradient fallback if no cover
   - Overlapping community avatar circle (56px) with initials, positioned at bottom of cover
   - Community name (bold, line-clamp-1)
   - Member count with people icon (muted text)
   - Description (2 lines, muted text, line-clamp-2)
   - "Created by [username]" footer
   - Join button (primary blue outline) OR Leave button (destructive outline) OR "Joined ✓" badge (green)
   - Hover lift effect on cards

STATES: Loading spinner centered, Empty state with icon and "No communities yet" message.
Background: #050a18. Cards: dark with border.
```

---

## 6. Community Detail

```
A dark-themed community detail page for a social running app called "URC".

LAYOUT:

1. HERO SECTION (height 256px):
   - Full-width cover image with dark gradient overlay from bottom
   - Community name (h1, white, bold) over the gradient
   - Description below name
   - Member count badge
   - Action buttons: "Join/Leave" (primary), "Sync Drive" (secondary)

2. COMMUNITY GOAL BANNER (optional, below hero):
   - Goal title, current progress, target
   - Progress bar (green/primary fill)
   - "X% complete" label

3. TAB BAR: horizontal scrollable tabs - Feed, Events, Calendar, Chat, Rooms, Members, Invites, Settings
   Active tab: primary blue background. Others: secondary background.

4. FEED TAB:
   - Create post box: avatar + textarea + "Post" button
   - Upcoming events strip (3 mini event cards horizontal)
   - Posts list:
     - Each post: author avatar + name + timestamp, post content, photo album grid (if PHOTO_ALBUM type), like/comment/pin actions, comments thread expandable
     - Pinned posts have a pin icon badge
     - Admin actions: delete post, pin/unpin

5. EVENTS TAB: create event form + event cards grid

6. MEMBERS TAB: member list with avatar, name, role badge (ADMIN/MODERATOR/MEMBER), admin actions (kick, change role)

7. CHAT TAB: message bubbles (own = blue right-aligned, others = gray left-aligned), message input at bottom

Background: #050a18. Tabs: dark buttons. Content: dark cards.
```

---

## 7. Feed Page

```
A dark-themed social feed page for a social running app called "URC".

LAYOUT (max-width 672px, centered):

1. HEADER: "Feed" title, "Your running community feed" subtitle in muted text.

2. CREATE POST CARD:
   - User avatar circle (40px) left
   - Textarea "What's on your mind?" right
   - "Post" blue button below, right-aligned
   - Dark card background with border

3. POSTS LIST (vertical, gap 24px):
   Each post card:
   - Top row: author avatar (40px) + author name (bold) + timestamp (muted, relative time)
   - Content text (white, multi-line)
   - Photo grid (if photo album post): 2-column grid of images with rounded corners
   - Action row: Like button (heart icon + count), Comment button (chat icon + count), Share button
   - Liked state: filled heart in red
   - Comments section (expandable):
     - Comment list: avatar + name + text + timestamp
     - Comment input: small avatar + text input + send button

STATES: Loading skeleton, empty state "No posts yet. Be the first to share!"
Background: #050a18. Cards: dark with subtle border. Hover effects on action buttons.
```

---

## 8. Activities Page

```
A dark-themed activities tracking page for a social running app called "URC".

LAYOUT (max-width 1024px):

1. HEADER ROW: "My Activities" title, "Log Run" blue button right.

2. STATS GRID (4 cards, 2 columns mobile → 4 columns desktop):
   - Total KM: large number + "km" unit, route icon (blue)
   - Weekly KM: number + trend icon (green)
   - Monthly KM: number + calendar icon (purple)
   - Total Runs: number + activity icon (orange)
   Each card: dark background, colored icon, large bold number, muted label.

3. LOG ACTIVITY FORM (collapsible, animated slide-down):
   Card with fields in grid:
   - Title input
   - Date picker
   - Distance (km) number input
   - Duration (HH:MM:SS) input
   - Location input
   - Notes textarea
   - "Save Activity" blue button + "Cancel" ghost button

4. ACTIVITIES LIST (vertical, gap 12px):
   Each activity card:
   - Left: colored run icon circle (40px)
   - Middle: title (bold) + date below (muted) + location (muted with map-pin icon)
   - Right: three badges in a row:
     - Distance: blue badge "X.XX km"
     - Duration: gray badge "Xh Xm Xs"
     - Pace: gray badge "X:XX /km"
   - "Export to Studio" small button
   - Expandable notes section

STATES: Loading spinner, empty state "No activities yet. Log your first run!"
Background: #050a18. Cards: dark with border.
```

---

## 9. Events Page

```
A dark-themed events listing page for a social running app called "URC".

LAYOUT (max-width 1280px):

1. HEADER: "Events" title, subtitle "Discover and join running events".

2. EVENTS GRID (3 columns desktop, 2 tablet, 1 mobile, gap 24px):
   Each event card (vertical flex):
   - Date badge (56px square, rounded): large day number on top, 3-letter month below. Primary blue bg for upcoming, gray for past.
   - Event name (h3, bold, white)
   - Time with clock icon (muted)
   - Description (2-line clamp, muted)
   - Detail chips row:
     - Location chip (map-pin icon + city name)
     - Distance chip (route icon + "X km")
     - Participants chip (people icon + count)
     - Price chip (green badge "Free" or price amount)
   - Footer: "Organized by [community name]" muted text
   - Action: "Register" blue button for upcoming, "Past Event" gray badge for past
   - Hover lift and border highlight effects

STATES: Loading spinner, empty state with calendar icon "No events yet."
Past events are dimmed (opacity 60%).
Background: #050a18. Cards: dark with border.
```

---

## 10. Training Programs

```
A dark-themed training programs page for a social running app called "URC".

LAYOUT (max-width 1280px):

1. HEADER: "Training Programs" title, "Build your running with structured programs" subtitle.

2. ACTIVE PROGRAMS SECTION (shown only if user has active programs):
   - Section title "Your Active Programs"
   - Cards (horizontal scroll or grid):
     - Program name (bold)
     - Level badge (colored: green for BEGINNER, yellow for INTERMEDIATE, red for ADVANCED)
     - "Week X of Y" indicator
     - Large progress percentage
     - Progress bar (h-2, primary blue fill)
     - "X/Y sessions completed" text
     - "Continue" button

3. ALL PROGRAMS GRID (3 columns desktop, 2 tablet, 1 mobile):
   Each program card:
   - Level badge top-right corner (colored pill)
   - "Enrolled ✓" green badge if user is enrolled
   - Program name (h3, bold)
   - Description (2-line clamp, muted)
   - Stats row (3 items):
     - Duration: calendar icon + "X weeks"
     - Distance goal: route icon + "X km"
     - Sessions: activity icon + "X sessions"
   - "Start Program" blue button (or "Enrolled" disabled state)
   - Hover effects

Background: #050a18. Cards: dark with border.
```

---

## 11. Rankings / Leaderboard

```
A dark-themed leaderboard page for a social running app called "URC".

LAYOUT (max-width 768px, centered):

1. HEADER: "Rankings" title centered.

2. TAB BUTTONS (3 pills in a row, centered):
   - "Weekly" | "Monthly" | "All Time"
   - Active tab: primary blue background, white text
   - Inactive: secondary dark background, muted text

3. RANKINGS TABLE (no visible table headers, clean list):
   Each rank row (flex, aligned center, padding 16px, gap 16px):
   - Rank indicator:
     - 1st place: 🥇 gold medal emoji (large)
     - 2nd place: 🥈 silver medal emoji
     - 3rd place: 🥉 bronze medal emoji
     - 4th+: plain number in muted text
   - User avatar circle (40px) with profile image or initials
   - Username (bold, white). If current user: add a small "You" primary blue badge next to name
   - Right side: distance "XX.X km" (bold) + "X runs" below (muted, smaller)

   Row backgrounds:
   - 1st place: subtle yellow tint (yellow-500 at 5% opacity)
   - 2nd place: subtle gray tint (slate-500 at 5% opacity)
   - 3rd place: subtle blue tint (blue-600 at 5% opacity)
   - Current user row: primary at 10% opacity background
   - Hover: secondary background
   - Divider lines between rows

Background: #050a18. Clean, minimal design.
```

---

## 12. Profile Page

```
A dark-themed runner profile page for a social running app called "URC".

LAYOUT (max-width 1024px):

1. HERO CARD (full-width, gradient background from blue-900/20 to transparent):
   - Large profile avatar (112px circle) with image or initials on gradient circle
   - Full name (h1, bold, white)
   - @username below (muted)
   - Badge row (horizontal flex wrap, gap 8px):
     - Category badge (e.g., "Road Runner", primary pill)
     - Location badge (map-pin icon, muted pill)
     - Experience badge ("X years running", muted pill)
     - Weekly goal badge (target icon, muted pill)
     - Role badge (if admin/moderator, colored pill)
     - RunPoints badge (star icon, yellow pill with points)
   - Bio quote (italic, muted, with quote marks)
   - Instagram handle link (if set)
   - "Edit Profile" outline button

2. STATS GRID (4 cards, 2 cols mobile → 4 cols desktop):
   - Total Runs (activity icon, blue)
   - Total KM (route icon, green)
   - Weekly KM (trending icon, purple)
   - Badges Earned (star icon, yellow)

3. PERSONAL BESTS GRID (4 cards, 2→4 cols):
   - 5K best time
   - 10K best time
   - Half Marathon best
   - Marathon best
   Each: distance label (muted), time (large bold), or "--:--" if not set.

4. TWO-COLUMN BOTTOM (1/2 + 1/2 on desktop):
   LEFT: Edit profile form (expandable):
   - Profile photo URL + preview
   - First/Last name inputs
   - Location, Gender selects
   - Running category, years experience
   - Weekly goal, bio, Instagram handle
   - Personal bests (5K, 10K, Half, Marathon)
   - Save button

   RIGHT:
   - Badges section: grid of earned badge cards (icon + name + date)
   - Integrations: Strava connected status, Garmin connected status

Background: #050a18. Cards: dark with border.
```

---

## 13. Chat Page

```
A dark-themed community chat page for a social running app called "URC".

LAYOUT (full viewport height minus header, split horizontally):

LEFT SIDEBAR (width 288px, fixed, dark background):
- Header: "Communities" label (muted, uppercase, small)
- Community list (vertical scroll):
  Each community button:
  - Initials avatar circle (40px) with colored background
  - Community name (bold, white, line-clamp-1)
  - Member count below (muted, small)
  - Selected state: primary blue left border bar (3px), slightly lighter background
  - Hover: secondary background
- Divider line between communities

RIGHT CHAT AREA (flex-1):
- Chat header bar:
  - Community avatar (32px) + Community name (bold)
  - Border bottom separator

- Messages area (scrollable, flex-grow):
  - Own messages: blue bubble (primary bg), right-aligned, rounded-2xl with bottom-right square corner
  - Others' messages: gray bubble (secondary bg), left-aligned, rounded-2xl with bottom-left square corner
  - Username label above other's messages (muted, small)
  - Timestamp below each message (muted, tiny)
  - Messages grouped by sender

- Message input bar (bottom, sticky):
  - User avatar (32px) left
  - Text input field (dark bg, rounded-full, "Type a message..." placeholder)
  - Send button (primary blue circle with arrow icon) right

MOBILE: sidebar hidden, show only chat area with back button to return to community list.
Background: #050a18.
```

---

## 14. Calendar Page

```
A dark-themed running calendar page for a social running app called "URC".

LAYOUT (max-width 1280px):

1. HEADER ROW: "Calendar" title, "Today" ghost button, "Log Run" blue button.

2. TWO-COLUMN GRID (2/3 + 1/3 on xl, stacked on mobile):

LEFT (2/3) - CALENDAR:
- Month navigation: left chevron, "March 2026" bold center, right chevron
- Weekday labels row: Mon Tue Wed Thu Fri Sat Sun (muted, small)
- Calendar grid (7 columns):
  - Each day cell (square-ish, padding):
    - Day number (white, or muted if outside current month)
    - Small colored dots below number:
      - Blue dot = has activity
      - Green dot = has event
    - Selected day: primary blue ring border
    - Today: subtle primary background
    - Hover: lighter background
  - Past days with no events: slightly muted
- Legend below: "● Activity" (blue) "● Event" (green)

RIGHT (1/3) - DAY DETAIL PANEL:
- Selected date label (bold, "Thursday, March 19, 2026")
- Activity count: "X activities"
- Activities list:
  - Each: run icon (blue circle), title, distance badge, duration badge, pace badge
- Events list:
  - Each: event icon (green circle), title, location, distance
- Empty state: "No activities or events on this day"

MONTH STATS CARD (below detail panel):
- "March Stats" heading
- Activities count
- Events count
- Quick links: "View all activities →", "View all events →"

Background: #050a18. Calendar cells: dark with subtle borders.
```

---

## 15. Export Studio

```
A dark-themed export studio page for a social running app called "URC". This is a creative tool for generating shareable running activity graphics.

LAYOUT (full-screen, no scroll):

1. TOP HEADER BAR:
   - Back arrow button (left)
   - "Export Studio" title (center)
   - Action buttons (right): Share icon button, Video icon button, "Export" primary blue button

2. TAB SWITCHER: "Builder" tab, "Explore Community" tab (pill-style toggle)

3. BUILDER TAB:
   - LEFT: CANVAS PREVIEW (centered, phone-aspect-ratio frame ~375×667px):
     - Background image or gradient
     - Overlay content: activity stats (distance, time, pace) in styled layout
     - URC branding stamp/watermark (small logo in corner)
     - Real-time preview updates as controls change

   - RIGHT: CONTROLS SIDEBAR (scrollable):
     - Template selector (thumbnail grid)
     - Background options (color picker, image upload, presets)
     - Stats display toggles
     - Font/color customization
     - Branding position selector
     - Layout options

4. EXPLORE COMMUNITY TAB:
   - Filter pills row: "All", "Minimalist", "Bold", "Gradient", "Photo" (scrollable)
   - Sort tabs: "Trending", "Top Rated", "Newest"
   - Masonry grid of community-created templates:
     Each template card:
     - Thumbnail preview image
     - Creator row: avatar + username
     - Vote button (arrow up + count)
     - Save/bookmark button
     - Template name (bold)
     - Short description
     - Download count with icon

Background: #050a18. Canvas area: dark with subtle grid pattern. Controls: dark cards.
```

---

## Usage Instructions

1. Go to https://stitch.withgoogle.com
2. Open project "urc-website"
3. For each page above, click "New Screen" and paste the prompt
4. Name each screen to match the page (e.g., "login", "dashboard", "communities")
5. After generating all screens, sync with Claude Code:

```bash
# List all screens
npx @_davideast/stitch-mcp screens -p 3640690512314944608

# Get specific screen code
npx @_davideast/stitch-mcp tool get_screen_code -d '{"projectId":"3640690512314944608","screenId":"SCREEN_ID"}'

# Preview locally
npx @_davideast/stitch-mcp serve -p 3640690512314944608
```

6. In Claude Code, you can then say:
   > "Get the dashboard screen from Stitch and update my Angular dashboard component to match"
