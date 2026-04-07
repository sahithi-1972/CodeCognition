package com.codecognition.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public class AnalyzeRequest {
    @NotBlank(message = "Repository URL is required")
    @Size(min = 5, max = 255, message = "Repository URL must be between 5 and 255 characters")
    public String repo_url;
    
    public boolean use_mock = true;
}

