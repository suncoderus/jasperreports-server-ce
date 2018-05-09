package com.jaspersoft.jasperserver.rest.test;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class RESTClient {
	static Logger logger = Logger.getLogger(RESTClient.class);
	public static void main (String[] args){
		
		PropertyConfigurator.configure(RESTTestUtils.LOG4J_PATH);
		Result result = JUnitCore.runClasses(	PUTTest.class , 
												GETTest.class , 
												POSTTest.class
											);
		
		if (result.getFailures().size()!=0){
			for (Failure failure : result.getFailures()) {
				System.out.println(failure.toString());
			}
		}
		else {
			System.out.println("JUNIT completed successfully");
		}
		
	}
}
