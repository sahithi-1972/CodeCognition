package com.codecognition.model;

public class RepositoryResponse {
    public Long id;
    public String name;
    public String url;
    public String owner;
    public String description;
    public Boolean isPrivate;
    public String analysisStatus;
    public String lastAnalyzed;

    public RepositoryResponse() {}

    public RepositoryResponse(Repository repo) {
        this.id = repo.getId();
        this.name = repo.getName();
        this.url = repo.getUrl();
        this.owner = repo.getOwner();
        this.description = repo.getDescription();
        this.isPrivate = repo.getIsPrivate();
        this.analysisStatus = repo.getAnalysisStatus();
        this.lastAnalyzed = repo.getLastAnalyzed() != null ? repo.getLastAnalyzed().toString() : null;
    }
}
