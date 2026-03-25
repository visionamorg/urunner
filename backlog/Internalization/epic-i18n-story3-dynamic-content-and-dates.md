# Epic: Platform Internationalization (i18n)
## Story: Feature - Dynamic Content & Regional Formatting

**As a** Global User
**I want to** see dates, currencies, and dynamic messages formatted correctly according to my locale
**So that** numbers and times make sense for my region.

### Description
Translating static text is not enough; dynamic content like "You have 3 new messages", currency displays in the store, and event dates need to adapt to the language and locale. Formatting rules change drastically (e.g., DD/MM/YYYY vs MM/DD/YYYY, and €10,00 vs $10.00).

### Acceptance Criteria
- [ ] Dates and times across the app (feed posts, events, messages) use the Angular `date` pipe passing the current user's locale, OR use a library like date-fns formatted to the locale.
- [ ] Prices in the Community Store format correctly using the `currency` pipe dynamically adjusting to the user's locale.
- [ ] Parameterized translations are implemented for strings containing variables (e.g., `Hello {{username}}`).
- [ ] Pluralization rules are handled (e.g., "1 member" vs "5 members").

### Technical Notes for Claude
- Use parameters in `ngx-translate`: `{{ 'GREETING' | translate:{name: user.username} }}`.
- For Pluralization in ngx-translate, you can use the `@ngx-translate/core` paired with a pluralization mechanism or just use tertiary logic in the template for simple cases (e.g., `count === 1 ? 'MEMBER.SINGULAR' : 'MEMBER.PLURAL'`).
- Angular's native currency and date pipes behave according to the `LOCALE_ID` provided to the application module. You may need to dynamically override a provider or pass the locale directly into the pipes depending on the architecture.
