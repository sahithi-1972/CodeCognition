# 🤖 CodeCognition

*AI-powered repository analysis & insights platform*

---

## ✨ What It Does

CodeCognition analyzes your GitHub repositories and provides intelligent insights:

- 🔍 **Security Analysis** — Identify vulnerabilities and risks
- 📊 **Code Quality** — Detect issues and improvements
- 📦 **Dependency Review** — Check for outdated or risky packages
- 🎯 **Documentation Gaps** — Find missing docs
- 🔮 **Quantum Risk Scoring** — ML-based risk assessment per file
- 🌐 **Digital Twin** — Visualize dependency impact & blast radius
- 📋 **Agent Logs** — Real-time analysis tracking

---

## 🚀 Quick Start

### Option 1: Frontend Only (Easiest)
```bash
cd frontend
npm install
npm run dev
# Open http://localhost:5173
```

### Option 2: Full Stack
```bash
# Terminal 1 - Backend
cd backend-java
mvn spring-boot:run

# Terminal 2 - Frontend
cd frontend
npm run dev
```

---

## 📦 Tech Stack

| Layer | Technology |
|-------|-----------|
| **Frontend** | React 18 + Vite + Tailwind CSS + Framer Motion |
| **Backend** | Java Spring Boot |
| **AI Engine** | Claude API (claude-sonnet-4-20250514) |
| **GitHub API** | Personal Access Token Integration |
| **Styling** | Tailwind CSS + Dark Mode Support |

---

## 🔗 Connect GitHub

1. Go to **Settings** → **GitHub Integration**
2. Create token: [github.com/settings/tokens](https://github.com/settings/tokens)
   - Select scopes: `repo` + `read:user`
3. Paste token and connect
4. Your repos appear in dashboard instantly

---

## 📁 Project Structure

```
CodeCognition/
├── frontend/                    ← React + Vite UI
│   ├── src/pages/              ← Dashboard, Repos, Analysis, Settings
│   ├── src/components/         ← Reusable UI components
│   ├── src/context/            ← Auth & Theme state
│   └── src/hooks/              ← GitHub & API integrations
│
├── backend-java/               ← Java Spring Boot API
│   ├── api/                    ← REST controllers
│   ├── service/                ← Business logic
│   ├── model/                  ← Data models
│   └── pom.xml                 ← Maven dependencies
│
└── docs/                       ← Setup guides & documentation
```

---

## 🎯 Pages & Features

| Page | Feature |
|------|---------|
| **Dashboard** | Overview of repos & analysis status |
| **Repositories** | Browse all GitHub repos |
| **Repo Detail** | Deep dive analysis view |
| **Security** | Vulnerability & risk report |
| **Code Quality** | Issue detection & suggestions |
| **Digital Twin** | Dependency graph visualization |
| **Quantum Risk** | ML-based risk scoring |
| **Agent Logs** | Analysis activity stream |
| **Settings** | GitHub token management |

---

## 🔧 Configuration

### Frontend (.env)
```env
VITE_API_URL=http://localhost:8000
```

### Backend (application.properties)
```properties
server.port=8000
spring.application.name=CodeCognition
```

---

## 📊 How It Works

1. **Connect** your GitHub account
2. **Select** a repository to analyze
3. **AI analyzes** code, dependencies, docs, security
4. **View insights** across multiple analysis tabs
5. **Explore** digital twin & risk scores

---

## 🛠️ Development

### Requirements
- Node.js 18+ (frontend)
- Maven 3.8+ (backend)
- Java 17+ (backend)

### Run in Development Mode
```bash
# Frontend with hot reload
cd frontend && npm run dev

# Backend with auto-compile
cd backend-java && mvn spring-boot:run
```

### Build for Production
```bash
# Frontend
cd frontend && npm run build

# Backend
cd backend-java && mvn clean package
```

---

## 📚 Documentation

- **[Setup Guide](./COLLABORATOR_SETUP.md)** — Complete installation & setup
- **[Backend Setup](./backend-java/README.md)** — Java backend details
- **[Frontend Guide](./frontend/README.md)** — React app info

---

## 🎨 Features Highlight

✅ Real-time GitHub sync  
✅ AI-powered code analysis  
✅ Security vulnerability detection  
✅ Dependency risk assessment  
✅ Interactive dashboards  
✅ Dark mode support  
✅ Multi-tab insights view  
✅ Live analysis logs  

---

## 📝 License

MIT

---

## 🤝 Contributing

Pull requests are welcome! For major changes, open an issue first.

---

*Built with AI for developers. Made simple. Made powerful.*

