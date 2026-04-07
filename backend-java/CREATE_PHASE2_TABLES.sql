-- Phase 2: GitHub Integration Tables
-- This script creates tables needed for repository management and code analysis

USE codecognition_db;

-- Create repositories table
CREATE TABLE IF NOT EXISTS repositories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    owner VARCHAR(255) NOT NULL,
    description TEXT,
    is_private BOOLEAN DEFAULT FALSE,
    last_analyzed DATETIME,
    analysis_status VARCHAR(50) DEFAULT 'PENDING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_repo (user_id, url)
);

-- Create analysis_results table
CREATE TABLE IF NOT EXISTS analysis_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    repository_id BIGINT NOT NULL,
    analysis_type VARCHAR(100),
    result_data LONGTEXT,
    confidence_score DECIMAL(5, 2),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (repository_id) REFERENCES repositories(id) ON DELETE CASCADE
);

-- Create findings table
CREATE TABLE IF NOT EXISTS findings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    analysis_result_id BIGINT,
    severity VARCHAR(50),
    title VARCHAR(255),
    description TEXT,
    file_path VARCHAR(500),
    line_number INT,
    code_snippet LONGTEXT,
    recommendation TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (analysis_result_id) REFERENCES analysis_results(id) ON DELETE CASCADE
);

-- Create agent_logs table
CREATE TABLE IF NOT EXISTS agent_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    repository_id BIGINT,
    agent_action VARCHAR(255),
    status VARCHAR(50),
    details LONGTEXT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (repository_id) REFERENCES repositories(id) ON DELETE CASCADE
);

-- Add indexes for performance
CREATE INDEX idx_repositories_user_id ON repositories(user_id);
CREATE INDEX idx_repositories_status ON repositories(analysis_status);
CREATE INDEX idx_analysis_results_repo_id ON analysis_results(repository_id);
CREATE INDEX idx_findings_result_id ON findings(analysis_result_id);
CREATE INDEX idx_agent_logs_repo_id ON agent_logs(repository_id);

-- Verify tables exist
SHOW TABLES LIKE 'repositories';
SHOW TABLES LIKE 'analysis_results';
SHOW TABLES LIKE 'findings';
SHOW TABLES LIKE 'agent_logs';
