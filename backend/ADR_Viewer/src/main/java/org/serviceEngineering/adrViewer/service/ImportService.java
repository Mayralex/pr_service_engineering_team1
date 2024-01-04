package org.serviceEngineering.adrViewer.service;

import org.serviceEngineering.adrViewer.div.ADRParser;
import org.serviceEngineering.adrViewer.entity.ADR;
import org.serviceEngineering.adrViewer.entity.ImportTask;
import org.serviceEngineering.adrViewer.entity.RestResponse;
import org.serviceEngineering.adrViewer.exceptions.ServiceException;
import org.serviceEngineering.adrViewer.repository.ADRRepository;
import org.serviceEngineering.adrViewer.repository.ImportTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.cache.annotation.CacheDefaults;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@CacheDefaults(cacheName = "adr")
public class ImportService {
    private final RestTemplate restTemplate;
    @Value("${github.api.url}")
    private String githubApiUrl;
    @Value("${github.api.token}")
    private String githubApiToken;

    private final ADRRepository aDRRepository;
    private final ImportTaskRepository importTaskRepository;

    private final Logger log = LoggerFactory.getLogger(ImportService.class);

    private Thread workerThread;

    @Autowired
    public ImportService(RestTemplate restTemplate,
                         ADRRepository aDRRepository,
                         ImportTaskRepository importTaskRepository) {
        this.restTemplate = restTemplate;
        this.aDRRepository = aDRRepository;
        this.importTaskRepository = importTaskRepository;
    }

    /**
     * Retrieves an import task based on it's id
     * @param id the id of the import task to retrieve
     * @return requested import task
     */
    public ImportTask getImportTask(int id) {
        return importTaskRepository.findImportTaskById(id);
    }

    /**
     * Start parsing a repository
     *
     * @param repoOwner: GitHub Username of the person created the GitHub repository
     * @param repoName: Name of the repository stored in GitHub
     * @param directoryPath: Path to the directory where to ADRs are stores ---> To be decommissioned
     * @param branch: Github branch
     * @return the newly created import task
     */
    public ImportTask parseRepository(String repoOwner, String repoName, String directoryPath, String branch) {
        var newImportTask = new ImportTask(false, LocalDateTime.now().toString(), repoOwner, repoName, directoryPath, branch);
        importTaskRepository.save(newImportTask);

        final var repoContent = fetchRepositoryContent(repoOwner, repoName, directoryPath, branch);
        final int importTaskId = newImportTask.getId();

        // Halt execution of current task & start new one
        if (workerThread != null && workerThread.isAlive()) {
            workerThread.interrupt();
        }
        workerThread = new Thread(() -> {
            parseList(repoContent, repoOwner, repoName, branch, importTaskId);

            if (Thread.currentThread().isInterrupted()) {
                log.info("Parsing of adrs for import task {} did not finish.", importTaskId);
                return;
            }

            var retrievedImportTask = importTaskRepository.findImportTaskById(importTaskId);
            retrievedImportTask.setFinished(true);
            importTaskRepository.save(retrievedImportTask);

            log.info("Parsing for import task {} has completed", importTaskId);
        });
        workerThread.start();

        return newImportTask;
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
                restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), RestResponse[].class);

        // TODO: Check for failure

        return responseEntity.getBody();
    }

    /**
     * Parses a list of RestResponse items representing ADR files from a repository.
     * Parses each ADR file and saves it to memory
     *
     * @param list      Array of RestResponse items obtained from the repository directory.
     * @param repoOwner GitHub Username of the person who created the GitHub repository.
     * @param repoName  Name of the repository stored in GitHub.
     * @param branch    GitHub branch.
     */
    public void parseList(RestResponse[] list, String repoOwner, String repoName, String branch, int importTaskId) {
        log.info("parsing adrs from repo {}", repoName);

        for (RestResponse response : list) {
            if (!response.getType().equals("file")) {
                continue;
            }

            String filepath = response.getPath();
            log.info(filepath);

            ADR adr = parseADRFile(repoOwner, repoName, filepath, branch);
            adr.setImportTaskId(importTaskId);

            if (Thread.currentThread().isInterrupted()) {
                log.info("Parsing of adrs for import task {} has been aborted.", importTaskId);
                break;
            }

            log.info("Saving adr #{} titled {}", adr.getId(), adr.getTitle());
            aDRRepository.save(adr);
        }
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
     *  Service method for fetching the content of a specific ADR file from a GitHub repository.
     * @param owner    GitHub Username of the person created the GitHub repository
     * @param repoName Name of the repository stored in GitHub
     * @param filePath Path to the markdown file
     * @param branch   GitHub branch
     * @return markdown file as String
     */
    public String fetchADRFile(String owner, String repoName, String filePath, String branch) {
        if (filePath.endsWith(".md")) {
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json");
            headers.add("Authorization", "Bearer " + githubApiToken);
            String apiUrl = String.format("%s/repos/%s/%s/contents/%s?ref=%s", githubApiUrl, owner, repoName, filePath, branch);
            ResponseEntity<RestResponse> responseEntity =
                    restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), RestResponse.class);
            log.info(String.valueOf(responseEntity));
            return restTemplate.exchange(Objects.requireNonNull(responseEntity.getBody()).getDownload_url(), HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
        } else throw new ServiceException("Path not pointing to markdown file", filePath);
    }
}
