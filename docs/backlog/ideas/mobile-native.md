# Native Mobile App — Ideas Pool

All ideas for a native mobile app (Capacitor/Ionic or React Native).
**Prerequisite:** The web app must be feature-complete before starting a mobile app.

---

## Auth & Onboarding

| Idea | Description |
|------|-------------|
| Splash screen | Animated RunHub logo on app launch |
| Email registration | Standard email + password sign-up flow |
| Apple Sign In | iOS requirement for App Store apps |
| Google Sign In | Android/iOS convenience |
| Forgot password | Email-based reset flow |
| Onboarding: permissions | Request GPS, notifications, motion permissions with explanation |
| Onboarding: profile setup | Name, photo, location on first launch |
| Onboarding: physical metrics | Height, weight, max HR — used for calorie calc |
| Onboarding: goal setting | Pick a goal race + target date → seeds the AI plan |
| Onboarding: find friends | Import contacts or search by username |
| JWT token refresh | Silent background token refresh |
| Logout + Delete account | Full account deletion with data export |
| App tour | First-run guided tour of key features |

---

## Core Run Tracking

| Idea | Description |
|------|-------------|
| Start Run button | Large CTA on main screen |
| Activity type selector | Run / Walk / Hike / Bike / Trail |
| GPS signal indicator | Show GPS accuracy before starting |
| Live map view | Real-time polyline on map during run |
| Auto-pause | Pause timer when GPS shows no movement |
| Audio cues | Pace, distance, HR announcements via TTS |
| Lock screen controls | Play/pause/lap from lock screen |
| Swipe to finish | Confirm run end with swipe (prevents accidental stop) |
| Save activity form | Title, shoe selection, effort rating, notes |
| Perceived effort | RPE scale 1–10 after run |
| Local SQLite sync | Store run data locally before uploading (offline support) |
| Run countdown | 3-2-1 countdown before tracking starts |
| Live metrics dashboard | Current pace, HR, distance, cadence in large format |

---

## Social Feed

| Idea | Description |
|------|-------------|
| Infinite scroll feed | Pull-to-refresh + infinite scroll |
| Pull to refresh | Swipe down to reload feed |
| Inline polyline maps | Miniature route map on each activity post |
| Double-tap kudos | Instagram-style double-tap to like |
| In-app comments | Comment without leaving the feed |
| Mention autocomplete | @username suggestion while typing |
| Photo carousel | Swipe through multiple photos in a post |
| Share to stories | Export activity card directly to Instagram stories |
| Feed filters | Filter by: Following / Community / All |
| Block & report | Safety feature |

---

## Profile & Settings

| Idea | Description |
|------|-------------|
| Profile header | Avatar, stats, follow/following count |
| Activity history list | Scrollable list of past runs |
| Edit profile | Photo, bio, location, PBs |
| Privacy settings | Public / Followers only / Private |
| Unit preferences | km vs miles, °C vs °F |
| Push preferences | Per-notification-type toggles |
| Follow / unfollow | Social graph from mobile |
| User search | Find runners by name or username |
| Theme toggle | Dark / Light / System |
| Trophy cabinet | All earned badges in a visual display |

---

## Community & Events

| Idea | Description |
|------|-------------|
| Club discovery | Nearby clubs based on GPS location |
| Club chat | In-app community chat rooms |
| Event listing | Browse events near me (GPS-based filter) |
| Event RSVP | Register/unregister for events |
| Event route preview | GPX route on map before the event |
| Club leaderboard | Weekly KM leaderboard per community |

---

## Gear & Wearables

| Idea | Description |
|------|-------------|
| Bluetooth HR monitor | Connect BLE heart rate strap during run |
| Apple Health sync | Import/export to Apple Health (HealthKit) |
| Shoe garage | Select shoe before a run |
| Select shoe post-run | Assign shoe after saving the activity |
| Wearable import poll | Auto-check for new Garmin/Apple Watch activities |
| Voice feedback settings | Customize audio cue frequency and content |

---

## Gamification

| Idea | Description |
|------|-------------|
| Global leaderboards | Weekly/monthly/all-time mobile-native view |
| Monthly challenges | Progress bars for community challenges |
| Leveling system | Runner Level 1–50 based on total lifetime KM |
| Streak flame | Visual flame animation for active streaks |

---

## Technical Mobile Stories

| Idea | Description |
|------|-------------|
| Cross-platform architecture | Capacitor (Angular-based) vs React Native decision |
| Background GPS tracking | Continue tracking when app is backgrounded |
| Push notifications | Firebase Cloud Messaging (FCM) for mobile push |
| Offline mode | Cache feed + activities for offline viewing |
| Native camera export | Share export studio cards using native share sheet |
| Biometric login | Face ID / fingerprint authentication |
