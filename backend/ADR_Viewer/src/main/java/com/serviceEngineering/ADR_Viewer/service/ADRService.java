package com.serviceEngineering.ADR_Viewer.service;

import com.serviceEngineering.ADR_Viewer.ADRParser;
import com.serviceEngineering.ADR_Viewer.entity.ADR;
import com.serviceEngineering.ADR_Viewer.entity.RestResponse;
import com.serviceEngineering.ADR_Viewer.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class ADRService {
    private final RestTemplate restTemplate;
    @Value("${github.api.url}")
    private String githubApiUrl;
    @Value("${github.api.token}")
    private String githubApiToken;

    @Autowired
    public ADRService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /***
     * TODO: create search methode in order to search for ADRs inside a project ---> Goal is that, the user does not have to specify
     *  the path to the folder
     * @param owner: GitHub Username of the person created the GitHub repository
     * @param repoName: Name of the repository stored in GitHub
     * @param directoryPath: Path to the directory where to ADRs are stores ---> To be decommissioned
     * @param branch: Github branch
     * @return: Array of RestResponse. Class in JSON
     */
    public RestResponse[] fetchRepositoryContent(String owner, String repoName, String directoryPath, String branch) {
        String apiUrl = String.format("%s/repos/%s/%s/contents/%s?ref=%s", githubApiUrl, owner, repoName, directoryPath, branch);

        ResponseEntity<RestResponse[]> responseEntity =
                restTemplate.getForEntity(apiUrl, RestResponse[].class);
        return responseEntity.getBody();
    }

    /**
     * @param owner    GitHub Username of the person created the GitHub repository
     * @param repoName Name of the repository stored in GitHub
     * @param filePath Path to the markdown file
     * @param branch   GitHub branch
     * @return Markdown file as String
     */
    public String fetchADRFile(String owner, String repoName, String filePath, String branch) {
        if (filePath.endsWith(".md")) {
            String apiUrl = String.format("%s/repos/%s/%s/contents/%s?ref=%s", githubApiUrl, owner, repoName, filePath, branch);
            ResponseEntity<RestResponse> responseEntity =
                    restTemplate.getForEntity(apiUrl, RestResponse.class);
            return restTemplate.getForEntity(Objects.requireNonNull(responseEntity.getBody()).getDownload_url(), String.class).getBody();
        } else throw new ServiceException("Path not pointing to markdown file", filePath);
    }

    /**
     * @param owner    GitHub Username of the person created the GitHub repository
     * @param repoName Name of the repository stored in GitHub
     * @param filePath Path to the markdown file
     * @param branch   GitHub branch
     * @return Converted markdown file into HTML
     */
    public String parseADRFileToHTML(String owner, String repoName, String filePath, String branch) {
        String markdown = fetchADRFile(owner, repoName, filePath, branch);
        return ADRParser.convertMarkdownToHTML(markdown);
    }

    /**
     *
     * @param owner    GitHub Username of the person created the GitHub repository
     * @param repoName Name of the repository stored in GitHub
     * @param filePath Path to the markdown file
     * @param branch   GitHub branch
     * @return JSON representation parsed ADR
     */
    public ResponseEntity<Object> parseADRFile(String owner, String repoName, String filePath, String branch) {
        String markdown = fetchADRFile(owner, repoName, filePath, branch);
        String html = ADRParser.convertMarkdownToHTML(markdown);
        ADR adr = ADRParser.convertHTMLToADR(html);
        return new ResponseEntity<>(adr, HttpStatus.OK);
    }


}
