package edu.doc_ti.bigdatamicroservices.kafkastreams;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.doc_ti.bigdatamicroservices.data.DataProcessing;
import edu.doc_ti.bigdatamicroservices.data.LookupData;

public class ProcessorData implements Processor<String, String, String, String> {
	private static final Logger LOG = LoggerFactory.getLogger(ProcessorData.class);

    ProcessorContext<String, String> _context ;
	final static String auxArrHT[] = {"bssmap", "internalcause", "nrn", "operator", "ranap", "tac"};
   
    private HttpClient httpClient;
    
//	private ObjectMapper mapper;
//	private TypeReference<Map<String, String>> typeRef;

    int counterRecords = 0 ;
    
	private DataProcessing dp;

	BufferedWriter bw;
	FileWriter fw;

	@Override
    public void init(final ProcessorContext<String, String> context) {
    	
    	this._context = context ;
//        this.mapper = new ObjectMapper();
//        this.typeRef = new TypeReference<Map<String, String>>() {};
        this.httpClient = HttpClientBuilder.create().build();
        this.dp = new DataProcessing() ;
        
       String host = MainTopology.urlBase ;
        
    	String aux[] = host.split("/") ;
    	host = aux[2].replace(':', '-') ;
        
        try {
        	String serverType = makeHttpRequestGet(MainTopology.urlBase + "/identity", "", "") ;
        	File dir = new File ( context.applicationId() + ".threads_" + MainTopology.numThreads + "." + host + "." + serverType + "." + MainTopology.modeString) ;
        	dir.mkdir();
			fw = new FileWriter(new File(dir, context.applicationId() + "." + context.taskId() + ".th_" + MainTopology.numThreads + "." + host + ".txt"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1) ;
		}
  	  	bw = new BufferedWriter(fw);
  	  	
  	  	MainTopology.register(this) ;
  	  	
        LOG.info("Processor initialized.");
        
    	
//        context.schedule(Duration.ofSeconds(1), PunctuationType.STREAM_TIME, timestamp -> {
//            try (final KeyValueIterator<String, Integer> iter = kvStore.all()) {
//                while (iter.hasNext()) {
//                    final KeyValue<String, Integer> entry = iter.next();
//                    context.forward(new Record<>(entry.key, entry.value.toString(), timestamp));
//                }
//            }
//        });
//        kvStore = context.getStateStore("Counts");
    }

    @SuppressWarnings("unchecked")
	@Override
    public void process(final Record<String, String> record) {
    	
    	counterRecords++ ;
    	
    	if ( counterRecords%1000 == 0 ) {
    		LOG.info("Procesed " + counterRecords + " in task:" + _context.taskId() );
    	}
    	
    	long t0 = -System.nanoTime() ;

    	String result = "" ;
    	
    	switch (MainTopology.mode) {
			case MainTopology.MODE_LOCAL:
	    		result = dp.process( record.value() ) ;
			break ;
			
			case MainTopology.MODE_FULL :
	    		result = makeHttpRequestPost(MainTopology.urlBase + "/process", "record" , record.value() ) ;
			break ;
			
			case MainTopology.MODE_DUMMY :
	    		result = dp.process( record.value() ) ;
	    		for (int n=0 ; n<MainTopology.NUM_DUMMYS; n++)
	    			makeHttpRequestGet(MainTopology.urlBase + "/dummy", "" , "" ) ;
			break ;
			
			case MainTopology.MODE_SEARCH :
				
				JSONObject json = dp.processNoSearch(record.value()) ;
				
				for ( String table : LookupData.namesHT) {
					String key = "" ;
					Object aux= json.get("cod_" + table) ;
					if ( aux != null ) {
						key = aux.toString() ;
					}
		    		result = makeHttpRequestGet(MainTopology.urlBase + "/search/" + table, "key" , key ) ;
		    		if ( result != null) {
		    			json.put(table, result) ;
		    		}
				}
				result = json.toString();
			break ;
    		
    	}

    	t0 += System.nanoTime() ;
    	try {
			bw.write( ( "" + System.currentTimeMillis() + " " + t0 + "\n").toCharArray());
			bw.flush();
		} catch (IOException e) {
		}
//    	System.out.println(t0) ;

    	Record<String, String> recordOut = new Record<String, String>(record.key(),
//				_context.recordMetadata().get().topic() + ":" + _context.recordMetadata().get().partition() + ":" + _context.recordMetadata().get().offset() + ";" +  
				result, System.currentTimeMillis() ) ;
    	
    	_context.forward(recordOut);
    }


    public static void main(String[] args) {
  
    	String record = "7,10,019342,28305,COD_RANUP,86943504,18,3100043090631206,97.205.248.124,1255,11.48.240.193,dusty_sandmann,12345,34.18.111.4,287,66,959,268,7,700,4638,5,3,84,482,38,6,5,2024-03-08T14:10:47,2024-03-08T05:51:55,8,0,10.499,uebx28va86,1670464820,0,2,50,3100043389448771,1,3,6,6,5,01,0,9,3,6,AN,BE,CYK,DX66,9.67,7.04,12.34,2024-03-08T14:32:39,5G" ;
     	ProcessorData p = new ProcessorData() ;
        p.httpClient = HttpClientBuilder.create().build();


    	for (int nn= 0 ; nn<1 ;nn++) {

//        	String URL= "http://localhost:8080" ;
//        	String URL= "http://localhost:8081" ;
//        	String URL= "http://localhost:8080/api-rest" ;
        	String URL = "http://192.168.80.32:8080" ;
        	
        	String auxS[] = {"/identity", "/dummy" , "/process"} ;
	    	String x = "";
        	for (String aux : auxS) { 
            	long t0 = -System.nanoTime() ;
	        	x = p.makeHttpRequestGet(URL + aux , "record", record) ;
		    	t0 += System.nanoTime() ;
		    	if ( x != null ) {
			    	System.out.println(x) ;
		    	}
		    	System.out.println(t0/1000) ;

	        	t0 = -System.nanoTime() ;
		    	x = p.makeHttpRequestPost(URL + aux , "record", record) ;
		    	t0 += System.nanoTime() ;
		    	if ( x != null ) {
			    	System.out.println(x) ;
		    	}
		    	System.out.println(t0/1000) ;
        	}
    	}
	}
    
    @SuppressWarnings("unused")
	private String makeHttpRequestGet(String url, String param, String value) {
        HttpGet httpGet = new HttpGet(url+ "?" + param + "=" + value);

        try {
            HttpResponse response = httpClient.execute(httpGet );
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                HttpEntity entity = response.getEntity();
                // Aseg�rate de que la entidad no sea null y que haya contenido para leer
                if (entity != null && entity.getContentLength() != 0) {
                    // Formato JSON
                    String responseString = EntityUtils.toString(entity);
                    return responseString ;
//                    return this.mapper.readValue(responseString, this.typeRef);
                } else {
                    // Maneja el caso en que no haya contenido en la respuesta
                    System.out.println("No content to map due to end-of-input for URL: " + url);
                    LOG.error("No content to map due to end-of-input for URL: " + url);
                    return null ;
                }
            } else {
                System.out.println("Error: Received HTTP status code " + statusCode + " for URL: " + url);
                LOG.error("Error: Received HTTP status code " + statusCode + " for URL: " + url);
                return null ;
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Connection refused")) {
            	try {
        			bw.write( ( "ERROR CONNECTION!\n").toCharArray());
        			bw.flush();
        		} catch (Exception ex) {}
            	System.exit(-1);
            }
            return null ;        }
    }    
    
