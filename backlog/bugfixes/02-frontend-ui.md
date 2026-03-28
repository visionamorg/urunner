# Bug Fixes: 02 - Frontend & UI (Items 21-40)

This document tracks the second batch of 20 bug fixes and UX polish items for the Angular frontend.

| ID | Title | File / Location | Description |
|---|---|---|---|
| B-021 | Missing Loading States | `DashboardComponent.ts` | Activity charts remain empty for several seconds with no loader, making the app feel frozen. |
| B-022 | Memory Leak: RxJS | `ActivityDetailComponent.ts` | Observables are not unsubscribed on `OnDestroy`, causing memory to climb on every view. |
| B-023 | No Error feedback: Form | `LoginComponent.html` | Validation errors (e.g., "Invalid email") are not shown, users just see the button pulse briefly. |
| B-024 | Hardcoded API Base | `environment.ts` | `/api/` is hardcoded as a prefix in some services instead of being a configurable constant. |
| B-025 | Mobile Nav Overlap | `LayoutComponent.css` | The bottom navigation bar covers the last item in the activity list on iPhones. |
| B-026 | Incorrect Date Format | `ActivityCardComponent.html` | Dates show as `2024-03-28` instead of localized strings (e.g. "Mar 28"). |
| B-027 | No Confirm: Delete | `ActivityDetailComponent.ts` | Clicking the "Delete" button immediately deletes the activity with no confirmation dialog. |
| B-028 | Interceptor Gaps | `AuthInterceptor.ts` | Doesn't handle 401s correctly when the JWT naturally expires, leads to a loop of failed requests. |
| B-029 | Flash of Unthemed Content | `index.html` | Dark mode doesn't apply before the JS loads, causing a white flash on startup. |
| B-030 | Missing Favicon | `src/assets` | Standard Angular favicon is missing or uses the default logo instead of the RunHub icon. |
| B-031 | Large Polyline: Canvas | `MapComponent.ts` | Rendering a marathon-length polyline crashes the browser on low-end devices. Use point simplification. |
| B-032 | No "Empty State" | `CommunitiesComponent.html` | When a user has no communities, a blank white screen is shown instead of a "Discover" call-to-action. |
| B-033 | Image Placeholder Error | `AvatarComponent.html` | Broken profile images show a 404 broken icon instead of a circular initials placeholder. |
| B-034 | Search Debounce missing | `CommunitySearch.ts` | Every keystroke triggers an API call, potentially DDOSing the backend. |
| B-035 | Untranslated Text | `ExportStudioComponent.html` | The "Export to Instagram" feature has several buttons with hardcoded English strings. |
| B-036 | No Password Strength | `RegisterComponent.ts` | Allows simple "123456" passwords, leaving users vulnerable. |
| B-037 | Breadcrumb Drift | `AppRoutes.ts` | Nested routes for community members result in a long breadcrumb that breaks on mobile. |
| B-038 | Scroll Position Lost | `AppRoutingModule.ts` | Navigating back from an activity scrolls the user to the top, losing their place in the feed. |
| B-039 | Missing 'Alt' Tags | `PostComponent.html` | Activity photos are missing alternative text, making it inaccessible for screen readers. |
| B-040 | Duplicate Toast Alerts | `ToastService.ts` | Clicking a button twice result in multiple identical "Success" messages stacking up. |
