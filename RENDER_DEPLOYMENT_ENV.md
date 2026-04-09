# Render Deployment - Environment Variables Configuration

## Backend (Render) - Environment Variables to Set

Set these environment variables in your Render backend deployment settings:

### Database Configuration
```
DB_HOST=your-railways-db-host.railway.internal
DB_PORT=5432
DB_NAME=your_database_name
DB_USER=your_db_user
DB_PASSWORD=your_db_password
```

**How to find these from Railways:**
1. Go to Railways dashboard
2. Find your PostgreSQL database
3. Click on it
4. View the "Connect" tab
5. Copy the connection string values

### JWT Configuration (IMPORTANT!)
```
JWT_SECRET=your-super-secure-random-jwt-secret-at-least-32-characters
JWT_EXPIRATION_MS=86400000
```

**⚠️ SECURITY:** 
- Generate a strong random secret (at least 32 characters)
- Never commit secrets to git
- Use environment variables only

### CORS Configuration (Optional - only if frontend domain changes)
```
ALLOWED_ORIGINS=https://your-netlify-domain.netlify.app,https://your-custom-domain.com
```

If not set, defaults to:
- http://localhost:5173 (dev frontend)
- http://localhost:3000 (alternative frontend port)
- http://localhost:8000 (dev backend)
- https://spiffy-syrniki-74f808.netlify.app (production)

---

## Frontend (Netlify) - Environment Variables

Create/Update `.env.production` in the frontend directory:

```
VITE_API_URL=https://your-render-backend-url.onrender.com
VITE_GOOGLE_CLIENT_ID=your_google_client_id_or_placeholder
VITE_MICROSOFT_CLIENT_ID=your_microsoft_client_id_or_placeholder
```

**Find your Render backend URL:**
1. Go to Render dashboard
2. Click on your service
3. Copy the URL from the "Environments" section or use the default format

---

## Troubleshooting Guide

### Issue: Sign In/Sign Up Not Responding

**Step 1: Check Browser Console**
- Open DevTools (F12)
- Go to Console tab
- Look for error messages with "[SignIn]" or "[SignUp]" prefix

**Step 2: Check Backend Logs**
- Go to Render dashboard
- Click your backend service
- View "Logs" tab
- Look for error messages

**Step 3: Verify Database Connection**
- Check if DB_HOST, DB_PORT, DB_NAME are correctly set
- Try connecting to the database directly using a DB client
- Verify the database is running and accessible

**Step 4: Verify CORS**
- Open DevTools → Network tab
- Click "Sign In" button
- Look for failed requests with CORS errors
- If found, update ALLOWED_ORIGINS environment variable

**Step 5: Check API URL**
- In frontend DevTools Console, run:
  ```javascript
  console.log(import.meta.env.VITE_API_URL)
  ```
- Should show your Render backend URL
- If it shows undefined or wrong URL, check .env.production file

---

## Sign In/Sign Up Workflow - What's Fixed

### ✅ Button States (FIXED)
- Sign Up button is now **disabled during Sign In** loading
- Sign In button remains disabled during Sign Up
- Prevents accidental clicks during authentication

### ✅ Error Handling (FIXED)
- All error paths now properly reset loading state
- No more stuck "Signing in..." states
- Detailed error messages for network issues

### ✅ Network Reliability (FIXED)
- Added 15-second timeout for all auth requests
- Better handling of connection errors
- Specific messages for timeout vs network vs server errors

### ✅ Debugging (FIXED)
- Comprehensive console logging with prefixes:
  - `[SignIn]` - Sign in operations
  - `[SignUp]` - Sign up operations
  - `[GitHub]` - GitHub authentication
  - `[AUTH]` - Backend authentication
- Monitor these logs to track authentication flow

### ✅ Backend Error Responses (FIXED)
- Now returns proper HTTP status codes:
  - 200: Success
  - 400: Bad request (email exists, validation failed)
  - 401: Unauthorized (invalid credentials)
  - 500: Server error
- Error messages are now descriptive and actionable

---

## Testing Sign In/Sign Up Locally

Before deploying, test locally:

```bash
# Terminal 1: Start backend
cd backend-java
mvn spring-boot:run

# Terminal 2: Start frontend
cd frontend
npm run dev

# Open browser to http://localhost:5173
```

Monitor console for `[SignIn]`, `[SignUp]`, `[AUTH]` messages.

---

## Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| "Network error" in Sign In | Backend URL not reachable | Verify VITE_API_URL is correct and backend is running |
| "Connection timeout" | Backend slow to respond | Check backend logs, may be cold start on Render |
| "Invalid email or password" | Database not connected | Verify DB_HOST, DB_PORT, DB_NAME in Render env vars |
| "Email already registered" | User exists | Use different email or try Sign In instead |
| CORS error in console | Frontend domain not allowed | Update ALLOWED_ORIGINS with correct Netlify domain |
| Sign Up button clickable during Sign In | Old code | Rebuild frontend with latest changes |

---

## Deployment Checklist

- [ ] Database created on Railways
- [ ] DB credentials copied to Render env vars
- [ ] JWT_SECRET set in Render env vars (32+ characters, random)
- [ ] Frontend built and deployed to Netlify
- [ ] .env.production has correct VITE_API_URL
- [ ] ALLOWED_ORIGINS includes Netlify domain
- [ ] Backend logs show "Backend starting..." message
- [ ] Test Sign In with valid credentials
- [ ] Test Sign Up with new email
- [ ] Check browser console for no errors during auth
- [ ] Verify loading states work correctly

---

## Quick Deploy Commands

```bash
# Build frontend
cd frontend
npm run build

# Deploy to Netlify (if using Netlify CLI)
netlify deploy --prod --dir=dist

# Backend automatically redeploys on git push to Render
git add .
git commit -m "Fix: authentication flow and error handling"
git push
```
