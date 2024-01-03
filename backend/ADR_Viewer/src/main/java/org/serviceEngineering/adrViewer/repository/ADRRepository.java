package org.serviceEngineering.adrViewer.repository;

import org.serviceEngineering.adrViewer.entity.ADR;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.util.List;

public interface ADRRepository extends JpaRepository<ADR, Long> {
    @Query("select a from ADR a where lower(a.status) like ?1")
    List<ADR> getStatus(@Nullable Object unknownAttr1);

    Page<ADR> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);
}
