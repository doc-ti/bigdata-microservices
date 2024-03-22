package edu.doc_ti.bigdatamicroservices.jersey3.service;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;


@Path("/dummy")
public class DummyService {
 
    @GET
    public String identGet()  {
        return "OK";
    }
       
    @POST
    @Produces("application/json")
    public String identPost() {
        return "OK";
    }
    
}