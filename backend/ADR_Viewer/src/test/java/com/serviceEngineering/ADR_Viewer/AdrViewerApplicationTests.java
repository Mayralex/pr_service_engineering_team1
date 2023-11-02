package com.serviceEngineering.ADR_Viewer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.serviceEngineering.ADR_Viewer.Controller.ADRController;
import com.serviceEngineering.ADR_Viewer.div.Status;
import com.serviceEngineering.ADR_Viewer.entity.ADR;
import com.serviceEngineering.ADR_Viewer.service.GithubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureMockMvc
class AdrViewerApplicationTests {

	private final String owner = "flohuemer";
	private final String repoName = "graal";
	private final String directoryPath = "wasm/docs/arch";
	private final String filePath = "wasm/docs/arch/adr-001.md";
	private final String branch = "adrs";
	private final int nunADRs = 27;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private GithubService githubService;

	@Autowired
	private ADRController adrController;

	private static ADR initTestADR() {
		ADR adr = new ADR();

		adr.setTitle("ADR 001: Pipeline architecture");
		adr.setContext("WebAssembly is designed to be decoded, validated and compiled in a single pass. This equally applies to JIT and AOT compilers. " +
				"Furthermore, the WebAssembly bytecode format defines an element section and a data section for the initial data of the table and memory of a module. " +
				"In addition, imports and exports are defined by every module to allow the usage of external resources and to provide resources to the embedding environment or other modules. " +
				"Typically compilers are implemented as a pipeline architecture that has several steps and transforms the input source code into one or several intermediate representations. " +
				"It starts off in a parser that generates some form of intermediate representation." +
				"This intermediate representation is then either executed in an interpreter or, after some potential optimizations, directly compiled to machine code." +
				"There might be some intermediate step for linking if the source format supports several modules. The Truffle framework uses abstract syntax trees as its intermediate representation." +
				"Guest languages like GraalWasm are required to transform their source code into an AST for Truffle to be able to partially evaluate and compile the resulting interpreter." +
				"Any other form or running code is currently not supported.");
		adr.setDecision("We will implement a three step pipeline architecture for GraalWasm." +
				"In the first step, the bytecode is parsed and validated." +
				"The output of this parsing step is an AST containing the control structures of the bytecode and actions that need to be performed during linking." +
				"In the second step, the linker executes all actions provided by the parsing step, resolves imports and exports and initializes the table and memory of every module." +
				"In the third step, the AST is executed in an interpreter that gets automatically optimized by the Truffle framework.");
		adr.setStatus(Status.ACTIVE);
		adr.setConsequences("Having several steps allows for optimizations before the execution of the code in the interpreter." +
				"Furthermore, this makes the compliance to the Truffle frameworks AST representation easier." +
				"In addition, this architecture is easier to comprehend than having everything in a single step." +
				"This architecture is also easier to test, since the individual steps can be tested independent of each other. This approach will increase warmup time since additional work has to be performed before executing the code." +
				"In addition, extending the code with new features might imply changes in several locations.");
		adr.setArtifacts("");
		adr.setRelations("");
		return adr;
	}


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
	void parseSingleADRToJSONNotNull() throws JsonProcessingException {
		assertThat(githubService.parseADRFile(owner, repoName, directoryPath, branch)).isNotNull();
	}

	@Test
	void parseADRToEntity() {
		ADR adr = initTestADR();
		String markdown = githubService.fetchADRFile(owner, repoName, filePath, branch);
		String html = ADRParser.convertMarkdownToHTML(markdown);
		ADR result = ADRParser.convertHTMLToADR(html);
		assertThat(result.getTitle()).isEqualTo(adr.getTitle());
		assertThat(result.getContext()).isEqualTo(adr.getContext());
		assertThat(result.getDecision()).isEqualTo(adr.getDecision());
		assertThat(result.getStatus()).isEqualTo(adr.getStatus());
		assertThat(result.getConsequences()).isEqualTo(adr.getConsequences());
		assertThat(result.getArtifacts()).isEqualTo(adr.getArtifacts());
		assertThat(result.getRelations()).isEqualTo(adr.getRelations());
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
