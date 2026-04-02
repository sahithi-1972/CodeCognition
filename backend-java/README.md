# CodeCognition AI - Java Backend

This is a **Spring Boot 3.2** implementation of the CodeCognition AI backend, replacing the Python FastAPI version.

## 📁 Project Structure

```
backend-java/
├── pom.xml                          # Maven configuration
├── .project                         # Eclipse project file
├── .classpath                       # Eclipse classpath
├── src/
│   ├── main/
│   │   ├── java/com/CodeCognition/
│   │   │   ├── CodeCognitionApplication.java     # Main Spring Boot app
│   │   │   ├── api/
│   │   │   │   └── CodeCognitionController.java  # REST endpoints
│   │   │   ├── service/
│   │   │   │   └── AnalysisService.java         # Analysis logic (rule-based engine)
│   │   │   └── model/
│   │   │       ├── AnalyzeRequest.java
│   │   │       ├── RepoAnalyzeRequest.java
│   │   │       ├── AnalysisResult.java
│   │   │       ├── Finding.java
│   │   │       ├── SimulationRequest.java
│   │   │       └── AgentLog.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/...
└── target/                          # Built artifacts (auto-generated)
```

## ✅ Getting Started with Eclipse

### 1. Import the Project

1. Open **Eclipse IDE**
2. Go to **File → Import**
3. Select **Existing Maven Projects**
4. Click **Next**
5. Browse to: `C:\Users\sahit\AppData\Local\Temp\CodeCognition-AI\backend-java`
6. Click **Finish**

Eclipse will:
- Auto-detect the Maven project
- Download dependencies (may take 1-2 minutes first time)
- Build the project automatically

### 2. Run the Application

**Option A: From Eclipse UI**
1. Right-click on the project → **Run As → Java Application**
2. Select `CodeCognitionApplication` class
3. Click **OK**

**Option B: From Terminal (in Eclipse)**
1. Open **Terminal** in Eclipse (View → Terminal)
2. Run:
   ```bash
   mvn clean spring-boot:run
   ```

**Option C: From PowerShell**
```bash
cd c:\Users\sahit\AppData\Local\Temp\CodeCognition-AI\backend-java
mvn clean spring-boot:run
```

### 3. Verify It's Running

Open your browser and go to:
```
http://localhost:8000/ping
```

You should see:
```json
{
  "status": "online",
  "service": "CodeCognition AI v3.3 (Java)",
  "ts": "2026-04-02T12:00:00"
}
```

## 📋 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/ping` | Health check |
| `POST` | `/analyze-repo` | Analyse a repository |
| `GET` | `/health-status` | Get cached analysis results |
| `POST` | `/simulation` | Digital twin simulation |
| `GET` | `/quantum-risk` | Quantum risk scores |
| `GET` | `/agent-logs` | Agent activity logs |
| `GET` | `/simulation/files` | List available files |

## 🔍 Features

✅ **Rule-based analysis engine** (works with zero API keys)
✅ **Security pattern scanning** (SQL injection, hardcoded secrets, etc.)
✅ **Dependency vulnerability detection** (Python & JS packages)
✅ **Code quality checks** (tests, CI/CD, documentation)
✅ **Health score calculation** (weighted scoring)
✅ **Quantum risk profiling** (file-based risk assessment)
✅ **Digital twin simulation** (impact analysis)
✅ **Agent logs streaming** (visual activity tracking)

## 📦 Dependencies

- **Spring Boot 3.2.0** - REST API framework
- **Lombok** - Reduce boilerplate code
- **Jackson** - JSON serialization
- **OkHttp** - HTTP client
- **Maven** - Build tool

## 🛠️ Development

### Edit Code in Eclipse

1. Open any `.java` file to edit
2. Eclipse will auto-compile on save
3. Right-click → **Run As → Java Application** to test changes

### Debug Mode

1. Right-click project → **Debug As → Java Application**
2. Set breakpoints by clicking on line numbers
3. Step through code using F5/F6/F7

### Maven Commands

```bash
# Clean build
mvn clean install

# Run tests
mvn test

# Build JAR
mvn package

# Run application
mvn spring-boot:run

# Skip tests during build
mvn clean install -DskipTests
```

## 🚀 Deployment

Build a standalone JAR:
```bash
mvn clean package
java -jar target/CodeCognition-ai-3.3.0.jar
```

## 📝 Next Steps

1. **Frontend Integration**: Update frontend's `VITE_API_URL` to `http://localhost:8000`
2. **Environment Variables**: Create `.env` file in `backend-java/` for OpenAI keys
3. **Database** (optional): Add Spring Data JPA for persistence
4. **Authentication** (optional): Add Spring Security

## ❓ Troubleshooting

**"Cannot find CodeCognitionApplication class"**
- Right-click project → **Maven → Update Project**
- Press F5 to refresh

**"Port 8000 already in use"**
- Change in `application.properties`: `server.port=8001`

**"Maven dependencies not downloading"**
- Right-click project → **Maven → Update Project**
- Or run: `mvn dependency:resolve`

**"Build errors after opening in Eclipse"**
- Right-click project → **Maven → Update Project**
- Go to **Project → Clean → Clean all projects**

## 📞 Support

For issues or questions, check the main README in the repo root.

---

**Version**: 3.3.0 (Java)  
**Last Updated**: April 2, 2026

