package edu.doc_ti.bigdatamicroservices.jersey3;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import edu.doc_ti.bigdatamicroservices.jersey3.config.AutoScanFeature;
import edu.doc_ti.bigdatamicroservices.jersey3.resource.LookupData;
import edu.doc_ti.bigdatamicroservices.jersey3.service.DummyService;
import edu.doc_ti.bigdatamicroservices.jersey3.service.IdentityService;
import edu.doc_ti.bigdatamicroservices.jersey3.service.ProcessService;
import edu.doc_ti.bigdatamicroservices.jersey3.service.SearchService;

public class MainApp {

    private static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());

    // we start at port 8080
    public static String BASE_URI = "";

    // Starts Grizzly HTTP server
    public static HttpServer startServer(String[] args) {

    	int port = 8080 ;
    	String host = "localhost";
    	
		Options options = new Options();
		
		options.addOption(new Option("h", "help", false, "Print this help"));
		options.addOption(new Option("p", "port", true, "port (default 8080)"));
		options.addOption(new Option("a", "address", true, "address / host / ip (default localhost)"));

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
			formatter.printHelp("MainApp", options);
			System.exit(0) ;
		}
       
		if ( cmd.hasOption('p')  ) {
			try {
				port = Integer.parseInt(cmd.getParsedOptionValue("p").toString());
			} catch (Exception e) {
			}
		} 
		
		if ( cmd.hasOption('a')  ) {
			try {
				host = cmd.getParsedOptionValue("a").toString();
			} catch (Exception e) {
			}
		} 
		
		BASE_URI = "http://" + host + ":" + port + "/";
		
		LOGGER.info("Listening in " + BASE_URI) ;
		
        // scan packages
        final ResourceConfig config = new ResourceConfig();
        // config.packages(true, "com.mkyong");
        config.register(ProcessService.class);
        config.register(DummyService.class);
        config.register(IdentityService.class);
        config.register(SearchService.class);

        // enable auto scan @Contract and @Service
        config.register(AutoScanFeature.class);

        LOGGER.info("Starting Server........");
        
        LookupData.htMain.size();

        final HttpServer httpServer =
                GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);

        return httpServer;

    }

    public static void main(String[] args) {

        try {

            final HttpServer httpServer = startServer(args);

            // add jvm shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    System.out.println("Shutting down the application...");

                    httpServer.shutdownNow();

                    System.out.println("Done, exit.");
                } catch (Exception e) {
                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, e);
                }
            }));

            System.out.println(String.format("Application started.%nStop the application using CTRL+C"));

            // block and wait shut down signal, like CTRL+C
            Thread.currentThread().join();

        } catch (InterruptedException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}