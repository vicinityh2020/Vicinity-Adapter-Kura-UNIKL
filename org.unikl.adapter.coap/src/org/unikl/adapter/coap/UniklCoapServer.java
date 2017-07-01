package org.unikl.adapter.coap;

import java.util.Timer;
import java.util.TimerTask;

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

	/* Connected sensors */
	SensorResource temperatureResource = new SensorResource("temperature", "Thermostate");
	SensorResource pressureResource = new SensorResource("pressure", "Pressure Gauge");

	public UniklCoapServer() {
		super();

		_uniklAdapterResource = new CoapResource("unikl");
		_sensorsRootResource = new CoapResource("sensors");
		_actuatorsRootResource = new CoapResource("actuators");

		_uniklAdapterResource.add(_sensorsRootResource, _actuatorsRootResource);
	}

	protected void setUniklSensorService(SensorService sensorService) {
		_sensorService = sensorService;
	}

	protected void unsetUniklSensorService(SensorService sensorService) {
		_sensorService = null;
	}

	protected void activate(ComponentContext componentContext) {
		s_logger.info("Activating UniklCoapServer...");

		_coapServer = new CoapServer();
		_coapServer.add(_uniklAdapterResource);
		_coapServer.start();

		//_actuatorsRootResource.add(hueLight);

		/* add sensors */
		/* TODO: fix it for the case of a big number of sensors */
		try {
			temperatureResource.setSensorValue("" + _sensorService.getSensorValue("temperature"));
			_sensorsRootResource.add(temperatureResource);
			
			pressureResource.setSensorValue("" + _sensorService.getSensorValue("pressure"));
			_sensorsRootResource.add(pressureResource);
		} catch (NoSuchSensorOrActuatorException e) {
			e.printStackTrace();
		}
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			  @Override
			  public void run() {
					try {
						temperatureResource.setSensorValue("" + _sensorService.getSensorValue("temperature"));
					} catch (NoSuchSensorOrActuatorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					_sensorsRootResource.add(temperatureResource);
					
					try {
						pressureResource.setSensorValue("" + _sensorService.getSensorValue("pressure"));
					} catch (NoSuchSensorOrActuatorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					_sensorsRootResource.add(pressureResource);			  }
			}, 500, 500);

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