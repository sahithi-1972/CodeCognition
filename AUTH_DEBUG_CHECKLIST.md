# Authentication Debug Checklist

## Before You Commit - Run This Checklist

### 1. Frontend - Button States
- [ ] Open Login page
- [ ] Click "Sign In" tab
- [ ] Start typing email and password
- [ ] Verify "Sign Up" button is **VISUALLY DISABLED** (grayed out)
- [ ] Click "Sign In" and watch button text change to "Signing in..."
- [ ] Verify "Sign Up" button remains disabled throughout loading

### 2. Frontend - Loading States
- [ ] Open DevTools Console (F12)
- [ ] Go to Sign In page
- [ ] Enter valid credentials
- [ ] Look for `[SignIn] Attempting login to:` message
- [ ] After success or error, look for log entries
- [ ] **Verify no console errors related to loading state**

### 3. Frontend - Network Tab
- [ ] Open DevTools Network tab
- [ ] Click "Sign In" 
- [ ] Watch the `/auth/login` POST request
- [ ] Check response status (should be 200 on success, 401 on bad credentials)
- [ ] Verify response has `token`, `email`, `fullName`, `role` fields

### 4. Frontend - Error Handling
- [ ] Try signing in with **wrong email** → should show "User not found" or "Invalid email or password"
- [ ] Try signing in with **correct email, wrong password** → should show "Invalid email or password"
- [ ] Try signing in with **empty fields** → should show "Enter your email and password"
- [ ] In each case, verify **Sign Up button becomes enabled again** after error

### 5. Frontend - Network Error Simulation
- [ ] Go to Network tab, throttle to "Offline"
- [ ] Try to sign in
- [ ] Should see error: "Network error. Check your connection..."
- [ ] Sign Up button should be enabled again

### 6. Frontend - Timeout Testing (if deployed)
- [ ] Change VITE_API_URL to unreachable domain (e.g., `https://invalid-domain-xyz.com`)
- [ ] Try to sign in
- [ ] Should see: "Connection timeout. Check your internet..." after 15 seconds
- [ ] Sign Up button should be enabled again

### 7. Backend - Environment Variables (Render)
- [ ] Go to Render dashboard
- [ ] Click your backend service
- [ ] Go to "Settings" → "Environment Variables"
- [ ] Verify these are set:
  - [ ] DB_HOST (should not be empty or "localhost")
  - [ ] DB_PORT (should be 5432 or 3306)
  - [ ] DB_NAME (should not be empty)
  - [ ] DB_USER (should not be empty)
  - [ ] DB_PASSWORD (should not be empty)
  - [ ] JWT_SECRET (should be 32+ characters and random)

### 8. Backend - Logs
- [ ] Go to Render dashboard
- [ ] Click your service → Logs
- [ ] Restart the service (gray button at top right)
- [ ] Look for these startup messages:
  - [ ] `=== CodeCognition Backend Environment Status ===`
  - [ ] `[✓] Backend starting...`
  - [ ] `[DB] Host: your-db-host` (should NOT be "localhost")
  - [ ] `[JWT] Secret configured: ✓`
- [ ] After restarting, try to sign in
- [ ] Look for in logs:
  - [ ] `[AUTH] Login attempt for: user@email.com`
  - [ ] `[AUTH] Login successful for: user@email.com`

### 9. Backend - Database Connection
- [ ] Check if database is actually accessible
- [ ] If using Railways, go to Railways dashboard
- [ ] Find your database
- [ ] Try to connect using your client (DBeaver, pgAdmin, etc.)
- [ ] Verify tables exist (User table should exist)

### 10. CORS - Browser Check
- [ ] Open DevTools Console
- [ ] Try to sign in
- [ ] Look for errors like:
  - [ ] `Access to XMLHttpRequest has been blocked by CORS`
  - [ ] If found, your ALLOWED_ORIGINS is not configured correctly

### 11. Frontend - Sign Up Flow
- [ ] Open Sign In page
- [ ] Click "Sign Up" button (or navigate to signup)
- [ ] Notice the page title changes to "Create your account"
- [ ] Enter email and password
- [ ] Click "Sign Up with Email"
- [ ] Verify Sign In button is disabled during loading
- [ ] Check console for `[SignUp]` messages
- [ ] After success, verify sync overlay appears

### 12. GitHub Integration (if testing)
- [ ] Go to GitHub tab
- [ ] Paste a valid GitHub PAT
- [ ] Click "Verify"
- [ ] Should see checkmark and "GitHub account detected: username"
- [ ] Click "Sign In with GitHub"
- [ ] Verify loading state management

---

## Critical Fixes Applied

✅ **Fix 1: Sign Up button now disabled during Sign In loading**
- Location: EmailAuthForm component, Sign Up button
- Change: Added `disabled={loading}` prop

✅ **Fix 2: Proper error state reset in all catch blocks**
- Location: handleEmailSignIn, handleEmailSignUp
- Change: All error paths call `setLoading(false)`

✅ **Fix 3: Fetch timeout helper added**
- Location: fetchWithTimeout function at top of Login.jsx
- Effect: 15-second timeout prevents infinite hangs

✅ **Fix 4: Comprehensive error logging**
- Location: All auth functions
- Prefix: [SignIn], [SignUp], [GitHub], [AUTH]
- Enables debugging in production

✅ **Fix 5: Better backend error responses**
- Location: AuthController catch blocks
- Change: Returns proper HTTP status codes (400, 401, 500)

✅ **Fix 6: Environment variable logging on startup**
- Location: CodeCognitionApplication.main()
- Effect: Immediately shows if config is correct

✅ **Fix 7: Dynamic CORS configuration**
- Location: SecurityConfig.corsConfigurationSource()
- Effect: Can override origins with ALLOWED_ORIGINS env var

---

## If Something Still Doesn't Work

1. **Check browser console** for `[SignIn]` or `[SignUp]` messages
2. **Check Render backend logs** for `[AUTH]` messages
3. **Compare error messages** - note the exact text
4. **Open GitHub issue** with:
   - Exact error message from console
   - Screenshot of network request/response
   - Relevant log entries
   - Steps to reproduce

---

## Production Ready Checklist

Before going live:

- [ ] All error handling shows user-friendly messages
- [ ] No raw exception messages shown in UI
- [ ] Logging includes timestamps and context
- [ ] Database backups are configured
- [ ] JWT secret is strong and random
- [ ] ALLOWED_ORIGINS includes all frontend domains
- [ ] Backend and frontend are deployed
- [ ] Test workflow: Sign Up → Sign In → View Dashboard
- [ ] Monitor Render logs for first 24 hours
- [ ] Have a rollback plan if issues arise
