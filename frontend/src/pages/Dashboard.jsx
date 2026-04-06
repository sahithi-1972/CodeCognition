import { useState } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { Shield, GitFork, Activity, Star, Clock, ArrowRight, Bell, ChevronRight, Lock, BookOpen, Eye } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useGitHubRepos } from '../hooks/useGitHub';
import { useCommitWatch } from '../hooks/useCommitWatch';
import HealthGauge from '../components/HealthGauge';
import CommitActivity from '../components/CommitActivity';

const LANG_COLORS = {
  JavaScript: '#f7df1e',
  TypeScript: '#3178c6',
  Python: '#3776ab',
  Java: '#ed8b00',
  Go: '#00add8',
  Rust: '#ce422b',
  Ruby: '#cc342d',
  'C++': '#00599c',
  C: '#555',
  PHP: '#8892bf',
  Swift: '#ff6b35',
  Kotlin: '#7f52ff',
};

function timeAgo(iso) {
  if (!iso) return '';
  const d = (Date.now() - new Date(iso)) / 1000;
  if (d < 60) return 'now';
  if (d < 3600) return `${Math.floor(d / 60)}m`;
  if (d < 86400) return `${Math.floor(d / 3600)}h`;
  if (d < 2592000) return `${Math.floor(d / 86400)}d`;
  if (d < 31536000) return `${Math.floor(d / 2592000)}mo`;
  return `${Math.floor(d / 31536000)}y`;
}

function StatCard({ icon: Icon, label, value, color }) {
  const theme = {
    primary: { text: 'text-cyan-300', bg: 'bg-cyan-500/10', border: 'border-cyan-500/20' },
    green: { text: 'text-emerald-300', bg: 'bg-emerald-500/10', border: 'border-emerald-500/20' },
    yellow: { text: 'text-amber-300', bg: 'bg-amber-500/10', border: 'border-amber-500/20' },
    blue: { text: 'text-sky-300', bg: 'bg-sky-500/10', border: 'border-sky-500/20' },
  }[color] || { text: 'text-slate-100', bg: 'bg-slate-800/80', border: 'border-slate-800/80' };

  return (
    <div className="rounded-[2rem] border border-slate-800/80 bg-slate-900/95 p-5 shadow-[0_24px_60px_rgba(0,0,0,0.2)]">
      <div className={`flex h-12 w-12 items-center justify-center rounded-3xl ${theme.bg} ${theme.border}`}>
        <Icon className={`h-5 w-5 ${theme.text}`} />
      </div>
      <div className="mt-4">
        <div className={`text-2xl font-semibold ${theme.text}`}>{value}</div>
        <div className="mt-2 text-sm text-slate-500">{label}</div>
      </div>
    </div>
  );
}

function RepoCard({ repo, onClick }) {
  const [hover, setHover] = useState(false);
  const lc = LANG_COLORS[repo.language] || '#64748b';

  return (
    <button
      type="button"
      onClick={onClick}
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
      className={`group w-full text-left rounded-[2rem] border p-5 transition ${hover ? 'border-cyan-400/30 bg-slate-900/90 shadow-[0_20px_50px_rgba(0,0,0,0.22)]' : 'border-slate-800/80 bg-slate-900/80'}`}
    >
      <div className="flex items-start gap-3">
        <div className="rounded-3xl bg-slate-800/80 p-3 text-slate-400">
          {repo.private ? <Lock className="h-5 w-5" /> : <BookOpen className="h-5 w-5" />}
        </div>
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2 text-sm font-semibold text-slate-100">
            <span className="truncate">{repo.name}</span>
            {repo.private && <span className="rounded-full bg-amber-500/10 px-2 py-1 text-[11px] text-amber-300">Private</span>}
          </div>
          {repo.description && <p className="mt-3 text-sm text-slate-500 line-clamp-2">{repo.description}</p>}
        </div>
      </div>
      <div className="mt-5 flex flex-wrap items-center gap-4 text-sm text-slate-500">
        {repo.language && (
          <span className="inline-flex items-center gap-2">
            <span className="h-2.5 w-2.5 rounded-full" style={{ background: lc }} />
            {repo.language}
          </span>
        )}
        <span className="inline-flex items-center gap-1">
          <Star className="h-4 w-4 text-amber-300" />
          {repo.stargazers_count ?? 0}
        </span>
        <span className="inline-flex items-center gap-1">
          <GitFork className="h-4 w-4 text-slate-500" />
          {repo.forks_count ?? 0}
        </span>
        <span className="ml-auto inline-flex items-center gap-1 text-slate-500">
          <Clock className="h-4 w-4" />
          {timeAgo(repo.updated_at)}
        </span>
      </div>
    </button>
  );
}

