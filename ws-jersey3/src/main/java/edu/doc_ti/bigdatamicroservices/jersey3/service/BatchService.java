package edu.doc_ti.bigdatamicroservices.jersey3.service;

import org.json.JSONException;

import edu.doc_ti.bigdatamicroservices.jersey3.resource.DataProcessing;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;


@Path("/batch")
public class BatchService {
 
    @POST
    @Produces("application/json")
    public String processPost(
    		String data
    		) throws JSONException {

    	DataProcessing dp = new DataProcessing() ;
        String result = dp.processBatch(data) ;
		
        return result ;
    }
       
}