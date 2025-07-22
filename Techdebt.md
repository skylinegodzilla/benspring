# Technical Debt Log

This document tracks areas of technical debt in the Family Checklist project that require future refactoring or cleanup.
I may or may have not addressed some of these allready as this is not Jera im not trakcing my work using tickets. I am buildign as I go.
To be honnist this project is probabley going to blow up in to a kind of mono repo project as a one server app project for all the
Home projects So I don't have to keep recreating the core for different projects (user handling, session tokens, ect)

anyway point is this page is where I will dump stuff that I realise I should address at some point but cant do at that time because
doing so would derail what ever it is that I was already working on at that time.

---

## Backend

### 1. DTO Constructor Inconsistency
Some DTOs include constructors, others rely on setters. This inconsistency can lead to confusion and bugs.
- 🔧 **Planned Fix:** Standardize all DTOs to either use constructors or JavaBean-style setters.

### 2. `UserLoginResponseDTO` Previously Contained `userId`
We had user IDs included in login response DTOs, which violates our security design principles.
- ✅ **Status:** Fixed. `userId` has been removed.

### 3. Manual Role Injection Post Login
Originally, the role was retrieved via a separate `/me` endpoint. We've now moved to include the role in the login response, simplifying state management.
- ✅ **Status:** Resolved, but tight coupling of login and user context remains.
- 🔧 **Future Fix:** Reconsider a cleaner separation of authentication and user context retrieval.

### 4. Frontend-side Role Checks Only
Backend lacks proper enforcement of admin roles on protected routes.
- 🔧 **Planned Fix:** Enforce role validation server-side for all admin-protected endpoints.

### 5. Session Token Storage
Session tokens are stored in sessionStorage without expiration handling or refresh strategy.
- ⚠️ **Risk:** Could lead to stale sessions or unclear logout behavior.
- 🔧 **Future Fix:** Add expiry + auto-logout support or token refresh flow.

### 6. Entity Relationships
Earlier implementations used raw `userId` fields in entities instead of proper JPA relationships.
- ✅ **Status:** Fixed using `@ManyToOne` and `@OneToMany` with DTO mapping.

### 7. Use of `null` Instead of Optional
Java lacks robust optional handling for values like `UserRole`, causing potential null propagation.
- ⚠️ **Risk:** `null` values must be defensively handled across layers.
- 🔧 **Planned Fix:** Use `Optional` where appropriate in services and controllers.

### 8. Logging Format in IntelliJ
Logs aren't colorized or well-formatted for readability.
- ❓ **Note:** May be limited by IntelliJ's console defaults.
- 🔧 **Planned Fix:** Investigate better log formatter (e.g., JSON, colored layout).

---

## Frontend

### 9. Role Not Persisting After Login
The `role` wasn't stored in sessionStorage after login, breaking conditional UI logic.
- ✅ **Status:** Fixed once `role` was included in login response and saved to storage.

### 10. Inconsistent Role Handling Across Pages
Some pages fetch user role on mount, others rely on `sessionStorage`.
- 🔧 **Planned Fix:** Centralize role retrieval via a shared context or React hook.

### 11. Poor Error Handling on Auth Failures
Error feedback is basic; no visual distinction between wrong password, missing session, or server issues.
- 🔧 **Planned Fix:** Improve UX around error messages and edge cases.

### 12. Hardcoded Strings and Messages
Many strings (like “Login failed”) are hardcoded throughout components.
- 🔧 **Planned Fix:** Extract to centralized constants or localization file.

---

## Testing

### 13. No Unit or Integration Tests Yet
No test coverage exists for backend or frontend.
- 🔧 **Planned Fix:** Add full coverage once core features are implemented. 
- It was allways planed to add the tests from the beginning, but I needed to conform that it all worked before "locking in the logic" with tests.
- This is why we first needed to create the front end to check that the endpoints did what they need to do. (postman can only test so much)
- And is why I created frontend before creating mobile.

---

## Architecture

### 14. No Separation of Admin Context
Admin features are mixed with user features without clean boundaries.
- 🔧 **Planned Fix:** Introduce modular design for Admin vs User flows.

### 15. No Offline Support Layer Yet
Offline functionality for mobile remains a future goal.
- 🕒 **Status:** Not started.
- 🔧 **Planned Fix:** Add local caching and sync layer.

---

## To Do

- [ ] Finalize DTO constructor pattern and refactor
- [ ] Begin writing unit/integration tests
- [ ] Refactor backend to enforce role-based access
- [ ] Standardize all DTO field exposure and naming
- [ ] Improve IntelliJ log readability
- [ ] Add token expiration and refresh support
- [ ] Use Optional for nullable service return types
- [ ] Centralize frontend role state in React context
- [ ] Improve login error handling and messages
- [ ] Extract strings/constants from components
- [ ] Plan architecture for offline sync support
