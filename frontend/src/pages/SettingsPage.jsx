import { useState } from 'react';
import { motion } from 'framer-motion';
import { Github, Trash2, Bell, Shield, Eye, User } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { fetchGitHubUser } from '../hooks/useGitHub';

function Section({ title, icon: Icon, children }) {
  return (
    <motion.div className="rounded-[2rem] border border-slate-800/90 bg-slate-900/95 shadow-[0_28px_60px_rgba(0,0,0,0.28)]">
      <div className="flex items-center gap-3 px-6 py-5 border-b border-slate-800/80 bg-slate-950/80">
        <Icon className="h-4 w-4 text-cyan-400" />
        <span className="text-sm font-semibold text-slate-100">{title}</span>
      </div>
      <div className="p-6">{children}</div>
    </motion.div>
  );
}

function Toggle({ label, sub, value, onChange }) {
  return (
    <div className="flex items-center justify-between gap-4 border-b border-slate-800/70 py-4 last:border-0">
      <div>
        <div className="text-sm text-slate-100">{label}</div>
        {sub && <div className="mt-1 text-xs text-slate-500">{sub}</div>}
      </div>
      <button
        onClick={onChange}
        className={`relative inline-flex h-7 w-14 items-center rounded-full transition ${value ? 'bg-cyan-500' : 'bg-slate-800'}`}
      >
        <span className={`absolute left-1 top-1 h-5 w-5 rounded-full bg-slate-950 shadow transition ${value ? 'translate-x-7' : ''}`} />
      </button>
    </div>
  );
}

