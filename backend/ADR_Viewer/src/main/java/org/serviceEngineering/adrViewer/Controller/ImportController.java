package org.serviceEngineering.adrViewer.Controller;

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

    @Operation(summary = "Retrieves details about a import task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    @CrossOrigin(origins = "http://localhost:4200") // only allows access from our frontend
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImportTask> getImportTaskStatus(@PathVariable int id) {
        var importTask = importService.getImportTask(id);
        return new ResponseEntity<>(importTask, HttpStatus.OK);
    }

}
