package org.serviceEngineering.adrViewer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.serviceEngineering.adrViewer.client.CommitHistoryClient;
import org.serviceEngineering.adrViewer.dto.ADRPageDTO;
import org.serviceEngineering.adrViewer.entity.ADR;
import org.serviceEngineering.adrViewer.entity.ImportTask;
import org.serviceEngineering.adrViewer.exceptions.ServiceException;
import org.serviceEngineering.adrViewer.service.ADRService;
import org.serviceEngineering.adrViewer.service.ImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "ADR", description = "ADR Api")
@RestController
@RequestMapping("/api/v2/")
public class ADRController {

    private final ADRService adrService;
    private final ImportService importService;
    private final CommitHistoryClient commitHistoryClient;
    private final Logger log = LoggerFactory.getLogger(ADRController.class);

    /**
     * Constructor for the ADRController class.
     * <p>
     * Initializes the ADRController with the provided instances of ADRService, ImportService, and CommitHistoryClient.
     *
     * @param adrService          Service for managing Architectural Decision Records (ADRs).
     * @param importService       Service for managing ADR imports.
     * @param commitHistoryClient Client for retrieving commit history from a GitHub repository.
     */
    @Autowired
    public ADRController(ADRService adrService, ImportService importService, CommitHistoryClient commitHistoryClient) {
        this.adrService = adrService;
        this.importService = importService;
        this.commitHistoryClient = commitHistoryClient;
    }

    /**
     * Controller method for retrieving an Architectural Decision Record (ADR) by its unique identifier.
     * <p>
     * This endpoint allows fetching a specific ADR by providing its unique ID. If the ADR with the
     * specified ID is found in the database, it will be returned with an HTTP status of 200 (OK).
     * If the ADR is not found or if there's a service exception, it will return an appropriate HTTP
     * status code along with the error message.
     *
     * @param id The unique identifier of the ADR to retrieve.
     * @return ResponseEntity containing the ADR object if found, or an error message with an appropriate
     * HTTP status code if the ADR is not found or if there's a service exception.
     */
    @Operation(summary = "Get single ADR", description = "Fetches single ADR from storage via ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "resource not found")
    })
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("adr/{id}")
    public ResponseEntity<Object> getADR(@PathVariable int id) {
        try {
            ADR adr = adrService.getADR(id);
            return new ResponseEntity<>(adr, HttpStatus.OK);
        } catch (ServiceException e) {
            return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Controller method for retrieving all Architectural Decision Records (ADRs).
     * <p>
     * This endpoint allows fetching a list of all ADRs. The ADRs are retrieved from the storage,
     * and if successful, the list is returned with an HTTP status of 200 (OK).
     *
     * @return ResponseEntity containing a list of ADRs if the operation is successful.
     */
    @Operation(summary = "Get all ADRs", description = "Get a list of all ADRs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("getAllADRs")
    public ResponseEntity<Object> getAllADRs() {
        List<ADR> result = adrService.getAll();
        log.info("List of ADRs consists of {} ADRs", result.size());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Controller method for retrieving all Architectural Decision Records (ADRs) of the current project.
     * <p>
     * This endpoint allows fetching a list of all ADRs associated with the current project identified
     * by the provided importTaskId. The ADRs are retrieved from the storage, and if successful, the list
     * is returned with an HTTP status of 200 (OK).
     *
     * @param importTaskId The identifier of the import task representing the current project.
     * @return ResponseEntity containing a list of ADRs if the operation is successful.
     */
    @Operation(summary = "Get all ADRs of current project", description = "Get a list of all ADRs in the current project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("getAllADRsOfProject")
    public ResponseEntity<Object> getAllADRsOfCurrentProject(@RequestParam int importTaskId) {
        List<ADR> result = adrService.getAllByProject(importTaskId);
        log.info("List of ADRs consists of {} ADRs", result.size());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Controller method for retrieving a list of Architectural Decision Records (ADRs) by status.
     * <p>
     * This endpoint allows fetching a list of ADRs from storage based on the provided status.
     * If successful, the list is returned with an HTTP status of 200 (OK).
     *
     * @param status The status of ADRs to retrieve.
     * @return ResponseEntity containing a list of ADRs if the operation is successful.
     */
    @Operation(summary = "Get list of ADRs by status", description = "Get a list of ADRs from storage with provided status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("getByStatus")
    public ResponseEntity<Object> getByStatus(@RequestParam String status) {
        List<ADR> result = adrService.getByStatus(status);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Controller method for retrieving a page of Architectural Decision Records (ADRs) by pageOffset and limit.
     * <p>
     * This endpoint allows retrieving a specified number of ADRs based on pageOffset and limit,
     * and it also considers filtering with a query. If successful, the result is returned with
     * an HTTP status of 200 (OK).
     *
     * @param importTaskId The identifier of the import task representing the current project.
     * @param query        Search text to filter ADRs.
     * @param pageOffset   Offset of the page.
     * @param limit        Limit of ADRs per page.
     * @return ResponseEntity containing a page of ADRs if the operation is successful.
     */
    @Operation(summary = "Get page of ADRs by pageOffset and limit", description = "Get a page of ADRs from storage by pageOffset and limit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("ADR")
    public ResponseEntity<Object> getByPageOffsetAndLimit(
            @RequestParam int importTaskId,
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam int pageOffset,
            @RequestParam int limit
    ) {
        ADRPageDTO result = adrService.getADRsByPageOffsetAndLimit(importTaskId, query, pageOffset, limit);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Controller method for retrieving commit history for a specific file within a GitHub repository.
     * <p>
     * This endpoint allows fetching commit history for a particular file in a GitHub repository,
     * including the commit OID, committed date, commit message, and ADR status.
     *
     * @param importTaskId The identifier of the import task representing the current project.
     * @param filePath      Path to the file related to the Architectural Decision Record (ADR).
     * @return ResponseEntity containing the commit history information if the operation is successful.
     * @throws IOException If an I/O exception occurs during the operation.
     */
    @Operation(summary = "Get commit history", description = "Get the commit history (oid, committedDate, message, ADR status)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("getHistory")
    public ResponseEntity<Object> getHistory(
            @RequestParam int importTaskId,
            @RequestParam String filePath
    ) throws IOException {
        ImportTask importTask = importService.getImportTask(importTaskId);
        Object result = commitHistoryClient.getHistory(importTask.getRepoOwner(), importTask.getRepoName(), filePath, importTask.getBranch());
        log.info(result.toString());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Controller method for clearing the cache of repositories.
     * <p>
     * This endpoint allows removing all objects from the cache associated with repositories.
     * It performs cache clearance, and if successful, logs the information with an HTTP status of 200 (OK).
     */
    @Operation(summary = "Clear cache", description = "Remove all objects from cache")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("clear")
    public void clear() {
        log.info("Clearing cache");
        adrService.clear();
        importService.clear();
    }
}
