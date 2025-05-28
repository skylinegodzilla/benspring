# ✅ FULL PROJECT CHECK LIST

## 1. 🧠 Architecture & Planning
- [x] Define project scope (shared family to-do list)
- [x] Local-only server (runs inside home network)
- [x] Use ARP/Nmap to detect presence of family members
- [x] Use push notifications for mobile reminders
- [x] Use Spring Boot + PostgreSQL backend
- [x] Use Docker for deployment
- [x] Build native iOS (SwiftUI) and Android (Jetpack Compose) apps

## 2. 📦 Spring Boot Backend

### ✅ Base Setup
- [x] Create Spring Boot project
- [x] Connect to PostgreSQL
- [x] Inject DB password with environment variable
- [x] Expose API over HTTP
- [x] Package and run with Docker
- [x] Deploy using Portainer
- [x] Store Docker Compose YAML and `.env` files

### 🔐 Multi-user Support
- [x] Create `User` entity and repository
- [x] Create `SessionToken` entity and auth logic
- [x] Implement sign-up (`/auth/signup`)
- [x] Implement login (`/auth/login`)
- [ ] Link ToDo items to users
- [ ] Restrict all ToDo actions by session

### 📝 ToDo API
- [x] Create `ToDo` entity (title, description, due date, completed)
- [x] Create basic ToDo controller (create, read, update, delete)
- [ ] Return only current user’s ToDos
- [ ] Add filtering/sorting endpoints (e.g. by due date or completion)
- [ ] Add reminder flag

## 3. 🌐 Web Interface for Testing (before mobile apps)
- [ ] HTML login and sign-up form
- [ ] Save session token in localStorage
- [ ] Fetch and display todos for current user
- [ ] Add new to-do (form)
- [ ] Mark as complete / delete
- [ ] Log out / clear session

## 4. 📱 Mobile Apps

### 📲 Shared App Design
- [x] Decide on separate native apps (iOS & Android)
- [x] Design shared backend API
- [ ] Define shared ToDo model (with sync behavior)
- [ ] Define push notification payload structure

### 🍏 iOS App (SwiftUI)
- [ ] SwiftUI MVVM structure
- [ ] Local session token storage (Keychain)
- [ ] Sign-up & login UI
- [ ] ToDo list screen
- [ ] Add/edit ToDo screen
- [ ] Notification scheduling
- [ ] Detect presence via local network scan trigger
- [ ] Auto-refresh UI when home

### 🤖 Android App (Jetpack Compose)
- [ ] Jetpack Compose MVVM structure
- [ ] Local session token storage (EncryptedSharedPreferences)
- [ ] Sign-up & login UI
- [ ] ToDo list screen
- [ ] Add/edit ToDo screen
- [ ] Notification scheduling
- [ ] Detect presence via local network scan trigger
- [ ] Auto-refresh UI when home

## 5. 🏠 Presence Detection

### 🧠 Home Detection Logic
- [x] Select strategy: ARP/Nmap scan
- [ ] Script to scan IP range on home server
- [ ] Update server with list of currently-at-home users
- [ ] API to query presence status
- [ ] Trigger push notification if ToDo is due and user is home

## 6. ⚙️ Deployment & Environments
- [ ] Separate dev / UAT / prod configs
  - [ ] `application-dev.properties`, `application-prod.properties`
  - [ ] `SPRING_PROFILES_ACTIVE` selection via env
- [ ] Use `.env` files and Docker secrets
- [ ] Use Docker Compose override files for each environment
- [ ] Never commit secrets or tokens
- [ ] Validate config and env separation before mobile release

## 7. 🔔 Notifications System
- [ ] Periodically scan for presence
- [ ] Match presence + upcoming to-dos
- [ ] Trigger push notification to user’s phone
- [ ] Deduplicate notifications
- [ ] Respect notification permissions and Do Not Disturb

## 8. 🧪 Testing
- [x] Unit tests for backend services
- [ ] Integration tests for auth + ToDo API
- [ ] Local web UI smoke tests
- [ ] iOS unit tests for ViewModels
- [ ] Android unit tests for ViewModels
- [ ] Manual tests on home network and offline edge cases

## 9. 🧼 Polish & UX
- [ ] iOS & Android dark mode support
- [ ] Basic animations for transitions
- [ ] Empty states for no ToDos
- [ ] Display presence status in app
- [ ] Add subtle haptics
- [ ] Add basic accessibility support (labels, contrast)

## 10. 📄 Documentation
- [x] Write README for backend setup
- [x] Document API endpoints
- [ ] Setup instructions for mobile apps
- [ ] Add security notes on environment management
- [ ] Add future roadmap (guest mode, shared to-dos, etc.)
