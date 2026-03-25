# Epic: Platform Internationalization (i18n)
## Story: Feature - Static Content Translation

**As a** Global User
**I want to** see the website navigation, buttons, and static text in my native language
**So that** I can easily navigate and understand the platform.

### Description
With the i18n infrastructure in place, the next step is a sweeping refactor of the frontend templates to replace hardcoded English strings with translation keys. This covers the navigation bar, sidebars, buttons, form labels, and general page headings.

### Acceptance Criteria
- [ ] All major navigation items (e.g., Feed, Events, Store, Profile) are extracted to translation keys (e.g., `NAV.FEED`).
- [ ] Common buttons (Save, Cancel, Delete, Edit) use translation keys (e.g., `COMMON.BUTTONS.SAVE`).
- [ ] Form labels and placeholders across Auth and Profile components are translated.
- [ ] Both `en.json` and the secondary language (e.g., `fr.json`) are updated with the corresponding extracted key-value pairs.
- [ ] The UI looks identical to before, but text is now served through the translate pipe/directive.

### Technical Notes for Claude
- Use the `translate` pipe or `[translate]` directive in Angular templates (e.g., `{{ 'NAV.FEED' | translate }}`).
- Organize the JSON translation files logically using nested objects.
Example:
```json
{
  "NAV": {
    "HOME": "Home",
    "COMMUNITIES": "Communities"
  },
  "COMMON": {
    "SAVE": "Save",
    "CANCEL": "Cancel"
  }
}
```
- A custom script or tool (like `ngx-translate-extract`) can be used to harvest missing keys automatically if desired.
