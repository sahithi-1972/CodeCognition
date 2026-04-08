import { useState, useEffect, useCallback, useRef } from 'react';
import { AnimatePresence, motion } from 'framer-motion';
import { Link, useNavigate } from 'react-router-dom';
import { Shield, Lock, Eye, EyeOff, Github, Mail, CheckCircle, AlertCircle, HelpCircle, ChevronDown, ChevronUp } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { fetchGitHubUser } from '../hooks/useGitHub';
import SyncOverlay from '../components/SyncOverlay';

function useGoogleAuth(onSuccess) {
  const initialized = useRef(false);

  useEffect(() => {
    if (initialized.current) return;
    initialized.current = true;

    const script = document.createElement('script');
    script.src = 'https://accounts.google.com/gsi/client';
    script.async = true;
    script.defer = true;
    script.onload = () => {
      const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
      if (!clientId || clientId === 'YOUR_GOOGLE_CLIENT_ID') return;
      window.google?.accounts?.id?.initialize({
        client_id: clientId,
        callback: (response) => {
          try {
            const payload = JSON.parse(atob(response.credential.split('.')[1]));
            onSuccess({
              name: payload.name,
              email: payload.email,
              picture: payload.picture,
              given_name: payload.given_name,
              verified_email: payload.email_verified,
            });
          } catch (error) {
            console.error('Google token decode failed', error);
          }
        },
      });
    };
    document.head.appendChild(script);
  }, [onSuccess]);

  const signIn = useCallback(() => {
    const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
    if (!clientId || clientId === 'YOUR_GOOGLE_CLIENT_ID') {
      alert('Google login requires VITE_GOOGLE_CLIENT_ID in your .env file. Use email login or GitHub login instead.');
      return false;
    }
    if (!window.google?.accounts?.id?.prompt) {
      alert('Google login is still loading. Please try again in a moment.');
      return false;
    }
    window.google.accounts.id.prompt();
    return true;
  }, []);

  return { signIn };
}

function useMicrosoftAuth(onSuccess) {
  const initialized = useRef(false);
  const msalInstance = useRef(null);

  useEffect(() => {
    if (initialized.current) return;
    initialized.current = true;

    const script = document.createElement('script');
    script.src = 'https://alcdn.msauth.net/browser/2.40.0/js/msal-browser.min.js';
    script.async = true;
    script.defer = true;
    script.onload = () => {
      const clientId = import.meta.env.VITE_MICROSOFT_CLIENT_ID;
      if (!clientId || clientId === 'YOUR_MICROSOFT_CLIENT_ID') return;
      const PublicClientApplication = window.msal?.PublicClientApplication;
      if (!PublicClientApplication) return;

      msalInstance.current = new PublicClientApplication({
        auth: {
          clientId,
          redirectUri: window.location.origin,
        },
        cache: {
          cacheLocation: 'localStorage',
          storeAuthStateInCookie: false,
        },
      });
    };
    document.head.appendChild(script);
  }, []);

  const signIn = useCallback(async () => {
    const clientId = import.meta.env.VITE_MICROSOFT_CLIENT_ID;
    if (!clientId || clientId === 'YOUR_MICROSOFT_CLIENT_ID') {
      alert('Microsoft login requires VITE_MICROSOFT_CLIENT_ID in your .env file. Use email signup or Google signup instead.');
      return false;
    }
    if (!msalInstance.current) {
      alert('Microsoft login is still loading. Please try again in a moment.');
      return false;
    }

    try {
      const response = await msalInstance.current.loginPopup({
        scopes: ['openid', 'profile', 'email'],
      });
      const account = response.account || msalInstance.current.getAllAccounts()[0];
      if (!account) throw new Error('No Microsoft account returned');

      onSuccess({
        name: account.name || account.username,
        email: account.username,
        picture: null,
        given_name: account.name ? account.name.split(' ')[0] : account.username.split('@')[0],
        verified_email: true,
      });
      return true;
    } catch (error) {
      throw error;
    }
  }, [onSuccess]);

  return { signIn };
}

