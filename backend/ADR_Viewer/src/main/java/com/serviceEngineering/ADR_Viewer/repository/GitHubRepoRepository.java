package com.serviceEngineering.ADR_Viewer.repository;

import com.serviceEngineering.ADR_Viewer.entity.GithubRepo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GitHubRepoRepository extends JpaRepository<GithubRepo, Integer> {
}
