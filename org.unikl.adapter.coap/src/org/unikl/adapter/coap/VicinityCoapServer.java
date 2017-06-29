package org.unikl.adapter.coap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.osgi.service.component.ComponentContext;
import org.unikl.adapter.sensors.SensorChangedListener;
import org.unikl.adapter.sensors.SensorService;
import org.unikl.adapter.sensors.SensorService.NoSuchSensorOrActuatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VicinityCoapServer implements SensorChangedListener {
	private static final Logger s_logger = LoggerFactory.getLogger(VicinityCoapServer.class);
	
	private CoapServer _coapServer;
	
	private CoapResource _uniklAdapterResource;
	private CoapResource _sensorsRootResource;
	private CoapResource _actuatorsRootResource;

	private SensorService _sensorService;

	public VicinityCoapServer() {
		super();

		_uniklAdapterResource = new CoapResource("gh");
		_sensorsRootResource = new CoapResource("sens");
		_actuatorsRootResource = new CoapResource("act");

		_uniklAdapterResource.add(_sensorsRootResource, _actuatorsRootResource);
	}

	protected void setUniklAdapterSensorService(SensorService sensorService) {
		_sensorService = sensorService;
	}

	protected void unsetUniklAdapterSensorService(SensorService sensorService) {
		_sensorService = null;
	}

	protected void activate(ComponentContext componentContext) {
		s_logger.info("Activating UniklAdapterCoapServer...");

		_coapServer = new CoapServer();
		_coapServer.add(_uniklAdapterResource);
		_coapServer.start();

		/* create sensors for the BMP280 */
		SensorResource temperatureSensor = new SensorResource("temperature");
		SensorResource pressureSensor = new SensorResource("pressure");
		SensorResource humiditySensor = new SensorResource("humidity");

		_sensorsRootResource.add(temperatureSensor);
		_sensorsRootResource.add(pressureSensor);
		_sensorsRootResource.add(humiditySensor);
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
			
			SensorResource humidityResource = new SensorResource("humidity");
			humidityResource.setSensorValue("" + _sensorService.getSensorValue("humidity"));
			_sensorsRootResource.add(humidityResource);
			
		} catch (NoSuchSensorOrActuatorException e) {
			e.printStackTrace();
		}

		s_logger.info("Activating UniklAdapterCoapServer... Done.");
	}

	protected void deactivate(ComponentContext componentContext) {
		s_logger.debug("Deactivating UniklAdapterCoapServer...");

		_coapServer.stop();

		s_logger.debug("Deactivating UniklAdapterCoapServer... Done.");
	}

	@Override
	public void sensorChanged(String sensorName, Object newValue) {
		SensorResource sensorResource = (SensorResource) _sensorsRootResource
				.getChild(sensorName);
		if (sensorResource != null) {
			sensorResource.setSensorValue(newValue.toString());
		}
	}

}
