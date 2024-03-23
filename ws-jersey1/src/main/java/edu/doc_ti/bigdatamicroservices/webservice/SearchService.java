package edu.doc_ti.bigdatamicroservices.webservice;

import java.util.Hashtable;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.json.JSONException;

import edu.doc_ti.bigdatamicroservices.data.LookupData;

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
	
	
//    return Response.status(200).entity(result).build();
    
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