package edu.doc_ti.bigdatamicroservices.webservice;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

import edu.doc_ti.bigdatamicroservices.data.LookupData;

@WebListener
public class ContextManager implements ServletContextListener {

	final static Logger LOG = Logger.getLogger(ContextManager.class);
	


	
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    	
    	LOG.info("LOGGING: starting app");
    	System.out.println("LOGGING: starting app ------------------------------------------------");
    	
    	LookupData.htMain.size() ; 
    	
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    	LOG.info("LOGGING: stopping app");
    	System.out.println("LOGGING: stopping app");

    }
}
