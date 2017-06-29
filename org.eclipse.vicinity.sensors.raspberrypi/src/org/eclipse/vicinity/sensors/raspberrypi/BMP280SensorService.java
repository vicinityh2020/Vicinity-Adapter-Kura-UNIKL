package org.eclipse.vicinity.sensors.raspberrypi;

import org.eclipse.vicinity.sensors.SensorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BMP280SensorService implements SensorService{
	private static final Logger s_logger = LoggerFactory.getLogger(BMP280SensorService.class);

	protected void activate() {
		s_logger.info("============ loaded");
	}
	
	protected void deactivate() {
		s_logger.info("============ unloaded");
	}
	
	@Override
	public Object getSensorValue(String sensorName) throws NoSuchSensorOrActuatorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActuatorValue(String actuatorName, Object value) throws NoSuchSensorOrActuatorException {
		// TODO Auto-generated method stub
		
	}
	
	
}
