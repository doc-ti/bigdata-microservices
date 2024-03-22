package edu.doc_ti.bigdatamicroservices.ws_spring.services;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;




@RestController
public class DummyService {

	private static final String RESP = "OK";

	@GetMapping("/dummy")
    public String identGet()  {
        return RESP ;
    }

	@PostMapping("/dummy")
    public String identPost()  {
        return RESP ;
    }
	
}