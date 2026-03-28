# Bug Fixes: 04 - Security & Auth (Items 61-80)

This document tracks the fourth batch of 20 bug fixes for security, permissions, and authentication.

| ID | Title | File / Location | Description |
|---|---|---|---|
| B-061 | Missing Rate Limiting | `AuthService.java` | No limit on login/registration, allowing brute-force/dictionary attacks. |
| B-062 | Insecure JWT Logout | `AuthService.ts` | Logout only clears local storage. The JWT remains valid until expiration. |
| B-063 | Role Escalation Risk | `UserController.java` | Setting the `role` field in a profile update DTO might allow users to make themselves ADMINs. |
| B-064 | Plaintext sensitive info | `application.properties` | OAuth client secrets are stored in plaintext in the config instead of env variables or a vault. |
| B-065 | Improper CORS Origin | `SecurityConfig.java` | Using `*` for CORS origins instead of a strict whitelist of frontend URLs. |
| B-066 | Information Leakage: user | `AuthController.java` | Response for "User Not Found" differs from "Invalid Password", revealing which emails are registered. |
| B-067 | No Password Complexity | `UserService.java` | Accepts passwords like "abc" without warning or rejection. |
| B-068 | Missing CSRF Token | `SecurityConfig.java` | CSRF protection might be disabled for API calls but missing equivalent double-submit cookie protection. |
| B-069 | Direct URL Access | `event-detail.guard.ts` | If a user enters a private community URL, they see the page skeleton briefly before the guard kicks in. |
| B-070 | Auth Provider Flip-flop | `User.java` | Linking a local account to Strava might overwrite the `authProvider` field and lock the user out of local login. |
| B-071 | Unverified Email | `RegisterRequest.java` | Users can register with any email without verification, leading to database pollution. |
| B-072 | Token Refresh Exposure | `OAuthController.java` | Returning the full `refreshToken` to the frontend. It should be stored in an HttpOnly cookie. |
| B-073 | Missing Audit: Delete | `ActivityService.java` | No record of quién eliminated an activity, complicating support requests. |
| B-074 | Open Redirect: Auth | `OAuthController.java` | The `redirect_uri` param on success might be manipulatable, leading to phishing. |
| B-075 | ID Guessing (IDOR) | `ActivityController.java` | Uses numeric IDs (`/api/activities/123`). Malicious users can guess IDs to access other users' data. |
| B-076 | Missing Admin Check | `CommunityController.java` | Non-admins can potentially edit a community if the `@PreAuthorize` is missing on the `update` endpoint. |
| B-077 | Insecure File Types | `ActivityController.java` | GPX/FIT upload service doesn't validate the magic bytes, potentially allowing binary attacks. |
| B-078 | No session timeout | `Angular State` | Frontend state never expires. If a user leaves their tab open for a month, it still tries to sync with an dead JWT. |
| B-079 | JWT Secret: Low Entropy | `.env.example` | The example secret is "mysecretkeywhicheveryoneknows". Users might not change it in production. |
| B-080 | Exposure of DB ID | `AuthResponse.java` | Returning the database `id` to the client. Use a UUID for public-facing identifiers. |
