# 🏗️ CodeCognition - Complete Tech Stack Analysis

**Date:** April 6, 2026  
**Project Version:** 3.3.0  
**Owner:** sahithi-1972

---

## 📊 Overview

Your project is a **Full-Stack AI-Powered Repository Analysis Platform** with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────┐
│              Frontend (React + Vite)                    │
│         Browser-based UI with real-time updates         │
└────────────────────────┬────────────────────────────────┘
                         │ HTTP/REST API
                         │ (CORS enabled)
┌────────────────────────▼────────────────────────────────┐
│           Backend (Java Spring Boot)                    │
│         REST API server on port 8000                    │
└────────────────────────┬────────────────────────────────┘
                         │ External APIs
         ┌───────────────┼───────────────┐
         │               │               │
         ▼               ▼               ▼
    GitHub API     Claude API      (Optional)
    (Read Repos)   (AI Analysis)    Database
```

---

## 🎨 FRONTEND STACK

### Framework & Build
- **React** v18.2.0 - Modern UI library with hooks
- **Vite** v5.2.0 - Ultra-fast development & production build
- **React Router** v6.22.3 - Client-side routing
- **Node.js** - JavaScript runtime

### Styling & UI
- **Tailwind CSS** v3.4.3 - Utility-first CSS framework
- **PostCSS** v8.4.38 - CSS transformations
- **Autoprefixer** v10.4.19 - Browser compatibility
- **Framer Motion** v11.0.8 - Animation library
- **Lucide React** v0.378.0 - Icon library (700+ icons)

### State Management
- **React Context API** - Global state (AuthContext, ThemeContext)
- **React Hooks** - Local component state (useState, useEffect, useCallback)

### API Communication
- **Axios** v1.13.6 - HTTP client library
- **Fetch API** (native) - GitHub API calls with native fetch

### Authentication
- **Google Identity Services** (via script tag) - Google OAuth
- **GitHub Personal Access Token (PAT)** - GitHub API auth
- **localStorage/sessionStorage** - Session persistence

### Development Tools
- **@vitejs/plugin-react** v4.2.1 - React plugin for Vite
- **ESLint** (configured but no explicit config shown) - Code linting

---

## 🖥️ BACKEND STACK

### Framework & Runtime
- **Java** 17 (LTS) - Programming language
- **Spring Boot** v3.2.0 - Enterprise framework
- **Maven** - Build automation & dependency management

### Core Dependencies
| Dependency | Version | Purpose |
|---|---|---|
| `spring-boot-starter-web` | Latest | REST API, embedded Tomcat |
| `spring-boot-starter-logging` | Latest | Logging infrastructure |
| `spring-boot-starter-test` | Latest | Testing framework |
| `lombok` | Latest | Boilerplate reduction |
| `jackson-databind` | Latest | JSON serialization/deserialization |
| `okhttp3` | Latest | HTTP client for external APIs |
| `json-path` | Latest | JSON parsing & querying |
| `java-dotenv` | 5.2.2 | Environment variable loading (.env) |

### Server Configuration
- **Port:** 8000
- **Context Path:** / (root)
- **Servlet:** Tomcat (embedded in Spring Boot)
- **Logging Level:** DEBUG for `com.codecognition`, INFO for others

### External API Integration
- **GitHub API** (`api.github.com`) - Fetch repo data, files, commits
- **Claude API** - AI analysis via Anthropic

### CORS Configuration
- **Enabled:** Yes (in `CodeCognitionApplication.java`)
- **Allowed Origins:** `*` (all origins - not secure for production!)
- **Allowed Methods:** GET, POST, PUT, DELETE, OPTIONS
- **Max Age:** 3600 seconds (1 hour)

---

## 🔒 SECURITY ANALYSIS

### ⚠️ **SECURITY ISSUES FOUND:**

#### 1. **CORS is Too Permissive** ❌
**Current:**
```java
.allowedOrigins("*")  // Allows ALL origins
```

**Risk:** Anyone can call your API from any website  
**Fix:** Whitelist specific origins:
```java
.allowedOrigins(
  "http://localhost:5173",
  "https://yourfrontenddomain.com"
)
```

#### 2. **No Authentication/Authorization** ❌
**Status:** MISSING

**Issues:**
- Anyone can call backend endpoints without credentials
- GitHub token passed from frontend = exposed to client
- No session management
- No API key validation

**Needed:**
- JWT (JSON Web Tokens) for stateless auth
- API key validation
- Rate limiting
- Input validation

#### 3. **No HTTPS/TLS** ❌
**Status:** HTTP only

**Risk:** Data transmitted in plain text  
**Fix:** Require HTTPS in production

#### 4. **Secrets Not Protected** ❌
**Current:** Using `.env` file (checked into git?)

**Issues:**
- Environment variables visible in code
- GitHub token exposed in frontend
- No secret vault

**Fix:**
- Don't commit `.env` to git (add to `.gitignore`)
- Use Azure Key Vault / AWS Secrets Manager
- Rotate tokens regularly

#### 5. **No Input Validation** ❌
**Status:** MISSING

**Risk:** SQL injection, command injection, XSS  
**Fix:** Validate all inputs in backend

#### 6. **No SQL Injection Protection** ⚠️
**Status:** N/A (no database currently)

**But when you add DB:** Use parameterized queries/ORM

#### 7. **No OWASP Compliance** ❌
**Missing:**
- XSS protection (Content Security Policy headers)
- CSRF protection
- Rate limiting
- Audit logging

---

## 🗄️ DATABASE ANALYSIS

### **Current Status: NONE** ❌

**What you have:**
- No database (MySQL, PostgreSQL, etc.)
- No JPA/Hibernate ORM
- No SQL dependencies in pom.xml
- All data is **ephemeral** (lost on restart)

### **What You Need:**

**When to add database:**
1. **User persistence** - Store user accounts, settings
2. **Analysis history** - Keep past analysis results
3. **Agent logs** - Store execution history
4. **Rate limiting** - Track API usage per user
5. **Caching** - Avoid re-analyzing same repo

**Recommended: PostgreSQL**
```xml
<!-- Add to pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.0</version>
    <scope>runtime</scope>
