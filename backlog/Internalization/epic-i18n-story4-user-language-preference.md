# Epic: Platform Internationalization (i18n)
## Story: Feature - User Language Preference

**As a** Global User
**I want to** select my preferred language from a dropdown menu and have it remembered across sessions
**So that** I don't have to manually switch the language every time I log in.

### Description
We need a user interface component (usually in the header or profile settings) that allows the user to switch the active language on the fly. This preference should be stored in the browser (LocalStorage) and synchronized with the user's profile in the backend, meaning if they log in from a new device, their language preference is restored automatically.

### Acceptance Criteria
- [ ] A Language Selector component (dropdown or modal) is available in the main navigation or footer.
- [ ] Selecting a new language instantly translates the UI without a full page refresh.
- [ ] The user's choice is saved to LocalStorage, and on subsequent visits, the app initializes `ngx-translate` with this saved language.
- [ ] For authenticated users, selecting a language triggers an API call (`PATCH /api/users/profile/language`) to save the preference in the database.
- [ ] When a user logs in, the app overrides any local language setting with the one stored in their backend profile.

### Technical Notes for Claude
- Add a `preferredLanguage` column (String, e.g., 'en', 'es', 'fr') to the `User` entity in the backend.
- Create a `LanguageService` in the Angular frontend that wraps `TranslateService`. The service should have an `initLanguage()` method called in `APP_INITIALIZER` or `AppComponent` `ngOnInit` to check LocalStorage/Auth state.
- Ensure the dropdown only shows supported locales. Provide a configuration array in `environment.ts` for supported languages (e.g., `SUPPORTED_LOCALES = ['en', 'fr']`).
