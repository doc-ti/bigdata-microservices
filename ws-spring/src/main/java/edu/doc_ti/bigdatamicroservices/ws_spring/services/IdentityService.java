package edu.doc_ti.bigdatamicroservices.ws_spring.services;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;




@RestController
public class IdentityService {

	private static final String IDENT = "spring3";

	@GetMapping("/identity")
    public String identGet()  {
        return IDENT ;
    }

	@PostMapping("/identity")
    public String identPost()  {
        return IDENT ;
    }
	
}