package edu.doc_ti.bigdatamicroservices.ws_spring;

import org.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.doc_ti.bigdatamicroservices.ws_spring.resource.DataProcessing;




@RestController
public class IdentityService {

	private static final String IDENT = "spring3";

	@GetMapping("/identify")
    public String identGet()  {
        return IDENT ;
    }

	@PostMapping("/identify")
    public String identPost()  {
        return IDENT ;
    }
	
}