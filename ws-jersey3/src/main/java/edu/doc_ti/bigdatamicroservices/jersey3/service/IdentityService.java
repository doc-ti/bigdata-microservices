package edu.doc_ti.bigdatamicroservices.jersey3.service;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;


@Path("/identify")
public class IdentityService {
 
	String IDENT="jersey3" ;
	
    @GET
    public String identGet()  {
        return IDENT;
    }
       
    @POST
    @Produces("application/json")
    public String identPost() {
        return IDENT;
    }
    
}