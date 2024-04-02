package edu.doc_ti.bigdatamicroservices.jersey3.service;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;

@Path("/identity")
public class IdentityService {

	static String IDENT="jersey3" ;
    static {
        Package jerseyPackage = Application.class.getPackage();
    	IDENT="jersey_" + jerseyPackage.getImplementationVersion().replace('.', '_') ;
    	
        try {
            // Intenta cargar una clase específica de Tomcat
            Class.forName("org.apache.catalina.startup.Bootstrap");
            IDENT = "tomcat_jersey_" + jerseyPackage.getImplementationVersion().replace('.', '_') ;
        } catch (ClassNotFoundException e) {}    	
    	
    }	
	
	
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