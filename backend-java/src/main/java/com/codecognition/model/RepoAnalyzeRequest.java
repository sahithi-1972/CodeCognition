package com.codecognition.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepoAnalyzeRequest {
    private String owner;
    private String repo;
    private String language;
    private int size = 0;
    private int stars = 0;
    private int forks = 0;
    private int open_issues = 0;
    private String description;
    private String default_branch = "main";
    private List<String> topics;
    private boolean archived = false;
    private boolean is_empty = false;
    private int file_count = 0;
    private boolean has_tests = false;
    private boolean has_ci = false;
    private boolean has_docker = false;
    private boolean has_readme = false;
    private boolean has_license = false;
    private boolean has_security_md = false;
    private boolean has_wiki = false;
    private String repo_name;
    private String file_context = "";
    private List<String> tree;
}
