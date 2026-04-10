# US-005 — Export Studio: Story Global Template

## Status: [x] Done

## Description
Add a new export template called **Story Global** — an editorial, magazine-quality story card inspired by modern running community aesthetics (Strava shares, run club posters).

### Design Inspiration
- Editorial "Activity On [Day]" headline with contrasting font weights
- Category pill tag (URC accent color)
- Semi-transparent frosted stats panel showing Distance / Pace / Time + quote
- Full-bleed photo background
- Clean bottom strip with @username and date

### Template Layout (1080×1920)
```
┌─────────────────────────────────┐
│  [URC pill]                     │
│                                 │
│  Activity  ↗                    │
│  On TUESDAY                     │
│                                 │
│  ┌──────────────────────────┐   │
│  │ Distance    Pace   Time  │   │
│  │ 21.10km   5:27  1:55:00  │   │
│  │                          │   │
│  │ [activity title / quote] │   │
│  └──────────────────────────┘   │
│                                 │
│  @alice_runner    Mar 15, 2026   │
└─────────────────────────────────┘
```

## Acceptance Criteria
- [x] New "Story Global" template selectable from carousel
- [x] Shows day of week extracted from activity date ("On TUESDAY")
- [x] Stats panel with distance, pace, duration (respects visibility toggles)
- [x] Activity title used as the in-card quote
- [x] Fully works with background photo upload and collage
- [x] Captured correctly in exported PNG / video

## Status: [x] Done
