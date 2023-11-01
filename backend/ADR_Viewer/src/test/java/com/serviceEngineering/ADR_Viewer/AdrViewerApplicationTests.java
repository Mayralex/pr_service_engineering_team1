package com.serviceEngineering.ADR_Viewer;

import com.serviceEngineering.ADR_Viewer.Controller.ADRController;
import com.serviceEngineering.ADR_Viewer.service.GithubService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class AdrViewerApplicationTests {

	private final String owner = "flohuemer";
	private final String repoName = "graal";
	private final String directoryPath = "wasm/docs/arch";
	private final String filePath = "wasm/docs/arch/adr-001.md";
	private final String branch = "adrs";
	private final int nunADRs = 27;

	@Mock
	private RestTemplate restTemplate;

	@Autowired
	private GithubService githubService;

	@Autowired
	private ADRController adrController;

	@Test
	public void contextLoads() throws Exception {
		assertThat(githubService).isNotNull();
		assertThat(adrController).isNotNull();
	}

	@Test
	void connectGithub() {
		assertThat(githubService.fetchRepositoryContent(owner, repoName, directoryPath, branch)).isNotNull();
	}

	@Test
	void extractADRsAndCheckNumberOfADRs() {
		assertThat(githubService.fetchRepositoryContent(owner, repoName, directoryPath, branch)).hasSize(nunADRs);
	}

	@Test
	void parseSingleADRToJSON() {

	}

	@Test
	void readSingleADR() {
		assertThat(githubService.fetchADRFile(owner, repoName, filePath, branch)).isNotNull();
	}

	@Test
	void convertADRtoHTML() {
		assertThat(githubService.parseADRFileToHTML(owner, repoName, filePath, branch)).isNotNull();
	}
}
