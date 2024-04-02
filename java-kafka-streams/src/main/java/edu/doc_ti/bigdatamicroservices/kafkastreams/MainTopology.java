/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.doc_ti.bigdatamicroservices.kafkastreams;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.doc_ti.bigdatamicroservices.data.LookupData;

/**
 * In this example, we implement a simple LineSplit program using the high-level Streams DSL
 * that reads from a source topic "streams-plaintext-input", where the values of messages represent lines of text;
 * the code split each text line in string into words and then write back into a sink topic "streams-linesplit-output" where
 * each record represents a single word.
 */
public class MainTopology {

	
	private static final Logger LOG = LoggerFactory.getLogger(MainTopology.class);
	
	public static String urlBase = "http://localhost:8080" ;

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
	static int numThreads = 1 ;
	
	static final int MODE_FULL = 1 ;
	static final int MODE_DUMMY = 2 ;
	static final int MODE_SEARCH = 3 ;
	static final int MODE_LOCAL = 4 ;
	
	public static int NUM_DUMMYS=1 ;
	
	public static int mode = MODE_FULL ;
	

	private static ArrayList<ProcessorData> arrProcesors = new ArrayList<ProcessorData>();

	public static String modeString = "full";

    public static void main(String[] args) {
    	
		String topicIn = "topic_in_microserv" ; 
		String topicOut = "topic_out_microserv" ; 
		String bootstrapServers = "127.0.0.1:9092" ;
		
		Options options = new Options();
		options.addOption(new Option("h", "help", false, "Print this help"));
		options.addOption(new Option("b", "broker", true, "List of kafka bootstrap servers (def: " + bootstrapServers + ")"));
		options.addOption(new Option("t", "topic_in", true, "Input topic name (default: " + topicIn + ")"));
		options.addOption(new Option("u", "url", true, "URL base (default: " + urlBase + "), 'local' for local processing of data"));
		options.addOption(new Option("n", "threads", true, "Number of threads (default: " + numThreads + " )"));
		options.addOption(new Option("o", "topic_out", true, "Output topic name (default: " + topicOut + ")"));
		options.addOption(new Option("m", "mode", true, "Mode [f: full record, dN: N dummy calls, s: only searchs, l: local (no microservice)] (default: f)"));
    	
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null ;
		try {
			cmd = parser.parse(options, args);
		} catch (org.apache.commons.cli.ParseException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}

		if ( cmd.hasOption('h') || cmd.getOptions().length < 0 ) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("MainTopology", options);
			System.exit(0) ;
		}
		
		if ( cmd.hasOption('b')  ) {
			try {
				bootstrapServers = cmd.getParsedOptionValue("b").toString() ;
			} catch (Exception e) {
			}
		}
       
		if ( cmd.hasOption('m')  ) {
			try {
				String aux = cmd.getParsedOptionValue("m").toString() ;
				if ( aux.compareToIgnoreCase("f") == 0 ) {
					mode = MODE_FULL ;
					modeString = "full" ;
				}
				if ( aux.compareToIgnoreCase("l") == 0 ) {
					mode = MODE_LOCAL ;
					modeString = "local" ;
				}
				LookupData.htMain.size();
				
				if ( aux.compareToIgnoreCase("s") == 0 ) {
					mode = MODE_SEARCH;
					modeString = "search" ;
				}
				if ( aux.substring(0,1).compareToIgnoreCase("d") == 0 ) {
					mode = MODE_DUMMY ;
					try {
						NUM_DUMMYS = Integer.parseInt(aux.substring(1)) ;
					} catch (Exception ex) {}
					modeString  = "dummys_" + NUM_DUMMYS ;
				}
			} catch (Exception e) {
			}
		}
       
		if ( cmd.hasOption('t')  ) {
			try {
				topicIn = cmd.getParsedOptionValue("t").toString() ;
			} catch (Exception e) {
			}
		}
		
		if ( cmd.hasOption('u')  ) {
			try {
				urlBase = cmd.getParsedOptionValue("u").toString() ;
			} catch (Exception e) {
			}
		}
		
		if ( mode == MODE_LOCAL ) {
			LookupData.htMain.size();
		}

		if ( cmd.hasOption('n')  ) {
			try {
				numThreads = Integer.parseInt( cmd.getParsedOptionValue("n").toString() ) ;
			} catch (Exception e) {
			}
		}		

