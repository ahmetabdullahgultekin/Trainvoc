# TrainvocWeb - Claude AI Development Guide

## Overview

**TrainvocWeb** is a React-based web platform for multiplayer vocabulary games. It provides real-time multiplayer game sessions where users can compete against each other in vocabulary quizzes.

---

## Tech Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Framework** | React | 19.1.0 |
| **Build Tool** | Vite | 6.3.5 |
| **Language** | TypeScript | 5.8.3 |
| **UI Library** | Material-UI (MUI) | 7.1.2 |
| **Styling** | Emotion | 11.14.0 |
| **Routing** | React Router DOM | 7.6.2 |
| **HTTP Client** | Axios | 1.10.0 |
| **i18n** | i18next | 25.2.1 |
| **Animations** | Lottie React | 2.4.1 |

---

## Project Structure

```
TrainvocWeb/
├── public/                     # Static assets
│   └── vite.svg               # Favicon
├── src/
│   ├── animations/            # Lottie animation files
│   │   └── rocket.json
│   ├── components/            # Reusable components
│   │   ├── shared/            # Shared/common components
│   │   │   ├── Button.tsx
│   │   │   ├── Footer.tsx
│   │   │   ├── FullscreenButton.tsx
│   │   │   ├── Loader.tsx
│   │   │   ├── Modal.tsx
│   │   │   ├── Navbar.tsx
│   │   │   ├── RoomCard.tsx
│   │   │   ├── ScrollToTop.tsx
│   │   │   ├── hashPassword.ts
│   │   │   ├── useNick.ts
│   │   │   └── useProfile.ts
│   │   ├── GameFinal.tsx      # Game completion screen
│   │   ├── GameQuestion.tsx   # Quiz question component
│   │   ├── GameRanking.tsx    # Live ranking display
│   │   ├── GameStartCountdown.tsx
│   │   └── PlaySidebar.tsx    # Game sidebar
│   ├── interfaces/            # TypeScript interfaces
│   │   ├── game.ts            # Game-related types
│   │   └── gameExtra.ts       # Additional game types
│   ├── locales/               # i18n translation files
│   │   ├── en/                # English translations
│   │   └── tr/                # Turkish translations
│   ├── pages/                 # Page components (routes)
│   │   ├── AboutPage.tsx      # About page
│   │   ├── ContactPage.tsx    # Contact page
│   │   ├── CreateRoomPage.tsx # Create game room
│   │   ├── GamePage.tsx       # Active game screen
│   │   ├── HomePage.tsx       # Landing page
│   │   ├── JoinRoomPage.tsx   # Join existing room
│   │   ├── LeaderboardPage.tsx # Global leaderboard
│   │   ├── LobbyPage.tsx      # Game lobby
│   │   ├── MobileAppPage.tsx  # Mobile app promotion
│   │   ├── PlayLayout.tsx     # Game area layout wrapper
│   │   ├── PlayPage.tsx       # Play menu
│   │   ├── ProfilePage.tsx    # User profile
│   │   └── RoomDetailPage.tsx # Room details/waiting
│   ├── App.tsx                # Main app component
│   ├── api.ts                 # Axios API configuration
│   ├── i18n.ts                # i18next configuration
│   ├── main.tsx               # React entry point
│   ├── style.css              # Global styles
│   └── vite-env.d.ts          # Vite type definitions
├── index.html                 # HTML template
├── package.json               # Dependencies
├── tsconfig.json              # TypeScript configuration
├── vite.config.ts             # Vite configuration
├── .htaccess                  # Apache server config
└── CLAUDE.md                  # This file
```

---

## Key Files

### Configuration

| File | Purpose |
|------|---------|
| `vite.config.ts` | Vite bundler config, proxy settings, code splitting |
| `tsconfig.json` | TypeScript compiler options |
| `package.json` | Dependencies and scripts |
| `api.ts` | Axios instance with base URL |
| `i18n.ts` | Internationalization setup |

### Entry Points

| File | Purpose |
|------|---------|
| `index.html` | HTML template |
| `main.tsx` | React application entry |
| `App.tsx` | Root component with routing |

---

## Routing Structure

```
/                    → HomePage (landing)
/about               → AboutPage
/contact             → ContactPage
/mobile              → MobileAppPage
/play/*              → PlayLayout (game area)
    /play            → PlayPage (menu)
    /play/lobby      → LobbyPage
    /play/create     → CreateRoomPage
    /play/join       → JoinRoomPage
    /play/room/:id   → RoomDetailPage
    /play/game/:id   → GamePage
    /play/leaderboard → LeaderboardPage
    /play/profile    → ProfilePage
```

---

## API Integration

### Configuration (`api.ts`)

```typescript
import axios from 'axios';

const api = axios.create({
    baseURL: "http://localhost:8080/",  // Development
    // baseURL: 'https://api.trainvoc.rollingcatsoftware.com:8443/', // Production
    withCredentials: true
});

export default api;
```

