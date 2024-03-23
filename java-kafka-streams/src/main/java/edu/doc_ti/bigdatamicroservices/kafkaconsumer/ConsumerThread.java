package edu.doc_ti.bigdatamicroservices.kafkaconsumer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.doc_ti.bigdatamicroservices.kafkastreams.MainTopology;

public class ConsumerThread extends Thread {
	private static final Logger LOG = LoggerFactory.getLogger(ConsumerThread.class);
    private final Properties props;
    private final TopicPartition partition;
	private boolean stop = false ;
	private CloseableHttpClient httpClient;
	BufferedWriter bw;
	FileWriter fw;
	KafkaProducer<String, String> producer ;
    JSONParser parser = new JSONParser();
	private String topicOut;

	
    public ConsumerThread(Properties props, TopicPartition partition, String topicOut) {
        this.props = props;
        this.partition = partition;
        this.topicOut = topicOut ;

        this.httpClient = HttpClientBuilder.create().build();
        
       String host = MainTopology.urlBase ;
        
    	String aux[] = host.split("/") ;
    	host = aux[2].replace(':', '-') ;
        
        try {
        	String serverType = makeHttpRequestGet(KafkaConsumerParallel.urlBase + "/identity", "", "") ;
        	File dir = new File ( 
        				KafkaConsumerParallel.applicationId + ".threads_" + KafkaConsumerParallel.numPartitions + "." + host + "." + serverType + "." + KafkaConsumerParallel.modeString + ".batch_" +KafkaConsumerParallel.batchSize 
        			) ;
    		dir.mkdir();
			fw = new FileWriter(new File(dir, 
						KafkaConsumerParallel.applicationId + "." + partition + ".th_" + KafkaConsumerParallel.numPartitions + "." + host + ".txt")
					);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1) ;
		}
  	  	bw = new BufferedWriter(fw);
    
    
    
    }
    
    public void stopConsumer() {
    	stop = true ;
    }

    
    @SuppressWarnings("unchecked")
	private void process(JSONArray jarr) {
        JSONObject json = new JSONObject() ;
    	
        if ( jarr.size() > 0 ) {
        	json.put("records", jarr);
//        	System.out.println(json.toString()) ;
           	long t0 = -System.nanoTime() ;
        	String result = makeHttpRequestPost(KafkaConsumerParallel.urlBase + "/batch", json.toString()) ;
        	
        	try {
				JSONArray jarrRes = (JSONArray) parser.parse(result) ;
		    	t0 += System.nanoTime() ;
		    	try {
		    		String msg = "" + System.currentTimeMillis() + " " + t0 + " " + jarr.size() ;
//		    		System.out.println(msg);
					bw.write( ( msg + "\n").toCharArray());
					bw.flush();
				} catch (IOException e) {
				}
				
				for (int index = 0 ; index < jarrRes.size(); index++) {
					ProducerRecord<String, String> record = new ProducerRecord<>(topicOut, jarrRes.get(index).toString());
	                producer.send(record);				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        
        }
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void run() {
        producer = new KafkaProducer<>(props);
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.assign(Collections.singletonList(partition));
        consumer.seekToBeginning(Collections.singletonList(partition));

        try {
            while (! stop  ) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                if ( records.count() > 0 ) {
	                LOG.info("Thread {}, records = {}",
	                		Thread.currentThread().getName(),
	                		records.count());
                }

                JSONArray jarr = new JSONArray() ;
                for (ConsumerRecord<String, String> record : records) {
                	jarr.add( record.value() ) ;
                	if ( jarr.size() >= KafkaConsumerParallel.batchSize ) {
                		process(jarr);
                		jarr.clear();
                	}
                }
        		process(jarr);
                
//                for (ConsumerRecord<String, String> record : records) {
//                    System.out.printf("Thread %s, Partition = %d Offset = %d, Key = %s, Value = %s%n",
//                            Thread.currentThread().getName(),
//                            record.partition(),
//                            record.offset(), 
//                            record.key(), 
//                            record.value());
//                }
            }
        } finally {
            consumer.close();
            producer.close();
        }
    }

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

    private String makeHttpRequestPost(String url, String data) {
    	
        HttpPost httpPost = new HttpPost(url);
        StringEntity entityIn = null;
		try {
			entityIn = new StringEntity(data);
		} catch (UnsupportedEncodingException e1) {}    	

		httpPost.setEntity(entityIn);
//        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
    	
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
    
}