		if ( cmd.hasOption('o')  ) {
			try {
				topicOut = cmd.getParsedOptionValue("o").toString() ;
			} catch (Exception e) {
			}
		}
		
		
		Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ms-test-" + sdf.format(new Date()) + "-" + ((int)Math.random()*1000000) );
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, Integer.toString(numThreads)) ;
        
        Topology builder = new Topology();

     builder.addSource("source", topicIn)
         .addProcessor("process", () -> new ProcessorData(), "source")
         .addSink("sink", topicOut, "process") 
         ;        

     final KafkaStreams streams = new KafkaStreams(builder, props);
     
        final CountDownLatch latch = new CountDownLatch(1);

        Thread thControl = new Thread("control-thread") {
            @Override
            public void run() {
            	int recordsLast = -1 ;
            	long lastTS = System.currentTimeMillis();
            	
            	int recordsNew = 0 ;
            	boolean exit = false ;
            	while ( ! exit ) {
            		try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
            		recordsLast = recordsNew ;

            		recordsLast = recordsNew ;
        			recordsNew = 0 ;
            		for ( ProcessorData p: arrProcesors ) {
            			recordsNew += p.counterRecords ;
            		}
            		
            		LOG.info("Total of records: " + recordsNew);
            		
            		if (( recordsNew > recordsLast )) {
                    	lastTS = System.currentTimeMillis();
            		} else {
            			if ( System.currentTimeMillis() - lastTS > 20000) {
            				exit = true ;
            				LOG.info("Exiting, 20 seconds without activity");
            			}
            		}
            	}
            	
            	long recs = 0 ;
            	double recsD = 0 ;
            	double sum = 0 ;
            	double sum_2 = 0 ;
            	long tMin = Long.MAX_VALUE ;
            	long tMax = 0 ;
            	String serverType = "" ;
        		for ( ProcessorData p: arrProcesors ) {
        			try {
        				recs  += p.counterRecords ;
        				recsD = recs ;
        				System.out.println("RECS:" + recs) ;
        				sum   += p.counterSum ;
        				sum_2 += p.counterSum_2 ;
        				serverType = p.serverType ;
        				
        				if ( p.counterTMin < tMin ) tMin = p.counterTMin;
        				if ( p.counterTMax > tMax ) tMax = p.counterTMax;
        				
						p.bw.flush() ;
						p.fw.flush();
					} catch (IOException e) {}
        			try {
        				p.bw.close();
        				p.fw.close();
					} catch (IOException e) {}
        		}
           	
                String server = "unknown" ;
                try {
                    InetAddress dir = InetAddress.getLocalHost();
                    server = dir.getHostName();
                } catch (Exception e) {}

    	        String remoteHost = MainTopology.urlBase ;
    	    	String aux[] = remoteHost.split("/") ;
    	    	remoteHost = aux[2].replace(':', '-') ;
//				context.applicationId() + "." + context.taskId() + ".th_" + MainTopology.numThreads + "." + host + ".txt"));
//
                
                System.out.println (
                		String.format("OUTPUT,%s,%s,threads_%d,%d,%s,%s,%s,%d,%d,%f,%f\n", 
                				 server,
                				 sdf.format(new Date() ),
                				 MainTopology.numThreads,
                				 MainTopology.numThreads,
                				 remoteHost,
                				 serverType,
                				 MainTopology.modeString,
                				 recs,
                				 (tMax-tMin),
                				 sum/recs,
                				 Math.sqrt((sum_2/recsD - (sum*sum)/recsD/recsD)) 
                		)) ;
                
                System.out.println( sum + " / " + sum_2 ) ;
                System.out.println( (sum_2/recs  ) );
                System.out.println( (sum*sum)/recs/recs ) ;
                System.out.println( (sum_2/recs - (sum*sum)/recs/recs) ) ;
                System.out.println() ;
                System.out.println() ;
                System.out.println() ;
                streams.close();
                latch.countDown();
                
        		try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {}
        		
        		System.exit(0);

            }
        } ;
        
        thControl.start(); 
        
        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        
        for ( ProcessorData p : arrProcesors) {
        	try {
				p.bw.flush() ;
				p.bw.close() ;
				p.fw.flush();
				p.fw.close();
			} catch (IOException e) {}
        }
        
        System.exit(0);
    }

	public static synchronized void register(ProcessorData processorData) {
		arrProcesors .add(processorData) ;
	}
	
}
