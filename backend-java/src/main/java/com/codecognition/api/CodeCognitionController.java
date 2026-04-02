package com.codecognition.api;

import com.codecognition.model.*;
import com.codecognition.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class CodeCognitionController {

    @Autowired
    private AnalysisService analysisService;

    private Map<String, AnalysisResult> cache = new HashMap<>();

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "online");
        response.put("service", "CodeCognition AI v3.3 (Java)");
        response.put("ts", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, String>> analyze(@RequestBody AnalyzeRequest req) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "queued");
        response.put("repo", req.repo_url);
        // In a real app, this would be background
        return ResponseEntity.ok(response);
    }

    @PostMapping("/analyze-repo")
    public ResponseEntity<AnalysisResult> analyzeRepo(@RequestBody RepoAnalyzeRequest req) {
        // Handle legacy repo_name field
        if (req.repo_name != null && "unknown".equals(req.owner)) {
            String[] parts = req.repo_name.split("/");
            req.owner = parts.length >= 2 ? parts[0] : "unknown";
            req.repo = parts.length >= 2 ? parts[1] : req.repo_name;
        }

        AnalysisResult result = analysisService.analyzeRepository(req);
        cache.put(req.owner + "/" + req.repo, result);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health-status")
    public ResponseEntity<Map<String, Object>> getHealthStatus(
            @RequestParam(required = false) String repo_url) {
        
        Map<String, Object> response = new HashMap<>();

        if (repo_url != null && cache.containsKey(repo_url)) {
            AnalysisResult r = cache.get(repo_url);
            response.put("status", "complete");
            response.put("repo", repo_url);
            response.put("health_score", r.health_score);
            response.put("findings", r.findings);
            response.put("summary", r.summary);
            response.put("agent_logs", r.agent_logs);
            response.put("grade", gradeScore(r.health_score));
            response.put("last_updated", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        } else {
            response.put("status", "no_analysis");
            response.put("repo", repo_url);
            response.put("health_score", null);
            response.put("findings", new ArrayList<>());
            response.put("message", "Not yet analysed.");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/simulation")
    public ResponseEntity<Map<String, Object>> runSimulation(@RequestBody SimulationRequest req) {
        Map<String, Object> response = new HashMap<>();
        response.put("changed_file", req.changed_file);
        response.put("direct_impact", new ArrayList<>());
        response.put("indirect_impact", new ArrayList<>());
        response.put("safe_files", new ArrayList<>());
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_files", 0);
        stats.put("affected_count", 0);
        stats.put("safe_count", 0);
        response.put("stats", stats);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/quantum-risk")
    public ResponseEntity<Map<String, Object>> getQuantumRisk(
            @RequestParam(required = false) String repo_url) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("repo", repo_url != null ? repo_url : "demo");
        response.put("computed_at", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        response.put("profiles", new ArrayList<>());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/agent-logs")
    public ResponseEntity<List<Map<String, Object>>> streamLogs() {
        List<Map<String, Object>> logs = new ArrayList<>();
        Map<String, Object> log = new HashMap<>();
        log.put("agent", "Orchestrator");
        log.put("msg", "Stream started");
        log.put("status", "running");
        logs.add(log);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/simulation/files")
    public ResponseEntity<Map<String, Object>> listFiles() {
        Map<String, Object> response = new HashMap<>();
        response.put("files", new ArrayList<>());
        return ResponseEntity.ok(response);
    }

    private String gradeScore(int score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 55) return "D";
        return "F";
    }
}
