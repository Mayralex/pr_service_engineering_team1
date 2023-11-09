package org.serviceEngineering.adrViewer.Controller;

import org.serviceEngineering.adrViewer.entity.ADR;
import org.serviceEngineering.adrViewer.entity.RestResponse;
import org.serviceEngineering.adrViewer.exceptions.ServiceException;
import org.serviceEngineering.adrViewer.service.ADRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@RequestMapping("/api/v2")
public class ADRControllerV2 {

    private final ADRService adrService;
    private final RestTemplate restTemplate;
    private final Logger log = LoggerFactory.getLogger(ADRControllerV2.class);
    @Autowired
    public ADRControllerV2(RestTemplate restTemplate,
                           ADRService adrService) {
        this.restTemplate = restTemplate;
        this.adrService = adrService;
    }

    /**
     * Controller method for retrieving an ADR by its unique identifier.
     * <p>
     * This endpoint allows you to fetch a specific ADR by providing its unique ID. If the ADR with the
     * specified ID is found in the database, it will be returned with an HTTP status of 200 (OK).
     * If the ADR is not found or if there's a service exception, it will return an appropriate HTTP
     * status code along with the error message.
     *
     * @param id The unique identifier of the ADR to retrieve.
     * @return ResponseEntity containing the ADR object if found, or an error message with an appropriate
     * HTTP status code if the ADR is not found or if there's a service exception.
     */
    @GetMapping(value = "/getADR")
    public ResponseEntity<Object> getADR(@RequestParam long id) {
        try{
            ADR adr = adrService.getADR(id);
            return new ResponseEntity<>(adr, HttpStatus.OK);
        } catch (ServiceException e){
            return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Controller method for retrieving ADRs from a repository.
     *
     * This endpoint allows you to fetch ADRs from a specific repository by specifying the repository owner,
     * repository name, directory path, and branch. It first checks if there are any ADRs in the memory,
     * and if so, it returns them. If not, it fetches ADRs from the specified repository using a REST API call
     * and saves them in the database for future use.
     *
     * @param repoOwner The owner of the repository where ADRs are stored.
     * @param repoName The name of the repository where ADRs are stored.
     * @param directoryPath The path to the directory within the repository where ADRs are located.
     * @param branch The branch in the repository from which ADRs should be fetched.
     * @return ResponseEntity containing an array of ADR objects if ADRs are found, or an empty array if no ADRs are available.
     */
    @GetMapping(value = "/getAllADRs", produces = MediaType.APPLICATION_JSON_VALUE)
    //public List<ADR> getAllADRs(
    public ResponseEntity<Object> getAllADRs(
            @RequestParam String repoOwner,
            @RequestParam String repoName,
            @RequestParam String directoryPath,
            @RequestParam String branch
    ) {
        List<ADR> result = adrService.getAll();
        log.info(result.toString());
        if (!result.isEmpty()) new ResponseEntity<>(result, HttpStatus.OK);
        RestResponse[] list = adrService.fetchRepositoryContent(repoOwner, repoName, directoryPath, branch);

        for (RestResponse response : list) {
            if (!response.getType().equals("file")) continue;
            String filepath = response.getPath();
            log.info(filepath);
            ADR adr = adrService.parseADRFile(repoOwner, repoName, filepath, branch);
            adrService.save(adr);
            result.add(adr);
        }
        //adrService.saveAll(result);
        //return result;
        //return restTemplate.getForEntity(result.toString(), RestResponse[].class).getBody();
        log.info(result.toString());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