function GoogleBrandIcon({ className }) {
  return (
    <svg viewBox="0 0 533.5 544.3" className={className} aria-hidden="true">
      <path fill="#4285F4" d="M533.5 278.4c0-18.5-1.5-36.3-4.3-53.6H272v101.5h146.9c-6.3 34-25.4 62.8-54.3 82v68h87.7c51.3-47.2 80.2-116.7 80.2-197.9z" />
      <path fill="#34A853" d="M272 544.3c73.7 0 135.6-24.4 180.8-66.3l-87.7-68c-24.4 16.3-55.7 26-93.1 26-71.5 0-132.1-48.3-153.8-113.2h-90.7v70.9c45.7 90 140.2 150.6 244.5 150.6z" />
      <path fill="#FBBC05" d="M118.2 322.4c-10.4-31.6-10.4-65.8 0-97.4v-70.9h-90.7c-39.2 77.3-39.2 169.4 0 246.7l90.7-78.4z" />
      <path fill="#EA4335" d="M272 107.1c39.8 0 75.6 13.7 103.8 40.5l77.8-77.8C412.5 24.5 343.2 0 272 0 167.7 0 73.2 60.6 27.5 150.6l90.7 70.9C139.9 155.4 200.5 107.1 272 107.1z" />
    </svg>
  );
}

function MicrosoftBrandIcon({ className }) {
  return (
    <svg viewBox="0 0 24 24" className={className} aria-hidden="true">
      <path fill="#F25022" d="M1 1h10v10H1z" />
      <path fill="#7FBA00" d="M13 1h10v10H13z" />
      <path fill="#00A4EF" d="M1 13h10v10H1z" />
      <path fill="#FFB900" d="M13 13h10v10H13z" />
    </svg>
  );
}

function AuthTabs({ activeTab, setActiveTab }) {
  return (
    <div className="flex rounded-3xl bg-slate-900/50 p-1 mb-6">
      <button
        onClick={() => setActiveTab('email')}
        className={`flex-1 rounded-2xl px-4 py-2 text-sm font-semibold transition ${
          activeTab === 'email'
            ? 'bg-cyan-500 text-slate-950'
            : 'text-slate-400 hover:text-slate-100'
        }`}
      >
        Email
      </button>
      <button
        onClick={() => setActiveTab('github')}
        className={`flex-1 rounded-2xl px-4 py-2 text-sm font-semibold transition ${
          activeTab === 'github'
            ? 'bg-cyan-500 text-slate-950'
            : 'text-slate-400 hover:text-slate-100'
        }`}
      >
        GitHub
      </button>
    </div>
  );
}

function AuthModeToggle({ authMode, setAuthMode }) {
  return (
    <div className="flex rounded-3xl bg-slate-900/50 p-1 mb-6">
      {/* <button
        type="button"
        onClick={() => setAuthMode('signin')}
        className={`flex-1 rounded-2xl px-4 py-2 text-sm font-semibold transition ${
          authMode === 'signin'
            ? 'bg-cyan-500 text-slate-950'
            : 'text-slate-400 hover:text-slate-100'
        }`}
      >
        Sign In
      </button> */}
    </div>
  );
}

