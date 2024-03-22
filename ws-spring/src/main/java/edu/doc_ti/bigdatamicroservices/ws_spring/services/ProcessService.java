package edu.doc_ti.bigdatamicroservices.ws_spring.services;

import org.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.doc_ti.bigdatamicroservices.ws_spring.resource.DataProcessing;




@RestController
public class ProcessService {
	
	@GetMapping("/process")
    public String processGet(
    		@RequestParam(value = "record", defaultValue = "")  String data
    		)  {

    	DataProcessing dp = new DataProcessing() ;
    	
        String result = dp.process(data) ;
        return result;
    }
       
	@PostMapping(path="/process", produces = "application/json")
    public String processPost(
    		@RequestBody String data
    		) throws JSONException {

    	DataProcessing dp = new DataProcessing() ;
    	
        String result = dp.process(data) ;
        return result ;
    }
       
}