package org.serviceEngineering.adrViewer.Controller;

import org.serviceEngineering.adrViewer.entity.ADR;
import org.serviceEngineering.adrViewer.entity.RestResponse;
import org.serviceEngineering.adrViewer.exceptions.ServiceException;
import org.serviceEngineering.adrViewer.service.ADRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/v2")
public class ADRControllerV2 {

    private final ADRService adrService;
    private final Logger log = LoggerFactory.getLogger(ADRControllerV2.class);
    @Autowired
    public ADRControllerV2(ADRService adrService) {
        this.adrService = adrService;
    }

    @GetMapping(value = "/getADR")
    public ResponseEntity<Object> getADR(@RequestParam long id) {
        try{
            ADR adr = adrService.getADR(id);
            return new ResponseEntity<>(adr, HttpStatus.OK);
        } catch (ServiceException e){
            return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/getAllADRs")
    public List<ADR> getAllADRs(
            @RequestParam String repoOwner,
            @RequestParam String repoName,
            @RequestParam String directoryPath,
            @RequestParam String branch
    ) {
        ArrayList<ADR> result = new ArrayList<>();
        RestResponse[] list = adrService.fetchRepositoryContent(repoOwner, repoName, directoryPath, branch);

        for (RestResponse response : list) {
            if (!response.getType().equals("file")) continue;
            String filepath = response.getPath();
            log.info(filepath);
            result.add(adrService.parseADRFile(repoOwner, repoName, filepath, branch));
        }
        adrService.saveAll(result);
        return result;
    }


}
