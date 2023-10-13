package com.serviceEngineering.ADR_Viewer.Controller;

import com.serviceEngineering.ADR_Viewer.ADRParser;
import com.serviceEngineering.ADR_Viewer.service.GithubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ADRController {

    @Autowired
    private ADRParser adrParser;

    @Autowired
    private GithubService githubService;

    @GetMapping("/scanADRs")
    public String scanADRs(
            @RequestParam String owner,
            @RequestParam String repoName,
            @RequestParam String filePath,
            @RequestParam String branch) {
        String content = githubService.fetchRepositoryContent(owner, repoName, filePath, branch);
        //return adrParser.parseADRs(content);
        return content;
    }
}
