# Epic: Standalone Mobile App Development Strategy (iOS & Android)

This document is a comprehensive guide for Claude Code (or any mobile developer) to create, architecture, and build a **brand-new, standalone mobile application** for `UR Urban Runners Casablanca` (Runhub) from scratch.

This mobile app will consume the existing Spring Boot REST API (`runhub-backend`) and replicate the core functionality of the web frontend, but optimized purely for native iOS and Android environments.

---

## 1. Technology Selection: React Native (Expo) ⚛️

Since the web frontend is built with Angular (TypeScript), the most logical choice for a purely standalone mobile app is **React Native with Expo** using **TypeScript**. 

- **Why React Native?**: It allows you to build truly native iOS and Android applications from a single codebase.
- **Why Expo?**: Expo provides incredible developer experience, built-in routing (Expo Router), and managed native modules (Camera, Location, Push Notifications) without needing to ever open Xcode or Android Studio until absolute necessary.
- **Why TypeScript?**: Keeps the tooling consistent with the existing Angular web app, allowing you to copy-paste exact Interfaces/Models from the web frontend directly into the mobile app.

---

## 2. Phase 1: Creating the New Mobile Project 🛠️

Claude Code must bootstrap the new standalone application.

### Step-by-Step Execution for Claude Code:

**1. Navigate to the root directory (Runhub project root):**
```bash
cd /path/to/urunner/runhub
```

**2. Initialize the new Expo project in a dedicated folder (e.g., `mobile`):**
```bash
npx create-expo-app@latest mobile --template tabs
```
*(This creates a beautifully pre-configured React Native project with Expo Router, TypeScript, and a bottom tab navigation layout).*

**3. Install Core Dependencies & State Management:**
```bash
cd mobile
npm install axios @tanstack/react-query
npm install zustand # For lightweight global state (auth tokens, user profiles)
npm install react-native-safe-area-context react-native-vector-icons
```
*(We use `axios` and `react-query` to interact seamlessly with the existing Spring Boot backend APIs).*

---

## 3. Phase 2: Architecture & Folder Structure 🏗️

Claude Code should organize the `mobile` folder mimicking the feature-driven architecture of the backend/frontend.

Inside the `mobile/` directory:
```text
mobile/
├── app/                  # Expo Router file-based navigation (Screens)
│   ├── (tabs)/           # Bottom Tab Navigator (Feed, Events, Profile)
│   ├── modal.tsx         # Global modals (e.g., Activity Summary)
│   ├── _layout.tsx       # Root layout and Providers
├── src/
│   ├── api/              # Axios instance & API endpoint definitions (matches Spring Boot)
│   ├── components/       # Reusable UI (Buttons, RunCards, Avatars)
│   ├── store/            # Zustand state stores (useAuthStore)
│   ├── types/            # TypeScript interfaces (Copy from Angular frontend!)
│   ├── hooks/            # Custom React hooks (useLocation, useRuns)
│   └── constants/        # Theme colors, URLs (http://127.0.0.1:8080)
```

---

## 4. Phase 3: Connecting to the Existing Backend 🌐

The mobile app must communicate with the existing Java Spring Boot backend.

**Network Configuration (`src/api/client.ts`):**
```typescript
import axios from 'axios';
import { useAuthStore } from '../store/useAuthStore';

// In development, the iOS simulator maps localhost to the host machine.
// Android emulator maps it to 10.0.2.2.
// For physical devices, use your actual local IP (e.g. 192.168.1.X)
const BASE_URL = __DEV__ 
  ? 'http://localhost:8080/api' // Adjust for Android/Physical
  : 'https://api.urunner.com/api';

export const apiClient = axios.create({
  baseURL: BASE_URL,
});

// Interceptor to inject JWT Token (Matching the Angular implementation)
apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

---

## 5. Phase 4: Core Mobile Features Implementation 🚀

Claude Code must implement these core native features using Expo SDKs to match the existing platform specs:

### A. Geolocation & Interactive GPX Routes (Strava/Garmin)
Runners need native offline maps and live tracking:
```bash
npx expo install expo-location react-native-maps
```
*Goal: Replicate the `epic4-story1-interactive-gpx-routes.md` logic on mobile using `react-native-maps` to render polyline routes for races.*

### B. Push Notifications
To notify members about events natively:
```bash
npx expo install expo-notifications expo-device
```
*Goal: Ask the user for Push Notification permissions upon first login, register the Expo Push Token to the backend, and handle incoming notifications for new Community Events.*

### C. Native Camera & Export Studio
For creating Weekly Agendas and sharing running statistics overlaid on photos:
```bash
npx expo install expo-camera expo-image-manipulator expo-file-system expo-sharing
```
*Goal: Allow users to take a picture of their run, overlay text/graphics locally via React Native, and share it directly to Instagram Stories using `expo-sharing`.*

---

## 6. Phase 5: Authentication (OAuth 2.0 & JWT) 🔐

Because mobile apps handle OAuth differently than Web Browsers:
```bash
npx expo install expo-auth-session expo-secure-store
```
*Goal: Use `expo-auth-session` to open the Strava / Garmin authorization pages natively and securely store the resulting JWT token in the device's Keychain/Keystore using `expo-secure-store`.*

---

## 7. Claude Code Execution Checklist 📋

If instructed to "Build the mobile app", Claude Code must execute the following sequentially:
- [ ] Read this complete `epic-standalone-mobile-app-strategy.md` document.
- [ ] Run `npx create-expo-app` in the root folder to generate the `mobile` codebase.
- [ ] Establish the `src` folder structure and copy over the TypeScript types/interfaces from the existing Angular `frontend` to `mobile/src/types`.
- [ ] Configure the Axios `apiClient` to point to the `8080` Spring Boot backend.
- [ ] Set up the `(tabs)` router for feed, community, events, and profile.
- [ ] Build the Login Screen to acquire the JWT and store it.
- [ ] Tell the user to run `cd mobile && npx expo start` and scan the QR code using the "Expo Go" app on their physical iOS/Android phone to see the magic happen instantly.

This process guarantees a robust, standalone native app perfectly tailored for Runhub's existing backend architecture!
