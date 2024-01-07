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

    /**
     * finds an ADR by its ID
     * @param id id of the ADR
     * @return ADR with specified ID
     */
    ADR findADRById(int id);

    /**
     * returns a page of ADR, considering filtering by title
     * @param title searchString that is included in the title (ignore case-sensitive)
     * @param importTaskId importTaskId belonging to the ADRs
     * @param pageable Pageable object that contains metainfo for the pagination
     * @return a page of ADRs
     */
    Page<ADR> findAllByTitleContainingIgnoreCaseAndImportTaskId(String title, int importTaskId, Pageable pageable);
}
