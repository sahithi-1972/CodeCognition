# CodeCognition Frontend Documentation

**Version:** 2.4  
**Last Updated:** April 6, 2026  
**Purpose:** Complete reference guide for the old frontend structure to track changes and detect conflicts

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Tech Stack](#tech-stack)
3. [Project Structure](#project-structure)
4. [Core Architecture](#core-architecture)
5. [Key Files & Components](#key-files--components)
6. [Pages & Routes](#pages--routes)
7. [Context & State Management](#context--state-management)
8. [Hooks & API Integration](#hooks--api-integration)
9. [Components Breakdown](#components-breakdown)
10. [Styling & Theme](#styling--theme)
11. [Authentication Flow](#authentication-flow)
12. [Key Features](#key-features)

---

## Overview

CodeCognition AI is a **React + Vite** web application that provides autonomous vulnerability detection, code health monitoring, and AI-powered fix suggestions for GitHub repositories. It features a dark cyberpunk-themed UI with real-time analysis capabilities.

### Key Features
- GitHub OAuth integration for repo access
- Multi-agent AI analysis system
- Digital twin dependency mapping
- Quantum risk scoring
- Real-time commit watching
- Agent logging dashboard

---

## Tech Stack

### Frontend Dependencies
```json
{
  "react": "^18.2.0",
  "react-dom": "^18.2.0",
  "react-router-dom": "^6.22.3",
  "axios": "^1.13.6",
  "framer-motion": "^11.0.8",
  "lucide-react": "^0.378.0"
}
```

### Build & Dev Tools
```json
{
  "vite": "^5.2.0",
  "@vitejs/plugin-react": "^4.2.1",
  "tailwindcss": "^3.4.3",
  "postcss": "^8.4.38",
  "autoprefixer": "^10.4.19"
}
```

### Key Libraries
- **Vite** - Ultra-fast build tool & dev server
- **React 18** - UI framework with hooks
- **React Router v6** - Client-side routing
- **Tailwind CSS** - Utility-first CSS framework
- **Framer Motion** - Animation library
- **Lucide React** - Icon library (700+ icons)
- **Axios** - HTTP client for backend API calls

---

## Project Structure

```
frontend/
├── index.html                    # Entry HTML
├── package.json                  # Dependencies & scripts
├── vite.config.js               # Vite configuration
├── tailwind.config.js           # Tailwind theme & extensions
├── postcss.config.js            # PostCSS plugins
├── src/
│   ├── main.jsx                 # React DOM render + Provider setup
│   ├── App.jsx                  # Main router component
│   ├── index.css                # Global styles
│   ├── context/
│   │   ├── AuthContext.jsx      # User auth & GitHub token storage
│   │   └── ThemeContext.jsx     # Light/dark theme toggle
│   ├── hooks/
│   │   ├── useApi.js            # Backend API queries (health, quantum-risk, simulation)
│   │   ├── useGitHub.js         # GitHub API integration (repos, files, tree, readme)
│   │   └── useCommitWatch.js    # Real-time commit activity monitoring
│   ├── pages/
│   │   ├── Login.jsx            # Google OAuth + GitHub token input
│   │   ├── Dashboard.jsx        # Home page with recent repos & stats
│   │   ├── Repositories.jsx     # Full repo list with filters
│   │   ├── RepoDetail.jsx       # Single repo analysis (6 tabs)
│   │   ├── AgentLogsPage.jsx    # Agent execution logs
│   │   └── SettingsPage.jsx     # User preferences
│   └── components/
│       ├── Layout.jsx           # Main layout wrapper (Sidebar + content)
│       ├── Sidebar.jsx          # Navigation + user profile
│       ├── HealthGauge.jsx      # Circular health indicator
│       ├── CommitActivity.jsx   # Git activity timeline
│       ├── AgentLog.jsx         # Individual agent execution card
│       ├── DigitalTwin.jsx      # Dependency graph visualization
│       ├── QuantumRisk.jsx      # Risk scoring visualization
│       ├── IssueFixer.jsx       # AI fix suggestions panel
│       ├── StatusBadge.jsx      # Status indicator component
│       ├── SyncOverlay.jsx      # Loading/syncing overlay
│       └── ThemeToggle.jsx      # Theme switch button
```

---

## Core Architecture

### Application Flow

```
index.html
    ↓
main.jsx (React DOM + Providers)
    ↓
ThemeProvider (Context)
    ↓
AuthProvider (Context)
    ↓
App.jsx (React Router)
    ├── /login          → Login page (public)
    ├── /dashboard      → Dashboard (protected)
    ├── /repositories   → Repo list (protected)
    ├── /repositories/:owner/:repo → Repo detail (protected)
    ├── /logs           → Agent logs (protected)
    ├── /settings       → Settings (protected)
    └── *               → Redirect to /login
```

### Protected Routes Pattern

```jsx
function Protected({ children }) {
  const { isAuthenticated, isLoading } = useAuth();
  if (isLoading) return <Loader />;
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  return <Layout>{children}</Layout>;
}
```

---

## Key Files & Components

### Entry Point (`main.jsx`)

```jsx
import { AuthProvider } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';
import App from './App.jsx';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ThemeProvider>
      <AuthProvider>
        <App />
      </AuthProvider>
    </ThemeProvider>
  </React.StrictMode>,
);
```

**Purpose:** Wraps entire app with theme and auth providers in correct order.

---

### Main Router (`App.jsx`)

**Exports:** `App` component  
**Routes:**
- `/login` - Public login page
- `/dashboard` - Protected dashboard
- `/repositories` - Protected repo list
- `/repositories/:owner/:repo` - Protected repo detail
- `/logs` - Protected agent logs
- `/settings` - Protected settings
- `*` - Fallback to login

**Key Functions:**
- `Loader()` - Loading spinner with animation
- `Protected()` - Route guard component
- `Public()` - Reverse route guard (authenticated → redirect to dashboard)

---

## Pages & Routes

### 1. **Login.jsx** (`/login`)

**Features:**
- Google OAuth via Google Identity Services SDK
- GitHub token input (PAT - Personal Access Token)
- Email/password input (mock)
- Feature showcase cards (4 features)

**Key Functions:**
- `useGoogleAuth()` - Loads Google Identity Services script
- OAuth flow: Token → decode JWT → extract email/name/picture
- GitHub token: User pastes token → `connectGitHub()` stores in AuthContext

**GitHub Token Scopes Required:**
- `repo` - Full control of private repos
- `read:user` - Read user profile

---

### 2. **Dashboard.jsx** (`/dashboard`)

**Features:**
- Welcome message with user name
- 4 stat cards (Total Repos, Security Issues, Code Quality, Last Updated)
- Recent repos grid (max 6)
- Repo card shows: name, description, language, stars, forks, last update

**Components Used:**
- `StatCard()` - Colored stat display
- `RepoCard()` - Clickable repo preview
- `HealthGauge` - Health indicator
- `CommitActivity` - Commit history

**Data Sources:**
- `useGitHubRepos()` - Fetch user's repos
- `useCommitWatch()` - Monitor commit activity

**Language Colors:**
```javascript
const LANG_COLORS = {
  JavaScript: '#f7df1e',
  TypeScript: '#3178c6',
  Python: '#3776ab',
  Java: '#ed8b00',
  Go: '#00add8',
  Rust: '#ce422b',
  // ... more languages
};
```

---

### 3. **Repositories.jsx** (`/repositories`)

**Features:**
- Full repo list (from GitHub API or demo data if no token)
- Search by name (real-time filter)
- Filter by language
- Filter by type (All/Public/Private)
- Repo row shows: icon, name, private badge, language, stars, forks, update time
- Click to navigate to detail page

**Components:**
- `RepoRow()` - Individual repo row
- Icons: `BookOpenIcon`, `StarIcon`

**Filters:**
- Language: Extracted from repos list
- Type: "All", "Public", "Private"

**Demo Repos:** 7 hardcoded repos if no GitHub token

---

### 4. **RepoDetail.jsx** (`/repositories/:owner/:repo`)

**Features:** 6-tab interface for deep repository analysis

**Tabs:**
1. **Overview** - Repository metadata (stars, forks, language, etc.)
2. **Security** - Vulnerability findings
3. **Code Quality** - Code quality issues
4. **Digital Twin** - Dependency graph & impact analysis
5. **Quantum Risk** - Risk scoring visualization
6. **AI Fix Suggestions** - Auto-fix recommendations

**Key Functions:**
- `runAIAnalysis()` - POST to `/analyze-repo` backend endpoint
- Input: Repository metadata (owner, repo, language, stars, forks, etc.)
- Output: Analysis results with findings

**Analysis Fields Sent:**
```javascript
{
  owner: string,
  repo: string,
  language: string | null,
  stars: number,
  forks: number,
  open_issues: number,
  size: number,
  topics: string[],
  description: string | null,
  default_branch: string,
  has_wiki: boolean,
  archived: boolean,
  is_empty: boolean,
  file_count: number,
  has_tests: boolean,
  has_ci: boolean,
  has_docker: boolean,
  has_readme: boolean,
  has_license: boolean,
  has_security_md: boolean,
  file_context: string,
  tree: string[],
}
```

**Tab Components Used:**
- `HealthGauge` - Visual health indicator
- `AgentLog` - Agent execution details
- `DigitalTwin` - Dependency visualization
- `QuantumRisk` - Risk scoring

**Severity Colors:**
```javascript
const SEV = {
  CRITICAL: { color: '#f85149', icon: XCircle },
  HIGH: { color: '#e07b39', icon: AlertTriangle },
  MEDIUM: { color: '#d29922', icon: AlertCircle },
  LOW: { color: '#388bfd', icon: AlertCircle },
  INFO: { color: '#8d96a0', icon: CheckCircle },
};
```

---

### 5. **AgentLogsPage.jsx** (`/logs`)

**Features:**
- Lists all agent executions
- Filter by agent, status, severity
- Each log shows: timestamp, agent name, status, duration, findings count

**Components Used:**
- `AgentLog` - Individual log card

---

### 6. **SettingsPage.jsx** (`/settings`)

**Features:**
- GitHub token management
- Theme preferences
- Notification settings
- API key configuration
- Account info

---

## Context & State Management

### **AuthContext.jsx**

**State Variables:**
```javascript
{
  user: {
    name: string,
    email: string,
    picture: string | null,
    given_name: string,
    verified: boolean,
    githubToken: string | null,
    githubUser: object | null,
    rememberMe: boolean,
    loginAt: ISO8601,
  },
  isAuthenticated: boolean,
  isLoading: boolean,
  githubToken: string | null,
  githubUser: object | null,
}
```

**Functions:**
- `login(profile, rememberMe)` - Set user session
- `logout()` - Clear session
- `connectGitHub(token, ghUser)` - Store GitHub token & user info

**Storage:**
- Uses `localStorage` if "Remember Me" checked
- Uses `sessionStorage` otherwise
- Session key: `rg_session`
- Remember key: `rg_remember`

**Hook:**
```javascript
const { user, isAuthenticated, isLoading, login, logout, githubToken, githubUser, connectGitHub } = useAuth();
```

### **ThemeContext.jsx**

**State:**
```javascript
{
  theme: 'dark' | 'light',
  toggle: () => void,
}
```

**CSS Variables Set:** (in document root)
```css
--bg: #050810 (dark)
--bg-2: #090d1a
--bg-3: #0d1220
--text: #e2e8f0
--text-2: #a1aab9
--text-3: #64748b
--border: #1a2240
--border-2: #253854
--purple: #7c3aed
--purple-fg: #a78bfa
--cyan: #00e5ff
--green: #3fb950
```

---

## Hooks & API Integration

### **useGitHub.js**

**Exports:**

| Hook/Function | Purpose |
|---|---|
| `useGitHubRepos(token)` | Fetch user's repositories |
| `useGitHubUser(token)` | Fetch authenticated user profile |
| `fetchGitHubUser(token)` | Async fetch user data |
| `fetchRepoDetails(token, owner, repo)` | Fetch repo metadata + contributors + commits + branches + PRs |
| `fetchRepoTree(token, owner, repo, branch)` | Fetch full file tree (recursive) |
| `fetchRepoReadme(token, owner, repo)` | Fetch README.md content (graceful 404) |
| `fetchFileContent(token, owner, repo, path)` | Fetch single file content |
| `getRepoFileContext(token, owner, repo, repoData)` | Rich context builder for analysis |

**API Base:** `https://api.github.com`

**Headers:**
```javascript
{
  Authorization: `token ${token}`,
  Accept: 'application/vnd.github.v3+json',
}
```

---

### **useApi.js**

**Axios Instance:**
```javascript
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8000',
  timeout: 30000,
});
```

**Exports:**

| Hook | Endpoint | Purpose |
|---|---|---|
| `useHealth(repoUrl)` | `GET /health` | Backend health check |
| `useQuantumRisk(repoUrl)` | `GET /quantum-risk` | Quantum risk scoring |
| `useSimulationFiles()` | `GET /simulation/files` | Fetch simulation data |
| `runSimulation(changedFile)` | `POST /simulation` | Run impact simulation |
| `triggerAnalysis(repoUrl, useMock)` | `POST /analyze` | Trigger backend analysis |

**Pattern:**
```javascript
function useQuery(url, deps = []) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetch = useCallback(async () => { /* ... */ }, [url]);
  useEffect(() => { fetch(); }, [fetch, ...deps]);
  
  return { data, loading, error, refetch: fetch };
}
```

**Returns:** `{ data, loading, error, refetch }`

---

### **useCommitWatch.js**

**Purpose:** Monitor repository commit activity in real-time

**Returns:**
```javascript
{
  commits: CommitData[],
  loading: boolean,
  error: string | null,
}
```

---

## Components Breakdown

### **Layout.jsx**

```jsx
function Layout({ children }) {
  return (
    <div className="flex min-h-screen bg-[#050810]">
      <Sidebar />
      <main className="flex-1 overflow-y-auto min-w-0">
        {children}
      </main>
    </div>
  );
}
```

**Purpose:** Wrapper for all protected pages. Includes sidebar navigation.

---

### **Sidebar.jsx**

**Features:**
- Logo + version badge
- Theme toggle (Sun/Moon icons)
- Navigation links: Dashboard, Repositories, Agent Logs, Settings
- User profile section: avatar/initials, name, email/login
- Agents status pill (4 agents active)
- Logout button

**Navigation:**
```javascript
const NAV = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/repositories', icon: GitFork, label: 'My Repositories' },
  { to: '/logs', icon: ScrollText, label: 'Agent Logs' },
  { to: '/settings', icon: Settings, label: 'Settings' },
];
```

---

### **HealthGauge.jsx**

**Props:**
- `score: number` (0-100)
- `size: 'sm' | 'md' | 'lg'`
- `status: 'healthy' | 'warning' | 'critical'`

**Features:**
- Circular SVG gauge
- Color: Green → Amber → Red based on score
- Animated needle

---

### **CommitActivity.jsx**

**Props:**
- `repo: object` (GitHub repo data)
- `commits: object[]` (Commit history)

**Features:**
- Timeline of recent commits
- Shows commit message, author, timestamp
- Activity graph

---

### **AgentLog.jsx**

**Props:**
- `agent: object` (Agent execution data)
- `findings: object[]` (Analysis findings)

**Features:**
- Agent name + execution ID
- Status badge (success/error/running)
- Findings list with severity colors
- Execution duration
- Timestamp

---

### **DigitalTwin.jsx**

**Props:**
- `repo: object`
- `dependencies: object[]`

**Features:**
- Dependency graph visualization
- Blast radius simulation
- Impact analysis
- Node-link diagram

---

### **QuantumRisk.jsx**

**Props:**
- `riskScore: number`
- `factors: object[]`

**Features:**
- Quantum-inspired risk scoring
- Heat map visualization
- Risk factor breakdown
- Probability indicators

---

### **IssueFixer.jsx**

**Props:**
- `issues: object[]`
- `suggestions: object[]`

**Features:**
- AI-powered fix recommendations
- Code diff preview
- Apply fix button
- Success/error feedback

---

### **StatusBadge.jsx**

**Props:**
- `status: 'active' | 'pending' | 'completed' | 'error'`
- `label: string`

**Features:**
- Color-coded status indicator
- Pulsing animation for active

---

### **SyncOverlay.jsx**

**Props:**
- `visible: boolean`
- `message: string`

**Features:**
- Full-screen loading overlay
- Loading spinner
- Sync message
- Blocks user interaction

---

### **ThemeToggle.jsx**

**Features:**
- Sun/Moon icon button
- Toggles light/dark theme
- Stored in ThemeContext

---

## Styling & Theme

### **tailwind.config.js**

**Custom Theme:**
```javascript
colors: {
  cyber: {
    bg: '#050810',
    panel: '#090d1a',
    border: '#1a2240',
    cyan: '#00e5ff',
    emerald: '#00ff88',
    violet: '#7c3aed',
    red: '#ff3366',
    amber: '#ffaa00',
  }
}
```

**Custom Animations:**
- `pulse-slow` - 3s pulse
- `spin-slow` - 8s spin
- `glow-pulse` - Cyan glow effect
- `scan-line` - Vertical scan line
- `float` - Floating animation

**Custom Fonts:**
```javascript
fontFamily: {
  mono: ['"JetBrains Mono"', 'monospace'],
  display: ['"Rajdhani"', 'sans-serif'],
  body: ['"DM Sans"', 'sans-serif'],
}
```

**Background Patterns:**
- `grid-pattern` - Cyan grid overlay
- `hero-gradient` - Violet + cyan gradient
- `scan-lines` - Horizontal scan lines

---

### **index.css**

**Global Styles:**
- CSS custom properties for colors
- Scrollbar styling
- Baseline typography
- Animations keyframes

---

## Authentication Flow

### **Login Flow:**

```
1. User visits /login
2. Google OAuth Option:
   - Script loads Google Identity Services
   - User clicks "Sign in with Google"
   - Google returns JWT token
   - Decode JWT → extract name, email, picture
   - Call login() in AuthContext
   
3. GitHub Token Option:
   - User generates PAT on github.com/settings/tokens
   - User pastes token in input
   - Call connectGitHub(token) in AuthContext
   - Fetch user profile with token
   - Store in localStorage/sessionStorage
   
4. Redirect to /dashboard if authenticated
```

### **Session Persistence:**

- **Remember Me checked:** Use localStorage (persistent)
- **Remember Me unchecked:** Use sessionStorage (cleared on browser close)
- **Session key:** `rg_session` (JSON stringified)
- **Remember key:** `rg_remember` (boolean)

### **Protected Routes:**

On app load:
1. Check `isLoading` in AuthContext
2. Read session from storage
3. Set `isAuthenticated` + `user` state
4. Routes check `isAuthenticated` before rendering
5. If not authenticated → redirect to `/login`

---

## Key Features

### **1. GitHub Integration**

- Real GitHub API integration (`api.github.com`)
- Fetch repos, tree, files, README
- User authentication via PAT token
- Demo repos fallback when no token

### **2. Multi-Agent AI Analysis**

- Sends repo metadata to backend `/analyze-repo`
- Backend runs 4 CrewAI agents
- Returns findings: security, quality, etc.

### **3. Digital Twin**

- Maps repository dependency graph
- Simulates blast radius of changes
- Shows impact analysis

### **4. Quantum Risk Scoring**

- Quantum-inspired risk calculation
- Probabilistic vulnerability scoring
- Risk heat map visualization

### **5. Commit Activity Monitoring**

- Real-time commit watching
- Activity timeline
- Contributor tracking

### **6. Dark Theme with Animations**

- Cyberpunk aesthetic
- Framer Motion animations
- Tailwind CSS utility classes
- Responsive design

### **7. Agent Logging**

- Track all agent executions
- Filter by status, severity
- Detailed execution logs
- Performance metrics

---

## Configuration

### **.env Variables** (Frontend)

```
VITE_API_URL=http://localhost:8000          # Backend URL
VITE_GOOGLE_CLIENT_ID=your_google_client_id # Google OAuth ID
```

If not set:
- `VITE_API_URL` defaults to `http://localhost:8000`
- `VITE_GOOGLE_CLIENT_ID` defaults to placeholder (skips Google Auth)

### **Build Commands**

```bash
npm install              # Install dependencies
npm run dev              # Start dev server (Vite)
npm run build            # Production build
npm run preview          # Preview production build
```

---

## Common Tasks & Patterns

### **Add New Page**

1. Create file: `src/pages/NewPage.jsx`
2. Add route in `App.jsx`:
   ```jsx
   <Route path="/newpage" element={<Protected><NewPage /></Protected>} />
   ```
3. Add nav link in `Sidebar.jsx`:
   ```javascript
   { to: '/newpage', icon: IconName, label: 'Page Label' }
   ```

### **Call GitHub API**

```javascript
import { fetchRepoDetails } from '../hooks/useGitHub';

const data = await fetchRepoDetails(githubToken, owner, repo);
```

### **Call Backend API**

```javascript
import { useApi } from '../hooks/useApi';

const { data, loading, error, refetch } = useHealth(repoUrl);
```

### **Access Auth Context**

```javascript
import { useAuth } from '../context/AuthContext';

const { user, isAuthenticated, githubToken, login, logout } = useAuth();
```

### **Access Theme Context**

```javascript
import { useTheme } from '../context/ThemeContext';

const { theme, toggle } = useTheme();
```

### **Use Animations**

```jsx
import { motion } from 'framer-motion';

<motion.div
  initial={{ opacity: 0, y: 12 }}
  animate={{ opacity: 1, y: 0 }}
  transition={{ duration: 0.35 }}
>
  Content
</motion.div>
```

---

## Notes for Collaborator Integration

### **Potential Conflict Areas:**

1. **Route Changes** - If new pages added, check `App.jsx` routes
2. **Context State** - If AuthContext or ThemeContext modified, verify all consumers
3. **API Endpoints** - If backend endpoints change, update `useApi.js` hooks
4. **Component Props** - If component signatures change, update all usages
5. **Styling** - If Tailwind config or global CSS modified, check for class name conflicts
6. **Authentication Flow** - If OAuth flow changed, verify Login.jsx compatibility

### **Checklist Before Merging:**

- [ ] All routes still accessible (test each page)
- [ ] GitHub token connection works
- [ ] Google OAuth works (if configured)
- [ ] Repo list loads and filters work
- [ ] Repo detail page loads and tabs work
- [ ] Theme toggle works
- [ ] Responsive design maintained
- [ ] No console errors
- [ ] No missing dependencies in package.json

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 2.4 | Apr 2026 | Current - Full documentation created |
| 2.3 | Mar 2026 | Added quantum risk scoring |
| 2.2 | Feb 2026 | Digital twin implementation |
| 2.1 | Jan 2026 | Multi-agent analysis |
| 2.0 | Dec 2025 | Complete rewrite with Vite + React 18 |
| 1.0 | Oct 2025 | Initial release |

---

## Quick Reference

### File Locations
- Routes: `src/App.jsx`
- Auth: `src/context/AuthContext.jsx`
- Theme: `src/context/ThemeContext.jsx`
- GitHub API: `src/hooks/useGitHub.js`
- Backend API: `src/hooks/useApi.js`
- Navigation: `src/components/Sidebar.jsx`
- Pages: `src/pages/*.jsx`
- Components: `src/components/*.jsx`

### Important Classes
- Layout wrapper: `.flex.min-h-screen.bg-[#050810]`
- Sidebar: `width: 240px, background: var(--bg-2)`
- Main content: `.flex-1.overflow-y-auto`
- Panels: `background: var(--bg-2), border: 1px solid var(--border)`

### Key Colors
- Dark background: `#050810`
- Panel background: `#090d1a`
- Border: `#1a2240`
- Cyan: `#00e5ff`
- Purple: `#7c3aed`
- Green: `#3fb950`
- Red (critical): `#f85149`

---

**Document created to ensure accurate tracking of changes and prevent feature loss during collaborator contributions.**
