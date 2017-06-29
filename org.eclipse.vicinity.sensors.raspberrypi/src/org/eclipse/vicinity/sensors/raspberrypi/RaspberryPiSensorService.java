package org.eclipse.vicinity.sensors.raspberrypi;

import org.eclipse.vicinity.sensors.SensorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaspberryPiSensorService implements SensorService{
	private static final Logger s_logger = LoggerFactory.getLogger(RaspberryPiSensorService.class);

	// TODO: separate file with global definitions?
	private static final String PPOJECT_ID = "VICINITY"; 
	private static final String BUNDLE_ID = "org.eclipse.vicinity.sensors.raspberrypi"; 

	protected void activate() {
		s_logger.info("[" + PPOJECT_ID + "] " + BUNDLE_ID + " bundle is loaded");
	}
	
	protected void deactivate() {
		s_logger.info("[" + PPOJECT_ID + "] " + BUNDLE_ID + " bundle is unloaded");
	}
	
	@Override
	public Object getSensorValue(String sensorName) throws NoSuchSensorOrActuatorException {
		if ("temperature".equals(sensorName)) {
			return null/* readTemperature() */;
		} else if ("light".equals(sensorName)) {
			return null /*readLightState()*/;
		} else
			throw new SensorService.NoSuchSensorOrActuatorException();
	}

	@Override
	public void setActuatorValue(String actuatorName, Object value) throws NoSuchSensorOrActuatorException {
		// TODO Auto-generated method stub
	}	
}
