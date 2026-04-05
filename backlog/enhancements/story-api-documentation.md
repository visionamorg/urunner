# Story: API Documentation (Swagger/OpenAPI)

## 🎯 Goal
Provide a comprehensive, interactive documentation portal for the RunHub backend to accelerate frontend development and third-party integrations.

## 👤 User Story
`As a Developer, I want to see all available API endpoints, their request schemas, and response types in a UI so that I can integrate features faster and with fewer errors.`

## Status: DONE ✓ (2026-04-05)

## 🛠️ Acceptance Criteria
- [x] Backend: Add `springdoc-openapi-starter-webmvc-ui` dependency to `pom.xml`.
- [x] Config: Customize Swagger UI title, version, and description.
- [x] Security: Ensure `/swagger-ui/**` and `/v3/api-docs/**` are publicly accessible in `SecurityConfig.java`.
- [x] Documentation: Annotate key controllers (`AuthController`, `ActivityController`, `CommunityController`, `EventController`, `RankingController`) with `@Operation` and `@Tag`.
- [x] Validation: Automate schema generation from JSR-303 annotations (`@NotBlank`, `@Size`).

## 🚀 Powerful Addition: "Try It Out" JWT
Configure Swagger to include a "Authorize" button that allows devs to paste their JWT and test protected endpoints directly from the browser.

## 💡 Technical Strategy
1. Add Maven dependency.
2. Create `OpenApiConfig.java` to define global `Server` and `Info`.
3. Verify that all standard DTOs are correctly scanned and represented in the "Schemas" section of the UI.
