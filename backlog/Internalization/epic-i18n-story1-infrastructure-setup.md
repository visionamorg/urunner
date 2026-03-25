# Epic: Platform Internationalization (i18n)
## Story: Feature - i18n Infrastructure Setup

**As a** Developer
**I want to** integrate a localization library into the frontend application
**So that** the platform can support multiple languages without hardcoding translations in components.

### Description
Before we can translate the application, we need to lay down the foundational infrastructure. This involves adding the appropriate translation library (such as `@ngx-translate/core` for Angular), configuring the HTTP loader to fetch translation JSON files, and defining the initial language files (e.g., `en.json`, `fr.json`).

### Acceptance Criteria
- [ ] The localization library (`ngx-translate` or angular's native i18n) is installed and configured in the `AppModule`.
- [ ] A translation loader is set up to load JSON files from the `assets/i18n/` directory.
- [ ] The default language is set to English (`en`).
- [ ] A fallback language is defined (English) in case a translation key is missing in another language.
- [ ] The app successfully loads and renders a test translated string in the root component without console errors.

### Technical Notes for Claude
- For Angular, we usually prefer `@ngx-translate/core` and `@ngx-translate/http-loader` for dynamic translation switching at runtime without page reloads.
- Create `src/assets/i18n/en.json` and a secondary language file like `fr.json` with a single test key `{ "TEST": "Success" }`.
- Ensure the `TranslateModule` is imported into `SharedModule` so it's readily available across all lazy-loaded feature modules.
