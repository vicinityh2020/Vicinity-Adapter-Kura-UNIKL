package org.unikl.adapter.coap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.osgi.service.component.ComponentContext;
import org.unikl.adapter.sensors.SensorChangedListener;
import org.unikl.adapter.sensors.SensorService;
import org.unikl.adapter.sensors.SensorService.NoSuchSensorOrActuatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UniklCoapServer implements SensorChangedListener {
	private static final Logger s_logger = LoggerFactory.getLogger(UniklCoapServer.class);
	
	private CoapServer _coapServer;
	
	private CoapResource _uniklAdapterResource;
	private CoapResource _sensorsRootResource;
	private CoapResource _actuatorsRootResource;

	private SensorService _sensorService;

	public UniklCoapServer() {
		super();

		_uniklAdapterResource = new CoapResource("unikl");
		_sensorsRootResource = new CoapResource("sensors");
		_actuatorsRootResource = new CoapResource("actuators");

		_uniklAdapterResource.add(_sensorsRootResource, _actuatorsRootResource);
	}

	protected void setUniklAdapterSensorService(SensorService sensorService) {
		_sensorService = sensorService;
	}

	protected void unsetUniklAdapterSensorService(SensorService sensorService) {
		_sensorService = null;
	}

	protected void activate(ComponentContext componentContext) {
		s_logger.info("Activating UniklCoapServer...");

		_coapServer = new CoapServer();
		_coapServer.add(_uniklAdapterResource);
		_coapServer.start();

		/* create sensors for the BMP280 */
		SensorResource temperatureSensor = new SensorResource("temperature");
		SensorResource pressureSensor = new SensorResource("pressure");

		_sensorsRootResource.add(temperatureSensor);
		_sensorsRootResource.add(pressureSensor);
		//_actuatorsRootResource.add(hueLight);

		/* add sensors */
		/* TODO: fix it for the case of a big number of sensors */
		try {
			SensorResource temperatureResource = new SensorResource("temperature");
			temperatureResource.setSensorValue("" + _sensorService.getSensorValue("temperature"));
			_sensorsRootResource.add(temperatureResource);
			
			SensorResource pressureResource = new SensorResource("pressure");
			pressureResource.setSensorValue("" + _sensorService.getSensorValue("pressure"));
			_sensorsRootResource.add(pressureResource);
		} catch (NoSuchSensorOrActuatorException e) {
			e.printStackTrace();
		}

		s_logger.info("Activating UniklCoapServer... Done.");
	}

	protected void deactivate(ComponentContext componentContext) {
		s_logger.debug("Deactivating UniklCoapServer...");

		_coapServer.stop();

		s_logger.debug("Deactivating UniklCoapServer... Done.");
	}

	@Override
	public void sensorChanged(String sensorName, Object newValue) {
		SensorResource sensorResource = (SensorResource) _sensorsRootResource.getChild(sensorName);
		
		if (sensorResource != null) {
			sensorResource.setSensorValue(newValue.toString());
		}
	}
}