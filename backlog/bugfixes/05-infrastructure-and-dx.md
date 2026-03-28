# Bug Fixes: 05 - Infrastructure & DX (Items 81-100)

This final document tracks the last 20 tasks focusing on performance, deployment stability, and developer experience.

| ID | Title | File / Location | Description |
|---|---|---|---|
| B-081 | Large Image Uploads | `GoogleDriveService.java` | No limit on image size, potentially crashing the backend heap or Drive quota. |
| B-082 | Missing .dockerignore | `Root` | Docker builds include the entire `node_modules` and `target` folders, slowing down deploy. |
| B-083 | Hardcoded Database URL | `docker-compose.yml` | The port for Postgres is hardcoded to 5432, causing issues if the host has another DB. |
| B-084 | Inefficient Feed Search | `PostRepository.java` | Searching the feed uses `LIKE %term%`, which is extremely slow on large datasets. |
| B-085 | No Log Rotation | `logback-spring.xml` | Spring logs grow indefinitely on the server, eventually filling up the disk. |
| B-086 | Unstructured Logging | `StravaSyncService.java` | Some logs include large JSON blobs as strings, making them unsearchable in tools like ELK/Sentry. |
| B-087 | Dev profile Leakage | `application-dev.yml` | Development tools (like H2 Console) might be active in prod profiles by mistake. |
| B-088 | No Startup Check | `BackendApplication.java` | Doesn't check if the database or Strava keys are reachable on startup; fails the first User request instead. |
| B-089 | Excessive DB Pool | `application.properties` | Setting a fixed pool of 100 connections might overwhelm a small RDS instance. |
| B-090 | Missing Build Number | `Dashboard.component.html` | No version indicator in the UI to confirm which deployment is currently live. |
| B-091 | Dead Code: MockData | `RankingService.java` | The "seed" logic is still present in the production service, wasting memory. |
| B-092 | Missing Asset Hashing | `angular.json` | Some assets don't have unique hashes, leading to cache issues in the browser after a deploy. |
| B-093 | No Favicon check | `frontend` | 404 error and console warning on startup due to a missing default favicon.ico. |
| B-094 | Slow Startup: Flyway | `V_...sql` | Large data migrations run on application startup, causing timeout during blue-green deployment. |
| B-095 | Inconsistent API types | `ActivitiesDto.java` | Some numeric fields are `long`, others are `int`. Standardize to prevent overflow. |
| B-096 | No Read-Only User | `docker-compose.yml` | The application uses the same DB superuser for all operations. Use a lower-permission user. |
| B-097 | Missing Swagger UI | `Spring Security` | `/swagger-ui` is blocked by default, making documentation hard to access for devs. |
| B-098 | Angular: Missing Polyfills | `polyfills.ts` | Missing support for older browsers, leading to blank white screens on legacy Safari versions. |
| B-099 | Inactive Sync Cleanup | `StravaSyncService.java` | No mechanism to stop sync for users who haven't logged in for 6 months. |
| B-100 | The Bug-Hunt Finish | `The Project` | 100th bug item. Ensure all technical debt is linked to a Story or Epic. |