</dependency>
```

### **Alternative: H2 (In-Memory, for testing)**
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.2.224</version>
    <scope>runtime</scope>
</dependency>
```

---

## 🔐 AUTHENTICATION & AUTHORIZATION

### **Current Implementation:**

#### Frontend
- ✅ Google OAuth (optional)
- ✅ GitHub PAT token input
- ✅ Session storage (localStorage/sessionStorage)

#### Backend
- ❌ **NO AUTHENTICATION**
- ❌ **NO AUTHORIZATION**
- ❌ **NO JWT**
- ❌ **NO OAUTH2**

### **What's Missing:**

#### 1. **JWT (JSON Web Tokens)** - NEEDED
```
Flow:
1. Frontend login → Backend validates credentials
2. Backend generates JWT token
3. Frontend stores JWT in localStorage/cookie
4. Frontend sends JWT in Authorization header: Bearer <token>
5. Backend validates JWT on each request
```

**Add to pom.xml:**
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
```

#### 2. **Spring Security** - HIGHLY RECOMMENDED
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

**Features:**
- Authentication filters
- Authorization rules
- Password encryption (BCrypt)
- CSRF protection
- XSS protection headers

#### 3. **OAuth2 Server** - OPTIONAL (for multi-app access)
```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-authorization-server</artifactId>
    <version>1.2.0</version>
</dependency>
```

#### 4. **API Key Authentication** - SIMPLE ALTERNATIVE
Instead of OAuth:
- Generate API key for each user
- Client sends in header: `X-API-Key: <key>`
- Backend validates in interceptor

---

## 🚨 MISSING CRITICAL COMPONENTS

### Priority 1 (Do First)
| Component | Purpose | Impact |
|---|---|---|
| **Database** | Data persistence | High - Can't save analysis results |
| **Input Validation** | Security | Critical - Vulnerabilities |
| **Error Handling** | User experience | High - Poor error messages |
| **JWT/Auth** | Security | Critical - Anyone can call API |
| **API Rate Limiting** | Security/Cost | High - Prevent abuse |
| **Logging** | Debugging | Medium - Can't track issues |

### Priority 2 (Do Later)
| Component | Purpose | Impact |
|---|---|---|
| **Database Encryption** | Data security | Medium |
| **Audit Trails** | Compliance | Medium |
| **API Documentation** | Developer experience | Low |
| **Unit Tests** | Code quality | Medium |
| **CI/CD Pipeline** | Deployment | Low |
| **Monitoring/Alerts** | Ops | Low |

### Priority 3 (Nice to Have)
| Component | Purpose | Impact |
|---|---|---|
| **Caching (Redis)** | Performance | Low |
| **Message Queue (RabbitMQ)** | Async tasks | Low |
| **WebSocket** | Real-time updates | Low |
| **GraphQL** | Alternative API | Very Low |

---

## 📋 RECOMMENDED TECH ADDITIONS

### For Security (MUST HAVE)
```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>

<!-- Input Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### For Database (SHOULD HAVE)
```xml
<!-- PostgreSQL + JPA/Hibernate -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.0</version>
    <scope>runtime</scope>
</dependency>
```

### For Better Error Handling (NICE TO HAVE)
```xml
<!-- Global exception handling -->
<!-- Already have Lombok for @Builder, @Data -->
```

### For Logging (NICE TO HAVE)
```xml
<!-- Logback already included with spring-boot-starter-logging -->
<!-- Add SLF4J annotations: @Slf4j -->
```

---

## 🔄 DATA FLOW ARCHITECTURE

