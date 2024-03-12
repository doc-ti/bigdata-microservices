package edu.doc_ti.bigdatamicroservices.jersey3.service;

import org.json.JSONException;

import edu.doc_ti.bigdatamicroservices.jersey3.resource.DataProcessing;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;


@Path("/process")
public class ProcessService {
 
    @GET
    public String processGet(
    		@QueryParam("record") String data
    		)  {

    	DataProcessing dp = new DataProcessing() ;
    	
        String result = dp.process(data) ;
        return result;
    }
       
//    @Path("{data}")
    @POST
    @Produces("application/json")
    public String processPost(
    		String data
    		) throws JSONException {

    	DataProcessing dp = new DataProcessing() ;
    	
        String result = dp.process(data) ;
        return result ;
    }
    
       
}