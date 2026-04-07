package com.codecognition.repository;

import com.codecognition.model.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    @Query("SELECT a FROM AnalysisResult a WHERE a.repo_url = :repoUrl")
    AnalysisResult findByRepoUrl(@Param("repoUrl") String repo_url);
}
