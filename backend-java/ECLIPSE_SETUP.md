# 🚀 How to Open Java Backend in Eclipse - Step by Step

## ✅ Prerequisites

- **Eclipse IDE** installed (Java EE or Spring Tools Suite preferred)
- **Java 17+** installed
- **Maven** installed (or Eclipse has built-in Maven)

---

## 📥 STEP 1: Import the Project into Eclipse

### Method A: Using File Menu (Easiest)

1. Open **Eclipse IDE**
2. Click **File** (top menu)
3. Click **Import...**
4. In the dialog, expand **Maven**
5. Select **Existing Maven Projects**
6. Click **Next**

### Method B: Using Project Explorer

1. In **Project Explorer** panel on left
2. Right-click → **Import...**
3. Select **Existing Maven Projects**

---

## 📂 STEP 2: Browse to Project Location

1. Click **Browse...**
2. Navigate to: 
   ```
   C:\Users\sahit\AppData\Local\Temp\CodeCognition-AI\backend-java
   ```
3. Click **Select Folder**
4. You should see `pom.xml` listed
5. Make sure it's **checked** ✓
6. Click **Finish**

---

## ⏳ STEP 3: Wait for Build (1-2 minutes first time)

Eclipse will:
- ✓ Detect the Maven project
- ✓ Download dependencies from Maven Central
- ✓ Build the project
- ✓ Index the code

**You'll see:**
- Progress bar at bottom right
- `[INFO]` messages in Console

**Wait until you see:** `BUILD SUCCESS` or the progress completes

---

## 🎯 STEP 4: Open and Explore the Project

In **Project Explorer**, expand the tree:

```
CodeCognition-ai/
├── src/main/java/
│   ├── com/CodeCognition/
│   │   ├── CodeCognitionApplication.java        ← MAIN APP
│   │   ├── api/
│   │   │   └── CodeCognitionController.java     ← REST ENDPOINTS
│   │   ├── service/
│   │   │   └── AnalysisService.java            ← ANALYSIS LOGIC
│   │   └── model/
│   │       ├── AnalyzeRequest.java
│   │       ├── AnalysisResult.java
│   │       ├── Finding.java
│   │       └── ... (other models)
├── src/main/resources/
│   └── application.properties
├── pom.xml                                      ← MAVEN CONFIG
└── README.md
```

---

## ▶️ STEP 5: Run the Application

### Method A: Run Button (Easiest)

1. Click on `CodeCognitionApplication.java` to open it
2. Click the **▶ Run** button (green play icon in toolbar)
3. Or press **Ctrl + F11**
4. Select **Java Application**
5. Click **OK**

**You should see in Console:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_|\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

2026-04-02 12:00:00.000  INFO 1234 --- [main] c.r.CodeCognitionApplication : ...
2026-04-02 12:00:02.123  INFO 1234 --- [main] o.s.b.w.e.tomcat.TomcatWebServer : Tomcat started on port(s): 8000 (http)
2026-04-02 12:00:02.456  INFO 1234 --- [main] c.r.CodeCognitionApplication : Started CodeCognitionApplication ...
```

### Method B: Using Terminal in Eclipse

1. In Eclipse, go to **View → Terminal** (or press Ctrl + Alt + T)
2. Terminal opens at project root
3. Type:
   ```bash
   mvn clean spring-boot:run
   ```
4. Wait for "Tomcat started on port 8000"

### Method C: Run as Maven Build

1. Right-click on project → **Run As → Maven build**
2. In dialog, enter: `clean spring-boot:run`
3. Click **Run**

---

## ✅ STEP 6: Test the API

### Option 1: In Browser

Open a new browser tab and go to:
```
http://localhost:8000/ping
```

**You should see JSON:**
```json
{
  "status": "online",
  "service": "CodeCognition AI v3.3 (Java)",
  "ts": "2026-04-02T12:00:00.123456"
}
```

### Option 2: Using Postman

1. Open **Postman** (or Insomnia)
2. Create **GET** request to: `http://localhost:8000/ping`
3. Click **Send**

### Option 3: Using PowerShell

```powershell
Invoke-WebRequest -Uri "http://localhost:8000/ping" | Select-Object -ExpandProperty Content | ConvertFrom-Json
```

---

## 🎨 STEP 7: Edit Code & See Changes

1. Double-click any `.java` file to open it
2. Eclipse auto-compiles on save
3. To see changes: **Stop** the app (red square in toolbar) and **Run** again

**Tip:** Use **Ctrl + Shift + F** to format code

---

## 🐛 Debug Mode (Advanced)

1. Set a **breakpoint** by clicking left margin of any line
2. Right-click project → **Debug As → Java Application**
3. Code will pause at breakpoint
4. Use **F5** (Step Into), **F6** (Step Over), **F8** (Continue)

---

## 🆘 Troubleshooting

### Problem: "Cannot find CodeCognitionApplication"

**Solution:**
1. Right-click project → **Maven → Update Project**
2. Press **F5** to refresh
3. Go to **Project → Clean** and clean the project

### Problem: "Port 8000 already in use"

**Solution:**
1. Change port in `application.properties`:
   ```
   server.port=8001
   ```
2. Or kill the process using port 8000

### Problem: "BUILD FAILURE" in console

**Solution:**
1. Check **Console** for error messages
2. Right-click project → **Maven → Update Project**
3. Delete `target/` folder and rebuild

### Problem: Dependencies not downloading

**Solution:**
1. Right-click project → **Maven → Update Project**
2. Run in terminal: `mvn dependency:resolve`

---

## 📡 Next: Connect Frontend

Edit `frontend/.env`:
```
VITE_API_URL=http://localhost:8000
```

Then in another terminal:
```bash
cd frontend
npm run dev
```

Now frontend will talk to your Java backend! 🎉

---

## 📚 Useful Eclipse Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl + F11` | Run project |
| `Ctrl + Shift + F` | Format code |
| `Ctrl + I` | Fix indentation |
| `Ctrl + /` | Comment/uncomment |
| `F3` | Go to definition |
| `Ctrl + Shift + T` | Open class by name |
| `Alt + Left/Right` | Navigate back/forward |
| `Ctrl + Shift + O` | Organize imports |

---

**✨ You're all set! Enjoy your Java backend!**