function SignupForm({
  email,
  setEmail,
  password,
  setPassword,
  showPass,
  setShowPass,
  error,
  loading,
  googleLoading,
  msLoading,
  onEmailSignUp,
  onGoogleSignup,
  onMicrosoftSignup,
  hasGoogleClientId,
  hasMicrosoftClientId,
  onSwitchToSignIn,
}) {
  return (
    <div className="space-y-5">
      {error && (
        <div className="rounded-3xl border border-red-500/20 bg-red-500/10 px-4 py-3 text-sm text-red-200">
          <AlertCircle className="inline h-4 w-4 mr-2 align-text-bottom" />
          {error}
        </div>
      )}

      <form onSubmit={onEmailSignUp} className="space-y-4">
        <div className="space-y-3">
          <label className="block text-sm font-semibold text-slate-200">Email</label>
          <div className="relative">
            <Mail className="pointer-events-none absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500" />
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="you@company.com"
              className="w-full rounded-3xl border border-slate-800/80 bg-slate-950/90 px-4 py-3 pl-12 text-slate-100 placeholder:text-slate-500 focus:border-cyan-400/70"
            />
          </div>
        </div>

        <div className="space-y-3">
          <label className="block text-sm font-semibold text-slate-200">Password</label>
          <div className="relative">
            <Lock className="pointer-events-none absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500" />
            <input
              type={showPass ? 'text' : 'password'}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Create a secure password"
              className="w-full rounded-3xl border border-slate-800/80 bg-slate-950/90 px-4 py-3 pl-12 pr-12 text-slate-100 placeholder:text-slate-500 focus:border-cyan-400/70"
            />
            <button
              type="button"
              onClick={() => setShowPass(!showPass)}
              className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-100"
            >
              {showPass ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
            </button>
          </div>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full rounded-full bg-cyan-500 px-5 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400 disabled:opacity-60"
        >
          {loading ? 'Creating account...' : 'Sign Up with Email'}
        </button>
      </form>

      {/* <div className="relative">
        <div className="absolute inset-0 flex items-center" aria-hidden="true">
          <div className="w-full border-t border-slate-800" />
        </div>
        <div className="relative flex justify-center text-xs uppercase tracking-[0.3em] text-slate-500">
          Or continue with
        </div>
      </div>

      <div className="grid gap-3">
        <button
          type="button"
          onClick={onGoogleSignup}
          disabled={googleLoading || !hasGoogleClientId}
          className="flex items-center justify-center gap-3 rounded-full border border-slate-700 bg-slate-900/90 px-5 py-3 text-sm font-semibold text-slate-100 transition hover:border-cyan-400/50 disabled:opacity-60"
        >
          <GoogleBrandIcon className="h-5 w-5" />
          {googleLoading ? 'Continue with Google…' : 'Continue with Google'}
        </button>

        <button
          type="button"
          onClick={onMicrosoftSignup}
          disabled={msLoading || !hasMicrosoftClientId}
          className="flex items-center justify-center gap-3 rounded-full border border-slate-700 bg-slate-900/90 px-5 py-3 text-sm font-semibold text-slate-100 transition hover:border-cyan-400/50 disabled:opacity-60"
        >
          <MicrosoftBrandIcon className="h-5 w-5" />
          {msLoading ? 'Continue with Microsoft…' : 'Continue with Microsoft'}
        </button>
      </div> */}

      <div className="pt-4 text-center text-sm text-slate-400">
        Already have an account?{' '}
        <button type="button" onClick={onSwitchToSignIn} className="font-semibold text-cyan-300 hover:text-cyan-100">
          Sign in
        </button>
      </div>
    </div>
  );
}

function EmailAuthForm({ email, setEmail, password, setPassword, showPass, setShowPass, remember, setRemember, error, loading, onSignIn, onSignUp }) {
  return (
    <div className="space-y-5">
      {error && (
        <div className="rounded-3xl border border-red-500/20 bg-red-500/10 px-4 py-3 text-sm text-red-200">
          <AlertCircle className="inline h-4 w-4 mr-2 align-text-bottom" />
          {error}
        </div>
      )}

      <form onSubmit={onSignIn} className="space-y-4">
        <div className="space-y-3">
          <label className="block text-sm font-semibold text-slate-200">Email</label>
          <div className="relative">
            <Mail className="pointer-events-none absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500" />
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="you@company.com"
              className="w-full rounded-3xl border border-slate-800/80 bg-slate-950/90 px-4 py-3 pl-12 text-slate-100 placeholder:text-slate-500 focus:border-cyan-400/70"
            />
          </div>
        </div>

        <div className="space-y-3">
          <label className="block text-sm font-semibold text-slate-200">Password</label>
          <div className="relative">
            <Lock className="pointer-events-none absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500" />
            <input
              type={showPass ? 'text' : 'password'}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter your password"
              className="w-full rounded-3xl border border-slate-800/80 bg-slate-950/90 px-4 py-3 pl-12 pr-12 text-slate-100 placeholder:text-slate-500 focus:border-cyan-400/70"
            />
            <button
              type="button"
              onClick={() => setShowPass(!showPass)}
              className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-100"
            >
              {showPass ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
            </button>
          </div>
        </div>

        <div className="flex items-center justify-between text-sm text-slate-400">
          <label className="inline-flex items-center gap-2">
            <input
              type="checkbox"
              checked={remember}
              onChange={(e) => setRemember(e.target.checked)}
              className="h-4 w-4 rounded border-slate-700 bg-slate-950 text-cyan-400 focus:ring-cyan-400"
            />
            Remember me
          </label>
        </div>

        <div className="flex gap-3">
          <button
            type="submit"
            disabled={loading}
            className="flex-1 rounded-full bg-cyan-500 px-5 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400 disabled:opacity-60"
          >
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
          <button
            type="button"
            onClick={onSignUp}
            className="rounded-full border border-slate-700 bg-slate-950/70 px-5 py-3 text-sm font-semibold text-slate-100 transition hover:border-cyan-400/50"
          >
            Sign Up
          </button>
        </div>
      </form>
    </div>
  );
}

function GithubAuthForm({ ghToken, setGhToken, ghLoading, ghError, ghSuccess, onVerifyToken, onGitHubLogin, showGuide, setShowGuide }) {
  return (
    <div className="space-y-5">
      <button
        type="button"
        onClick={onGitHubLogin}
        disabled={!ghSuccess || ghLoading}
        className="w-full rounded-full bg-slate-800/90 px-5 py-3 text-sm font-semibold text-slate-100 transition hover:border-cyan-400/60 disabled:opacity-60"
      >
        <Github className="inline h-4 w-4 mr-2" />
        Continue with GitHub
      </button>

      <div className="relative">
        <div className="absolute inset-0 flex items-center" aria-hidden="true">
          <div className="w-full border-t border-slate-800" />
        </div>
        <div className="relative flex justify-center text-xs uppercase tracking-[0.3em] text-slate-500">
          Or use personal access token
        </div>
      </div>

      <div className="rounded-[1.75rem] border border-slate-800/80 bg-slate-900/80 p-4">
        <div className="flex items-center justify-between text-sm text-slate-300">
          <span>GitHub Personal Access Token</span>
          {ghSuccess && <CheckCircle className="h-4 w-4 text-emerald-400" />}
        </div>
        <div className="mt-3 flex gap-3 flex-col sm:flex-row">
          <input
            type="password"
            value={ghToken}
            onChange={(e) => setGhToken(e.target.value)}
            placeholder="ghp_xxxxxx"
            className="w-full rounded-3xl border border-slate-800/80 bg-slate-950/90 px-4 py-3 text-slate-100 placeholder:text-slate-500 focus:border-cyan-400/70"
          />
          <button
            type="button"
            onClick={onVerifyToken}
            disabled={ghLoading}
            className="rounded-full bg-slate-800/90 px-5 py-3 text-sm font-semibold text-slate-100 transition hover:border-cyan-400/60 disabled:opacity-60"
          >
            {ghLoading ? 'Verifying...' : 'Verify'}
          </button>
        </div>
        {ghError && <div className="mt-3 text-sm text-red-300">{ghError}</div>}
        {ghSuccess && <div className="mt-3 text-sm text-emerald-300">GitHub account detected: {ghSuccess.login}</div>}
        <button
          type="button"
          onClick={onGitHubLogin}
          disabled={!ghSuccess || ghLoading}
          className="mt-4 w-full rounded-full bg-cyan-500 px-5 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400 disabled:opacity-60"
        >
          Sign In with GitHub
        </button>
      </div>

      <button
        type="button"
        onClick={() => setShowGuide(!showGuide)}
        className="flex items-center justify-center gap-2 text-sm text-cyan-400 hover:text-cyan-200"
      >
        <HelpCircle className="h-4 w-4" />
        Set up token
        {showGuide ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />}
      </button>

      <AnimatePresence>
        {showGuide && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: 'auto', opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.3 }}
            className="overflow-hidden"
          >
            <div className="rounded-[1.75rem] border border-slate-800/80 bg-slate-900/80 p-4 text-sm text-slate-400">
              <div className="font-semibold text-slate-100 mb-3">How to create a Personal Access Token:</div>
              <ol className="space-y-2 list-decimal list-inside">
                <li>Go to <a href="https://github.com/settings/tokens" target="_blank" rel="noreferrer" className="text-cyan-400 hover:underline">GitHub Settings → Developer settings → Personal access tokens</a></li>
                <li>Click "Generate new token (classic)"</li>
                <li>Select scopes: <span className="text-cyan-300">repo</span>, <span className="text-cyan-300">read:user</span></li>
                <li>Generate and copy the token</li>
                <li>Paste it above and click "Verify"</li>
              </ol>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

export default function Login() {
  const { login, isAuthenticated, connectGitHub } = useAuth();
  const navigate = useNavigate();

  const [authMode, setAuthMode] = useState('signin');
  const [activeTab, setActiveTab] = useState('email');

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPass, setShowPass] = useState(false);
  const [remember, setRemember] = useState(false);
  const [loading, setLoading] = useState(false);
  const [googleLoading, setGoogleLoading] = useState(false);
  const [msLoading, setMsLoading] = useState(false);
  const [error, setError] = useState('');
  const [pending, setPending] = useState(null);
  const [showSync, setShowSync] = useState(false);
  const [syncName, setSyncName] = useState('');

  const [ghToken, setGhToken] = useState('');
  const [ghLoading, setGhLoading] = useState(false);
  const [ghError, setGhError] = useState('');
  const [ghSuccess, setGhSuccess] = useState(null);
  const [showGuide, setShowGuide] = useState(false);

  useEffect(() => {
    if (isAuthenticated) navigate('/dashboard', { replace: true });
  }, [isAuthenticated, navigate]);

  const startSync = useCallback((profile, rememberMe) => {
    setPending({ profile, remember: rememberMe });
    setSyncName(profile.name);
    setShowSync(true);
    setGoogleLoading(false);
    setMsLoading(false);
    setLoading(false);
  }, []);

  const onSyncDone = useCallback(() => {
    if (pending) login(pending.profile, pending.remember);
  }, [pending, login]);

  const handleGoogleSuccess = useCallback((profile) => {
    setError('');
    setGoogleLoading(false);
    startSync(profile, remember);
  }, [remember, startSync]);

  const handleMicrosoftSuccess = useCallback((profile) => {
    setError('');
    setMsLoading(false);
    startSync(profile, remember);
  }, [remember, startSync]);

  const { signIn: googleSignIn } = useGoogleAuth(handleGoogleSuccess);
  const { signIn: microsoftSignIn } = useMicrosoftAuth(handleMicrosoftSuccess);

  const handleEmailSignIn = async (e) => {
    e.preventDefault();
    if (!email || !password) {
      setError('Enter your email and password.');
      return;
    }
    setError('');
    setLoading(true);
    
    try {
      const backendUrl = import.meta.env.VITE_API_URL || 'https://codecognition-backend.onrender.com';
      const response = await fetch(`${backendUrl}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || 'Invalid email or password');
      }

      const data = await response.json();
      
      const profile = {
        name: data.fullName || email.split('@')[0].replace(/[._]/g, ' ').replace(/\b\w/g, (char) => char.toUpperCase()),
        email: data.email,
        picture: null,
        given_name: data.fullName?.split(' ')[0] || email.split('@')[0],
        verified_email: true,
        jwtToken: data.token,
        role: data.role,
      };
      
      if (ghSuccess) {
        profile.githubToken = ghToken;
        profile.githubUser = ghSuccess;
      }
      
      startSync(profile, remember);
    } catch (err) {
      setError(err.message || 'Login failed. Please try again.');
      setLoading(false);
    }
  };

  const handleEmailSignUp = async (e) => {
    if (e) e.preventDefault();
    if (!email || !password) {
      setError('Enter your email and password to sign up.');
      return;
    }
    setError('');
    setLoading(true);
    
    try {
      const backendUrl = import.meta.env.VITE_API_URL || 'https://codecognition-backend.onrender.com';
      const response = await fetch(`${backendUrl}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email,
          password,
          fullName: email.split('@')[0].replace(/[._]/g, ' ').replace(/\b\w/g, (char) => char.toUpperCase()),
        }),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || 'Registration failed. Email might already be registered.');
      }

      const data = await response.json();
      
      const profile = {
        name: data.fullName,
        email: data.email,
        picture: null,
        given_name: data.fullName?.split(' ')[0] || email.split('@')[0],
        verified_email: true,
        jwtToken: data.token,
        role: data.role,
      };
      
      startSync(profile, remember);
    } catch (err) {
      setError(err.message || 'Registration failed. Please try again.');
      setLoading(false);
    }
  };

  const handleGoogleSignup = () => {
    setError('');
    setGoogleLoading(true);
    const started = googleSignIn();
    if (!started) setGoogleLoading(false);
  };

  const handleMicrosoftSignup = async () => {
    setError('');
    setMsLoading(true);
    const started = await microsoftSignIn().catch((err) => {
      setError('Microsoft login failed. Try again or use email signup.');
      setMsLoading(false);
      console.error(err);
      return false;
    });
    if (!started) setMsLoading(false);
  };

  const handleGitHubConnect = async () => {
    if (!ghToken.trim()) {
      setGhError('Paste your GitHub Personal Access Token.');
      return;
    }
    setGhError('');
    setGhLoading(true);
    try {
      const user = await fetchGitHubUser(ghToken.trim());
      setGhSuccess(user);
    } catch {
      setGhError('Invalid token. Make sure it has "repo" scope.');
    } finally {
      setGhLoading(false);
    }
  };

  const handleGitHubLogin = async () => {
    if (!ghSuccess) {
      setGhError('Verify your token first.');
      return;
    }
    setLoading(true);
    await new Promise((resolve) => setTimeout(resolve, 300));
    startSync({
      name: ghSuccess.name || ghSuccess.login,
      email: ghSuccess.email || `${ghSuccess.login}@github.com`,
      picture: ghSuccess.avatar_url,
      given_name: ghSuccess.login,
      verified_email: true,
      githubToken: ghToken.trim(),
      githubUser: ghSuccess,
    }, remember);
  };

  const hasGoogleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID && import.meta.env.VITE_GOOGLE_CLIENT_ID !== 'YOUR_GOOGLE_CLIENT_ID';
  const hasMicrosoftClientId = import.meta.env.VITE_MICROSOFT_CLIENT_ID && import.meta.env.VITE_MICROSOFT_CLIENT_ID !== 'YOUR_MICROSOFT_CLIENT_ID';

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      <AnimatePresence>{showSync && <SyncOverlay userName={syncName} onComplete={onSyncDone} />}</AnimatePresence>
      <div className="mx-auto max-w-7xl px-6 py-10 sm:px-8 lg:px-10">
        <div className="grid gap-10 lg:grid-cols-[1.2fr_0.9fr] items-center">
          <motion.section initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6 }} className="rounded-[2rem] border border-slate-800/80 bg-slate-950/80 p-10 shadow-[0_40px_80px_rgba(3,21,45,0.35)]">
            <div className="flex items-center gap-3 rounded-3xl bg-cyan-500/10 px-4 py-3 text-sm text-cyan-200">
              <Shield className="h-4 w-4" />
              Secure, intelligent repository monitoring for engineering teams.
            </div>
            <div className="mt-10">
              <p className="text-xl font-semibold text-slate-100">Welcome back to CodeCognition.</p>
              <p className="mt-4 max-w-xl text-sm leading-7 text-slate-400">
                Sign in to access your dashboard, sync GitHub repositories, and manage AI analysis settings in one polished control center.
              </p>
            </div>
            <div className="mt-12 grid gap-4 sm:grid-cols-2">
              <div className="rounded-[1.75rem] border border-slate-800/80 bg-slate-900/80 p-5">
                <div className="text-xs uppercase tracking-[0.3em] text-slate-500">Trusted workflows</div>
                <div className="mt-3 text-3xl font-semibold text-slate-100">Secure</div>
              </div>
              <div className="rounded-[1.75rem] border border-slate-800/80 bg-slate-900/80 p-5">
                <div className="text-xs uppercase tracking-[0.3em] text-slate-500">Speed</div>
                <div className="mt-3 text-3xl font-semibold text-cyan-300">Optimized</div>
              </div>
            </div>
          </motion.section>

          <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6, delay: 0.1 }} className="rounded-[2rem] border border-slate-800/90 bg-slate-900/95 p-8 shadow-[0_40px_80px_rgba(0,0,0,0.25)]">
            <div className="flex flex-col gap-4 mb-8 md:flex-row md:items-center md:justify-between">
              <div>
                <div className="text-xs uppercase tracking-[0.3em] text-slate-500">{authMode === 'signin' ? 'Sign in' : 'Sign up'}</div>
                <h1 className="mt-2 text-2xl font-semibold text-slate-100">
                  {authMode === 'signin' ? 'Access your workspace' : 'Create your account'}
                </h1>
                <p className="mt-3 max-w-xl text-sm leading-6 text-slate-400">
                  {authMode === 'signin'
                    ? 'Sign in to access your dashboard, sync GitHub repos, and manage AI analysis settings.'
                    : 'Choose how to create your account: manual email entry, Google, or Microsoft authentication.'}
                </p>
              </div>
              <div className="flex items-center gap-3">
                <AuthModeToggle authMode={authMode} setAuthMode={setAuthMode} />
                <Link to="/" className="text-xs uppercase tracking-[0.3em] text-cyan-400 hover:text-cyan-200">Return</Link>
              </div>
            </div>

            {authMode === 'signin' ? (
              <>
                <AuthTabs activeTab={activeTab} setActiveTab={setActiveTab} />
                <AnimatePresence mode="wait">
                  {activeTab === 'email' && (
                    <motion.div
                      key="email"
                      initial={{ opacity: 0, x: -20 }}
                      animate={{ opacity: 1, x: 0 }}
                      exit={{ opacity: 0, x: 20 }}
                      transition={{ duration: 0.3 }}
                    >
                      <EmailAuthForm
                        email={email}
                        setEmail={setEmail}
                        password={password}
                        setPassword={setPassword}
                        showPass={showPass}
                        setShowPass={setShowPass}
                        remember={remember}
                        setRemember={setRemember}
                        error={error}
                        loading={loading}
                        onSignIn={handleEmailSignIn}
                        onSignUp={() => setAuthMode('signup')}
                      />
                    </motion.div>
                  )}
                  {activeTab === 'github' && (
                    <motion.div
                      key="github"
                      initial={{ opacity: 0, x: 20 }}
                      animate={{ opacity: 1, x: 0 }}
                      exit={{ opacity: 0, x: -20 }}
                      transition={{ duration: 0.3 }}
                    >
                      <GithubAuthForm
                        ghToken={ghToken}
                        setGhToken={setGhToken}
                        ghLoading={ghLoading}
                        ghError={ghError}
                        ghSuccess={ghSuccess}
                        onVerifyToken={handleGitHubConnect}
                        onGitHubLogin={handleGitHubLogin}
                        showGuide={showGuide}
                        setShowGuide={setShowGuide}
                      />
                    </motion.div>
                  )}
                </AnimatePresence>
              </>
            ) : (
              <SignupForm
                email={email}
                setEmail={setEmail}
                password={password}
                setPassword={setPassword}
                showPass={showPass}
                setShowPass={setShowPass}
                error={error}
                loading={loading}
                googleLoading={googleLoading}
                msLoading={msLoading}
                onEmailSignUp={handleEmailSignUp}
                onGoogleSignup={handleGoogleSignup}
                onMicrosoftSignup={handleMicrosoftSignup}
                hasGoogleClientId={hasGoogleClientId}
                hasMicrosoftClientId={hasMicrosoftClientId}
                onSwitchToSignIn={() => setAuthMode('signin')}
              />
            )}
          </motion.div>
        </div>
      </div>
    </div>
  );
}