const DEMO_REPOS = [
  { id: 1, name: 'auth-service', description: 'Authentication microservice with JWT and OAuth2 support', language: 'Python', stargazers_count: 24, forks_count: 8, updated_at: new Date(Date.now() - 3600000).toISOString(), private: false, owner: { login: 'demo-user' } },
  { id: 2, name: 'api-gateway', description: 'Central API gateway with rate limiting and load balancing', language: 'Go', stargazers_count: 41, forks_count: 12, updated_at: new Date(Date.now() - 86400000).toISOString(), private: false, owner: { login: 'demo-user' } },
  { id: 3, name: 'frontend-app', description: 'React application with TypeScript and Tailwind CSS', language: 'TypeScript', stargazers_count: 18, forks_count: 5, updated_at: new Date(Date.now() - 172800000).toISOString(), private: true, owner: { login: 'demo-user' } },
  { id: 4, name: 'data-pipeline', description: 'ETL pipeline for processing large datasets', language: 'Python', stargazers_count: 7, forks_count: 2, updated_at: new Date(Date.now() - 604800000).toISOString(), private: false, owner: { login: 'demo-user' } },
  { id: 5, name: 'infra-terraform', description: 'Infrastructure as code using Terraform and AWS', language: 'JavaScript', stargazers_count: 15, forks_count: 6, updated_at: new Date(Date.now() - 1209600000).toISOString(), private: true, owner: { login: 'demo-user' } },
  { id: 6, name: 'notification-service', description: 'Email and push notification microservice', language: 'TypeScript', stargazers_count: 9, forks_count: 3, updated_at: new Date(Date.now() - 2592000000).toISOString(), private: false, owner: { login: 'demo-user' } },
];

