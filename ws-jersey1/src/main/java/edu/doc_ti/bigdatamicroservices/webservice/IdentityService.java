package edu.doc_ti.bigdatamicroservices.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;

@Path("/identify")
public class IdentityService {
 
	final static String IDENT = "jersey1" ; 

    @GET
    public Response identGet() {
        return Response.status(200).entity(IDENT).build();
    }
       
    @POST
    @Produces("application/json")
    public Response processPost() throws JSONException {
        return Response.status(200).entity(IDENT).build();
    }
       
}