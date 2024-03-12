package edu.doc_ti.bigdatamicroservices.jersey3.service;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;


@Path("/identity")
public class IdentityService {
 
	String IDENT="jersey3" ;
	
    @GET
    public String identGet()  {
        return IDENT;
    }
       
    @POST
    public String identPost() {
        return IDENT;
    }
    
}