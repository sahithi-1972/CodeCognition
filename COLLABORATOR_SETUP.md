# 🚀 CodeCognition - Complete Setup for Collaborators

**Copy & Paste These Commands to Get Started**

---

## 📋 What's In This Repo?

✅ **Frontend**: React 18 + Vite + Tailwind CSS  
✅ **Backend**: Java Spring Boot (running on port 8000)  
✅ **API**: REST endpoints for AI analysis  
✅ **Git Status**: All files committed ✓ (clean working tree)  

---

## ⚡ QUICKEST START (Frontend Only - 3 Minutes)

```bash
# 1. Clone/navigate to repo
cd CodeCognition

# 2. Install frontend dependencies
cd frontend
npm install

# 3. Copy environment config
copy .env.example .env

# 4. Start the dev server
npm run dev

# 5. Open in browser
# → http://localhost:5173
```

**That's it!** The frontend works standalone with demo data. To use your GitHub repos, you'll need a GitHub token (see step below).

---

## 🔗 Connect GitHub (Optional but Recommended)

Once the frontend is running:

1. Go to **https://github.com/settings/tokens**
2. Click **"Generate new token (classic)"**
3. Name it: `CodeCognition`
4. ✅ Check these boxes:
   - `repo` (full control of private repositories)
   - `read:user` (read user profile data)
5. Click **"Generate token"** → **Copy the token** (starts with `ghp_`)
6. In the app at **http://localhost:5173**:
   - Click **"Login"** → use any email/password (demo auth)
   - Go to **Settings** → **GitHub Integration**
   - Paste your token → **Connect**
7. Your repos now appear in the **Repositories** page! 🎉

---

## 🔧 FULL STACK (Frontend + Java Backend)

### Backend Setup (Java Spring Boot)

```bash
# 1. Navigate to backend
cd backend-java

# 2. Build with Maven
mvn clean compile

# 3. Run the backend
mvn spring-boot:run

# Backend will start on http://localhost:8000
# Check it's running: http://localhost:8000/ping
```

### Frontend Configuration

```bash
# 1. Update frontend/.env (if not already set)
# Edit the file and ensure:
VITE_API_URL=http://localhost:8000

# 2. In a NEW terminal window, start frontend
cd frontend
npm run dev

# Frontend runs at http://localhost:5173
```

---

## 📁 Project Structure

```
CodeCognition/
├── frontend/                    ← React + Vite app
│   ├── src/
│   │   ├── pages/              ← Dashboard, Repositories, RepoDetail, AgentLogs, Settings, Login
│   │   ├── components/         ← UI components (Sidebar, HealthGauge, DigitalTwin, etc.)
│   │   ├── context/            ← AuthContext (stores GitHub token)
│   │   └── hooks/              ← useGitHub.js, useApi.js
│   ├── package.json
│   └── .env                    ← Your local config (create from .env.example)
│
├── backend-java/               ← Java Spring Boot backend
│   ├── src/main/java/
│   │   └── com/codecognition/
│   │       ├── CodeCognitionApplication.java    ← Main entry point
│   │       ├── api/            ← REST controllers
│   │       ├── model/          ← Data models
│   │       └── service/        ← Business logic
│   ├── pom.xml                 ← Maven dependencies
│   └── README.md
│
├── HOW_TO_RUN.md               ← Detailed instructions
├── README.md                   ← Project overview
└── COLLABORATOR_SETUP.md       ← This file!
```

---

## 🎯 Features You Can Access

1. **Dashboard** → Overview of repos, AI analysis status
2. **Repositories** → Browse all GitHub repos (after connecting token)
3. **Repo Detail** → Deep dive into a specific repo
4. **Run AI Analysis** → Claude AI scans for:
   - Security vulnerabilities
   - Code quality issues
   - Dependency risks
   - Documentation gaps
5. **Quantum Risk** → Risk scoring with factor breakdown
6. **Digital Twin** → Dependency impact simulation
7. **Agent Logs** → View live analysis logs

---

## 🐛 Troubleshooting

### Port Already in Use?

```bash
# Frontend on different port:
npm run dev -- --port 3000

# Backend on different port (edit backend-java/src/main/resources/application.properties):
# Change: server.port=8080
mvn spring-boot:run
```

### Node modules issues?

```bash
cd frontend
rm -r node_modules
npm install
npm run dev
```

### Maven build fails?

```bash
cd backend-java
mvn clean install
mvn spring-boot:run
```

### GitHub token not working?

- Verify token has `repo` + `read:user` scopes
- Token hasn't expired
- Token is pasted correctly (no extra spaces)
- Try generating a new token

---

## ✅ Verify Everything Works

1. **Frontend running?** Open http://localhost:5173
2. **Backend running?** Open http://localhost:8000/ping → should show `pong`
3. **GitHub connected?** Repos appear in the Repositories page
4. **AI analysis?** Click "Run AI Analysis" on any repo

---

## 📝 Common Commands

```bash
# Frontend development
cd frontend && npm run dev

# Frontend build for production
npm run build

# Backend compilation
cd backend-java && mvn clean compile

# Backend run
mvn spring-boot:run

# View backend logs
# (run in same terminal as mvn spring-boot:run)

# Kill a running process
# Windows: taskkill /PID <pid> /F
# Mac/Linux: kill -9 <pid>
```

---

## 🤝 Next Steps

1. ✅ Run the frontend
2. ✅ Generate a GitHub token
3. ✅ Connect your repos
4. ✅ Try an AI analysis
5. ✅ Check out the Digital Twin & Quantum Risk features

**Happy analyzing!** 🎉

---

*Last updated: April 4, 2026*  
*All files committed to git ✓*
