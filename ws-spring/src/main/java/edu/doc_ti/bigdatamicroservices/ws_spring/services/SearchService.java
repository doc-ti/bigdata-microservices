package edu.doc_ti.bigdatamicroservices.ws_spring.services;

import java.util.Hashtable;

import org.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.doc_ti.bigdatamicroservices.ws_spring.resource.LookupData;


@RestController
public class SearchService {

	private static final String TABLE_NOT_FOUND = "Table not found";
	private static final String DEFAULT="N/A" ;
	
	String process(String table, String id ) {
		
    	String result = DEFAULT ;
    	if ( table != null && id != null ) {
    		Hashtable<String, String> htab = LookupData.htMain.get(table) ;
    		if (htab != null ) {
    			result = htab.get(id) ;
    		} else {
    			result = TABLE_NOT_FOUND ;
    		}
    		if ( result == null ) result = DEFAULT ; 
    	}
    	
        return result;
	}
	
	
	
	@GetMapping("/search/{table}")
    public String processGet(
    		@PathVariable("table") String table,
    		@RequestParam(value = "key", defaultValue = "")  String key
    		)  {

        return process(table, key);
    }
       
	@GetMapping("/search/{table}/{key}")
    public String processGet2(
    		@PathVariable("table") String table,
    		@PathVariable("key") String key
    		)  {

        return process(table, key);
    }
       
	@PostMapping(path="/search/{table}/{key}", produces = "application/json")
    public String processPost(
    		@PathVariable("table") String table,
    		@PathVariable("key") String key
    		) throws JSONException {

        return process(table, key);

	}

	@PostMapping(path="/search/{table}", produces = "application/json")
    public String processPost2(
    		@PathVariable("table") String table,
    		@RequestBody String key
    		) throws JSONException {

        return process(table, key);

	}

}