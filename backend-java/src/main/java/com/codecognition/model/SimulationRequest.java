package com.codecognition.model;

import jakarta.validation.constraints.NotBlank;

public class SimulationRequest {
    @NotBlank(message = "Changed file path is required")
    public String changed_file;
    
    public String repo_url;
}
