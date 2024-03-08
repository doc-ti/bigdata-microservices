package edu.doc_ti.bigdatamicroservices.webservice;

import java.util.Hashtable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import edu.doc_ti.bigdatamicroservices.data.LookupData;

@Path("/search")
public class LookupService {
 

    @Path("{tab}/{key}")
    @GET
    @Produces("application/json")
    public Response convertFtoCfromInput(
    		@PathParam("tab") String tab,
    		@PathParam("key") String key
    		) throws JSONException {

    	JSONObject jsonObject = new JSONObject();

    	Hashtable<String, String> htAux= LookupData.htMain.get(tab) ;
    	String res = null ;
    	
    	if ( htAux != null ){
    		res = htAux.get(key) ;
    	}
    	if ( res == null ) {
    		res = "NA" ;
    	}
    	
//        jsonObject.put("tab", tab);
        jsonObject.put("key", key);
        jsonObject.put("res", res);
        String result = jsonObject.toString();
        return Response.status(200).entity(result).build();
    }
    
    @Path("all/{keys}")
    @GET
    @Produces("application/json")
    public Response convertFtoCfromInput(
    		@PathParam("keys") String keys
    		) throws JSONException {

    	JSONObject jsonObject = new JSONObject();
    	
    	String keysArr [] =keys.split(",") ;
    	
    	
    	int count = 0 ;
    	for (String tabHT: LookupData.namesHT ) {
    		
    		String res = "" ;
    		try {
    			res = LookupData.htMain.get(tabHT).get( keysArr[count] ) ;
    		} catch (Exception ex) {}
    		if ( res == null ) {
    			res = "NA" ;
    		}
            jsonObject.put(tabHT, res);
            count++ ;
    	}

        String result = jsonObject.toString() ;
        return Response.status(200).entity(result).build();
    }
        
}