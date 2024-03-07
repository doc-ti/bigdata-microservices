package edu.doc_ti.bigdatamicroservices.datagenerator;


import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class LookupData  {

	final static Logger LOG = Logger.getLogger(LookupData.class);
	
	final static String auxArrHT[] = {"bssmap", "internalcause", "nrn", "operator", "ranap", "tac"};

	static Hashtable<String, Hashtable<String,String>> htMain = new Hashtable<String, Hashtable<String,String>>() ; 

	public static void readResourceHT(String resource, Hashtable<String,String> htAux) {
		
		LookupData l = new LookupData() ;
		
		InputStream inputStream = null ;

		LOG.info("Trying to load : " + "maps/master_" + resource + ".txt");
		inputStream = l.getClass()
				.getClassLoader().getResourceAsStream("maps/master_" + resource + ".txt");
		
		
		try {
			int cont = 0 ;
			@SuppressWarnings("resource")
			Scanner s = new Scanner(inputStream).useDelimiter("\\n");
			while (s.hasNext()) {
				String line = s.next() ;
				cont++ ;
				String aux[] = line.split(";") ;
				
				if (aux.length >= 2 ) {
					htAux.put(aux[0], aux[1]) ;
				}
				
			}
			
			s.close() ;
			inputStream.close();
			LOG.info("Loaded : " + "maps/master_" + resource + ".txt with " + cont + " records");

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1) ;
		}
		
	}	
	
    public static void loadData() {
    	
    	LOG.info("LOGGING: starting app");
    	System.out.println("LOGGING: starting app ------------------------------------------------");
    	
    	for (String aux : auxArrHT) {
    		Hashtable<String, String> htAux = new Hashtable<String,String>() ;
    		readResourceHT(aux, htAux) ;
    		htMain.put ( aux, htAux) ;
    	}
    }


}
