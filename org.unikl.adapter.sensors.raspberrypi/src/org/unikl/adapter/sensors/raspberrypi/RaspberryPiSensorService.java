package org.unikl.adapter.sensors.raspberrypi;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unikl.adapter.sensors.SensorChangedListener;
import org.unikl.adapter.sensors.SensorService;

import com.pi4j.io.i2c.I2CBus;

import net.pateras.iot.BMP280.BMP280;
import net.pateras.iot.BMP280.BMP280.IIRFilter;
import net.pateras.iot.BMP280.BMP280.Mode;
import net.pateras.iot.BMP280.BMP280.Pressure_Sample_Resolution;
import net.pateras.iot.BMP280.BMP280.Standby_Time;
import net.pateras.iot.BMP280.BMP280.Temperature_Sample_Resolution;

public class RaspberryPiSensorService implements SensorService{
	private static final Logger s_logger = LoggerFactory.getLogger(RaspberryPiSensorService.class);

	// TODO: separate file with global definitions?
	private static final String PPOJECT_ID = "VICINITY"; 
	private static final String BUNDLE_ID = "org.unikl.adapter.sensors.raspberrypi";
	
	private List<SensorChangedListener> _listeners = new CopyOnWriteArrayList<SensorChangedListener>();
	
	private static BMP280 _bmp280;
	double _temperature;
	double _pressure;

	protected void activate() throws Exception {
		_bmp280 = new BMP280(BMP280.Protocol.I2C, BMP280.ADDR_SDO_2_VDDIO, I2CBus.BUS_1);
		
		_bmp280.setIndoorNavigationMode();
		
		/* Example of setting individual settings 
		 * Settings which can be set are
		 * - Operation mode (sleep, force, normal)
		 * - Temperate oversample (skip, x1, x2, x4, x8, x16)
		 * - Pressure oversample (skip, x1, x2, x4, x8, x16)
		 * - IIRFilter value (off, x2, x4, x8, x16)
		 * - Standby time between device updating temp/pres registers (for normal mode) */
		_bmp280.setMode(Mode.NORMAL, true);
		_bmp280.setTemperatureSampleRate(Temperature_Sample_Resolution.TWO, true);
		_bmp280.setPressureSampleRate(Pressure_Sample_Resolution.SIXTEEN, true);
		_bmp280.setIIRFilter(IIRFilter.SIXTEEN, true);
		_bmp280.setStandbyTime(Standby_Time.MS_POINT_5, true);

		s_logger.info("[" + PPOJECT_ID + "] " + BUNDLE_ID + " bundle is loaded");
	}
	
	protected void deactivate() {
		s_logger.info("[" + PPOJECT_ID + "] " + BUNDLE_ID + " bundle is unloaded");
	}
	
	@Override
	public Object getSensorValue(String sensorName) throws NoSuchSensorOrActuatorException {
		if ("temperature".equals(sensorName)) {
			double[] results = null;
			try {
				results = _bmp280.sampleDeviceReads();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s_logger.info("Temperature in Celsius : {}", results[BMP280.TEMP_VAL_C]);
			return results[BMP280.TEMP_VAL_C];
		} else if ("pressure".equals(sensorName)) {
			double[] results = null;
			try {
				results = _bmp280.sampleDeviceReads();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s_logger.info("Pressure : {}", results[BMP280.PRES_VAL]);
			return results[BMP280.PRES_VAL];
		} else
			throw new SensorService.NoSuchSensorOrActuatorException();
	}

	@Override
	public void setActuatorValue(String actuatorName, Object value) throws NoSuchSensorOrActuatorException {
		// TODO Auto-generated method stub
	}
	
	public void addSensorChangedListener(SensorChangedListener listener) {
		_listeners.add(listener);
	}

	public void removeSensorChangedListener(SensorChangedListener listener) {
		_listeners.remove(listener);
	}

	private void notifyListeners(String sensorName, Object newValue) {
		for (SensorChangedListener listener : _listeners) {
			listener.sensorChanged(sensorName, newValue);
		}		
	}
}