### Backend Endpoints Used

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/rooms` | GET | List available rooms |
| `/api/rooms` | POST | Create new room |
| `/api/rooms/{id}` | GET | Get room details |
| `/api/rooms/{id}/join` | POST | Join a room |
| `/api/game/{id}` | GET | Get game state |
| `/api/game/{id}/answer` | POST | Submit answer |
| `/api/leaderboard` | GET | Global leaderboard |

### WebSocket Connection

Real-time game updates via WebSocket at `ws://localhost:8080/ws/game/{roomId}`

---

## Development Commands

```bash
# Install dependencies
npm install

# Start development server (port 5173)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

---

## Code Patterns

### Lazy Loading (Code Splitting)

```typescript
const HomePage = lazy(() => import('./pages/HomePage'));
const AboutPage = lazy(() => import('./pages/AboutPage'));

// Usage with Suspense
<Suspense fallback={<div>Loading...</div>}>
    <Routes>
        <Route path="/" element={<HomePage />} />
    </Routes>
</Suspense>
```

### Custom Hooks

```typescript
// useNick.ts - Nickname management
const { nick, setNick, isValid } = useNick();

// useProfile.ts - User profile state
const { profile, updateProfile, isLoading } = useProfile();
```

### Material-UI Theming

Uses MUI 7 with default theme. Components use `sx` prop for styling.

```typescript
<Button sx={{ backgroundColor: 'primary.main', px: 4 }}>
    Start Game
</Button>
```

---

## Internationalization (i18n)

### Supported Languages

- English (`en`)
- Turkish (`tr`)

### Usage

```typescript
import { useTranslation } from 'react-i18next';

function Component() {
    const { t } = useTranslation();
    return <h1>{t('welcome')}</h1>;
}
```

### Translation Files

```
src/locales/
├── en/
│   └── translation.json
└── tr/
    └── translation.json
```

---

## Known Issues

### Critical

1. **Hardcoded Backend URL**
   - `api.ts` has hardcoded `localhost:8080`
   - Should use environment variables

2. **No Authentication**
   - No login/logout functionality
   - User identified only by nickname

3. **Limited Error Handling**
   - API errors not consistently handled
   - No retry logic

### Medium

4. **No State Management Library**
   - Uses React state only
   - Could benefit from Redux/Zustand for complex state

5. **Missing Loading States**
   - Some pages lack loading indicators
   - Suspense fallback is basic

6. **No Offline Support**
   - Requires constant connection
   - No service worker

### Low

7. **No Unit Tests**
   - No test files present
   - Should add Jest + React Testing Library

8. **Console Warnings**
   - Some MUI deprecation warnings
   - Missing key props in some lists

---

## Recommendations

### Immediate Fixes

1. **Environment Variables**
   ```typescript
   // api.ts
   const api = axios.create({
       baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/',
   });
   ```

2. **Error Boundaries**
   ```typescript
   <ErrorBoundary fallback={<ErrorPage />}>
       <App />
   </ErrorBoundary>
   ```

### Future Improvements

1. Add proper authentication (JWT/OAuth)
2. Implement state management (Zustand recommended)
3. Add comprehensive error handling
4. Write unit and integration tests
5. Add PWA support for offline capability
6. Implement proper WebSocket reconnection logic

---

## Build Configuration

### Vite Config (`vite.config.ts`)

```typescript
export default defineConfig({
    plugins: [react()],
    server: {
        proxy: {
            '/api': 'http://localhost:8080',
        },
    },
    build: {
        rollupOptions: {
            output: {
                manualChunks(id) {
                    if (id.includes('node_modules')) return 'vendor';
                    if (id.includes('src/pages/')) return 'pages';
                },
            },
        },
        chunkSizeWarningLimit: 800,
    },
});
```

### TypeScript Config (`tsconfig.json`)

- Target: ES2020
- JSX: react-jsx
- Strict mode enabled
- Module resolution: bundler

---

## Deployment

### Apache (.htaccess)

```apache
RewriteEngine On
RewriteBase /
RewriteRule ^index\.html$ - [L]
RewriteCond %{REQUEST_FILENAME} !-f
RewriteCond %{REQUEST_FILENAME} !-d
RewriteRule . /index.html [L]
```

### Production Build

```bash
npm run build
# Output in dist/ directory
# Deploy dist/ contents to web server
```

---

## File Count Summary

| Type | Count |
|------|-------|
| TypeScript/TSX | 38 |
| CSS | 1 |
| JSON (translations) | 2+ |
| Configuration | 4 |

---

## Related Documentation

- **Root CLAUDE.md**: `/CLAUDE.md` - Monorepo overview
- **Backend CLAUDE.md**: `/TrainvocBackend/CLAUDE.md` - API documentation
- **Architecture**: `/ARCHITECTURE.md` - System design

---

*Last Updated: January 22, 2026*
