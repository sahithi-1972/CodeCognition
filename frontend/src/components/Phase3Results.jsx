import { motion } from 'framer-motion';
import { Shield, Code, Package, BookOpen, AlertCircle, CheckCircle, AlertTriangle, Info } from 'lucide-react';

const panelV = { hidden: { opacity: 0, y: 12 }, show: { opacity: 1, y: 0 } };

function ScoreCard({ label, score, color, icon: Icon }) {
  const percentage = Math.round((score / 100) * 100);
  const bgColor = score >= 80 ? '#10b981' : score >= 60 ? '#f59e0b' : '#ef4444';

  return (
    <motion.div variants={panelV} className="flex-1 p-4 rounded-lg bg-[#0f1824] border border-[#1a2c4a] hover:border-[#2a3c5a] transition-colors">
      <div className="flex items-center gap-2 mb-2">
        <Icon className="w-4 h-4" style={{ color: bgColor }} />
        <span className="text-xs font-mono text-slate-400">{label}</span>
      </div>
      <div className="text-2xl font-bold text-slate-100 mb-2">{Math.round(score)}</div>
      <div className="w-full bg-[#051220] rounded-full h-2">
        <div
          className="h-2 rounded-full transition-all duration-500"
          style={{ width: `${percentage}%`, background: bgColor }}
        />
      </div>
    </motion.div>
  );
}

function FindingRow({ finding }) {
  const severityColors = {
    CRITICAL: { bg: '#7f1d1d', border: '#dc2626', text: '#fca5a5', icon: '#ef4444' },
    HIGH: { bg: '#7c2d12', border: '#ea580c', text: '#fdba74', icon: '#f97316' },
    MEDIUM: { bg: '#78350f', border: '#d97706', text: '#fcd34d', icon: '#f59e0b' },
    LOW: { bg: '#1e3a8a', border: '#3b82f6', text: '#93c5fd', icon: '#60a5fa' },
  };

  const severity = finding.severity?.toUpperCase() || 'INFO';
  const colors = severityColors[severity] || severityColors.LOW;

  return (
    <motion.div
      variants={panelV}
      className="p-3 rounded-lg border transition-all hover:border-slate-500"
      style={{ background: colors.bg, borderColor: colors.border }}
    >
      <div className="flex items-start gap-3">
        <div style={{ color: colors.icon }} className="mt-0.5">
          {severity === 'CRITICAL' || severity === 'HIGH' ? (
            <AlertTriangle className="w-4 h-4" />
          ) : severity === 'MEDIUM' ? (
            <AlertCircle className="w-4 h-4" />
          ) : (
            <Info className="w-4 h-4" />
          )}
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 mb-1">
            <h3 className="text-sm font-semibold" style={{ color: colors.text }}>
              {finding.title}
            </h3>
            <span className="text-[10px] font-mono px-1.5 py-0.5 rounded" style={{ background: colors.bg, borderColor: colors.border, color: colors.text, border: `1px solid ${colors.border}` }}>
              {severity}
            </span>
          </div>
          {finding.description && (
            <p className="text-xs text-slate-400 mb-2">{finding.description}</p>
          )}
          {finding.fix && (
            <div className="text-xs bg-[#051220] rounded p-2 font-mono text-slate-300 border border-[#1a2c4a]">
              💡 Fix: {finding.fix}
            </div>
          )}
          {finding.file && (
            <div className="text-xs text-slate-500 mt-2">
              📄 File: <span className="text-cyan-400">{finding.file}</span>
            </div>
          )}
        </div>
      </div>
    </motion.div>
  );
}

export default function Phase3ResultsPanel({ result, loading }) {
  if (loading) {
    return (
      <div className="p-8 text-center">
        <div className="w-6 h-6 border-2 border-cyan-400/30 border-t-cyan-400 rounded-full animate-spin mx-auto mb-3" />
        <div className="text-sm font-mono text-slate-500">Analyzing repository...</div>
        <div className="text-xs text-slate-600 mt-2">Fetching files from GitHub and running analysis</div>
      </div>
    );
  }

  if (!result) return null;

  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="space-y-6">
      {/* Status */}
      {result.status === 'success' && (
        <div className="p-3 rounded-lg bg-green-500/10 border border-green-500/20 text-green-400 text-xs font-mono flex items-center gap-2">
          <CheckCircle className="w-4 h-4" />
          Analysis completed successfully
        </div>
      )}

      {/* Scores Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-3">
        <motion.div initial="hidden" animate="show" variants={{ show: { transition: { staggerChildren: 0.05 } } }}>
          <ScoreCard label="Health Score" score={result.health_score || 0} color="#3b82f6" icon={Code} />
        </motion.div>
        <motion.div initial="hidden" animate="show" variants={{ show: { transition: { staggerChildren: 0.05 } } }}>
          <ScoreCard label="Security" score={result.security_score || 0} color="#ef4444" icon={Shield} />
        </motion.div>
        <motion.div initial="hidden" animate="show" variants={{ show: { transition: { staggerChildren: 0.05 } } }}>
          <ScoreCard label="Quality" score={result.quality_score || 0} color="#f59e0b" icon={Code} />
        </motion.div>
        <motion.div initial="hidden" animate="show" variants={{ show: { transition: { staggerChildren: 0.05 } } }}>
          <ScoreCard label="Dependency" score={result.dependency_score || 0} color="#8b5cf6" icon={Package} />
        </motion.div>
        <motion.div initial="hidden" animate="show" variants={{ show: { transition: { staggerChildren: 0.05 } } }}>
          <ScoreCard label="Documentation" score={result.documentation_score || 0} color="#06b6d4" icon={BookOpen} />
        </motion.div>
      </div>

      {/* Summary */}
      {result.summary && (
        <motion.div variants={panelV} className="p-4 rounded-lg bg-[#0f1824] border border-[#1a2c4a]">
          <h3 className="text-sm font-semibold text-slate-100 mb-2">Summary</h3>
          <p className="text-sm text-slate-400 leading-relaxed">{result.summary}</p>
        </motion.div>
      )}

      {/* Findings */}
      {result.findings && result.findings.length > 0 && (
        <motion.div variants={panelV} className="space-y-3">
          <div className="flex items-center gap-2 mb-3">
            <AlertTriangle className="w-4 h-4 text-amber-500" />
            <h3 className="text-sm font-semibold text-slate-100">
              Findings ({result.findings_count || result.findings.length})
            </h3>
          </div>
          <motion.div className="space-y-2" initial="hidden" animate="show" variants={{ show: { transition: { staggerChildren: 0.05 } } }}>
            {result.findings.map((finding, idx) => (
              <FindingRow key={idx} finding={finding} />
            ))}
          </motion.div>
        </motion.div>
      )}

      {/* No Findings */}
      {result.findings && result.findings.length === 0 && (
        <motion.div variants={panelV} className="p-4 rounded-lg bg-green-500/10 border border-green-500/20 text-green-400 text-sm text-center">
          ✓ No critical issues found!
        </motion.div>
      )}
    </motion.div>
  );
}
