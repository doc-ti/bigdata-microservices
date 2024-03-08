package edu.doc_ti.bigdatamicroservices.kafkastreams;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProcessorData implements Processor<String, String, String, String> {
	private static final Logger LOG = LoggerFactory.getLogger(ProcessorData.class);

    ProcessorContext<String, String> _context ;
	final static String auxArrHT[] = {"bssmap", "internalcause", "nrn", "operator", "ranap", "tac"};
   
    private HttpClient httpClient;
    
    private ObjectMapper mapper;
    
//    String baseID = UUID.randomUUID().toString() ;
    
    int counterRecords = 0 ;

    private TypeReference<Map<String, String>> typeRef;
   
	@Override
    public void init(final ProcessorContext<String, String> context) {
    	
    	this._context = context ;
        this.mapper = new ObjectMapper();
        this.typeRef = new TypeReference<Map<String, String>>() {};
        this.httpClient = HttpClientBuilder.create().build();
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
    	
    	System.out.println("INPUT: "  + record.value() ) ;
//    	System.out.println( "THREAD: " + Thread.currentThread().getName() ) ;
    	
//    	Record<String, String> recordOut = new Record<String, String>(record.key(),
//    	    			Integer.toString( counterRecords) + ";" +
//    	    			_context.recordMetadata().get().topic() + ":" + _context.recordMetadata().get().partition() + ":" + _context.recordMetadata().get().offset() + ";" +  
//    	    			record.value(), record.timestamp()) ;
//		_context.forward(recordOut);

    	long t0 = -System.nanoTime() ;
    	makeHttpRequest(MainTopology.urlBase + "/bss/0") ;
    	t0 += System.nanoTime() ;
    	System.out.println(t0) ;
    	
    }


    public static void main(String[] args) {
    	
    	ProcessorData p = new ProcessorData() ;
    	p.init(null);
    	
    	for (int nn= 0 ; nn<100 ;nn++) {
        	long t0 = -System.nanoTime() ;
	    	String x = p.makeHttpRequest(MainTopology.urlBase + "/"+  auxArrHT [nn%auxArrHT.length]  +"/0") ;
	    	t0 += System.nanoTime() ;
	    	if ( x != null ) {
		    	System.out.println(x) ;
	    	}
	    	System.out.println(t0/1000) ;
    	}
	}
    
    
    private String makeHttpRequest(String url) {
        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse response = httpClient.execute(httpGet);
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
    
    
	@Override
    public void close() {
    	
    	System.out.println("CLOSING PROCESSOR " + this.toString());
    }


}
