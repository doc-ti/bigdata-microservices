package edu.doc_ti.bigdatamicroservices.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

@Path("/load")
public class LoadData {
 

    @Path("all")
    @GET
    @Produces("application/json")
    public Response convertFtoCfromInput() throws JSONException {

    	JSONObject jsonObject = new JSONObject();
    	
    	if (ContextManager.htMain.size() == 0 ) {
	        jsonObject.put("load", "NEW LOAD");
	    	ContextManager cm = new ContextManager() ;
	    	cm.contextInitialized(null);
    	} else {
	        jsonObject.put("load", "ALREADY LOADED");
    	}

    	for ( String key: ContextManager.htMain.keySet()) {
	        jsonObject.put(key, ContextManager.htMain.get(key).size());
    	}
        String result = jsonObject.toString();
        return Response.status(200).entity(result).build();
    }
    
       
}