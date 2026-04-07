package com.codecognition.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public class RepoAnalyzeRequest {
    @NotBlank(message = "Owner is required")
    public String owner;
    
    @NotBlank(message = "Repository name is required")
    @Size(min = 1, max = 100, message = "Repository name must be between 1 and 100 characters")
    public String repo;
    
    public String language;
    public int size = 0;
    public int stars = 0;
    public int forks = 0;
    public int open_issues = 0;
    public String description;
    public String default_branch = "main";
    public List<String> topics;
    public boolean archived = false;
    public boolean is_empty = false;
    public int file_count = 0;
    public boolean has_tests = false;
    public boolean has_ci = false;
    public boolean has_docker = false;
    public boolean has_readme = false;
    public boolean has_license = false;
    public boolean has_security_md = false;
    public boolean has_wiki = false;
    public String repo_name;
    public String file_context = "";
    public List<String> tree;
}
