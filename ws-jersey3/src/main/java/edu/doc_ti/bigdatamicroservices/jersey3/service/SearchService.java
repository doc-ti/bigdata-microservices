package edu.doc_ti.bigdatamicroservices.jersey3.service;

import java.util.Hashtable;

import org.json.JSONException;

import edu.doc_ti.bigdatamicroservices.jersey3.resource.LookupData;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;


@Path("/search")
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
	
	
    @GET
	@Path("{table}/{key}")
    @Produces("application/json")
    public String processGet(
    		@PathParam("table") String table, 
    		@PathParam("key") String key
    		)  {

    	return process(table, key) ;
    }
       
    @GET
	@Path("{table}")
    @Produces("application/json")
    public String processGet2(
    		@PathParam("table") String table, 
    		@QueryParam("key") String key
    		)  {

    	return process(table, key) ;
    }
       
    @POST
	@Path("{table}/{key}")
    @Produces("application/json")
    public String processPost(
    		@PathParam("table") String table, 
    		@PathParam("key") String key
    		) throws JSONException {

        return process(table, key) ;
    }
    
    @POST
	@Path("{table}")
    @Produces("application/json")
    public String processPost2(
    		@PathParam("table") String table, 
    		String key
    		) throws JSONException {

        return process(table, key) ;
    }
    
        
}