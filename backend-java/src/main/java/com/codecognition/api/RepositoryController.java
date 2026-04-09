package com.codecognition.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import com.codecognition.model.AddRepositoryRequest;
import com.codecognition.model.Repository;
import com.codecognition.model.RepositoryResponse;
import com.codecognition.model.User;
import com.codecognition.model.GitHubConnectRequest;
import com.codecognition.model.AnalyzeRepositoryRequest;
import com.codecognition.model.AnalysisResult;
import com.codecognition.model.Finding;
import com.codecognition.model.RepoAnalyzeRequest;
import com.codecognition.repository.UserRepository;
import com.codecognition.repository.AnalysisResultRepository;
import com.codecognition.service.RepositoryService;
import com.codecognition.service.GitHubService;
import com.codecognition.service.AnalysisService;

@RestController
@RequestMapping("/api/repositories")
@CrossOrigin(origins = {"https://spiffy-syrniki-74f808.netlify.app", "http://localhost:5173", "http://localhost:3000"})
@Tag(name = "Repository Management", description = "Manage repositories and run analyses")
@SuppressWarnings("unused")
public class RepositoryController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GitHubService githubService;

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private AnalysisResultRepository analysisResultRepository;

    /**
     * Add a new repository for analysis
     * POST /api/repositories/add
     */
    @PostMapping("/add")
    @Operation(summary = "Add repository", description = "Add a new repository for analysis (requires JWT token)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> addRepository(
            @RequestBody AddRepositoryRequest request,
            @RequestHeader("Authorization") String token) {
        
        try {
            // Extract user ID from token (simplified - you'd parse JWT in real app)
            // For now, we'll use a userId from header or session
            String userId = request.url; // Placeholder - fix this with actual JWT parsing
            
            // Get authenticated user (you should get this from JWT token)
            // For now, assuming user ID is 1 (demo purposes)
            Optional<User> user = userRepository.findById(1L);
            
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not found"));
            }

            Repository repo = repositoryService.parseAndAddRepository(request.url, user.get());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Repository added successfully");
            response.put("repository", new RepositoryResponse(repo));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error adding repository: " + e.getMessage()));
        }
    }

    /**
     * Get all repositories for authenticated user
     * GET /api/repositories
     */
    @GetMapping
    @Operation(summary = "Get repositories", description = "Retrieve all repositories for the authenticated user (requires JWT token)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getUserRepositories(
            @RequestHeader("Authorization") String token) {
        
        try {
            // Get authenticated user (placeholder - fix with JWT parsing)
            Optional<User> user = userRepository.findById(1L);
            
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not found"));
            }

            List<Repository> repositories = repositoryService.getUserRepositories(user.get());
            List<RepositoryResponse> responses = repositories.stream()
                .map(RepositoryResponse::new)
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("repositories", responses);
            response.put("total", responses.size());

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error fetching repositories"));
        }
    }

    /**
     * Get a specific repository
     * GET /api/repositories/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get repository details", description = "Retrieve details for a specific repository (requires JWT token)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getRepository(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        try {
            Optional<User> user = userRepository.findById(1L);
            
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not found"));
            }

            Optional<Repository> repo = repositoryService.getRepository(id, user.get());
            
            if (repo.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Repository not found"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("repository", new RepositoryResponse(repo.get()));

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error fetching repository"));
        }
    }

    /**
     * Delete a repository
     * DELETE /api/repositories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRepository(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        try {
            Optional<User> user = userRepository.findById(1L);
            
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not found"));
            }

            repositoryService.deleteRepository(id, user.get());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Repository deleted successfully");

            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error deleting repository"));
        }
    }

    /**
     * Get user's GitHub repositories using personal access token
     * POST /api/repositories/github/repos
     */
    @PostMapping("/github/repos")
    public ResponseEntity<?> getGitHubRepositories(
            @RequestBody GitHubConnectRequest request) {
        try {
            String githubToken = request.getGithubToken();
            
            if (githubToken == null || githubToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("GitHub token is required"));
            }
            
            // Fetch repositories from GitHub API using GitHubService
            List<Map<String, Object>> repos = githubService.getUserRepositories(githubToken);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "GitHub repositories fetched successfully",
                "repositories", repos,
                "total", repos.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to fetch repositories: " + e.getMessage()));
        }
    }

    /**
     * Test GitHub token validity
     * POST /api/repositories/test-github-token
     */
    @PostMapping("/test-github-token")
    @Operation(summary = "Test GitHub token", description = "Verify that a GitHub personal access token is valid (requires JWT token)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> testGithubToken(
            @RequestBody GitHubConnectRequest request) {
        try {
            String githubToken = request.getGithubToken();
            
            if (githubToken == null || githubToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("GitHub token is required"));
            }
            
            // Test by fetching repositories
            List<Map<String, Object>> repos = githubService.getUserRepositories(githubToken);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "GitHub token is valid",
                "repositoriesCount", repos.size(),
                "repositories", repos.stream().limit(5).toList()
            ));
        } catch (Exception e) {
            String errorMsg = e.toString() + " | " + e.getMessage();
            if (errorMsg.contains("403")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("GitHub PAT is invalid or revoked. Please regenerate it."));
            } else if (errorMsg.contains("401")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("GitHub PAT is expired or invalid."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error testing token: " + errorMsg));
        }
    }

    /**
     * Helper method to create error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        return response;
    }

    /**
     * Handle demo repository analysis - returns mock data without calling GitHub API
     */
    private ResponseEntity<Map<String, Object>> handleDemoRepositoryAnalysis(AnalyzeRepositoryRequest req) {
        try {
            String repoUrl = req.owner + "/" + req.repo;
            
            // Create demo analysis result
            AnalysisResult result = new AnalysisResult();
            result.repo_url = repoUrl;
            result.health_score = 78;
            result.security_score = 82;
            result.quality_score = 75;
            result.dependency_score = 70;
            result.documentation_score = 85;
            result.status = "success";
            result.summary = "Demo repository analysis - Sample public repository '" + req.repo + "' by " + req.owner;
            result._engine = "CodeCognition AI v3.3.0 (Demo Mode)";
            
            // Create sample findings
            java.util.List<Finding> findings = new java.util.ArrayList<>();
            
            Finding finding1 = new Finding();
            finding1.severity = "MEDIUM";
            finding1.category = "Code Quality";
            finding1.title = "Missing input validation";
            finding1.description = "Some API endpoints lack proper input validation";
            finding1.file = "src/api/controller.js";
            finding1.fix = "Add comprehensive input validation using a validation library";
            findings.add(finding1);
            
            Finding finding2 = new Finding();
            finding2.severity = "LOW";
            finding2.category = "Documentation";
            finding2.title = "Incomplete API documentation";
            finding2.description = "Some endpoints lack detailed documentation";
            finding2.file = "README.md";
            finding2.fix = "Add OpenAPI/Swagger documentation for all endpoints";
            findings.add(finding2);
            
            Finding finding3 = new Finding();
            finding3.severity = "INFO";
            finding3.category = "Best Practice";
            finding3.title = "Consider using async/await pattern";
            finding3.description = "Some functions use callbacks instead of promises";
            finding3.fix = "Refactor callbacks to use async/await for better readability";
            findings.add(finding3);
            
            result.findings = findings;
            
            // Save to database
            System.out.println("[ANALYZE] Saving demo analysis results to database...");
            AnalysisResult existing = analysisResultRepository.findByRepoUrl(repoUrl);
            if (existing != null) {
                result.id = existing.id;
                result.created_at = existing.created_at;
                System.out.println("[ANALYZE] Updating existing analysis...");
            } else {
                System.out.println("[ANALYZE] Creating new analysis record...");
            }
            analysisResultRepository.save(result);
            System.out.println("[ANALYZE] ✅ Results saved");
            
            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("repo_url", repoUrl);
            response.put("health_score", result.health_score);
            response.put("security_score", result.security_score);
            response.put("quality_score", result.quality_score);
            response.put("dependency_score", result.dependency_score);
            response.put("documentation_score", result.documentation_score);
            response.put("status_text", result.status);
            response.put("summary", result.summary);
            response.put("findings_count", result.findings != null ? result.findings.size() : 0);
            response.put("findings", result.findings);
            response.put("engine", result._engine);
            response.put("demo_mode", true);
            
            System.out.println("[ANALYZE] ✅ Demo analysis complete");
            System.out.println("[ANALYZE] ═══════════════════════════════════════════════════════════\n");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("[ANALYZE] ❌ Error in demo analysis: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Demo analysis failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }


    /**
     * Analyze repository from GitHub - Phase 3
     * POST /api/repositories/analyze-github-repo
     */
    @PostMapping("/analyze-github-repo")
    @Operation(summary = "Analyze repository from GitHub", description = "Fetch repository details from GitHub and run comprehensive analysis (requires JWT token)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> analyzeRepositoryFromGitHub(@Valid @RequestBody AnalyzeRepositoryRequest req) {
        try {
            String repoUrl = req.owner + "/" + req.repo;
            System.out.println("\n[ANALYZE] ═══════════════════════════════════════════════════════════");
            System.out.println("[ANALYZE] Repository Analysis Request");
            System.out.println("  Owner: " + req.owner);
            System.out.println("  Repo: " + req.repo);
            System.out.println("  GitHub Token: " + (req.githubToken != null ? req.githubToken.substring(0, Math.min(20, req.githubToken.length())) + "..." : "null"));
            System.out.println("─────────────────────────────────────────────────────────────────");
            
            // Check if this is a demo repository
            boolean isDemoRepo = "demo-user".equalsIgnoreCase(req.owner);
            
            if (isDemoRepo && req.githubToken == null) {
                System.out.println("[ANALYZE] Demo repository detected - using mock data");
                return handleDemoRepositoryAnalysis(req);
            }
            
            // For real repositories, GitHub token is required
            if (!isDemoRepo && (req.githubToken == null || req.githubToken.trim().isEmpty())) {
                System.out.println("[ANALYZE] ❌ Real repository requires GitHub token");
                Map<String, Object> error = new HashMap<>();
                error.put("status", "error");
                error.put("message", "GitHub token is required for real repositories");
                error.put("repo", repoUrl);
                return ResponseEntity.badRequest().body(error);
            }
            
            // Fetch repository details from GitHub
            System.out.println("[ANALYZE] Fetching repository details from GitHub...");
            Map<String, Object> repoDetails = githubService.getRepositoryDetails(req.owner, req.repo, req.githubToken);
            System.out.println("[ANALYZE] ✅ Repository details fetched");
            
            // Fetch repository tree
            System.out.println("[ANALYZE] Fetching repository tree...");
            List<String> files = githubService.getRepositoryTree(req.owner, req.repo, req.githubToken);
            System.out.println("[ANALYZE] ✅ Found " + files.size() + " files");
            
            // Fetch key dependency files
            System.out.println("[ANALYZE] Fetching key dependency files...");
            String depContent = githubService.getKeyFileContent(req.owner, req.repo, req.githubToken, "main");
            System.out.println("[ANALYZE] ✅ Dependency content fetched");
            
            // Fetch README
            System.out.println("[ANALYZE] Fetching README...");
            String readme = githubService.getReadme(req.owner, req.repo, req.githubToken);
            if (readme == null) readme = "";
            System.out.println("[ANALYZE] ✅ README fetched (" + readme.length() + " chars)");
            
            // Fetch source files for deeper analysis
            System.out.println("[ANALYZE] Fetching source files for analysis...");
            String sourceFiles = fetchSourceFilesForAnalysis(files, req.owner, req.repo, req.githubToken);
            System.out.println("[ANALYZE] ✅ Source files fetched");
            
            // Check for tests, CI/CD, config files
            System.out.println("[ANALYZE] Analyzing repository structure...");
            boolean hasTests = files.stream().anyMatch(f -> f.toLowerCase().contains("test") || f.contains("__tests__") || f.contains(".test.") || f.contains(".spec."));
            boolean hasCI = files.stream().anyMatch(f -> f.contains(".github/workflows") || f.contains(".gitlab-ci.yml") || f.contains(".circleci") || f.contains("Jenkinsfile"));
            System.out.println("[ANALYZE] - Has tests: " + hasTests + ", Has CI/CD: " + hasCI);
            
            // Build RepoAnalyzeRequest for analysis service
            System.out.println("[ANALYZE] Building analysis request...");
            RepoAnalyzeRequest analyzeReq = new RepoAnalyzeRequest();
            analyzeReq.owner = req.owner;
            analyzeReq.repo = req.repo;
            analyzeReq.language = (String) repoDetails.getOrDefault("language", "Unknown");
            analyzeReq.stars = (int) repoDetails.getOrDefault("stars", 0);
            analyzeReq.description = (String) repoDetails.getOrDefault("description", "");
            analyzeReq.default_branch = (String) repoDetails.getOrDefault("defaultBranch", "main");
            analyzeReq.file_context = depContent + "\n" + readme + "\n" + sourceFiles;
            analyzeReq.tree = files;
            analyzeReq.has_readme = !readme.isEmpty();
            analyzeReq.has_tests = hasTests;
            analyzeReq.has_ci = hasCI;
            analyzeReq.file_count = files.size();
            analyzeReq.is_empty = files.isEmpty();
            System.out.println("[ANALYZE] ✅ Analysis request built");
            
            // Run analysis
            System.out.println("[ANALYZE] Running code analysis...");
            AnalysisResult result = analysisService.analyzeRepository(analyzeReq);
            System.out.println("[ANALYZE] ✅ Analysis complete");
            System.out.println("  - Health Score: " + result.health_score);
            System.out.println("  - Security Score: " + result.security_score);
            System.out.println("  - Quality Score: " + result.quality_score);
            System.out.println("  - Findings: " + (result.findings != null ? result.findings.size() : 0));
            
            result.repo_url = repoUrl;
            
            // Save to database (update if exists, insert if new)
            System.out.println("[ANALYZE] Saving results to database...");
            AnalysisResult existing = analysisResultRepository.findByRepoUrl(repoUrl);
            if (existing != null) {
                // Update existing record
                System.out.println("[ANALYZE] Updating existing analysis...");
                result.id = existing.id;
                result.created_at = existing.created_at; // Preserve original creation date
            } else {
                System.out.println("[ANALYZE] Creating new analysis record...");
            }
            analysisResultRepository.save(result);
            System.out.println("[ANALYZE] ✅ Results saved");
            
            // Build response
            System.out.println("[ANALYZE] Building response...");
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("repo_url", repoUrl);
            response.put("health_score", result.health_score);
            response.put("security_score", result.security_score);
            response.put("quality_score", result.quality_score);
            response.put("dependency_score", result.dependency_score);
            response.put("documentation_score", result.documentation_score);
            response.put("status_text", result.status);
            response.put("summary", result.summary);
            response.put("findings_count", result.findings != null ? result.findings.size() : 0);
            response.put("findings", result.findings);
            response.put("engine", result._engine);
            
            System.out.println("[ANALYZE] ✅ Response built successfully");
            System.out.println("[ANALYZE] ═══════════════════════════════════════════════════════════\n");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("\n[ANALYZE] ❌ ERROR during analysis");
            System.err.println("[ANALYZE] Exception Type: " + e.getClass().getName());
            System.err.println("[ANALYZE] Message: " + e.getMessage());
            System.err.println("[ANALYZE] Cause: " + e.getCause());
            System.err.println("[ANALYZE] ═══════════════════════════════════════════════════════════\n");
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            error.put("repo", req.owner + "/" + req.repo);
            error.put("exception", e.getClass().getSimpleName());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Helper method to fetch source files from repository for analysis
     */
    private String fetchSourceFilesForAnalysis(List<String> files, String owner, String repo, String githubToken) {
        StringBuilder sourceContent = new StringBuilder();
        
        // Patterns to look for
        String[] mainPatterns = {
            "package.json", "requirements.txt", "pom.xml", "build.gradle",
            ".java", ".py", ".js", ".ts", ".jsx", ".tsx",
            "Dockerfile", "docker-compose.yml",
            ".eslintrc", ".prettierrc", "tsconfig.json",
            "README.md", "CHANGELOG.md"
        };
        
        int fileCount = 0;
        int maxFiles = 15; // Limit to 15 files for analysis
        
        for (String file : files) {
            if (fileCount >= maxFiles) break;
            
            boolean shouldFetch = false;
            for (String pattern : mainPatterns) {
                if (file.toLowerCase().endsWith(pattern.toLowerCase()) || 
                    file.toLowerCase().contains(pattern.toLowerCase())) {
                    shouldFetch = true;
                    break;
                }
            }
            
            if (shouldFetch && !file.contains("node_modules") && !file.contains(".git")) {
                try {
                    String content = githubService.getFileContent(owner, repo, file, githubToken);
                    if (content != null && !content.isEmpty()) {
                        sourceContent.append("\n## FILE: ").append(file).append("\n");
                        sourceContent.append(content.substring(0, Math.min(500, content.length())));
                        sourceContent.append("\n");
                        fileCount++;
                    }
                } catch (Exception e) {
                    // Continue to next file
                }
            }
        }
        
        return sourceContent.toString();
    }
}
