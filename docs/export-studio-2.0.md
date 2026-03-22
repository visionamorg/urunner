# Export Studio 2.0 — Implementation Tracker

## Vision
Canva meets Strava. Transform running activities into stunning, shareable story cards with 16 templates, real-time customization, stickers/badges, and one-click export.

---

## Phase 1: Core Upgrade (Current)

### Templates (16 total, 4 categories)

**Street**
- [x] Cyberpunk — Terminal neon, hacker aesthetic (existed)
- [x] Graffiti — Spray paint texture, bold street fonts
- [x] Brutalist — Raw B&W, massive typography
- [x] VHS Tape — 80s cassette, scanlines, retro distortion

**Elite**
- [x] Race Bib — Official race bib with runner number
- [x] Podium — Gold/silver/bronze medal ceremony
- [ ] Splits Chart — Lap-by-lap pace bar chart (needs splits data)
- [ ] Heatmap — Route as glowing heatmap (needs polyline data)

**Minimal**
- [x] Clear Info — Clean stat grid (existed)
- [x] Large Stat — One giant number (existed)
- [x] Receipt — Thermal printer monospace (existed)
- [x] Polaroid — Photo + handwritten caption

**Editorial**
- [x] Breaking News — Newspaper front page (existed as 'newspaper')
- [x] Magazine — Glossy editorial (existed as 'story-global')
- [x] Typography Poster — Words as visual art (existed)
- [x] Year Wrapped — Spotify Wrapped recap (existed as 'annual-wrapped')

### Template Categories
- [x] Category filter tabs: All / Street / Elite / Minimal / Editorial
- [x] Active category highlights
- [x] Grid adapts by category filter

### Card Format Presets
- [x] Instagram Story (9:16) — default
- [x] Instagram Post (1:1)
- [x] Landscape (16:9)
- [x] Canvas resizes dynamically per format

### Enhanced Background
- [x] Brightness slider (20–200%)
- [x] Contrast slider (20–200%)
- [x] Saturation slider (0–200%)
- [x] 12 gradient presets (running-themed)
- [x] Pattern presets (carbon fiber, track lanes, topographic, grid, dots)
- [x] Photo upload with collage (existed)
- [x] Auto color extraction (existed)
- [x] Auto-adapt blur + opacity on upload (added)
- [x] Vignette overlay (added)

### Text Editor
- [x] Custom headline (editable)
- [x] Custom subtitle
- [x] Font picker (8 fonts: Inter, Orbitron, Bebas Neue, Playfair Display, Permanent Marker, Space Grotesk, DM Sans, Share Tech Mono)
- [x] Text color picker

### Enhanced Data Visibility
- [x] Distance, Pace, Duration, Location (existed)
- [x] Calories toggle
- [x] Heart Rate toggle
- [x] Elevation Gain toggle
- [x] Activity Name toggle
- [x] Date & Time toggle
- [x] Club Name toggle
- [x] Runner Name toggle
- [x] Watermark (existed)
- [x] Weather Stamp (existed)

### Mobile Responsive
- [x] Mobile: full-screen card preview (controls hidden on mobile)
- [x] Floating bottom toolbar (Templates | Style | Data | Export)
- [ ] Each tab opens half-screen drawer
- [ ] Tablet: canvas top, bottom sheet panel
- [x] Desktop: canvas left, panel right (existed)

### Export Improvements
- [x] Export progress bar with percentage
- [ ] JPEG format option with quality slider (60–100%)
- [x] Video duration selector (5s / 8s / 12s) — configurable in TS
- [x] PNG export (dynamic resolution per format)
- [x] Video export with animated count-up (existed)
- [x] Web Share API (existed)

### Captions
- [x] Motivational caption (updated)
- [x] Aesthetic caption (updated)
- [x] Brag caption (updated)
- [x] One-click copy (existed)

---

## Phase 2: Advanced Features (Future)

### Stickers & Badges
- [ ] Achievement badges: PR, First 5K/10K/Half/Marathon, Streak, Negative Split, Zone 2 King, Hill Crusher
- [ ] Decorative stickers: club logo, pace zones, distance rings, weather icons, terrain icons
- [ ] Motivational stamps: EARNED IT, NO DAYS OFF, BUILT DIFFERENT
- [ ] Drag-and-drop placement
- [ ] Resize and rotate
- [ ] Lock/unlock layers

### Layer System
- [ ] 5 composable layers (background → route → stats → text → stickers)
- [ ] Reorder layers
- [ ] Toggle layer visibility
- [ ] Lock layers

### Advanced Export
- [ ] Route path draw animation in video
- [ ] Badges pop-in with bounce animation
- [ ] Background parallax drift
- [ ] Text slide-in from edge
- [ ] Preview before download
- [ ] QR code generation for run card
- [ ] Direct share to Instagram Stories / WhatsApp / Twitter

### PWA
- [ ] Offline mode
- [ ] Auto-save to camera roll on mobile
- [ ] Install prompt

---

## Design Tokens
```
--bg-primary:     #0a0c0f
--bg-secondary:   #0f1217
--bg-panel:       #1a1f2b
--border:         #2a3040
--text:           #e8eaf0
--muted:          #6b7591
```

## Template-Specific Tokens
```
Cyberpunk:  --cp-cyan: #00f5d4, --cp-pink: #ff6eb4, font: Orbitron / Share Tech Mono
Graffiti:   --gr-red: #ff2d2d, --gr-yellow: #ffe600, font: Permanent Marker
Brutalist:  --br-black: #000, --br-white: #fff, font: Bebas Neue
Race Bib:   --rb-blue: #003087, --rb-red: #cc0000, font: Inter
```
