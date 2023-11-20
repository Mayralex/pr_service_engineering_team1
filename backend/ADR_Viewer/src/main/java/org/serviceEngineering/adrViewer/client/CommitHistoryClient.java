package org.serviceEngineering.adrViewer.client;

import org.serviceEngineering.adrViewer.div.GraphqlRequestBody;
import org.serviceEngineering.adrViewer.div.GraphqlSchemaReaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.function.Consumer;


@Service
public class CommitHistoryClient {
    private final Logger log = LoggerFactory.getLogger(CommitHistoryClient.class);
    @Value("${github.api.graphql}")
    private String githubApiUrl;
    @Value("${github.api.token}")
    private String githubApiToken;

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

        return webClient.post()
                .uri(githubApiUrl)
                .bodyValue(graphQLRequestBody)
                .headers(consumer)
                .retrieve()
                .bodyToMono(String.class)
                .log()
                .block();
    }
}
