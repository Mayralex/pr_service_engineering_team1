package org.serviceEngineering.adrViewer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.serviceEngineering.adrViewer.entity.ImportTask;
import org.serviceEngineering.adrViewer.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Import", description = "Import Api")
@RestController
@RequestMapping("/api/v2/import_task")
public class ImportController {

    private final ImportService importService;

    @Autowired
    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    /**
     * Controller method for parsing a repository
     * This endpoint sends a post request and parses a repository
     * @param repoOwner the owner of the repository to analyze
     * @param repoName the name of the repository to analyze
     * @param directoryPath the directory path of the repository to analyze
     * @param branch the branch name of the repository to analyze
     * @return a new Import Task containing id, finished, data, repoOwner, repoName, directoryPath and branch
     */
    @Operation(summary = "Parse a repository")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @CrossOrigin(origins = "http://localhost:4200") // only allows access from our frontend
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImportTask> parseRepository(
            @RequestParam String repoOwner,
            @RequestParam String repoName,
            @RequestParam String directoryPath,
            @RequestParam String branch
    ) {
        var importTask = importService.parseRepository(repoOwner, repoName, directoryPath, branch);
        return new ResponseEntity<>(importTask, HttpStatus.OK);
    }

    /**
     * Controller method for retrieving an import task by its id
     * This endpoint allows you to get an import task based on its id
     * @param id the ID of the Import Task
     * @return an Import Task
     */
    @Operation(summary = "Retrieves details about a import task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @CrossOrigin(origins = "http://localhost:4200") // only allows access from our frontend
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImportTask> getImportTaskById(@PathVariable int id) {
        var importTask = importService.getImportTask(id);
        return new ResponseEntity<>(importTask, HttpStatus.OK);
    }

    /**
     * Controller method for retrieving the latest import task
     * This endpoint allows you to get the latest created import task from the repository
     * @return the latest import Task
     */
    @Operation(summary = "Retrieves the latest import task in the repo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @CrossOrigin(origins = "http://localhost:4200") // only allows access from our frontend
    @GetMapping(value = "/last", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImportTask> getLastImportTask() {
        var importTask = importService.getLastImportTask();
        return new ResponseEntity<>(importTask, HttpStatus.OK);
        //TODO: Fallback if no last import task available (Flag?, check if getById1 works?)
    }
}
