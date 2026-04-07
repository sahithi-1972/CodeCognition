package com.codecognition.service;

import com.codecognition.model.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

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

        // Code quality scan
        scanCodeQuality(req.file_context, req.tree, findings, findingId);

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
        result.health_score = healthScore;
        result.security_score = secScore;
        result.quality_score = qualScore;
        result.dependency_score = depScore;
        result.documentation_score = docScore;
        result.status = status;
        result.summary = summary;
        result.findings = findings;
        
        // Convert to JSON strings for database storage
        try {
            ObjectMapper mapper = new ObjectMapper();
            result.quantum_risk_json = mapper.writeValueAsString(quantumRisk);
            result.digital_twin_json = mapper.writeValueAsString(digitalTwin);
            result.agent_logs_json = mapper.writeValueAsString(agentLogs);
        } catch (Exception e) {
            result.quantum_risk_json = "[]";
            result.digital_twin_json = "{}";
            result.agent_logs_json = "[]";
        }
        
        result._engine = "rule-based";

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
                    f.id = "f" + (++findingId[0]);
                    f.severity = sp.severity;
                    f.category = "Security";
                    f.title = sp.title;
                    f.description = sp.description + " Found in " + fileName;
                    f.file = fileName;
                    f.fix = sp.fix;
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
                        f.id = "f" + (++findingId[0]);
                        f.severity = info[2];
                        f.category = "Dependency";
                        f.title = "Vulnerable: " + pkg + "==" + ver;
                        f.description = pkg + "==" + ver + " has security issues (" + info[1] + ")";
                        f.file = "requirements.txt";
                        f.fix = "Upgrade: " + pkg + ">=" + info[0];
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
                    f.id = "f" + (++findingId[0]);
                    f.severity = info[2];
                    f.category = "Dependency";
                    f.title = "Vulnerable npm: " + pkg;
                    f.description = pkg + " has known issues (" + info[1] + ")";
                    f.file = "package.json";
                    f.fix = "npm install " + pkg + "@latest";
                    findings.add(f);
                }
            });
        }
    }

    private void scanStructure(RepoAnalyzeRequest req, List<Finding> findings, int[] findingId) {
        boolean hasTests = !req.is_empty && req.has_tests;
        boolean hasCI = req.has_ci;
        boolean hasReadme = req.has_readme;

        if (!hasTests && !req.tree.isEmpty()) {
            Finding f = new Finding();
            f.id = "f" + (++findingId[0]);
            f.severity = "MEDIUM";
            f.category = "Quality";
            f.title = "No Automated Tests Detected";
            f.description = req.owner + "/" + req.repo + " has no test files";
            f.file = "Repository root";
            f.fix = "Create a tests/ directory";
            findings.add(f);
        }

        if (!hasCI && !req.tree.isEmpty()) {
            Finding f = new Finding();
            f.id = "f" + (++findingId[0]);
            f.severity = "LOW";
            f.category = "Quality";
            f.title = "No CI/CD Pipeline Found";
            f.description = "No GitHub Actions detected";
            f.file = "Repository root";
            f.fix = "Add .github/workflows/ci.yml";
            findings.add(f);
        }

        if (!hasReadme && !req.tree.isEmpty()) {
            Finding f = new Finding();
            f.id = "f" + (++findingId[0]);
            f.severity = "LOW";
            f.category = "Documentation";
            f.title = "Missing README Documentation";
            f.description = "No README found";
            f.file = "Repository root";
            f.fix = "Create README.md";
            findings.add(f);
        }
    }

    private void scanCodeQuality(String fileContext, List<String> files, List<Finding> findings, int[] findingId) {
        if (fileContext == null || fileContext.isEmpty()) return;

        // Check for large functions
        Pattern largeFunction = Pattern.compile("(?:function|def|async function|const\\s+\\w+\\s*=\\s*(?:async\\s*)?\\(|public\\s+\\w+\\s+\\w+\\s*\\().*?(?=(?:function|def|async function|const|public|private|protected|class|interface|}))", Pattern.DOTALL);
        Matcher m = largeFunction.matcher(fileContext);
        int functionCount = 0;
        while (m.find() && functionCount < 5) {
            String func = m.group();
            if (func.split("\n").length > 50) {
                Finding f = new Finding();
                f.id = "f" + (++findingId[0]);
                f.severity = "LOW";
                f.category = "Quality";
                f.title = "Long Function/Method Detected";
                f.description = "Functions should be kept below 50 lines for readability";
                f.file = "Multiple files";
                f.fix = "Refactor long functions into smaller, single-purpose functions";
                findings.add(f);
                break;
            }
            functionCount++;
        }

        // Check for code comments
        boolean hasComments = fileContext.contains("//") || fileContext.contains("/*") || fileContext.contains("#");
        if (!hasComments && !fileContext.isEmpty()) {
            Finding f = new Finding();
            f.id = "f" + (++findingId[0]);
            f.severity = "LOW";
            f.category = "Documentation";
            f.title = "Missing Code Comments";
            f.description = "Code lacks proper documentation and comments";
            f.file = "Source files";
            f.fix = "Add JSDoc/docstring comments for functions and complex logic";
            findings.add(f);
        }

        // Check for type definitions (TypeScript, Python type hints)
        boolean hasTypeInfo = fileContext.contains(": string") || fileContext.contains(": number") || 
                             fileContext.contains(": boolean") || fileContext.contains("-> ") ||
                             fileContext.contains(": List") || fileContext.contains(": Dict");
        if (!hasTypeInfo && fileContext.length() > 500) {
            Finding f = new Finding();
            f.id = "f" + (++findingId[0]);
            f.severity = "LOW";
            f.category = "Quality";
            f.title = "Missing Type Annotations";
            f.description = "Code lacks type safety annotations";
            f.file = "Source files";
            f.fix = "Add TypeScript types or Python type hints for better type safety";
            findings.add(f);
        }

        // Check for error handling
        boolean hasTryCatch = fileContext.contains("try {") || fileContext.contains("try:") || 
                             fileContext.contains("catch") || fileContext.contains("except");
        if (!hasTryCatch && fileContext.length() > 1000) {
            Finding f = new Finding();
            f.id = "f" + (++findingId[0]);
            f.severity = "MEDIUM";
            f.category = "Quality";
            f.title = "Insufficient Error Handling";
            f.description = "Code may lack proper try-catch/exception handling";
            f.file = "Source files";
            f.fix = "Add proper error handling with try-catch blocks or exception handlers";
            findings.add(f);
        }
    }

    private int calculateSecurityScore(List<Finding> findings) {
        int deduction = 0;
        for (Finding f : findings) {
            if ("Security".equals(f.category)) {
                switch (f.severity) {
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
            if ("Quality".equals(f.category)) {
                switch (f.severity) {
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
            if ("Dependency".equals(f.category)) {
                switch (f.severity) {
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
            if ("Documentation".equals(f.category)) {
                switch (f.severity) {
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
        int critical = (int) findings.stream().filter(f -> "CRITICAL".equals(f.severity)).count();
        int high = (int) findings.stream().filter(f -> "HIGH".equals(f.severity)).count();

        if (total == 0) {
            return req.owner + "/" + req.repo + " passed all automated checks with no issues detected.";
        }
        return req.owner + "/" + req.repo + " has " + total + " issues (" + critical + " critical, " + high + " high). Health: " + healthScore + "/100.";
    }

    private List<Map<String, Object>> generateQuantumRisk(List<Finding> findings, List<String> tree) {
        Map<String, Double> riskMap = new HashMap<>();
        for (Finding f : findings) {
            String file = f.file.split(":")[0];
            double weight = 0;
            switch (f.severity) {
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
            .filter(f -> "CRITICAL".equals(f.severity) || "HIGH".equals(f.severity))
            .map(f -> f.file.split(":")[0])
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
        result.health_score = 100;
        result.security_score = 100;
        result.quality_score = 100;
        result.dependency_score = 100;
        result.documentation_score = req.has_readme ? 70 : 20;
        result.status = "Healthy";
        result.summary = req.owner + "/" + req.repo + " is empty — no source files to analyse.";
        result.findings = new ArrayList<>();
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            result.quantum_risk_json = mapper.writeValueAsString(new ArrayList<>());
            
            Map<String, Object> dt = new HashMap<>();
            dt.put("critical_files", new ArrayList<>());
            dt.put("dependency_map", new HashMap<>());
            dt.put("impact_summary", "No files yet.");
            result.digital_twin_json = mapper.writeValueAsString(dt);
            
            List<Map<String, Object>> logs = new ArrayList<>();
            Map<String, Object> log = new HashMap<>();
            log.put("agent", "Orchestrator");
            log.put("msg", "Repository appears to be empty.");
            log.put("status", "warn");
            logs.add(log);
            result.agent_logs_json = mapper.writeValueAsString(logs);
        } catch (Exception e) {
            result.quantum_risk_json = "[]";
            result.digital_twin_json = "{}";
            result.agent_logs_json = "[]";
        }
        
        result._engine = "empty-repo";

        return result;
    }
}
