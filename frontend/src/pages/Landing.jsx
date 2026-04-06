import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { Shield, Zap, Eye, Lock } from 'lucide-react';

const features = [
  {
    icon: Shield,
    title: 'Continuous vulnerability monitoring',
    description: 'Proactively surface risks across source code, dependencies, and CI pipelines.',
  },
  {
    icon: Zap,
    title: 'AI-powered priority triage',
    description: 'Focus on the right findings with automated severity scoring and remediation guidance.',
  },
  {
    icon: Eye,
    title: 'Single-pane repo visibility',
    description: 'See health, activity, and agent updates across all repositories in one dashboard.',
  },
  {
    icon: Lock,
    title: 'Secure access & audit readiness',
    description: 'Manage GitHub integration and access settings with enterprise-ready controls.',
  },
];

export default function Landing() {
  return (
    <div className="min-h-screen bg-[radial-gradient(circle_at_top_left,_rgba(111,164,255,0.14),_transparent_24%),linear-gradient(180deg,#071622_0%,#081a2a_40%,#0d1f33_100%)] text-slate-100">
      <div className="mx-auto max-w-7xl px-6 py-12 sm:px-8 lg:px-10">
        <div className="grid gap-12 lg:grid-cols-[1.4fr_1fr] lg:items-center">
          <motion.div initial={{ opacity: 0, y: 18 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.7 }}>
            <div className="inline-flex items-center gap-2 rounded-full border border-cyan-500/30 bg-cyan-500/10 px-4 py-2 text-sm font-semibold uppercase tracking-[0.24em] text-cyan-200">
              CodeCognition
            </div>
            <h1 className="mt-8 text-4xl font-bold leading-tight tracking-tight sm:text-5xl">
              Professional repository intelligence for secure engineering teams.
            </h1>
            <p className="mt-6 max-w-2xl text-sm text-slate-300 sm:text-base">
              Drive faster, safer releases with an intelligent dashboard that blends vulnerability detection, code health, and GitHub insights in one polished workspace.
            </p>
            <div className="mt-8 flex flex-col gap-4 sm:flex-row">
              <Link to="/login" className="inline-flex items-center justify-center rounded-full bg-cyan-500 px-6 py-3 text-sm font-semibold text-slate-950 shadow-lg shadow-cyan-500/20 transition hover:bg-cyan-400">
                Get started
              </Link>
              <Link to="/dashboard" className="inline-flex items-center justify-center rounded-full border border-slate-700 bg-slate-950/70 px-6 py-3 text-sm font-semibold text-slate-100 transition hover:border-cyan-400/40 hover:bg-slate-900/90">
                Explore demo
              </Link>
            </div>
          </motion.div>

          <motion.div initial={{ opacity: 0, x: 18 }} animate={{ opacity: 1, x: 0 }} transition={{ duration: 0.7, delay: 0.1 }} className="rounded-[2rem] border border-slate-800/90 bg-slate-950/80 p-8 shadow-[0_40px_80px_rgba(3,21,45,0.35)]">
            <div className="flex items-center justify-between gap-4 rounded-3xl bg-slate-900/80 p-5">
              <div>
                <div className="text-xs uppercase tracking-[0.3em] text-slate-500">CodeCognition AI</div>
                <div className="mt-2 text-2xl font-semibold text-slate-100">Agency Suite</div>
              </div>
              <div className="rounded-2xl bg-cyan-500/10 px-3 py-2 text-sm text-cyan-200">Live</div>
            </div>
            <div className="mt-7 space-y-5">
              <div className="rounded-3xl border border-slate-800/90 bg-slate-900/80 p-5">
                <div className="text-sm text-slate-500">Portfolio health</div>
                <div className="mt-3 text-4xl font-semibold text-slate-100">92%</div>
                <div className="mt-2 text-sm text-slate-500">Security risk score across monitored repos.</div>
              </div>
              <div className="grid gap-4 sm:grid-cols-2">
                <div className="rounded-3xl border border-slate-800/90 bg-slate-900/80 p-5">
                  <div className="text-sm text-slate-500">Active agents</div>
                  <div className="mt-3 text-3xl font-semibold text-cyan-300">4</div>
                </div>
                <div className="rounded-3xl border border-slate-800/90 bg-slate-900/80 p-5">
                  <div className="text-sm text-slate-500">Repos monitored</div>
                  <div className="mt-3 text-3xl font-semibold text-slate-100">18</div>
                </div>
              </div>
            </div>
          </motion.div>
        </div>

        <section className="mt-20 grid gap-6 lg:grid-cols-2 xl:grid-cols-4">
          {features.map(({ icon: Icon, title, description }) => (
            <motion.div key={title} initial={{ opacity: 0, y: 16 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ duration: 0.55 }} className="rounded-[2rem] border border-slate-800/90 bg-slate-950/85 p-6 shadow-[0_18px_40px_rgba(3,21,45,0.24)]">
              <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-cyan-500/10 text-cyan-300">
                <Icon className="h-5 w-5" />
              </div>
              <h3 className="mt-5 text-lg font-semibold text-slate-100">{title}</h3>
              <p className="mt-3 text-sm leading-6 text-slate-400">{description}</p>
            </motion.div>
          ))}
        </section>
      </div>
    </div>
  );
}
