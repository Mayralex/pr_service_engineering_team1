package org.serviceEngineering.adrViewer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.serviceEngineering.adrViewer.controller.ADRController;
import org.serviceEngineering.adrViewer.controller.ImportController;
import org.serviceEngineering.adrViewer.div.ADRParser;
import org.serviceEngineering.adrViewer.entity.ADR;
import org.serviceEngineering.adrViewer.entity.Artifact;
import org.serviceEngineering.adrViewer.entity.Relation;
import org.serviceEngineering.adrViewer.repository.ADRRepository;
import org.serviceEngineering.adrViewer.service.ADRService;
import org.serviceEngineering.adrViewer.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdrViewerApplicationTests {

	private static final String owner = "flohuemer";
	private static final String repoName = "graal";
	private static final String directoryPath = "wasm/docs/arch";
	private final String filePath = "wasm/docs/arch/adr-001.md";
	private static final String branch = "adrs";

	@Autowired
	private ADRService ADRService;

	@Autowired
	private ImportService importService;

	@Autowired
	private ADRController adrController;

	@Autowired
	private ImportController importController;

	@Autowired
	private ADRRepository adrRepository;

	@Autowired
	private MockMvc mockMvc;

	private static ADR initTestADR() {
		ADR adr = new ADR();

		adr.setTitle("ADR 001: Pipeline architecture");
		adr.setContext("WebAssembly is designed to be decoded, validated and compiled in a single pass. This equally applies to JIT and AOT compilers.Furthermore, the WebAssembly bytecode format defines an element section and a data section for the initial data of the table and memory of a module. In addition, imports and exports are defined by every module to allow the usage of external resources and to provide resources to the embedding environment or other modules.Typically compilers are implemented as a pipeline architecture that has several steps and transforms the input source code into one or several intermediate representations. It starts off in a parser that generates some form of intermediate representation. This intermediate representation is then either executed in an interpreter or, after some potential optimizations, directly compiled to machine code. There might be some intermediate step for linking if the source format supports several modules.The Truffle framework uses abstract syntax trees as its intermediate representation. Guest languages like GraalWasm are required to transform their source code into an AST for Truffle to be able to partially evaluate and compile the resulting interpreter. Any other form or running code is currently not supported.");
		adr.setDecision("We will implement a three step pipeline architecture for GraalWasm. In the first step, the bytecode is parsed and validated. The output of this parsing step is an AST containing the control structures of the bytecode and actions that need to be performed during linking. In the second step, the linker executes all actions provided by the parsing step, resolves imports and exports and initializes the table and memory of every module. In the third step, the AST is executed in an interpreter that gets automatically optimized by the Truffle framework.");
		adr.setStatus("Active");
		adr.setConsequences("Having several steps allows for optimizations before the execution of the code in the interpreter. Furthermore, this makes the compliance to the Truffle frameworks AST representation easier. In addition, this architecture is easier to comprehend than having everything in a single step. This architecture is also easier to test, since the individual steps can be tested independent of each other.This approach will increase warmup time since additional work has to be performed before executing the code. In addition, extending the code with new features might imply changes in several locations.");
		adr.setArtifacts(new ArrayList<>(List.of(new Artifact(1, "org.graalvm.wasm.BinaryStreamParser=../../src/org.graalvm.wasm/src/org/graalvm/wasm/BinaryStreamParser.java", adr))));
		adr.setRelations(new ArrayList<>(List.of(new Relation(1, "enables", "ADR 002", adr))));
		adr.setDate("9999-12-31");
		adr.setCommit("179a300ef29");
		return adr;
	}

	private static MultiValueMap<String, String> setUpParams() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("repoOwner", owner);
		params.add("repoName", repoName);
		params.add("directoryPath", directoryPath);
		params.add("branch", branch);
		return params;
	}

	@BeforeEach
	public void deleteAll() {
		adrRepository.deleteAll();
	}

	@Test
	void contextLoads() {
		assertThat(ADRService).isNotNull();
		assertThat(adrController).isNotNull();
	}

	@Test
	void connectGithub() {
		adrRepository.deleteAll();
		assertThat(importService.fetchRepositoryContent(owner, repoName, directoryPath, branch)).isNotNull();
	}

	@Test
	void extractADRsAndCheckNumberOfADRs() {
		int nunADRs = 27;
		adrRepository.deleteAll();
		assertThat(importService.fetchRepositoryContent(owner, repoName, directoryPath, branch)).hasSize(nunADRs);
	}

	@Test
	void parseSingleADRToJSONNotNull() {
		assertThat(importService.parseADRFile(owner, repoName, filePath, branch)).isNotNull();
	}

	@Test
	void parseADRToEntity() {
		ADR adr = initTestADR();
		String markdown = importService.fetchADRFile(owner, repoName, filePath, branch);
		String html = ADRParser.convertMarkdownToHTML(markdown);
		ADR result = ADRParser.convertHTMLToADR(html);
		assertThat(result.getTitle()).isEqualTo(adr.getTitle());
		assertThat(result.getContext()).isEqualTo(adr.getContext());
		assertThat(result.getDecision()).isEqualTo(adr.getDecision());
		assertThat(result.getStatus()).isEqualTo(adr.getStatus());
		assertThat(result.getConsequences()).isEqualTo(adr.getConsequences());
		assertThat(result.getCommit()).isEqualTo(adr.getCommit());
	}

	@Test
	void readSingleADR() {
		assertThat(importService.fetchADRFile(owner, repoName, filePath, branch)).isNotNull();
	}

	@Test
	void bulkParse() throws Exception {
		MultiValueMap<String, String> params = setUpParams();
		this.mockMvc.perform(get("/api/v2/getAllADRs").params(params)).andDo(print()).andExpect(status().isOk());
	}

	@Test
	void getByStatus() throws InterruptedException {
		importController.parseRepository(owner, repoName, directoryPath, branch);
		adrController.getAllADRs();
		try {
			Thread.sleep(25000);
		} catch (InterruptedException e) {
			throw e;
		}
		assertThat(adrController.getAllADRs().getBody()).asList().hasSize(27);
		assertThat(adrController.getByStatus("active").getBody()).asList().hasSize(19);
		assertThat(adrController.getByStatus("deprecated").getBody()).asList().hasSize(8);
	}

	//@Test
	void getById() {
		ADR adr = initTestADR();
		adr.setId(1);
		//solve error: org.hibernate.LazyInitializationException: could not initialize proxy
		ADR result = (ADR) adrController.getADR(1).getBody();
		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo(adr.getTitle());
		assertThat(result.getContext()).isEqualTo(adr.getContext());
		assertThat(result.getDecision()).isEqualTo(adr.getDecision());
		assertThat(result.getStatus()).isEqualTo(adr.getStatus());
		assertThat(result.getConsequences()).isEqualTo(adr.getConsequences());
		assertThat(result.getArtifacts()).isEqualTo(adr.getArtifacts());
		assertThat(result.getRelations()).isEqualTo(adr.getRelations());
	}
}
