
package edu.doc_ti.bigdatamicroservices.datagenerator ;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.datafaker.formats.Csv;
import net.datafaker.formats.Format;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

@SuppressWarnings("deprecation")
public class FileGenerator {

//	public static String DELIMITER = "," ;
//	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat sdfFileName = new SimpleDateFormat("yyyyMMdd_HHmmss");
	static SimpleDateFormat sdfNice = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	static Date tsFrom = null ;
//	static Date tsTo = null ;
	static String path = "." ;
	static int maxFiles = 20 ;

	static MyCustomFaker myFaker = new MyCustomFaker();
	
	public static void main(String[] args) {
		
		Options options = new Options();
		
		int numRecords = 5000 ;
		
		options.addOption(new Option("h", "help", false, "Print this help"));
		options.addOption(new Option("d", "date", true, "Date for the input data, format [yyyy-mm-dd]"));
		options.addOption(new Option("s", "seconds", true, "Generate a file every s seconds (default 30)"));
		options.addOption(new Option("n", "numrecords", true, "Number of records in file (default 5000)"));
		options.addOption(new Option("m", "maxfiles", true, "Numeber of files to generate (default 20)"));
		options.addOption(new Option("p", "path", true, "Path to write the file"));
		

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
			formatter.printHelp("FileGenerator", options);
			System.exit(0) ;
		}
		
		
		try {
			MyCustomFaker.tsFrom = MyCustomFaker.sdf.parse(MyCustomFaker.sdf.format(new Date() ));
		} catch (ParseException e) {}
		
		if ( cmd.hasOption('d')  ) {
			try {
				MyCustomFaker.tsFrom = MyCustomFaker.sdf.parse(cmd.getParsedOptionValue("date").toString());
			} catch (Exception e) {
			}
		} 
		MyCustomFaker.tsTo = new Date( MyCustomFaker.tsFrom.getTime() + 24*3600*1000 - 1000);
		
		if ( cmd.hasOption('n')  ) {
			try {
				numRecords = Integer.parseInt(cmd.getParsedOptionValue("numrecords").toString());
			} catch (Exception e) {
			}
		} 

		if ( cmd.hasOption('m')  ) {
			try {
				maxFiles = Integer.parseInt(cmd.getParsedOptionValue("m").toString());
			} catch (Exception e) {
			}
		} 

		if ( cmd.hasOption('p')  ) {
			try {
				path = cmd.getParsedOptionValue("path").toString();
			} catch (Exception e) {}
		} 

		String filename = "" ;
		if ( cmd.hasOption('s')  ) {
			int seconds = 30 ;
			try {
				seconds = Integer.parseInt(cmd.getParsedOptionValue("seconds").toString());
			} catch (Exception e) {}
			
			if ( seconds < 1 ) {
				seconds = 1 ;
			}
			
			int counter = 0 ;
			while ( counter < maxFiles )  {
				
				counter++ ;
				long tnext = System.currentTimeMillis()  + 1000*seconds ;
				if ( !cmd.hasOption('d')  ) {
					MyCustomFaker.tsFrom = new Date() ;
					MyCustomFaker.tsTo = new Date( MyCustomFaker.tsFrom.getTime() + 1000*seconds - 100);
				}
				
				filename = "file_" + sdfFileName.format(new Date() ) + "_" + String.format( "%06d", counter); ;
				generateFile( filename, numRecords ) ;

				while ( System.currentTimeMillis() < tnext) {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {}
				}
			}
		} else {
			
			filename = "data_" + sdfFileName.format(new Date() ) ;
			generateFile( filename, numRecords ) ;
		}
	}
	
	private static void generateFile(String filename, int numRecords) {
		
		
		String fullFileName = path + "/" + filename + ".tmp" ;
		System.out.println (String.format( "%s - Generating file: %s, records: %d, date from: %s, to: %s",
				sdfNice.format(new Date() ), 
				fullFileName, numRecords, sdfNice.format(MyCustomFaker.tsFrom), sdfNice.format(MyCustomFaker.tsTo) )) ;
		
		int printed = 0 ;
		BufferedOutputStream bfout = null ;
		
		File myFile = new File(fullFileName) ;
		
		try {
			bfout = new BufferedOutputStream (new FileOutputStream(myFile ) ) ;
		
			while ( printed < numRecords ) {
				int numToPrint = numRecords - printed ;
				
				if ( numToPrint > 100 ) {
					numToPrint = 100 ;
				}
				printed += numToPrint ;
				
				String data = MyCustomFaker.getData(numToPrint).replaceAll("\"", "") ;
				bfout.write( data.getBytes() );
			}
			bfout.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println () ;
		}
		
		myFile.renameTo(new File(path + "/" + filename)) ;
	}
}
