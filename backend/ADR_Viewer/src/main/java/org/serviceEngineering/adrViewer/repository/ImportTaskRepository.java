package org.serviceEngineering.adrViewer.repository;

import org.serviceEngineering.adrViewer.entity.ImportTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportTaskRepository extends JpaRepository<ImportTask, Long> {

    /**
     * Retrieves the last import task
     * @return Last import task
     */
    ImportTask findFirstByOrderByIdDesc();

    /**
     * Finds import task by id
     * @return import task
     */
    ImportTask findImportTaskById(int id);

}
