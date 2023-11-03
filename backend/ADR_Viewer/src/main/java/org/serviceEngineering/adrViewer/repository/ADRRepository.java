package org.serviceEngineering.adrViewer.repository;

import org.serviceEngineering.adrViewer.entity.ADR;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ADRRepository extends JpaRepository<ADR, Long> {
}
