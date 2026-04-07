package com.codecognition.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codecognition.model.Repository;
import com.codecognition.model.User;

public interface RepositoryRepository extends JpaRepository<Repository, Long> {
    List<Repository> findByUser(User user);
    List<Repository> findByUserId(Long userId);
    Optional<Repository> findByUserAndName(User user, String name);
    Optional<Repository> findByUserAndUrl(User user, String url);
}
