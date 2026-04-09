package com.codecognition.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@SuppressWarnings("unused")
public class GitHubService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String GITHUB_API_URL = "https://api.github.com";
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Fetch list of repositories for a GitHub user using personal access token
     */
    public List<Map<String, Object>> getUserRepositories(String githubToken) {
        try {
            String url = GITHUB_API_URL + "/user/repos?per_page=100";
            
            HttpHeaders headers = new HttpHeaders();
            // GitHub uses "token" prefix, not "Bearer"
            headers.set("Authorization", "token " + githubToken);
            headers.set("Accept", "application/vnd.github.v3+json");
            headers.set("User-Agent", "CodeCognition");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            try {
                org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                String responseBody = response.getBody();
                
                if (responseBody == null || responseBody.isEmpty()) {
                    throw new Exception("Empty response from GitHub API");
                }
                
                JsonNode repos = objectMapper.readTree(responseBody);
                List<Map<String, Object>> repoList = new ArrayList<>();
                
                for (JsonNode repo : repos) {
                    Map<String, Object> repoData = new HashMap<>();
                    repoData.put("id", repo.get("id").asText());
                    repoData.put("name", repo.get("name").asText());
                    repoData.put("owner", repo.get("owner").get("login").asText());
                    repoData.put("url", repo.get("html_url").asText());
                    repoData.put("description", repo.get("description").asText());
                    repoData.put("language", repo.get("language").asText());
                    repoData.put("stars", repo.get("stargazers_count").asInt());
                    repoData.put("isPrivate", repo.get("private").asBoolean());
                    
                    repoList.add(repoData);
                }
                
                return repoList;
            } catch (Exception e) {
                // Check if it's a 403 error
                if (e.getMessage().contains("403")) {
                    throw new RuntimeException("GitHub API returned 403 Forbidden. Check if your PAT is valid and has correct scopes (repo, read:user)");
                } else if (e.getMessage().contains("401")) {
                    throw new RuntimeException("GitHub API returned 401 Unauthorized. Token is invalid or expired.");
                }
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch GitHub repositories: " + e.getMessage());
        }
    }
    
    /**
     * Get raw file content from GitHub repository
     */
    public String getFileContent(String owner, String repo, String filePath, String githubToken) {
        try {
            String url = String.format("https://raw.githubusercontent.com/%s/%s/main/%s", owner, repo, filePath);
            
            HttpHeaders headers = new HttpHeaders();
            if (githubToken != null && !githubToken.isEmpty()) {
                headers.set("Authorization", "Bearer " + githubToken);
            }
            headers.set("User-Agent", "CodeCognition");
            
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            return null; // File not found or error accessing
        }
    }
    
    /**
     * Get repository details from GitHub API
     */
    public Map<String, Object> getRepositoryDetails(String owner, String repo, String githubToken) {
        try {
            String url = String.format("%s/repos/%s/%s", GITHUB_API_URL, owner, repo);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "token " + githubToken);
            headers.set("Accept", "application/vnd.github.v3+json");
            headers.set("User-Agent", "CodeCognition");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String response = restTemplate.getForObject(url, String.class);
            
            JsonNode repoNode = objectMapper.readTree(response);
            Map<String, Object> repoData = new HashMap<>();
            
            repoData.put("id", repoNode.get("id").asText());
            repoData.put("name", repoNode.get("name").asText());
            repoData.put("owner", repoNode.get("owner").get("login").asText());
            repoData.put("url", repoNode.get("html_url").asText());
            repoData.put("description", repoNode.get("description").asText());
            repoData.put("language", repoNode.get("language").asText());
            repoData.put("stars", repoNode.get("stargazers_count").asInt());
            repoData.put("isPrivate", repoNode.get("private").asBoolean());
            repoData.put("defaultBranch", repoNode.get("default_branch").asText());
            
            return repoData;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch repository details: " + e.getMessage());
        }
    }

    /**
     * Fetch repository tree (file structure) from GitHub API
     */
    public List<String> getRepositoryTree(String owner, String repo, String githubToken) {
        try {
            String url = String.format("%s/repos/%s/%s/git/trees/HEAD?recursive=1", GITHUB_API_URL, owner, repo);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "token " + githubToken);
            headers.set("Accept", "application/vnd.github.v3+json");
            headers.set("User-Agent", "CodeCognition");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String response = restTemplate.getForObject(url, String.class);
            
            JsonNode treeNode = objectMapper.readTree(response);
            List<String> files = new ArrayList<>();
            
            for (JsonNode item : treeNode.get("tree")) {
                if (item.get("type").asText().equals("blob")) {
                    files.add(item.get("path").asText());
                }
            }
            
            return files;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Fetch README content from repository
     */
    public String getReadme(String owner, String repo, String githubToken) {
        try {
            String url = String.format("%s/repos/%s/%s/readme", GITHUB_API_URL, owner, repo);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "token " + githubToken);
            headers.set("Accept", "application/vnd.github.v3.raw");
            headers.set("User-Agent", "CodeCognition");
            
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch specific file content from repository using raw GitHub URL
     */
    public String getFileContentRaw(String owner, String repo, String filePath, String branch) {
        try {
            String url = String.format("https://raw.githubusercontent.com/%s/%s/%s/%s", owner, repo, branch, filePath);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "CodeCognition");
            
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch package.json, requirements.txt, pom.xml and other key files for dependency analysis
     */
    public String getKeyFileContent(String owner, String repo, String githubToken, String branch) {
        try {
            String[] keyFiles = {"package.json", "requirements.txt", "pom.xml", "Gemfile", "go.mod", "Cargo.toml"};
            
            for (String file : keyFiles) {
                String url = String.format("%s/repos/%s/%s/contents/%s", GITHUB_API_URL, owner, repo, file);
                
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "token " + githubToken);
                headers.set("Accept", "application/vnd.github.v3.raw");
                headers.set("User-Agent", "CodeCognition");
                
                HttpEntity<String> entity = new HttpEntity<>(headers);
                try {
                    String content = restTemplate.getForObject(url, String.class);
                    if (content != null && !content.isEmpty()) {
                        return content;
                    }
                } catch (Exception e) {
                    // Try next file
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }
}
