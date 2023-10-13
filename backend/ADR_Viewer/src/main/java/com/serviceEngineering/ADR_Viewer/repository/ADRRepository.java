package com.serviceEngineering.ADR_Viewer.repository;

import com.serviceEngineering.ADR_Viewer.entity.ADR;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ADRRepository extends JpaRepository<ADR, Integer> {
}
