package org.serviceEngineering.adrViewer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.serviceEngineering.adrViewer.client.CommitHistoryClient;
import org.serviceEngineering.adrViewer.entity.ADR;
import org.serviceEngineering.adrViewer.dto.ADRPageDTO;
import org.serviceEngineering.adrViewer.exceptions.ServiceException;
import org.serviceEngineering.adrViewer.service.ADRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "ADR", description = "ADR Api")
@RestController
@RequestMapping("/api/")
public class ADRController {

    private final ADRService adrService;
    private final CommitHistoryClient commitHistoryClient;
    private final Logger log = LoggerFactory.getLogger(ADRController.class);

    @Autowired
    public ADRController(ADRService adrService, CommitHistoryClient commitHistoryClient) {
        this.adrService = adrService;
        this.commitHistoryClient = commitHistoryClient;
    }

    /**
     * Controller method for retrieving an ADR by its unique identifier.
     * This endpoint allows you to fetch a specific ADR by providing its unique ID. If the ADR with the
     * specified ID is found in the database, it will be returned with an HTTP status of 200 (OK).
     * If the ADR is not found or if there's a service exception, it will return an appropriate HTTP
     * status code along with the error message.
     *
     * @param id The unique identifier of the ADR to retrieve.
     * @return ResponseEntity containing the ADR object if found, or an error message with an appropriate
     * HTTP status code if the ADR is not found or if there's a service exception.
     */
    @Operation(
            summary = "Get single adr",
            description = "Fetches single ADR from storage via ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "resource not found")
    })
    @CrossOrigin(origins = "http://localhost:4200") // only allows access from our frontend
    @GetMapping(value = "v2/adr/{id}")
    public ResponseEntity<Object> getADR(@PathVariable int id) {
        try {
            ADR adr = adrService.getADR(id);
            return new ResponseEntity<>(adr, HttpStatus.OK);
        } catch (ServiceException e) {
            return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Controller method for retrieving ADRs
     * This endpoint allows you to fetch all ADRs
     *
     * @return List of ADRs from memory
     */
    @Operation(
            summary = "Get all adrs",
            description = "Get a list of all ADRs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @CrossOrigin(origins = "http://localhost:4200") // only allows access from our frontend
    @GetMapping(value = "v2/getAllADRs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllADRs() {
        List<ADR> result = adrService.getAll();
        log.info("List of ADRs consists of {} adrs", result.toArray().length);
        return new ResponseEntity<>(result, HttpStatus.OK);
        //TODO: error handling if not successful
    }


    /**
     * Controller method for retrieving ADRs from a repository.
     * This endpoint allows you to fetch ADRs from a specific repository
     *
     * @return List of ADRs from memory
     */
    @Operation(
            summary = "Get all adrs of current project",
            description = "Get a list of all ADRs in the current project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @CrossOrigin(origins = "http://localhost:4200") // only allows access from our frontend
    @GetMapping(value = "v2/getAllADRsOfProject", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllADRsOfCurrentProject(
            @RequestParam int importTaskId
    ) {
        List<ADR> result = adrService.getAllByProject(importTaskId);
        log.info("List of ADRs consists of {} adrs", result.toArray().length);
        return new ResponseEntity<>(result, HttpStatus.OK);
        //TODO: error handling
    }

    /**
     * Controller method for retrieving Architectural Decision Records (ADRs) by their status.
     * This endpoint allows you to fetch ADRs from the database based on their status.
     *
     * @param status The status of ADRs to retrieve.
     * @return ResponseEntity containing a list of ADRs filtered by the specified status with an HTTP status of 200 (OK).
     */
    @Operation(
            summary = "Get list of ADRs by status",
            description = "Get a list of ADRs from storage with provided status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @CrossOrigin(origins = "http://localhost:4200") // only allows access from our frontend
    @GetMapping(value = "v2/getByStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getByStatus(
            @RequestParam String status
    ) {
        List<ADR> result = adrService.getByStatus(status);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Controller method for retrieving Architectural Decision Records (ADRs) by query, pageOffset and limit.
     * This endpoint allows you to retrieve a number of ADRs based on pageOffset and limit and also considers filtering with query
     *
     * @param query searchText
     * @param pageOffset offset of a page
     * @param limit limit of ADRs per page
     * @return ResponseEntity containing a list of ADRs
     */
    @Operation(
            summary = "Get page of ADRs by pageOffset and limit",
            description = "Get a page of ADRs from storage by pageOffset and limit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @CrossOrigin(origins = "http://localhost:4200") // only allows access from our frontend
    @GetMapping(value = "v2/ADR", produces = MediaType.APPLICATION_JSON_VALUE)
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
     * This endpoint allows you to fetch commit history for a particular file in a GitHub repository
     * based on the repository owner, repository name, file path, and branch.
     *
     * @param repoOwner GitHub Username of the repository owner.
     * @param repoName  Name of the repository stored in GitHub.
     * @param filePath  Path to the file in the repository for which commit history is requested.
     * @param branch    GitHub branch.
     * @return ResponseEntity containing the commit history of the specified file with an HTTP status of 200 (OK).
     * @throws IOException Signals that an I/O exception to some sort has occurred.
     */
    @Operation(
            summary = "Get commit history",
            description = "Get the complete commit history to a single file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @CrossOrigin(origins = "http://localhost:4200") // only allows access from our frontend
    @GetMapping(value = "v2/getHistory", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getHistory(
            @RequestParam String repoOwner,
            @RequestParam String repoName,
            @RequestParam String filePath,
            @RequestParam String branch
    ) throws IOException {
        Object result = commitHistoryClient.getHistory(repoOwner, repoName, filePath, branch);
        log.info(result.toString());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Clears the cache of repositories
     */
    @Operation(
            summary = "Clear cache",
            description = "Remove all objects from cache")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @CrossOrigin(origins = "http://localhost:4200") // only allows access from our frontend
    @GetMapping(value = "v2/clear")
    public void clear() {
        log.info("Clearing cache");
        adrService.clear();
    }
}