export default function Dashboard() {
  const { user, githubToken, githubUser } = useAuth();
  const navigate = useNavigate();
  const { repos, loading: reposLoading } = useGitHubRepos(githubToken);
  const { notification, agentTrigger, recentActivity, dismiss } = useCommitWatch(githubToken, repos);

  const displayName = githubUser?.name || githubUser?.login || user?.given_name || user?.name || 'Developer';
  const totalStars = repos.reduce((sum, repo) => sum + (repo.stargazers_count || 0), 0);
  const recentRepos = [...repos].sort((a, b) => new Date(b.updated_at) - new Date(a.updated_at)).slice(0, 6);
  const displayRepos = githubToken ? recentRepos : DEMO_REPOS;
  const portfolioScore = repos.length > 0 ? Math.min(100, Math.round(75 + (repos.filter((repo) => !repo.archived).length / repos.length) * 10 - (repos.filter((repo) => repo.open_issues_count > 10).length / repos.length) * 15)) : null;

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      <header className="sticky top-0 z-20 border-b border-slate-800/80 bg-slate-950/95 backdrop-blur-sm">
        <div className="mx-auto flex max-w-7xl items-center justify-between gap-4 px-6 py-5">
          <div>
            <div className="text-xs uppercase tracking-[0.28em] text-slate-500">CodeCognition AI › Dashboard</div>
            <h1 className="mt-2 text-2xl font-semibold text-slate-100">Dashboard</h1>
          </div>
          <div className="flex items-center gap-3">
            {!githubToken && (
              <button onClick={() => navigate('/settings')} className="rounded-full border border-amber-500/20 bg-amber-500/10 px-4 py-2 text-sm font-semibold text-amber-200 transition hover:bg-amber-500/15">
                Connect GitHub
              </button>
            )}
            <div className="inline-flex items-center gap-2 rounded-full bg-slate-900/80 px-4 py-2 text-sm text-slate-300">
              <span className="inline-flex h-2.5 w-2.5 rounded-full bg-emerald-400" />
              4 agents active
            </div>
            <button className="rounded-full border border-slate-800/80 bg-slate-900/80 p-3 text-slate-400 transition hover:border-cyan-400/40 hover:text-cyan-300">
              <Bell className="h-4 w-4" />
            </button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-6 py-8 space-y-8">
        <section className="rounded-[2rem] border border-slate-800/80 bg-slate-900/95 p-8 shadow-[0_28px_60px_rgba(0,0,0,0.24)]">
          <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
            <div>
              <div className="text-sm uppercase tracking-[0.25em] text-slate-500">Welcome back,</div>
              <h2 className="mt-2 text-3xl font-semibold text-slate-100">{displayName}</h2>
              {!githubToken && (
                <p className="mt-3 text-sm text-slate-400">
                  Demo mode is enabled. <button onClick={() => navigate('/settings')} className="font-semibold text-cyan-300 hover:text-cyan-200">Connect GitHub</button> to view your real repositories.
                </p>
              )}
            </div>
            <div className="grid gap-3 sm:grid-cols-3">
              <div className="rounded-3xl border border-slate-800/80 bg-slate-950/80 px-5 py-4 text-sm text-slate-300">
                <div className="text-xs uppercase tracking-[0.25em] text-slate-500">Repositories</div>
                <div className="mt-2 text-2xl font-semibold text-slate-100">{githubToken ? repos.length || 0 : DEMO_REPOS.length}</div>
              </div>
              <div className="rounded-3xl border border-slate-800/80 bg-slate-950/80 px-5 py-4 text-sm text-slate-300">
                <div className="text-xs uppercase tracking-[0.25em] text-slate-500">Stars</div>
                <div className="mt-2 text-2xl font-semibold text-slate-100">{githubToken ? totalStars : '-'}</div>
              </div>
              <div className="rounded-3xl border border-slate-800/80 bg-slate-950/80 px-5 py-4 text-sm text-slate-300">
                <div className="text-xs uppercase tracking-[0.25em] text-slate-500">Public repos</div>
                <div className="mt-2 text-2xl font-semibold text-slate-100">{githubToken ? repos.filter((repo) => !repo.private).length : '-'}</div>
              </div>
            </div>
          </div>
        </section>

        <section className="grid gap-6 xl:grid-cols-[1.6fr_1fr]">
          <div className="grid gap-6 md:grid-cols-2">
            <StatCard icon={GitFork} label="Repository count" value={githubToken ? repos.length || 0 : DEMO_REPOS.length} color="blue" />
            <StatCard icon={Activity} label="Agent activity" value="4 active" color="green" />
            <StatCard icon={Star} label="Total stars" value={githubToken ? totalStars : '-'} color="yellow" />
            <StatCard icon={Eye} label="Visibility" value={githubToken ? `${repos.filter((repo) => !repo.private).length} public` : '-'} color="primary" />
          </div>

          <div className="grid gap-6">
            <div className="rounded-[2rem] border border-slate-800/80 bg-slate-900/95 p-6 shadow-[0_24px_60px_rgba(0,0,0,0.2)]">
              <HealthGauge score={portfolioScore ?? 0} loading={reposLoading} placeholder={!portfolioScore && !reposLoading} />
            </div>
            <div className="rounded-[2rem] border border-slate-800/80 bg-slate-900/95 p-6 shadow-[0_24px_60px_rgba(0,0,0,0.2)]">
              <CommitActivity notification={notification} agentTrigger={agentTrigger} recentActivity={recentActivity} onDismiss={dismiss} hasToken={Boolean(githubToken)} />
            </div>
          </div>
        </section>

        <section className="space-y-6">
          <div className="flex items-center justify-between gap-4">
            <div>
              <h3 className="text-xl font-semibold text-slate-100">Recent repositories</h3>
              <p className="mt-1 text-sm text-slate-500">Quick access to your most active codebases.</p>
            </div>
            <button onClick={() => navigate('/repositories')} className="inline-flex items-center gap-2 rounded-full border border-slate-800/80 bg-slate-900/80 px-4 py-2 text-sm font-semibold text-cyan-300 transition hover:border-cyan-400/40 hover:text-cyan-200">
              View all
              <ArrowRight className="h-4 w-4" />
            </button>
          </div>

          {reposLoading ? (
            <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
              {Array.from({ length: 6 }).map((_, index) => (
                <div key={index} className="h-44 rounded-[2rem] bg-slate-900/80" />
              ))}
            </div>
          ) : (
            <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
              {displayRepos.map((repo) => (
                <RepoCard key={repo.id} repo={repo} onClick={() => navigate(`/repositories/${repo.owner?.login ?? 'demo-user'}/${repo.name}`)} />
              ))}
            </div>
          )}
        </section>
      </main>
    </div>
  );
}
