package com.codecognition.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {
    private int health_score;
    private int security_score;
    private int quality_score;
    private int dependency_score;
    private int documentation_score;
    private String status;  // Healthy, Moderate, Degraded, At Risk
    private String summary;
    private List<Finding> findings;
    private List<Map<String, Object>> quantum_risk;
    private Map<String, Object> digital_twin;
    private List<Map<String, Object>> agent_logs;
    private String _engine;  // "rule-based", "claude-ai", "empty-repo"
}
