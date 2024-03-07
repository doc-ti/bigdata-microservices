package edu.doc_ti.bigdatamicroservices.kafkastreams;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessorData implements Processor<String, String, String, String> {
	private static final Logger LOG = LoggerFactory.getLogger(ProcessorData.class);

    ProcessorContext<String, String> _context ;
    
//    String baseID = UUID.randomUUID().toString() ;
    String baseID = "str_" + RandomStringUtils.randomAlphanumeric(12) ;
    
    int counterTags = 1 ;
    int counterRecords = 0 ;
    String currentTag = baseID + "_" + String.format( "%06d", counterTags) ;
   
	@Override
    public void init(final ProcessorContext<String, String> context) {
    	
    	_context = context ;
    	
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
//    	System.out.println( "THREAD: " + Thread.currentThread().getName() ) ;
    	
    	Record<String, String> recordOut = new Record<String, String>(record.key(),
    					currentTag + ";" + 
    	    			Integer.toString( counterRecords) + ";" +
    	    			_context.recordMetadata().get().topic() + ":" + _context.recordMetadata().get().partition() + ":" + _context.recordMetadata().get().offset() + ";" +  
//    	    			Integer.toString( counterRecords) + ";" +
    	    			record.value(), record.timestamp()) ;
		_context.forward(recordOut);

    	
    }


	@Override
    public void close() {
    	
    	System.out.println("CLOSING PROCESSOR " + this.toString());
    }


}
