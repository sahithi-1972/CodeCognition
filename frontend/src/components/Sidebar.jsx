import { NavLink, useNavigate } from 'react-router-dom';
import { LayoutDashboard, GitFork, ScrollText, Settings, Brain, LogOut, ChevronRight, Circle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const NAV = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/repositories', icon: GitFork, label: 'Repositories' },
  { to: '/logs', icon: ScrollText, label: 'Agent Logs' },
  { to: '/settings', icon: Settings, label: 'Settings' },
];

export default function Sidebar() {
  const { user, logout, githubUser } = useAuth();
  const navigate = useNavigate();

  const displayName = githubUser?.name || githubUser?.login || user?.name || user?.email?.split('@')[0] || 'Developer';
  const displayEmail = githubUser?.login ? `@${githubUser.login}` : user?.email || '';
  const avatar = githubUser?.avatar_url || user?.picture;
  const initials = displayName.split(' ').map((word) => word[0]).join('').slice(0, 2).toUpperCase();

  const handleLogout = () => {
    logout();
    navigate('/', { replace: true });
  };

  return (
    <aside className="w-72 min-h-screen shrink-0 border-r border-slate-800 bg-slate-950/95 text-slate-100 flex flex-col">
      <div className="px-6 py-5 border-b border-slate-800">
        <div className="flex items-center gap-3">
          <div className="flex h-11 w-11 items-center justify-center rounded-3xl bg-slate-900 border border-slate-800 text-cyan-300">
            <Brain className="h-5 w-5" />
          </div>
          <div>
            <div className="text-base font-semibold text-slate-100">CodeCognition</div>
            <div className="text-xs uppercase tracking-[0.3em] text-slate-500">AI Intelligence</div>
          </div>
        </div>
      </div>

      <div className="px-6 py-4">
        <div className="rounded-3xl border border-slate-800/80 bg-slate-900/80 p-4">
          <div className="flex items-center justify-between gap-3 text-sm text-slate-300">
            <div>Agents</div>
            <span className="inline-flex h-2.5 w-2.5 rounded-full bg-emerald-400" />
          </div>
          <div className="mt-3 text-3xl font-semibold text-slate-100">4</div>
          <div className="mt-1 text-xs text-slate-500">Continuously monitoring</div>
        </div>
      </div>

      <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-2">
        {NAV.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              `group flex items-center gap-3 rounded-3xl px-4 py-3 text-sm font-medium transition ${
                isActive
                  ? 'bg-cyan-500/10 text-cyan-200 ring-1 ring-cyan-400/20'
                  : 'text-slate-400 hover:text-slate-100 hover:bg-slate-900/70'
              }`
            }
          >
            <Icon className="h-4 w-4" />
            <span className="grow">{label}</span>
            {({ isActive }) => isActive && <ChevronRight className="h-4 w-4 text-cyan-300" />}
          </NavLink>
        ))}
      </nav>

      <div className="px-6 py-5 border-t border-slate-800">
        <div className="flex items-center gap-3">
          {avatar ? (
            <img
              src={avatar}
              alt={displayName}
              referrerPolicy="no-referrer"
              className="h-11 w-11 rounded-3xl border border-slate-800 object-cover"
            />
          ) : (
            <div className="grid h-11 w-11 place-items-center rounded-3xl bg-cyan-500/10 text-cyan-300">
              {initials}
            </div>
          )}
          <div className="min-w-0">
            <div className="truncate text-sm font-semibold text-slate-100">{displayName}</div>
            <div className="truncate text-xs text-slate-500">{displayEmail}</div>
          </div>
          <button onClick={handleLogout} title="Sign out" className="rounded-2xl p-2 text-slate-400 transition hover:bg-slate-900 hover:text-red-400">
            <LogOut className="h-4 w-4" />
          </button>
        </div>
      </div>
    </aside>
  );
}
