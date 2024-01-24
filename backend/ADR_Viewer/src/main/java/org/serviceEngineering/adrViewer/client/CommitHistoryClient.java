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

/**
 * A client for retrieving commit history for a specific file within a GitHub repository using GraphQL.
 */
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

    /**
     * Constructs a CommitHistoryClient with the specified RestTemplate.
     *
     * @param restTemplate The RestTemplate to be used for making HTTP requests.
     */
    public CommitHistoryClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Retrieves commit history for a specific file within a GitHub repository using GraphQL.
     * <p>
     * This method makes a request to the GitHub API to fetch the commit history for a particular file
     * within the specified repository and branch.
     *
     * @param owner    GitHub Username of the person who created the GitHub repository.
     * @param repoName Name of the repository stored in GitHub.
     * @param filePath Path to the file in the repository for which commit history is requested.
     * @param branch   GitHub branch.
     * @return An array of objects representing the commit history of the file.
     * @throws IOException Signals that an I/O exception to some sort has occurred.
     */
    public Object getHistory(String owner, String repoName, String filePath, String branch) throws IOException {
        MultiValueMap<String, String> headers = createHttpHeaders();

        Consumer<HttpHeaders> consumer = it -> it.addAll(headers);

        WebClient webClient = WebClient.builder().build();

        GraphqlRequestBody graphQLRequestBody = buildGraphqlRequestBody(owner, repoName, filePath, branch);
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

        processNodes(owner, repoName, nodes, filePath);

        return nodes;
    }

    /**
     * Creates and returns a MultiValueMap containing HTTP headers required for making requests to the GitHub API.
     * <p>
     * The headers include "Content-Type" set to "application/json" and "Authorization" set to the provided
     * GitHub API token with the "Bearer" prefix.
     *
     * @return MultiValueMap representing the HTTP headers.
     */
    private MultiValueMap<String, String> createHttpHeaders() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + githubApiToken);
        return headers;
    }

    /**
     * Builds a GraphQL request body for retrieving commit history based on the provided parameters.
     * <p>
     * This method constructs a GraphQLRequestBody object, sets the query and variables using the GraphQL
     * schema obtained from external files, and replaces placeholder values with the actual parameters.
     *
     * @param owner    GitHub Username of the person who created the GitHub repository.
     * @param repoName Name of the repository stored in GitHub.
     * @param filePath Path to the file in the repository for which commit history is requested.
     * @param branch   GitHub branch.
     * @return A GraphQLRequestBody object representing the GraphQL request body.
     * @throws IOException Signals that an I/O exception to some sort has occurred.
     */
    private GraphqlRequestBody buildGraphqlRequestBody(String owner, String repoName, String filePath, String branch) throws IOException {
        GraphqlRequestBody graphQLRequestBody = new GraphqlRequestBody();
        final String query = GraphqlSchemaReaderUtil.getSchemaFromFileName("getCommitHistory");
        final String variables = GraphqlSchemaReaderUtil.getSchemaFromFileName("variables");

        graphQLRequestBody.setQuery(query);
        graphQLRequestBody.setVariables(variables.replace("demoOwner", owner).replace("demoName", repoName)
                .replace("demoFilePath", filePath).replace("demoBranch", branch));

        return graphQLRequestBody;
    }


    /**
     * Processes an array of Nodes by retrieving commit information and updating their status.
     * <p>
     * For each Node in the given array, this method calls the searchCommit method to fetch commit information
     * based on the owner, repository name, commit OID, and file path. It then logs the raw URL of the retrieved
     * Files and sets the status of the current Node using the getHistoricStatus method.
     *
     * @param owner    GitHub Username of the person who created the GitHub repository.
     * @param repoName Name of the repository stored in GitHub.
     * @param nodes    Array of Nodes representing commit history information.
     * @param filePath Path to the file in the repository for which commit history is being processed.
     */
    private void processNodes(String owner, String repoName, Nodes[] nodes, String filePath) {
        for (Nodes node : nodes) {
            Files files = searchCommit(owner, repoName, node.getOid(), filePath);
            log.info(files.getRaw_url());
            node.setStatus(getHistoricStatus(files.getRaw_url()));
        }
    }

    /**
     * Searches for commit information related to a specific file in a GitHub repository.
     * <p>
     * This method constructs the API URL for retrieving commit information based on the provided owner,
     * repository name, commit OID, and file path. It then makes a GET request to the GitHub API using
     * the RestTemplate, with the necessary headers, and retrieves the CommitResponse. Finally, it filters
     * and finds the Files matching the specified file path and returns the result.
     *
     * @param owner     GitHub Username of the person who created the GitHub repository.
     * @param repoName  Name of the repository stored in GitHub.
     * @param commitOid Commit OID representing a specific commit in the repository.
     * @param filePath  Path to the file in the repository for which commit information is sought.
     * @return Files object representing the commit information for the specified file, or null if not found.
     */
    private Files searchCommit(String owner, String repoName, String commitOid, String filePath) {
        String apiUrl = String.format("%s/repos/%s/%s/commits/%s", githubApiUrl, owner, repoName, commitOid);
        MultiValueMap<String, String> headers = createHttpHeaders();

        ResponseEntity<CommitResponse> responseEntity =
                restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), CommitResponse.class);

        return Arrays.stream(Objects.requireNonNull(responseEntity.getBody()).getFiles())
                .filter(files -> filePath.equals(files.getFilename()))
                .findAny().orElse(null);
    }

    /**
     * Retrieves historic status information for an ADR using its URL from a commit.
     * <p>
     * This method constructs the API URL for retrieving historic status information based on the provided URL,
     * replaces URL-encoded characters, and makes a GET request to fetch the markdown file content from the GitHub API.
     * The method then logs the content and returns the status obtained by converting the markdown content to HTML
     * and subsequently converting it to an Architecture Decision Record (ADR).
     *
     * @param url The URL of the ADR for which historic status information is sought.
     * @return A string representing the historic status of the file.
     */
    private String getHistoricStatus(String url) {
        MultiValueMap<String, String> headers = createHttpHeaders();
        url = url.replace("%2F", "/");

        String markdownFile = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
        log.info(markdownFile);

        return convertHTMLToADR(convertMarkdownToHTML(markdownFile)).getStatus();
    }
}