```
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND (React)                         │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Login Page                                           │   │
│  │ ├─ Google OAuth                                     │   │
│  │ └─ GitHub PAT Token Input                           │   │
│  └──────────────┬───────────────────────────────────────┘   │
└─────────────────┼───────────────────────────────────────────┘
                  │ HTTP Request + GitHub Token
┌─────────────────▼───────────────────────────────────────────┐
│               BACKEND (Spring Boot)                         │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ REST Controller (@PostMapping /analyze-repo)        │   │
│  │ Input: Repo metadata (owner, repo, language, etc.)  │   │
│  └──────────────┬───────────────────────────────────────┘   │
│                 │                                            │
│  ┌──────────────▼───────────────────────────────────────┐   │
│  │ Service Layer (Business Logic)                       │   │
│  │ ├─ Fetch file tree from GitHub                      │   │
│  │ ├─ Extract key files (package.json, etc.)           │   │
│  │ └─ Call Claude API with context                     │   │
│  └──────────────┬───────────────────────────────────────┘   │
│                 │                                            │
│  ┌──────────────▼───────────────────────────────────────┐   │
│  │ Model Layer (Data Models)                            │   │
│  │ ├─ AnalysisResult                                   │   │
│  │ ├─ Finding (with severity)                          │   │
│  │ └─ AgentLog                                         │   │
│  └──────────────┬───────────────────────────────────────┘   │
└─────────────────┼───────────────────────────────────────────┘
                  │ JSON Response
                  │
┌─────────────────▼───────────────────────────────────────────┐
│  External APIs Called                                       │
│  ├─ GitHub API (api.github.com) - Fetch repo files        │
│  └─ Claude API (Anthropic) - AI analysis                  │
└─────────────────────────────────────────────────────────────┘
```

---

## 📝 Configuration Files

### Frontend
- `vite.config.js` - Vite build config
- `tailwind.config.js` - Tailwind theme
- `postcss.config.js` - CSS processing
- `.env` (not committed) - API URLs, Google OAuth ID

### Backend
- `pom.xml` - Maven dependencies & build config
- `application.properties` - Spring Boot config
- `.env` (not committed) - API keys, secrets

### Version Control
- `.gitignore` - Exclude: node_modules/, target/, .env, .vscode/

---

## 🚀 CURRENT STATE vs PRODUCTION READY

### Current State
```
✅ Frontend: Working, styled, animated
✅ Basic GitHub integration
✅ Claude API calls
✅ REST API structure
❌ No authentication
❌ No database
❌ No security headers
❌ No error handling
❌ No input validation
❌ No logging
```

### To Make Production Ready (Priority Order)

**Phase 1 (Essential):** 2-3 weeks
1. Add Spring Security + JWT
2. Add input validation
3. Add error handling
4. Add database (PostgreSQL)
5. Add audit logging

**Phase 2 (Important):** 2-3 weeks
1. Add API rate limiting
2. Add HTTPS/TLS
3. Add CORS configuration (restrict origins)
4. Add monitoring/alerts
5. Add unit tests

**Phase 3 (Nice to Have):** 1-2 weeks
1. Add API documentation (Swagger/OpenAPI)
2. Add CI/CD pipeline
3. Add caching (Redis)
4. Add performance optimization
5. Add security scanning

---

## 💡 RECOMMENDATIONS

### Immediate Actions (This Week)

1. **Add `.env` to `.gitignore`**
   ```
   .env
   .env.local
   *.secret
   ```

2. **Add Spring Security to pom.xml**
   ```bash
   # In backend-java/
   # Add security dependency
   ```

3. **Fix CORS - Don't use `*` for origins**
   ```java
   .allowedOrigins("http://localhost:5173")
   ```

4. **Add input validation annotations**
   ```java
   @NotBlank(message = "Repo name required")
   private String repo;
   ```

5. **Add error handler**
   ```java
   @ControllerAdvice
   public class GlobalExceptionHandler {
       @ExceptionHandler(Exception.class)
       public ResponseEntity<?> handle(Exception e) { }
   }
   ```

### Short Term (1 Month)

1. **Add PostgreSQL database**
2. **Add JWT authentication**
3. **Add user management (register/login)**
4. **Add analysis history storage**
5. **Add API documentation**

### Long Term (2-3 Months)

1. **Add monitoring (DataDog, New Relic)**
2. **Add CI/CD (GitHub Actions, GitLab)**
3. **Add caching layer**
4. **Add performance optimization**
5. **Add security scanning (OWASP, SonarQube)**

---

## 📚 Quick Reference

### Current Tech
- **Frontend:** React 18 + Vite + Tailwind
- **Backend:** Java 17 + Spring Boot 3.2
- **APIs:** GitHub + Claude
- **Auth:** Google OAuth (frontend) + GitHub PAT
- **Database:** None
- **Security:** CORS only (not secure)

### What's Missing
- Database (PostgreSQL recommended)
- JWT authentication
- Spring Security
- Input validation
- Error handling
- Logging/monitoring
- Rate limiting
- HTTPS/TLS

### Quick Add Commands

```bash
# Add Spring Security
mvn dependency:resolve

# Add validation
# Already in Spring Boot starter-web

# Add JWT
# See pom.xml changes above

# Add PostgreSQL
# See pom.xml changes above
```

---

**Status:** ⚠️ MVP/Demo Ready but NOT Production Ready  
**Security Level:** Low - Needs hardening  
**Database Status:** Missing - Add ASAP  
**Authentication:** Partial - Needs backend auth  

