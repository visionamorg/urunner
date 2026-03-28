# Story: API Documentation (Swagger/OpenAPI)

## 🎯 Goal
Provide a comprehensive, interactive documentation portal for the RunHub backend to accelerate frontend development and third-party integrations.

## 👤 User Story
`As a Developer, I want to see all available API endpoints, their request schemas, and response types in a UI so that I can integrate features faster and with fewer errors.`

## 🛠️ Acceptance Criteria
- [ ] Backend: Add `springdoc-openapi-starter-webmvc-ui` dependency to `pom.xml`.
- [ ] Config: Customize Swagger UI title, version, and description.
- [ ] Security: Ensure `/swagger-ui/**` and `/v3/api-docs/**` are publicly accessible in `SecurityConfig.java`.
- [ ] Documentation: Annotate key controllers (`AuthController`, `RunningActivityController`, etc.) with `@Operation` and `@Tag`.
- [ ] Validation: Automate schema generation from JSR-303 annotations (`@NotBlank`, `@Size`).

## 🚀 Powerful Addition: "Try It Out" JWT
Configure Swagger to include a "Authorize" button that allows devs to paste their JWT and test protected endpoints directly from the browser.

## 💡 Technical Strategy
1. Add Maven dependency.
2. Create `OpenApiConfig.java` to define global `Server` and `Info`.
3. Verify that all standard DTOs are correctly scanned and represented in the "Schemas" section of the UI.
