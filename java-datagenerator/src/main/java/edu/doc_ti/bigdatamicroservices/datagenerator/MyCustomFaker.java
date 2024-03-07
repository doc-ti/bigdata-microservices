package edu.doc_ti.bigdatamicroservices.datagenerator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.datafaker.formats.Csv;
import net.datafaker.formats.Format;
import net.datafaker.providers.base.BaseFaker;

@SuppressWarnings("deprecation")
public class MyCustomFaker extends BaseFaker {
	static MyCustomFaker myFaker = new MyCustomFaker();

    public MyElements MyElements() {
        return getProvider(MyElements.class, MyElements::new, this);
    }

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static Date tsFrom =null ;
	static Date tsTo = null ;


	public static String DELIMITER = ";" ;
	
	
	public static void main(String[] args) {
		
		
		System.out.println(getData(1)) ;
		
	}
	
	public static String getData(int numToPrint) {
		
		if (tsFrom == null ) {
		    try {
				tsFrom = sdf.parse(sdf.format(new Date() ));
				tsTo = new Date( tsFrom.getTime() + 24*3600*1000 - 1000);
			} catch (ParseException e) {}
		}
		
        return Format.toCsv(
        		
                Csv.Column.of("f3",  () -> myFaker.MyElements().nextDeterminedDistribElement("id3")),
                Csv.Column.of("f4",  () -> myFaker.MyElements().nextDeterminedDistribElement("id4")),
                Csv.Column.of("f5",  () -> myFaker.MyElements().nextDeterminedDistribElement("id5")),
                Csv.Column.of("f6",  () -> myFaker.MyElements().exponentialDistributedNumber(500, 10000, 0) ),
                Csv.Column.of("f7",  () -> myFaker.MyElements().nextDeterminedDistribElement("id7")),
                Csv.Column.of("f8",  () -> myFaker.MyElements().nextDeterminedDistribElement("id8")),
                Csv.Column.of("f9",  () -> myFaker.MyElements().nextDeterminedDistribElement("id9")),
                Csv.Column.of("f10", () -> myFaker.MyElements().nextDeterminedDistribElement("id10")),
                Csv.Column.of("f11", () -> myFaker.expression("#{numerify '###'}")) ,
                Csv.Column.of("f12", () -> myFaker.expression("#{numerify '##'}")) ,
                Csv.Column.of("f13", () -> myFaker.expression("#{numerify '###'}")) ,
                Csv.Column.of("f14", () -> myFaker.expression("#{numerify '###'}")) ,
                Csv.Column.of("f15", () -> myFaker.expression("#{numerify '#'}")) ,
                Csv.Column.of("f16", () -> myFaker.expression("#{numerify '###'}")) ,
                Csv.Column.of("f17", () -> myFaker.expression("#{numerify '####'}")) ,
                Csv.Column.of("f18", () -> myFaker.expression("#{numerify '#'}")) ,
                Csv.Column.of("f19", () -> myFaker.expression("#{numerify '#'}")) ,
                Csv.Column.of("f20", () -> myFaker.expression("#{numerify '##'}")) ,
                Csv.Column.of("f21", () -> myFaker.expression("#{numerify '###'}")) ,
                Csv.Column.of("f22", () -> myFaker.MyElements().exponentialDistributedNumber(20, 200, 0) ),
                Csv.Column.of("f23", () -> myFaker.MyElements().exponentialDistributedNumber(5, 99, 0) ),
                Csv.Column.of("f24", () -> myFaker.expression("#{numerify '#'}")) ,
                Csv.Column.of("f25", () -> myFaker.date().between(tsFrom, tsTo, "yyyy-MM-dd'T'HH:mm:ss") ), 
                Csv.Column.of("f26", () -> myFaker.date().between(tsFrom, tsTo, "yyyy-MM-dd'T'HH:mm:ss") ),
                Csv.Column.of("f27", () -> myFaker.MyElements().exponentialDistributedNumber(25, 99, 0) ),
                Csv.Column.of("f28", () -> myFaker.MyElements().exponentialDistributedNumber(5, 19, 0) ),
                Csv.Column.of("f29", () -> myFaker.MyElements().exponentialDistributedNumber(100, 200) ),
                Csv.Column.of("f30", () -> myFaker.MyElements().nextDeterminedDistribElement("id30")),
                Csv.Column.of("f31", () -> myFaker.expression("#{numerify '16704#####'}")) ,
                Csv.Column.of("f32", () -> myFaker.expression("#{numerify '#'}")) ,
                Csv.Column.of("f33", () -> myFaker.expression("#{numerify '#'}")) ,
                Csv.Column.of("f34", () -> myFaker.expression("#{numerify '##'}")) ,	
                Csv.Column.of("f35", () -> myFaker.expression("#{numerify '310004##########'}")) ,
                Csv.Column.of("f36", () -> myFaker.expression("#{numerify '#'}")) ,
//                Csv.Column.of("f37", () -> myFaker.expression("#{numerify '#'}")) ,
                Csv.Column.of("f38", () -> myFaker.expression("#{numerify '#'}")) ,
                Csv.Column.of("f39", () -> myFaker.expression("#{numerify '#'}")) ,
                Csv.Column.of("f40", () -> myFaker.expression("#{numerify '#'}")) ,
                Csv.Column.of("f41", () -> myFaker.MyElements().nextDeterminedDistribElement("id41")),
                Csv.Column.of("f42", () -> myFaker.expression("#{numerify '#'}")) ,
                Csv.Column.of("f43", () -> myFaker.expression("#{numerify '#'}")) ,
                Csv.Column.of("f44", () -> myFaker.expression("#{numerify '#'}")) ,
                Csv.Column.of("f45", () -> myFaker.expression("#{numerify '#'}")) ,
                Csv.Column.of("f45", () -> myFaker.expression("#{letterify 'A?'}").toUpperCase()) ,
                Csv.Column.of("f45", () -> myFaker.expression("#{letterify 'B?'}").toUpperCase()) ,
                Csv.Column.of("f45", () -> myFaker.expression("#{letterify 'C??'}").toUpperCase()) ,
                Csv.Column.of("f49", () -> myFaker.expression("#{bothify 'D?##'}").toUpperCase()) ,
                Csv.Column.of("f50", () -> myFaker.MyElements().exponentialDistributedNumber(20, 80, 2) ),
                Csv.Column.of("f51", () -> myFaker.MyElements().exponentialDistributedNumber(20, 80, 2) ),
                Csv.Column.of("f52", () -> myFaker.MyElements().exponentialDistributedNumber(20, 80, 2) ),
                Csv.Column.of("f53", () -> myFaker.date().between(tsFrom, tsTo, "yyyy-MM-dd'T'HH:mm:ss") ), 
                Csv.Column.of("f54", () -> myFaker.MyElements().nextDeterminedDistribElement("id54"))
                )
            .separator(DELIMITER)
            .header(false)
            .limit(numToPrint).build().get();		
	}

    
}

