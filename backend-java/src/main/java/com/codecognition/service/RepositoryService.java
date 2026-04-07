package com.codecognition.service;

import com.codecognition.model.Repository;
import com.codecognition.model.User;
import com.codecognition.repository.RepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RepositoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(RepositoryService.class);
    
    @Autowired
    private RepositoryRepository repositoryRepository;

    /**
     * Parse GitHub URL to extract owner and repo name
     * Supports: https://github.com/owner/repo.git or https://github.com/owner/repo
     */
    public Repository parseAndAddRepository(String url, User user) {
        // Validate URL
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Repository URL cannot be empty");
        }

        // Check if repo already exists for this user
        Optional<Repository> existing = repositoryRepository.findByUserAndUrl(user, url);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Repository already added");
        }

        // Parse GitHub URL
        GitHubRepoInfo repoInfo = parseGitHubUrl(url);

        // Create repository entity
        Repository repo = new Repository();
        repo.setUser(user);
        repo.setUrl(url);
        repo.setName(repoInfo.repoName);
        repo.setOwner(repoInfo.owner);
        repo.setDescription(repoInfo.owner + "/" + repoInfo.repoName);
        repo.setIsPrivate(false);
        repo.setAnalysisStatus("PENDING");

        // Save to database
        Repository saved = repositoryRepository.save(repo);
        logger.info("Repository added: {} by user {}", url, user.getEmail());
        
        return saved;
    }

    /**
     * Get all repositories for a user
     */
    public List<Repository> getUserRepositories(User user) {
        return repositoryRepository.findByUser(user);
    }

    /**
     * Get a specific repository
     */
    public Optional<Repository> getRepository(Long repoId, User user) {
        Optional<Repository> repo = repositoryRepository.findById(repoId);
        
        // Verify ownership
        if (repo.isPresent() && !repo.get().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Repository not found");
        }
        
        return repo;
    }

    /**
     * Delete a repository
     */
    public void deleteRepository(Long repoId, User user) {
        Optional<Repository> repo = repositoryRepository.findById(repoId);
        
        if (repo.isEmpty()) {
            throw new IllegalArgumentException("Repository not found");
        }
        
        if (!repo.get().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized");
        }
        
        repositoryRepository.deleteById(repoId);
        logger.info("Repository deleted: {}", repoId);
    }

    /**
     * Update analysis status
     */
    public void updateAnalysisStatus(Long repoId, String status) {
        Optional<Repository> repo = repositoryRepository.findById(repoId);
        if (repo.isPresent()) {
            repo.get().setAnalysisStatus(status);
            repositoryRepository.save(repo.get());
        }
    }

    /**
     * Parse GitHub URL and extract owner and repo name
     */
    private GitHubRepoInfo parseGitHubUrl(String url) {
        // Remove .git suffix if present
        String cleanUrl = url.endsWith(".git") ? url.substring(0, url.length() - 4) : url;

        // Pattern: https://github.com/owner/repo or http://github.com/owner/repo
        Pattern pattern = Pattern.compile("(?:https?://)?(?:www\\.)?github\\.com/([^/]+)/([^/]+)/?$");
        Matcher matcher = pattern.matcher(cleanUrl);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid GitHub repository URL: " + url);
        }

        String owner = matcher.group(1);
        String repoName = matcher.group(2);

        return new GitHubRepoInfo(owner, repoName);
    }

    /**
     * Inner class to hold GitHub repo info
     */
    private static class GitHubRepoInfo {
        String owner;
        String repoName;

        GitHubRepoInfo(String owner, String repoName) {
            this.owner = owner;
            this.repoName = repoName;
        }
    }
}
