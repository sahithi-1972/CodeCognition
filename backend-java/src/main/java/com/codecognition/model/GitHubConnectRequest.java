package com.codecognition.model;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public class GitHubConnectRequest {
    
    @NotBlank(message = "GitHub token is required")
    @Schema(description = "GitHub Personal Access Token", example = "ghp_xxxxx")
    public String githubToken;
    
    @NotBlank(message = "Username is required")
    @Schema(description = "GitHub username", example = "sahithi-1972")
    public String username;
    
    // Constructors
    public GitHubConnectRequest() {}
    
    public GitHubConnectRequest(String githubToken, String username) {
        this.githubToken = githubToken;
        this.username = username;
    }
    
    // Getters and Setters
    public String getGithubToken() {
        return githubToken;
    }
    
    public void setGithubToken(String githubToken) {
        this.githubToken = githubToken;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
}
