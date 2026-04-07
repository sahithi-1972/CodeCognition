package com.codecognition.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "analysis_results")
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "repo_url", unique = true)
    public String repo_url;

    public int health_score;
    public int security_score;
    public int quality_score;
    public int dependency_score;
    public int documentation_score;
    public String status;  // Healthy, Moderate, Degraded, At Risk
    
    @Column(columnDefinition = "LONGTEXT")
    public String summary;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "analysis_result_id")
    public List<Finding> findings;
    
    @Column(columnDefinition = "LONGTEXT")
    public String quantum_risk_json;
    
    @Column(columnDefinition = "LONGTEXT")
    public String digital_twin_json;
    
    @Column(columnDefinition = "LONGTEXT")
    public String agent_logs_json;
    
    public String _engine;  // "rule-based", "claude-ai", "empty-repo"
    
    @Column(name = "created_at")
    public LocalDateTime created_at;
    
    @Column(name = "updated_at")
    public LocalDateTime updated_at;
    
    @PrePersist
    public void prePersist() {
        created_at = LocalDateTime.now();
        updated_at = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        updated_at = LocalDateTime.now();
    }
}
