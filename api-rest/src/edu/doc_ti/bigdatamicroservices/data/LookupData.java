package edu.doc_ti.bigdatamicroservices.data;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class LookupData  {

	
	final static Logger LOG = Logger.getLogger(LookupData.class);
	
	public final static String namesHT[] = {"bssmap", "internalcause", "nrn", "operator", "ranap", "tac"};

	public static Hashtable<String, ArrayList<String>> arrMain = new Hashtable<String, ArrayList<String>>() ; 
	public static Hashtable<String, Hashtable<String,String>> htMain = new Hashtable<String, Hashtable<String,String>>() ; 

	static {
		loadTablesInMemory() ;
	}

	public static void readResourceHT(String resource, ArrayList<String> arrAux, Hashtable<String, String> htAux ) {
		
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
					arrAux.add(aux[0]) ;
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
	
    public static void loadTablesInMemory() {
    	
    	LOG.info("LOGGING: loading lookup data ------------------------------------------------");
    	System.out.println("LOGGING: loading lookup data ------------------------------------------------");
    	
    	for (String aux : namesHT) {
    		ArrayList<String> arrAux = new ArrayList<String>() ;
    		Hashtable<String,String> htAux = new Hashtable<String,String>() ;
    		readResourceHT(aux, arrAux, htAux) ;
    		htMain.put ( aux, htAux) ;
    		arrMain.put ( aux, arrAux) ;
    	}
    }


}
