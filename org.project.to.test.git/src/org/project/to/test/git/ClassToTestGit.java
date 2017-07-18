package org.project.to.test.git;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassToTestGit {
	Logger s_logger=LoggerFactory.getLogger(ClassToTestGit.class);
	
	public void activate(){
		s_logger.info("Hello Git!!!");
		s_logger.info("Start Bundle");
	}
	
	public void deactivate(){
		s_logger.info("Bye Git!!!");
		s_logger.info("Stop Bundle");
	}
}
