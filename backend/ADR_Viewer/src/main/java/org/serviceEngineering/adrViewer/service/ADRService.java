package org.serviceEngineering.adrViewer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.serviceEngineering.adrViewer.dto.ADRPageDTO;
import org.serviceEngineering.adrViewer.dto.CommitDTO;
import org.serviceEngineering.adrViewer.entity.ADR;
import org.serviceEngineering.adrViewer.repository.ADRRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheResult;
import java.util.List;

@Service
@CacheDefaults(cacheName = "adr")
public class ADRService {
    private final ADRRepository aDRRepository;

    private final Logger log = LoggerFactory.getLogger(ADRService.class);

    @Autowired
    public ADRService(ADRRepository aDRRepository) {
        this.aDRRepository = aDRRepository;
    }

    /**
     * Service method for retrieving an ADR by its unique identifier.
     *
     * @param id The unique identifier of the ADR to retrieve.
     * @return The retrieved ADR object.
     */
    @CacheResult
    public ADR getADR(int id) throws EntityNotFoundException {
        try {
            ADR adr = aDRRepository.findADRById(id);
            log.info("Fetched adr from memory: \n {}", adr);
            return adr;
        } catch (EntityNotFoundException exception) {
            log.info(exception.getMessage());
            return null;
        }
    }

    /**
     * Service method for retrieving a page of ADRs.
     *
     * @param importTaskId id of the import task to look at
     * @param query searchText
     * @param pageOffset offset of page
     * @param limit limit per page
     * @return a new ADRPageDTO with Data (ADR data) and PageinationInfo (metadata for pagination)
     */
    public ADRPageDTO getADRsByPageOffsetAndLimit(int importTaskId, String query, int pageOffset, int limit) {
        Pageable pageable = PageRequest.of(pageOffset, limit);
        log.info("Fetched next adrs from memory: \n {}", pageable);
        var page = aDRRepository.findAllByTitleContainingIgnoreCaseAndImportTaskId(query, importTaskId, pageable);
        return new ADRPageDTO(page.getContent(), new ADRPageDTO.PaginationInfo(page.getNumber(), page.getTotalPages(), limit));
    }

    /**
     * Service method for retrieving all ADRs from the memory.
     *
     * @return List of all ADRs.
     */
    public List<ADR> getAll() {
        return aDRRepository.findAll();
    }

    /**
     * Service method for retrieving ADRs with a given importTaskId from memory
     *
     * @return List of ADRs.
     */
    public List<ADR> getAllByProject(int importTaskId) {
        return aDRRepository.findAllByImportTaskId(importTaskId);
    }

    /**
     * Service method for retrieving ADRs from the memory which have the same status as requested.
     *
     * @return List of ADRs with the corresponding status.
     */
    public List<ADR> getByStatus(String status) {
        return aDRRepository.getStatus(status.toLowerCase());
    }


    /**
     * Clears the cache of Architectural Decision Records (ADRs) by removing all records from the repository.
     * This method deletes all ADRs stored in the repository, effectively clearing the cache.
     * Use with caution, as it permanently removes all ADRs from the repository.
     */
    public void clear() {
        this.aDRRepository.deleteAll();
    }

    public CommitDTO extractLatestCommit(Object response) {

        System.out.println(response);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.toString());

            JsonNode nodes = jsonNode.path("data").path("repository").path("ref").path("target").path("history").path("nodes");

            if (nodes.isArray() && !nodes.isEmpty()) {
                JsonNode latestCommit = nodes.get(0);

                String oid = latestCommit.path("oid").asText();
                String committedDate = latestCommit.path("committedDate").asText();
                String message = latestCommit.path("message").asText();

                return new CommitDTO(oid, committedDate, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Extraction fails
    }
}
