package com.serviceEngineering.ADR_Viewer.service;

import com.serviceEngineering.ADR_Viewer.ADRParser;
import com.serviceEngineering.ADR_Viewer.entity.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GithubService {
    private final RestTemplate restTemplate;
    @Value("${github.api.url}")
    private String githubApiUrl;
    @Value("${github.api.token}")
    private String githubApiToken;

    @Autowired
    public GithubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /***
     * TODO: create search methode in order to search for ADRs inside a project ---> Goal is that, the user does not have to specify
     *  the path to the folder
     * @param owner: Github Username of the person created the Github repository
     * @param repoName: Name of the repository stored in Github
     * @param filePath: Path to the directory where to ADRs are stores ---> To be decommissioned
     * @param branch: Github branch
     * @return: Array of RestResponse.class in JSON
     */
    public RestResponse[] fetchRepositoryContent(String owner, String repoName, String filePath, String branch) {
        String apiUrl = String.format("%s/repos/%s/%s/contents/%s?ref=%s", githubApiUrl, owner, repoName, filePath, branch);

        ResponseEntity<RestResponse[]> responseEntity =
                restTemplate.getForEntity(apiUrl, RestResponse[].class);
        return responseEntity.getBody();
    }

    public String fetchADRFile(String owner, String repoName, String filePath, String branch) {
        String apiUrl = String.format("%s/repos/%s/%s/contents/%s?ref=%s", githubApiUrl, owner, repoName, filePath, branch);
        ResponseEntity<RestResponse> responseEntity =
                restTemplate.getForEntity(apiUrl, RestResponse.class);
        return restTemplate.getForEntity(responseEntity.getBody().getDownload_url(), String.class).getBody();
    }

    public String parseADRFile(String owner, String repoName, String filePath, String branch) {
        String markdown = fetchADRFile(owner, repoName, filePath, branch);
        return ADRParser.convertMarkdown(markdown);
    }
}
