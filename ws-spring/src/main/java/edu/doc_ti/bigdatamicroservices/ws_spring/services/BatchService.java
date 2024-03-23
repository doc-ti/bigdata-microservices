package edu.doc_ti.bigdatamicroservices.ws_spring.services;

import org.json.JSONException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.doc_ti.bigdatamicroservices.ws_spring.resource.DataProcessing;


@RestController
public class BatchService {
	
	@PostMapping(path="/batch", produces = "application/json")
    public String processPost(
    		@RequestBody String data
    		) throws JSONException {

    	DataProcessing dp = new DataProcessing() ;
        String result = dp.processBatch(data) ;
		
        return result ;
    }
       
}