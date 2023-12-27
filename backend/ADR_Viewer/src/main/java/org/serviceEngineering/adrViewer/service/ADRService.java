package org.serviceEngineering.adrViewer.service;

import jakarta.persistence.EntityNotFoundException;
import org.serviceEngineering.adrViewer.div.ADRParser;
import org.serviceEngineering.adrViewer.entity.ADR;
import org.serviceEngineering.adrViewer.entity.RestResponse;
import org.serviceEngineering.adrViewer.exceptions.ServiceException;
import org.serviceEngineering.adrViewer.repository.ADRRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@CacheDefaults(cacheName = "adr")
public class ADRService {
    private final RestTemplate restTemplate;
    @Value("${github.api.url}")
    private String githubApiUrl;
    @Value("${github.api.token}")
    private String githubApiToken;
    private final ADRRepository aDRRepository;

    private final Logger log = LoggerFactory.getLogger(ADRService.class);

    @Autowired
    public ADRService(RestTemplate restTemplate,
                      ADRRepository aDRRepository) {
        this.restTemplate = restTemplate;
        this.aDRRepository = aDRRepository;
    }

    /***
     * Service method for fetching the contents of a GitHub repository directory, including ADR files.
     * TODO: create search methode in order to search for ADRs inside a project ---> Goal is that, the user does not have to specify
     *  the path to the folder
     * @param owner: GitHub Username of the person created the GitHub repository
     * @param repoName: Name of the repository stored in GitHub
     * @param directoryPath: Path to the directory where to ADRs are stores ---> To be decommissioned
     * @param branch: Github branch
     * @return Array of RestResponse. Class in JSON
     */
    public RestResponse[] fetchRepositoryContent(String owner, String repoName, String directoryPath, String branch) {
        String apiUrl = String.format("%s/repos/%s/%s/contents/%s?ref=%s", githubApiUrl, owner, repoName, directoryPath, branch);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + githubApiToken);

        ResponseEntity<RestResponse[]> responseEntity =
                restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<Object>(headers), RestResponse[].class);
        return responseEntity.getBody();
    }

    /**
     *  Service method for fetching the content of a specific ADR file from a GitHub repository.
     * @param owner    GitHub Username of the person created the GitHub repository
     * @param repoName Name of the repository stored in GitHub
     * @param filePath Path to the markdown file
     * @param branch   GitHub branch
     * @return Markdown file as String
     */
    public String fetchADRFile(String owner, String repoName, String filePath, String branch) {
        if (filePath.endsWith(".md")) {
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json");
            headers.add("Authorization", "Bearer " + githubApiToken);
            String apiUrl = String.format("%s/repos/%s/%s/contents/%s?ref=%s", githubApiUrl, owner, repoName, filePath, branch);
            ResponseEntity<RestResponse> responseEntity =
                    restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<Object>(headers), RestResponse.class);
            log.info(String.valueOf(responseEntity));
            return restTemplate.exchange(Objects.requireNonNull(responseEntity.getBody()).getDownload_url(), HttpMethod.GET, new HttpEntity<Object>(headers), String.class).getBody();
        } else throw new ServiceException("Path not pointing to markdown file", filePath);
    }

    /**
     * Service method for parsing an ADR markdown file to HTML format
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
     * !!DEPRECATED!!
     * Service method for parsing an ADR markdown file to JSON format.
     * @param owner    GitHub Username of the person created the GitHub repository
     * @param repoName Name of the repository stored in GitHub
     * @param filePath Path to the markdown file
     * @param branch   GitHub branch
     * @return JSON representation parsed ADR
     */
    public ResponseEntity<Object> parseADRFileDeprecated(String owner, String repoName, String filePath, String branch) {
        String markdown = fetchADRFile(owner, repoName, filePath, branch);
        String html = ADRParser.convertMarkdownToHTML(markdown);
        ADR adr = ADRParser.convertHTMLToADR(html);
        return new ResponseEntity<>(aDRRepository.save(adr), HttpStatus.OK);
    }

    /**
     * Service method for parsing an ADR markdown file to an ADR object.
     *
     * @param owner    GitHub Username of the person who created the GitHub repository.
     * @param repoName Name of the repository stored in GitHub.
     * @param filePath Path to the markdown file.
     * @param branch   GitHub branch.
     * @return Parsed ADR object.
     */
    public ADR parseADRFile(String owner, String repoName, String filePath, String branch) {
        String markdown = fetchADRFile(owner, repoName, filePath, branch);
        String html = ADRParser.convertMarkdownToHTML(markdown);
        return ADRParser.convertHTMLToADR(html);
    }

    /**
     * Service method for saving a list of ADR objects to the database.
     *
     * @param adrList List of ADRs to be saved.
     */
    public void saveAll(List<ADR> adrList) {
        log.info("Saving {} ADRs from bulk parsing", adrList.size());
        aDRRepository.saveAll(adrList);
    }

    public void save(ADR adr) {
        log.info("Saving adr #{} titled {}", adr.getId(), adr.getTitle());
        aDRRepository.save(adr);
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

    /**
     * Asynchronously parses a list of RestResponse items representing ADR files from a repository.
     * Parses each ADR file, saves it to the memory, and returns a CompletableFuture containing a list of parsed ADR objects.
     *
     * @param list      Array of RestResponse items obtained from the repository directory.
     * @param repoOwner GitHub Username of the person who created the GitHub repository.
     * @param repoName  Name of the repository stored in GitHub.
     * @param branch    GitHub branch.
     * @return CompletableFuture containing a List of parsed ADR objects saved in the database.
     */
    @Async
    public CompletableFuture<List<ADR>> parseList(RestResponse[] list, String repoOwner, String repoName, String branch) {
        List<ADR> result = new ArrayList<>();
        log.info("parsing adrs from repo {}", repoName);
        for (RestResponse response : list) {
            if (!response.getType().equals("file")) continue;
            String filepath = response.getPath();
            log.info(filepath);
            ADR adr = parseADRFile(repoOwner, repoName, filepath, branch);
            save(adr);
            result.add(adr);
        }
        return CompletableFuture.completedFuture(result);
    }


    public void clear() {
        this.aDRRepository.deleteAll();
    }
}
