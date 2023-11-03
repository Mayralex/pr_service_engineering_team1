package com.serviceEngineering.ADR_Viewer.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceEngineering.ADR_Viewer.entity.ADR;
import com.serviceEngineering.ADR_Viewer.exceptions.ServiceException;
import com.serviceEngineering.ADR_Viewer.repository.ADRRepository;
import com.serviceEngineering.ADR_Viewer.service.ADRService;
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

    @Autowired
    private ADRService adrService;

    @Autowired
    private ObjectMapper mapper;

    @GetMapping(value = "/getADR")
    public ResponseEntity getADR(@RequestParam long id){
        try{
            ADR adr = adrService.getADR(id);
            return new ResponseEntity<>(adr, HttpStatus.OK);
        } catch (ServiceException e){
            return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);
        }
    }


}
