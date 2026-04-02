package com.codecognition.service;

import com.codecognition.model.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private static class SecurityPattern {
        String pattern;
        String severity;
        String title;
        String description;
        String fix;

        SecurityPattern(String pattern, String severity, String title, String description, String fix) {
            this.pattern = pattern;
            this.severity = severity;
            this.title = title;
            this.description = description;
            this.fix = fix;
        }
    }

    private static final List<SecurityPattern> SECURITY_PATTERNS = Arrays.asList(
        new SecurityPattern(
            "(?i)(password|secret|api_key|apikey|token|passwd)\\s*=\\s*[\"'][^\"']{4,}[\"']",
            "CRITICAL", "Hardcoded Secret",
            "A credential is hardcoded in source code.",
            "Use env vars: os.environ.get('KEY_NAME')"
        ),
        new SecurityPattern(
            "(?i)(execute|cursor\\.execute)\\s*\\([f\"'].*?(%s|\\{|format)",
            "HIGH", "SQL Injection Risk",
            "User input may be interpolated directly into a SQL query.",
            "Use parameterised queries"
        ),
        new SecurityPattern(
            "\\b(eval|exec)\\s*\\(",
            "HIGH", "Dangerous eval/exec",
            "eval() or exec() can run arbitrary code.",
            "Use ast.literal_eval() for safe data evaluation."
        ),
        new SecurityPattern(
            "(?i)(md5|sha1)\\s*\\(",
            "MEDIUM", "Weak Cryptographic Hash",
            "MD5/SHA-1 are broken for security use.",
            "Use hashlib.sha256() or bcrypt for passwords."
        ),
        new SecurityPattern(
            "(?i)DEBUG\\s*=\\s*True|app\\.run\\s*\\(.*debug\\s*=\\s*True",
            "HIGH", "Debug Mode Enabled",
            "Debug=True exposes stack traces in production.",
            "Set DEBUG=False"
        )
    );

    private static final Map<String, String[]> PYTHON_VULNS = new HashMap<>();
    private static final Map<String, String[]> JS_VULNS = new HashMap<>();

    static {
        PYTHON_VULNS.put("flask", new String[]{"2.3", "CVE-2023-30861", "HIGH"});
        PYTHON_VULNS.put("django", new String[]{"4.2", "CVE-2023-36053", "HIGH"});
        PYTHON_VULNS.put("requests", new String[]{"2.31", "CVE-2023-32681", "MEDIUM"});

        JS_VULNS.put("axios", new String[]{"1.6", "CVE-2023-45857", "HIGH"});
        JS_VULNS.put("lodash", new String[]{"4.17.21", "CVE-2021-23337", "HIGH"});
        JS_VULNS.put("express", new String[]{"4.19", "CVE-2024-29041", "MEDIUM"});
    }

    public AnalysisResult analyzeRepository(RepoAnalyzeRequest req) {
        if (req.is_empty) {
            return getEmptyResult(req);
        }

        List<Finding> findings = new ArrayList<>();
        int findingId[] = {0};

        // Security scan
        scanForSecurityIssues(req.file_context, findings, findingId);

        // Dependency scan
        scanDependencies(req.file_context, findings, findingId);

        // Structural checks
        scanStructure(req, findings, findingId);

        // Calculate scores
        int secScore = calculateSecurityScore(findings);
        int qualScore = calculateQualityScore(req, findings);
        int depScore = calculateDependencyScore(req, findings);
        int docScore = calculateDocScore(req, findings);
        int healthScore = (int) (secScore * 0.35 + qualScore * 0.30 + depScore * 0.20 + docScore * 0.15);

        String status = healthScore >= 80 ? "Healthy" : 
                       healthScore >= 60 ? "Moderate" :
                       healthScore >= 40 ? "Degraded" : "At Risk";

        String summary = generateSummary(req, findings, healthScore);
        List<Map<String, Object>> quantumRisk = generateQuantumRisk(findings, req.tree);
        Map<String, Object> digitalTwin = generateDigitalTwin(findings, req.tree);
        List<Map<String, Object>> agentLogs = generateAgentLogs(findings, healthScore);

        AnalysisResult result = new AnalysisResult();
        result.setHealth_score(healthScore);
        result.setSecurity_score(secScore);
        result.setQuality_score(qualScore);
        result.setDependency_score(depScore);
        result.setDocumentation_score(docScore);
        result.setStatus(status);
        result.setSummary(summary);
        result.setFindings(findings);
        result.setQuantum_risk(quantumRisk);
        result.setDigital_twin(digitalTwin);
        result.setAgent_logs(agentLogs);
        result.set_engine("rule-based");

        return result;
    }

    private void scanForSecurityIssues(String fileContext, List<Finding> findings, int[] findingId) {
        if (fileContext == null || fileContext.isEmpty()) return;

        String[] sections = fileContext.split("## ");
        for (String section : sections) {
            if (section.trim().isEmpty()) continue;
            
            String[] lines = section.split("\n");
            String fileName = lines.length > 0 ? lines[0] : "unknown";
            String content = section.substring(fileName.length());

            for (SecurityPattern sp : SECURITY_PATTERNS) {
                Pattern p = Pattern.compile(sp.pattern, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(content);
                if (m.find()) {
                    Finding f = new Finding();
                    f.setId("f" + (++findingId[0]));
                    f.setSeverity(sp.severity);
                    f.setCategory("Security");
                    f.setTitle(sp.title);
                    f.setDescription(sp.description + " Found in " + fileName);
                    f.setFile(fileName);
                    f.setFix(sp.fix);
                    findings.add(f);
                }
            }
        }
    }

    private void scanDependencies(String fileContext, List<Finding> findings, int[] findingId) {
        if (fileContext == null) return;

        // Scan Python dependencies
        if (fileContext.contains("requirements.txt")) {
            PYTHON_VULNS.forEach((pkg, info) -> {
                Pattern p = Pattern.compile("^" + pkg + "\\s*[=<>!]+\\s*([\\d.]+)", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(fileContext);
                if (m.find()) {
                    String ver = m.group(1);
                    if (!ver.startsWith(info[0])) {
                        Finding f = new Finding();
                        f.setId("f" + (++findingId[0]));
                        f.setSeverity(info[2]);
                        f.setCategory("Dependency");
                        f.setTitle("Vulnerable: " + pkg + "==" + ver);
                        f.setDescription(pkg + "==" + ver + " has security issues (" + info[1] + ")");
                        f.setFile("requirements.txt");
                        f.setFix("Upgrade: " + pkg + ">=" + info[0]);
                        findings.add(f);
                    }
                }
            });
        }

        // Scan JS dependencies
        if (fileContext.contains("package.json")) {
            JS_VULNS.forEach((pkg, info) -> {
                Pattern p = Pattern.compile("\"" + pkg + "\"\\s*:\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(fileContext);
                if (m.find()) {
                    Finding f = new Finding();
                    f.setId("f" + (++findingId[0]));
                    f.setSeverity(info[2]);
                    f.setCategory("Dependency");
                    f.setTitle("Vulnerable npm: " + pkg);
                    f.setDescription(pkg + " has known issues (" + info[1] + ")");
                    f.setFile("package.json");
                    f.setFix("npm install " + pkg + "@latest");
                    findings.add(f);
                }
            });
        }
    }

    private void scanStructure(RepoAnalyzeRequest req, List<Finding> findings, int[] findingId) {
        boolean hasTests = req.is_empty || req.is_empty == false && req.has_tests;
        boolean hasCI = req.has_ci;
        boolean hasReadme = req.has_readme;

        if (!hasTests && !req.tree.isEmpty()) {
            Finding f = new Finding();
            f.setId("f" + (++findingId[0]));
            f.setSeverity("MEDIUM");
            f.setCategory("Quality");
            f.setTitle("No Automated Tests Detected");
            f.setDescription(req.owner + "/" + req.repo + " has no test files");
            f.setFile("Repository root");
            f.setFix("Create a tests/ directory");
            findings.add(f);
        }

        if (!hasCI && !req.tree.isEmpty()) {
            Finding f = new Finding();
            f.setId("f" + (++findingId[0]));
            f.setSeverity("LOW");
            f.setCategory("Quality");
            f.setTitle("No CI/CD Pipeline Found");
            f.setDescription("No GitHub Actions detected");
            f.setFile("Repository root");
            f.setFix("Add .github/workflows/ci.yml");
            findings.add(f);
        }

        if (!hasReadme && !req.tree.isEmpty()) {
            Finding f = new Finding();
            f.setId("f" + (++findingId[0]));
            f.setSeverity("LOW");
            f.setCategory("Documentation");
            f.setTitle("Missing README Documentation");
            f.setDescription("No README found");
            f.setFile("Repository root");
            f.setFix("Create README.md");
            findings.add(f);
        }
    }

    private int calculateSecurityScore(List<Finding> findings) {
        int deduction = 0;
        for (Finding f : findings) {
            if ("Security".equals(f.getCategory())) {
                switch (f.getSeverity()) {
                    case "CRITICAL": deduction += 20; break;
                    case "HIGH": deduction += 12; break;
                    case "MEDIUM": deduction += 6; break;
                    case "LOW": deduction += 2; break;
                }
            }
        }
        return Math.max(0, Math.min(100, 100 - deduction));
    }

    private int calculateQualityScore(RepoAnalyzeRequest req, List<Finding> findings) {
        int base = req.has_tests ? 80 : 55;
        int deduction = 0;
        for (Finding f : findings) {
            if ("Quality".equals(f.getCategory())) {
                switch (f.getSeverity()) {
                    case "CRITICAL": deduction += 20; break;
                    case "HIGH": deduction += 12; break;
                    case "MEDIUM": deduction += 6; break;
                    case "LOW": deduction += 2; break;
                }
            }
        }
        return Math.max(0, Math.min(100, base - deduction));
    }

    private int calculateDependencyScore(RepoAnalyzeRequest req, List<Finding> findings) {
        int base = req.has_ci ? 85 : 75;
        int deduction = 0;
        for (Finding f : findings) {
            if ("Dependency".equals(f.getCategory())) {
                switch (f.getSeverity()) {
                    case "CRITICAL": deduction += 20; break;
                    case "HIGH": deduction += 12; break;
                    case "MEDIUM": deduction += 6; break;
                    case "LOW": deduction += 2; break;
                }
            }
        }
        return Math.max(0, Math.min(100, base - deduction));
    }

    private int calculateDocScore(RepoAnalyzeRequest req, List<Finding> findings) {
        int base = req.has_readme ? 70 : 45;
        int deduction = 0;
        for (Finding f : findings) {
            if ("Documentation".equals(f.getCategory())) {
                switch (f.getSeverity()) {
                    case "CRITICAL": deduction += 20; break;
                    case "HIGH": deduction += 12; break;
                    case "MEDIUM": deduction += 6; break;
                    case "LOW": deduction += 2; break;
                }
            }
        }
        return Math.max(0, Math.min(100, base - deduction));
    }

    private String generateSummary(RepoAnalyzeRequest req, List<Finding> findings, int healthScore) {
        int total = findings.size();
        int critical = (int) findings.stream().filter(f -> "CRITICAL".equals(f.getSeverity())).count();
        int high = (int) findings.stream().filter(f -> "HIGH".equals(f.getSeverity())).count();

        if (total == 0) {
            return req.owner + "/" + req.repo + " passed all automated checks with no issues detected.";
        }
        return req.owner + "/" + req.repo + " has " + total + " issues (" + critical + " critical, " + high + " high). Health: " + healthScore + "/100.";
    }

    private List<Map<String, Object>> generateQuantumRisk(List<Finding> findings, List<String> tree) {
        Map<String, Double> riskMap = new HashMap<>();
        for (Finding f : findings) {
            String file = f.getFile().split(":")[0];
            double weight = 0;
            switch (f.getSeverity()) {
                case "CRITICAL": weight = 0.4; break;
                case "HIGH": weight = 0.25; break;
                case "MEDIUM": weight = 0.15; break;
                case "LOW": weight = 0.05; break;
            }
            riskMap.put(file, Math.min(1.0, riskMap.getOrDefault(file, 0.1) + weight));
        }

        List<Map<String, Object>> result = new ArrayList<>();
        riskMap.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(8)
            .forEach(entry -> {
                Map<String, Object> m = new HashMap<>();
                m.put("file", entry.getKey());
                m.put("risk_score", Math.round(entry.getValue() * 1000) / 1000.0);
                m.put("risk_level", entry.getValue() >= 0.5 ? "HIGH" : entry.getValue() >= 0.25 ? "MEDIUM" : "LOW");
                m.put("factors", Arrays.asList("Code issue detected"));
                result.add(m);
            });

        return result;
    }

    private Map<String, Object> generateDigitalTwin(List<Finding> findings, List<String> tree) {
        List<String> criticalFiles = findings.stream()
            .filter(f -> "CRITICAL".equals(f.getSeverity()) || "HIGH".equals(f.getSeverity()))
            .map(f -> f.getFile().split(":")[0])
            .distinct()
            .limit(5)
            .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("critical_files", criticalFiles);
        result.put("dependency_map", new HashMap<>());
        result.put("impact_summary", "Analysis complete");
        return result;
    }

    private List<Map<String, Object>> generateAgentLogs(List<Finding> findings, int healthScore) {
        List<Map<String, Object>> logs = new ArrayList<>();

        Map<String, Object> log1 = new HashMap<>();
        log1.put("agent", "Orchestrator");
        log1.put("msg", "Scanning repository...");
        log1.put("status", "running");
        logs.add(log1);

        Map<String, Object> log2 = new HashMap<>();
        log2.put("agent", "Security Scout");
        log2.put("msg", "Running security pattern checks...");
        log2.put("status", "running");
        logs.add(log2);

        Map<String, Object> log3 = new HashMap<>();
        log3.put("agent", "Quality Architect");
        log3.put("msg", "Analysing code structure and tests...");
        log3.put("status", "running");
        logs.add(log3);

        Map<String, Object> logFinal = new HashMap<>();
        logFinal.put("agent", "Orchestrator");
        logFinal.put("msg", "Analysis complete. Health Score: " + healthScore + "/100");
        logFinal.put("status", "success");
        logs.add(logFinal);

        return logs;
    }

    private AnalysisResult getEmptyResult(RepoAnalyzeRequest req) {
        AnalysisResult result = new AnalysisResult();
        result.setHealth_score(100);
        result.setSecurity_score(100);
        result.setQuality_score(100);
        result.setDependency_score(100);
        result.setDocumentation_score(req.has_readme ? 70 : 20);
        result.setStatus("Healthy");
        result.setSummary(req.owner + "/" + req.repo + " is empty — no source files to analyse.");
        result.setFindings(new ArrayList<>());
        result.setQuantum_risk(new ArrayList<>());

        Map<String, Object> dt = new HashMap<>();
        dt.put("critical_files", new ArrayList<>());
        dt.put("dependency_map", new HashMap<>());
        dt.put("impact_summary", "No files yet.");
        result.setDigital_twin(dt);

        List<Map<String, Object>> logs = new ArrayList<>();
        Map<String, Object> log = new HashMap<>();
        log.put("agent", "Orchestrator");
        log.put("msg", "Repository appears to be empty.");
        log.put("status", "warn");
        logs.add(log);
        result.setAgent_logs(logs);
        result.set_engine("empty-repo");

        return result;
    }
}
