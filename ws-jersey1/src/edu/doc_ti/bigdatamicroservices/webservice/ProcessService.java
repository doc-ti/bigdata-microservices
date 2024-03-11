package edu.doc_ti.bigdatamicroservices.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;

import edu.doc_ti.bigdatamicroservices.data.DataProcessing;

@Path("/process")
public class ProcessService {
 

    @Path("{data}")
    @GET
    @Produces("application/json")
    public Response processGet(
    		@PathParam("data") String data
    		) throws JSONException {

    	DataProcessing dp = new DataProcessing() ;
    	
        String result = dp.process(data) ;
        return Response.status(200).entity(result).build();
    }
       
//    @Path("{data}")
    @POST
    @Produces("application/json")
    public Response processPost(
    		String data
    		) throws JSONException {

    	DataProcessing dp = new DataProcessing() ;
    	
        String result = dp.process(data) ;
        return Response.status(200).entity(result).build();
    }
       
}