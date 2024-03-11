package edu.doc_ti.bigdatamicroservices.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import edu.doc_ti.bigdatamicroservices.data.LookupData;

@Path("/load")
public class LoadData {
 

    @Path("all")
    @GET
    @Produces("application/json")
    public Response convertFtoCfromInput() throws JSONException {

    	JSONObject jsonObject = new JSONObject();
    	
    	if (LookupData.htMain.size() == 0 ) {
	        jsonObject.put("load", "NEW LOAD");
	        LookupData.loadTablesInMemory();
    	} else {
	        jsonObject.put("load", "ALREADY LOADED");
    	}

    	for ( String key: LookupData.htMain.keySet()) {
	        jsonObject.put(key, LookupData.htMain.get(key).size());
    	}
        String result = jsonObject.toString();
        return Response.status(200).entity(result).build();
    }
    
       
}