export default function SettingsPage() {
  const { user, githubToken, githubUser, connectGitHub, logout } = useAuth();
  const { theme, toggle } = useTheme();

  const [ghInput, setGhInput] = useState('');
  const [ghLoad, setGhLoad] = useState(false);
  const [ghErr, setGhErr] = useState('');
  const [ghOk, setGhOk] = useState(false);

  const [notifyAlerts, setNotifyAlerts] = useState(true);
  const [notifyWeekly, setNotifyWeekly] = useState(false);
  const [autoScan, setAutoScan] = useState(true);

  const displayName = githubUser?.name || user?.name || 'Developer';
  const displayEmail = githubUser?.email || user?.email || '';
  const avatar = githubUser?.avatar_url || user?.picture;
  const initials = displayName.split(' ').map((w) => w[0]).join('').slice(0, 2).toUpperCase();

  const handleConnectGitHub = async () => {
    if (!ghInput.trim()) {
      setGhErr('Paste your GitHub Personal Access Token.');
      return;
    }
    setGhErr('');
    setGhLoad(true);
    try {
      const ghU = await fetchGitHubUser(ghInput.trim());
      connectGitHub(ghInput.trim(), ghU);
      setGhOk(true);
      setGhInput('');
    } catch {
      setGhErr('Invalid token. Ensure it has "repo" and "read:user" scopes.');
    } finally {
      setGhLoad(false);
    }
  };

  const handleDisconnectGitHub = () => {
    connectGitHub(null, null);
    setGhOk(false);
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      <div className="mx-auto max-w-6xl px-6 py-8 sm:px-8 lg:px-10">
        <div className="mb-8 rounded-[2rem] border border-slate-800/90 bg-slate-900/95 p-6 shadow-[0_28px_60px_rgba(0,0,0,0.28)]">
          <div className="text-xs uppercase tracking-[0.3em] text-cyan-300">CodeCognition AI › Settings</div>
          <h1 className="mt-4 text-3xl font-semibold text-slate-100">Workspace settings</h1>
          <p className="mt-3 text-sm text-slate-400">Manage GitHub integration, appearance, and analysis preferences for your CodeCognition environment.</p>
        </div>

        <div className="grid gap-6">
          <Section title="Profile" icon={User}>
            <div className="flex flex-col gap-4 sm:flex-row sm:items-center">
              <div>
                {avatar ? (
                  <img src={avatar} alt={displayName} referrerPolicy="no-referrer" className="h-16 w-16 rounded-3xl border border-slate-800 object-cover" />
                ) : (
                  <div className="grid h-16 w-16 place-items-center rounded-3xl bg-cyan-500/10 text-cyan-300 text-lg font-semibold">{initials}</div>
                )}
              </div>
              <div>
                <div className="text-lg font-semibold text-slate-100">{displayName}</div>
                <div className="text-sm text-slate-500">{displayEmail}</div>
                {githubUser && <div className="mt-2 text-xs uppercase tracking-[0.25em] text-cyan-300">@{githubUser.login}</div>}
              </div>
            </div>
            <button onClick={logout} className="mt-6 inline-flex items-center gap-2 rounded-full border border-red-500/20 bg-red-500/10 px-4 py-3 text-sm font-semibold text-red-200 transition hover:bg-red-500/15">
              <Trash2 className="h-4 w-4" /> Sign out
            </button>
          </Section>

          <Section title="GitHub integration" icon={Github}>
            {githubToken && githubUser ? (
              <div className="space-y-5">
                <div className="rounded-[1.75rem] border border-slate-800/80 bg-slate-950/90 p-5">
                  <div className="flex items-center gap-3">
                    <img src={githubUser.avatar_url} alt={githubUser.login} className="h-12 w-12 rounded-3xl" />
                    <div>
                      <div className="text-sm font-semibold text-slate-100">{githubUser.name || githubUser.login}</div>
                      <div className="text-xs text-slate-500">@{githubUser.login}</div>
                    </div>
                  </div>
                </div>

                <div className="grid gap-3 sm:grid-cols-3">
                  <div className="rounded-3xl border border-slate-800/80 bg-slate-900/90 p-4 text-sm text-slate-300">
                    <div className="font-semibold text-slate-100">{githubUser.public_repos}</div>
                    Public repositories
                  </div>
                  <div className="rounded-3xl border border-slate-800/80 bg-slate-900/90 p-4 text-sm text-slate-300">
                    <div className="font-semibold text-slate-100">{githubUser.total_private_repos || 0}</div>
                    Private repositories
                  </div>
                  <div className="rounded-3xl border border-slate-800/80 bg-slate-900/90 p-4 text-sm text-slate-300">
                    <div className="font-semibold text-slate-100">{githubUser.followers}</div>
                    Followers
                  </div>
                </div>

                <a href={`https://github.com/${githubUser.login}`} target="_blank" rel="noreferrer" className="inline-flex items-center gap-2 text-sm text-cyan-300 hover:text-cyan-200">
                  View GitHub profile
                </a>
                <button onClick={handleDisconnectGitHub} className="inline-flex items-center gap-2 rounded-full border border-slate-800/80 bg-slate-900/90 px-4 py-3 text-sm font-semibold text-slate-100 transition hover:border-cyan-400/50">
                  Disconnect GitHub
                </button>
              </div>
            ) : (
              <div className="space-y-4">
                <div className="rounded-[1.75rem] border border-slate-800/80 bg-slate-950/90 p-4 text-sm text-slate-400">
                  <div className="font-semibold text-slate-100">GitHub token setup</div>
                  Paste a Personal Access Token with <span className="text-cyan-300">repo</span> and <span className="text-cyan-300">read:user</span> scopes.
                </div>
                {ghErr && <div className="rounded-3xl border border-red-500/20 bg-red-500/10 px-4 py-3 text-sm text-red-200">{ghErr}</div>}
                <div className="grid gap-3 sm:grid-cols-[1fr_auto]">
                  <input
                    type="password"
                    value={ghInput}
                    onChange={(e) => setGhInput(e.target.value)}
                    placeholder="Personal access token"
                    className="w-full rounded-3xl border border-slate-800/80 bg-slate-950/90 px-4 py-3 text-slate-100 placeholder:text-slate-500 focus:border-cyan-400/70"
                  />
                  <button onClick={handleConnectGitHub} disabled={ghLoad} className="rounded-full bg-cyan-500 px-5 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400 disabled:opacity-60">
                    {ghLoad ? 'Verifying' : 'Connect'}
                  </button>
                </div>
              </div>
            )}
          </Section>

          <Section title="Notifications" icon={Bell}>
            <Toggle label="Critical alerts" sub="Get notified when high-risk issues are found." value={notifyAlerts} onChange={() => setNotifyAlerts(!notifyAlerts)} />
            <Toggle label="Weekly summary" sub="Receive an automated health digest every week." value={notifyWeekly} onChange={() => setNotifyWeekly(!notifyWeekly)} />
          </Section>

          <Section title="Analysis" icon={Shield}>
            <Toggle label="Automatic scanning" sub="Run scheduled AI scans across all repos." value={autoScan} onChange={() => setAutoScan(!autoScan)} />
            <div className="mt-4">
              <div className="text-sm text-slate-100 mb-2">Scan cadence</div>
              <select className="w-full rounded-3xl border border-slate-800/80 bg-slate-950/90 px-4 py-3 text-sm text-slate-100 focus:border-cyan-400/70">
                <option>Every 6 hours</option>
                <option>Every 12 hours</option>
                <option>Every 24 hours</option>
                <option>Weekly</option>
              </select>
            </div>
          </Section>

          <Section title="Appearance" icon={Eye}>
            <Toggle label="Dark mode" sub="Toggle between dark and light dashboard themes." value={theme === 'dark'} onChange={toggle} />
          </Section>

          <div className="rounded-[2rem] border border-slate-800/90 bg-slate-900/95 p-5 text-center text-sm text-slate-500">
            CodeCognition AI  Intelligent repository monitoring with polished controls.
          </div>
        </div>
      </div>
    </div>
  );
}
