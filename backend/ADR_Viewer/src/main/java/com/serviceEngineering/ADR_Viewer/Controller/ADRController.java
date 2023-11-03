package com.serviceEngineering.ADR_Viewer.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.serviceEngineering.ADR_Viewer.ADRParser;
import com.serviceEngineering.ADR_Viewer.entity.RestResponse;
import com.serviceEngineering.ADR_Viewer.service.ADRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ADRController {

    @Autowired
    private ADRParser adrParser;

    @Autowired
    private ADRService ADRService;

    @GetMapping("/scanADRs")
    public RestResponse[] scanADRs(
            @RequestParam String owner,
            @RequestParam String repoName,
            @RequestParam String directoryPath,
            @RequestParam String branch) {
        return ADRService.fetchRepositoryContent(owner, repoName, directoryPath, branch);
    }

    @GetMapping("/fetchFile")
    public String fetchFile(
            @RequestParam String owner,
            @RequestParam String repoName,
            @RequestParam String filePath,
            @RequestParam String branch) {
        return ADRService.fetchADRFile(owner, repoName, filePath, branch);
    }

    @GetMapping("/parseFile")
    public Object parseFile(
            @RequestParam String owner,
            @RequestParam String repoName,
            @RequestParam String filePath,
            @RequestParam String branch
    ) throws JsonProcessingException {
        return ADRService.parseADRFile(owner, repoName, filePath, branch);
    }

    @GetMapping("/convertFile")
    public Object convertFileToHtml(
            @RequestParam String owner,
            @RequestParam String repoName,
            @RequestParam String filePath,
            @RequestParam String branch
    ) {
        return ADRService.parseADRFileToHTML(owner, repoName, filePath, branch);
    }
}
