package org.serviceEngineering.adrViewer.Controller;

import org.serviceEngineering.adrViewer.entity.RestResponse;
import org.serviceEngineering.adrViewer.service.ADRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ADRController {

    private final ADRService adrService;

    @Autowired
    public ADRController(ADRService adrService) {
        this.adrService = adrService;
    }

    @GetMapping("/scanADRs")
    public RestResponse[] scanADRs(
            @RequestParam String owner,
            @RequestParam String repoName,
            @RequestParam String directoryPath,
            @RequestParam String branch) {
        return adrService.fetchRepositoryContent(owner, repoName, directoryPath, branch);
    }

    @GetMapping("/fetchFile")
    public String fetchFile(
            @RequestParam String owner,
            @RequestParam String repoName,
            @RequestParam String filePath,
            @RequestParam String branch) {
        return adrService.fetchADRFile(owner, repoName, filePath, branch);
    }

    @GetMapping("/parseFile")
    public Object parseFile(
            @RequestParam String owner,
            @RequestParam String repoName,
            @RequestParam String filePath,
            @RequestParam String branch
    ) {
        return adrService.parseADRFile(owner, repoName, filePath, branch);
    }

    @GetMapping("/convertFile")
    public Object convertFileToHtml(
            @RequestParam String owner,
            @RequestParam String repoName,
            @RequestParam String filePath,
            @RequestParam String branch
    ) {
        return adrService.parseADRFileToHTML(owner, repoName, filePath, branch);
    }
}
