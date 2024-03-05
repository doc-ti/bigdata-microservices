package edu.doc_ti.bigdatamicroservices.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ReadResource {
	
	static ReadResource b = new ReadResource() ;
	
	@SuppressWarnings("resource")
	public static String readResource(String resource) {
		
		
		InputStream inputStream = null ;
		
		inputStream = b.getClass()
				.getClassLoader().getResourceAsStream("" + resource);
		
		
		try {
			Scanner s = new Scanner(inputStream).useDelimiter("\\A");
			String result = s.hasNext() ? s.next() : "";
			s.close() ;
			inputStream.close();
			return result ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "" ;
	}
}
