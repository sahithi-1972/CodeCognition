package com.codecognition.model;

import java.util.List;
import java.util.Map;

public class AnalysisResult {
    public int health_score;
    public int security_score;
    public int quality_score;
    public int dependency_score;
    public int documentation_score;
    public String status;  // Healthy, Moderate, Degraded, At Risk
    public String summary;
    public List<Finding> findings;
    public List<Map<String, Object>> quantum_risk;
    public Map<String, Object> digital_twin;
    public List<Map<String, Object>> agent_logs;
    public String _engine;  // "rule-based", "claude-ai", "empty-repo"
}
