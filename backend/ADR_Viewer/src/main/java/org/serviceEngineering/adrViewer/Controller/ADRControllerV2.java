package org.serviceEngineering.adrViewer.Controller;

import org.serviceEngineering.adrViewer.entity.ADR;
import org.serviceEngineering.adrViewer.exceptions.ServiceException;
import org.serviceEngineering.adrViewer.service.ADRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v2")
public class ADRControllerV2 {

    private final ADRService adrService;
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


}
