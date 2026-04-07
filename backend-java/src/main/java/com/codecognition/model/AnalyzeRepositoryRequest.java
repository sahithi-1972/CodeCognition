package com.codecognition.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class AnalyzeRepositoryRequest {
    @NotBlank(message = "GitHub token is required")
    @Schema(description = "GitHub Personal Access Token")
    public String githubToken;
    
    @NotBlank(message = "Owner is required")
    @Schema(description = "Repository owner (username or organization)")
    public String owner;
    
    @NotBlank(message = "Repository name is required")
    @Schema(description = "Repository name")
    public String repo;
}
