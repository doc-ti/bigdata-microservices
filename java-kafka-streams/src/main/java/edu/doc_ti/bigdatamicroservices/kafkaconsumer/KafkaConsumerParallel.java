package edu.doc_ti.bigdatamicroservices.kafkaconsumer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import edu.doc_ti.bigdatamicroservices.data.LookupData;

public class KafkaConsumerParallel {

//	public static String urlBase = "http://localhost:8081" ;
	public static String urlBase = "http://192.168.80.32:8080" ;
	
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
	static int numPartitions = 0 ;
	
	static final int MODE_FULL = 1 ;
	static final int MODE_DUMMY = 2 ;
	static final int MODE_SEARCH = 3 ;
	static final int MODE_LOCAL = 4 ;

	public static int batchSize = 100;
	
	public static int NUM_DUMMYS=1 ;
	
	public static int mode = MODE_FULL ;
	public static String modeString = "full";

	public static String applicationId;	
	
    public static void main(String[] args) {
		String topicIn = "topic_in_microserv" ; 
		String topicOut = "topic_out_microserv" ; 
		String bootstrapServers = "127.0.0.1:9092" ;
		
		Options options = new Options();
		options.addOption(new Option("h", "help", false, "Print this help"));
		options.addOption(new Option("b", "broker", true, "List of kafka bootstrap servers (def: " + bootstrapServers + ")"));
		options.addOption(new Option("t", "topic_in", true, "Input topic name (default: " + topicIn + ")"));
		options.addOption(new Option("u", "url", true, "URL base (default: " + urlBase + "), 'local' for local processing of data"));
		options.addOption(new Option("s", "batch", true, "Batch size (default: " + batchSize + " )"));
		options.addOption(new Option("o", "topic_out", true, "Output topic name (default: " + topicOut + ")"));
//		options.addOption(new Option("m", "mode", true, "Mode [f: full record, dN: N dummy calls, s: only searchs, l: local (no microservice)] (default: f)"));
    	
		
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

		if ( cmd.hasOption('s')  ) {
			try {
				batchSize = Integer.parseInt( cmd.getParsedOptionValue("s").toString() ) ;
			} catch (Exception e) {
			}
		}		

		if ( cmd.hasOption('o')  ) {
			try {
				topicOut = cmd.getParsedOptionValue("o").toString() ;
			} catch (Exception e) {
			}
		}
		
		
		applicationId = "microservice-test-" + sdf.format(new Date() ) ;
		Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId );
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Serdes.String().getClass());
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Serdes.String().getClass());

//        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, Integer.toString(numThreads)) ;

        // Build kafka consumer 
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        List<TopicPartition> assignedPartitions = consumer.partitionsFor(topicIn).stream()
                .map(partInfo -> new TopicPartition(partInfo.topic(), partInfo.partition()))
                .collect(Collectors.toList());

        // Build one thread by partition
        ArrayList<ConsumerThread> consumerThreads = new ArrayList<>();
        for (TopicPartition partition : assignedPartitions) {
            ConsumerThread consumerThread = new ConsumerThread(props, partition, topicOut);
            consumerThreads.add(consumerThread);
            consumerThread.start();
        }
        consumer.close();

        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
            	System.out.println("Stopping.....") ;
            	for ( ConsumerThread th : consumerThreads) {
            		th.stopConsumer() ;
            	}
            }
        });        
        
        // Wait for threads
        for (ConsumerThread consumerThread : consumerThreads) {
            try {
                consumerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

