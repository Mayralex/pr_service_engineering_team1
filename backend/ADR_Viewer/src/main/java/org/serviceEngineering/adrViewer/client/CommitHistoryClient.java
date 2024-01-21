package org.serviceEngineering.adrViewer.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.serviceEngineering.adrViewer.div.GraphqlRequestBody;
import org.serviceEngineering.adrViewer.div.GraphqlSchemaReaderUtil;
import org.serviceEngineering.adrViewer.entity.CommitResponse;
import org.serviceEngineering.adrViewer.entity.Files;
import org.serviceEngineering.adrViewer.entity.Nodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

import static org.serviceEngineering.adrViewer.div.ADRParser.convertHTMLToADR;
import static org.serviceEngineering.adrViewer.div.ADRParser.convertMarkdownToHTML;


@Service
public class CommitHistoryClient {
    private final Logger log = LoggerFactory.getLogger(CommitHistoryClient.class);
    private final RestTemplate restTemplate;
    @Value("${github.api.token}")
    private String githubApiToken;
    @Value("${github.api.graphql}")
    private String githubApiUrlGraphQL;
    @Value("${github.api.url}")
    private String githubApiUrl;

    public CommitHistoryClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Retrieves commit history for a specific file within a GitHub repository using GraphQL.
     *
     * This method makes a request to the GitHub API to fetch the commit history for a particular file
     * within the specified repository and branch.
     *
     * @param owner    GitHub Username of the person who created the GitHub repository.
     * @param repoName Name of the repository stored in GitHub.
     * @param filePath Path to the file in the repository for which commit history is requested.
     * @param branch   GitHub branch.
     * @return object representing the commit history of the file.
     * @throws IOException Signals that an I/O exception to some sort has occurred.
     */
    public Object getHistory(String owner, String repoName, String filePath, String branch) throws IOException {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + githubApiToken);

        Consumer<HttpHeaders> consumer = it -> it.addAll(headers);

        WebClient webClient = WebClient.builder().build();

        GraphqlRequestBody graphQLRequestBody = new GraphqlRequestBody();
        final String query = GraphqlSchemaReaderUtil.getSchemaFromFileName("getCommitHistory");
        final String variables = GraphqlSchemaReaderUtil.getSchemaFromFileName("variables");

        graphQLRequestBody.setQuery(query);
        graphQLRequestBody.setVariables(variables.replace("demoOwner", owner).replace("demoName", repoName)
                .replace("demoFilePath", filePath).replace("demoBranch", branch));

        log.info(graphQLRequestBody.toString());
        ObjectMapper mapper = new ObjectMapper();
        Nodes[] nodes = webClient.post()
                .uri(githubApiUrlGraphQL)
                .bodyValue(graphQLRequestBody)
                .headers(consumer)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> jsonNode.path("data").path("repository").path("ref").path("target").path("history").path("nodes"))
                .map(node -> mapper.convertValue(node, Nodes[].class))
                .log()
                .block();

        for (Nodes node : nodes) {
            Files files = searchCommit(owner, repoName, node.getOid(), filePath);
            log.info(files.getRaw_url());
            node.setStatus(getHistoricStatus(files.getRaw_url()));
        }

        return nodes;
    }


    private Files searchCommit(String owner, String repoName, String commitOid, String filePath) {
        String apiUrl = String.format("%s/repos/%s/%s/commits/%s", githubApiUrl, owner, repoName, commitOid);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + githubApiToken);

        ResponseEntity<CommitResponse> responseEntity =
                restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), CommitResponse.class);
        return Arrays.stream(Objects.requireNonNull(responseEntity.getBody()).getFiles()).filter(files -> filePath.equals(files.getFilename())).findAny().orElse(null);
    }

    private String getHistoricStatus(String url) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + githubApiToken);
        url = url.replace("%2F", "/");

        String markdownFile = restTemplate.getForObject(url, String.class);
        log.info(markdownFile);
        return convertHTMLToADR(convertMarkdownToHTML(markdownFile)).getStatus();
    }
}
