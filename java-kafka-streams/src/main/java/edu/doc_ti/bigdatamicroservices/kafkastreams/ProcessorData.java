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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.doc_ti.bigdatamicroservices.data.DataProcessing;

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
        
        if ( !MainTopology.isLocalProcessing ) {
        	String aux[] = host.split("/") ;
        	host = aux[2];
        	int pos = host.indexOf(":") ;
        	host = host.substring(0,pos) ;
        }
        
        
        try {
        	File dir = new File ( context.applicationId() + ".th_" + MainTopology.numThreads + "." + host) ;
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

    @Override
    public void process(final Record<String, String> record) {
    	
    	counterRecords++ ;
//    	System.out.println("INPUT: "  + record.value() ) ;
    	
    	if ( counterRecords%1000 == 0 ) {
    		LOG.info("Procesed " + counterRecords + " in task:" + _context.taskId() );
    	}
    	
    	long t0 = -System.nanoTime() ;

    	String result = "" ;
    	if ( MainTopology.isLocalProcessing ) {
    		result = "LOCAL:" + dp.process( record.value() ) ;
    	} else {
    		result = "REMOTE:" + makeHttpRequestPost(MainTopology.urlBase, "record=" + record.value() ) ;
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

    	
    	for (int nn= 0 ; nn<3 ;nn++) {
        	long t0 = -System.nanoTime() ;
//	    	String x = p.makeHttpRequestGet(MainTopology.urlBase + "/"+  auxArrHT [nn%auxArrHT.length]  +"/0", "") ;
//	    	String x = p.makeHttpRequestPost(MainTopology.urlBase , record) ;
//	    	String x = p.makeHttpRequestPost("http://192.168.80.33:8080/process" , record) ;
	    	String x = p.makeHttpRequestPost("http://192.168.80.33:8080/process" , record) ;
	    	t0 += System.nanoTime() ;
	    	if ( x != null ) {
		    	System.out.println(x) ;
	    	}
	    	System.out.println(t0/1000) ;
    	}
	}
    
    @SuppressWarnings("unused")
	private String makeHttpRequestGet(String url, String record) {
        HttpGet httpGet = new HttpGet(url+ "?record=" + record);

        try {
            HttpResponse response = httpClient.execute(httpGet );
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                HttpEntity entity = response.getEntity();
                // Asegúrate de que la entidad no sea null y que haya contenido para leer
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
            return null ;        }
    }    
    
    private String makeHttpRequestPost(String url, String record) {
    	
        HttpPost httpPost = new HttpPost(url);
        StringEntity entityIn = null;
		try {
			entityIn = new StringEntity("record="+record);
		} catch (UnsupportedEncodingException e1) {}    	

		httpPost.setEntity(entityIn);
//        httpPost.setHeader("Accept", "application/json");
//        httpPost.setHeader("Content-type", "application/json");
    	
        try {
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                HttpEntity entityOut = response.getEntity();
                // Asegúrate de que la entidad no sea null y que haya contenido para leer
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
            return null ;        }
    }    
    
    
	@Override
    public void close() {
    	
    	System.out.println("CLOSING PROCESSOR " + this.toString());
    }


}
