# Story: Automated Testing Suite (Quality Assurance)

## 🎯 Goal
Ensure the stability and reliability of RunHub by implementing a multi-layer automated testing strategy, preventing regressions during rapid feature development.

## 👤 User Story
`As a Developer, I want to run a single command to verify that all core user flows (Login, Activity Sync, Community Join) are working correctly.`

## 🛠️ Acceptance Criteria
- [ ] Backend: Setup JUnit 5 + Mockito for unit testing of Services.
- [ ] Integration: Use `@SpringBootTest` with Testcontainers (PostgreSQL) for Data Access testing.
- [ ] Frontend: Setup **Playwright** for End-to-End (E2E) testing.
- [ ] CI: Create a GitHub Actions workflow that runs tests on every Pull Request.
- [ ] Coverage: Minimum 70% line coverage for critical business logic (e.g., `RankingService`, `AuthService`).

## 🚀 Powerful Addition: "Visual Regression Testing"
Use Playwright's screenshot comparison feature to verify that the UI layout doesn't break on mobile vs desktop.

## 💡 Technical Strategy
1. Add testing dependencies to `pom.xml` and `package.json`.
2. Create a "Smoke Test" for the login flow: `login.spec.ts`.
3. Implement `AuthServiceTest.java` with 100% path coverage for the JWT validation logic.
