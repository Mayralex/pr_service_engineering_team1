package org.serviceEngineering.adrViewer.service;

import jakarta.persistence.EntityNotFoundException;
import org.serviceEngineering.adrViewer.entity.ADR;
import org.serviceEngineering.adrViewer.dto.ADRPageDTO;
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
    public ADR getADR(long id) throws EntityNotFoundException {
        try {
            ADR adr = aDRRepository.getReferenceById(id);
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
     * Service method for retrieving ADRs from the memory which have the same status as requested.
     *
     * @return List of ADRs with the corresponding status.
     */
    public List<ADR> getByStatus(String status) {
        return aDRRepository.getStatus(status.toLowerCase());
    }


    public void clear() {
        this.aDRRepository.deleteAll();
    }
}