    private String makeHttpRequestPost(String url, String param, String value) {
    	
        HttpPost httpPost = new HttpPost(url);
        StringEntity entityIn = null;
		try {
			entityIn = new StringEntity(param + "=" + value);
		} catch (UnsupportedEncodingException e1) {}    	

		httpPost.setEntity(entityIn);
//        httpPost.setHeader("Accept", "application/json");
//        httpPost.setHeader("Content-type", "application/json");
    	
        try {
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                HttpEntity entityOut = response.getEntity();
                // Aseg�rate de que la entidad no sea null y que haya contenido para leer
                if (entityOut != null && entityOut.getContentLength() != 0) {
                    // Formato JSON
                    String responseString = EntityUtils.toString(entityOut);
                    return responseString ;
//                    return this.mapper.readValue(responseString, this.typeRef);
                } else {
                    // Maneja el caso en que no haya contenido en la respuesta
                    System.out.println("No content to map due to end-of-input for URL: " + url);
                    LOG.error("No content to map due to end-of-input for URL: " + url);
                    return null ;
                }
            } else {
                System.out.println("Error: Received HTTP status code " + statusCode + " for URL: " + url);
                LOG.error("Error: Received HTTP status code " + statusCode + " for URL: " + url);
                return null ;
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Connection refused")) {
            	try {
        			bw.write( ( "ERROR CONNECTION!\n").toCharArray());
        			bw.flush();
        		} catch (IOException ex) {}
            	System.exit(-1);
            }
            return null ;        }
    }    
    
    
	@Override
    public void close() {
    	
    	System.out.println("CLOSING PROCESSOR " + this.toString());
    }


}
