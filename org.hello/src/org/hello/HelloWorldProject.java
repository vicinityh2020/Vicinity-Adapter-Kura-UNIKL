package org.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorldProject {
	Logger s_logger=LoggerFactory.getLogger(HelloWorldProject.class);
	
	public void activate(){
		s_logger.info("Hello Git!!!");
	}

}
