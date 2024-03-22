package edu.doc_ti.bigdatamicroservices.ws_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import edu.doc_ti.bigdatamicroservices.ws_spring.resource.LookupData;

@SpringBootApplication
public class WsSpringApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(WsSpringApplication.class, args);
		@SuppressWarnings("unused")
		int aux = LookupData.namesHT.length ;
	}

}
