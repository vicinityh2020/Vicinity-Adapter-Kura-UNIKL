package org.unikl.adapter.sensors.BMP280;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unikl.adapter.VicinityObjectInterface.VicinityObjectInterface;
import org.unikl.adapter.integrator.UniklResourceContainer;
import com.pi4j.io.i2c.I2CBus;

import net.pateras.iot.BMP280.BMP280;
import net.pateras.iot.BMP280.BMP280.IIRFilter;
import net.pateras.iot.BMP280.BMP280.Mode;
import net.pateras.iot.BMP280.BMP280.Pressure_Sample_Resolution;
import net.pateras.iot.BMP280.BMP280.Standby_Time;
import net.pateras.iot.BMP280.BMP280.Temperature_Sample_Resolution;

public class TemperaturePressureBMP280 implements VicinityObjectInterface {
	private static final Logger s_logger = LoggerFactory.getLogger(TemperaturePressureBMP280.class);

	// TODO: separate file with global definitions?
	private static final String SENSOR_NAME = "BMP280"; 
	private static final String BUNDLE_ID = "org.unikl.adapter.sensors.raspberrypi";
	
	private static BMP280 _bmp280;
	double _temperature;
	double _pressure;
	
	private String oid;

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

		oid = UniklResourceContainer.getInstance().addUniklResource("Thermostate");
    	UniklResourceContainer.getInstance().getObjectByObjectID(oid).setVicinityObjectInstance(this);
		s_logger.info("[" + BUNDLE_ID + "] bundle is loaded");
	}
	
	protected void deactivate() {
		UniklResourceContainer.getInstance().removeUniklResource(oid);
		s_logger.info("[" + BUNDLE_ID + "]  bundle is unloaded");
	}
	
	public double getSensorValue(String oid, String sensorName) throws Exception {
		if ("temp1".equals(sensorName)) {
			double[] results = null;
			try {
				results = _bmp280.sampleDeviceReads();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//s_logger.info("Temperature in Celsius : {}", results[BMP280.TEMP_VAL_C]);
			return results[BMP280.TEMP_VAL_C];
		} else if ("pressure".equals(sensorName)) {
			double[] results = null;
			try {
				results = _bmp280.sampleDeviceReads();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//s_logger.info("Pressure : {}", results[BMP280.PRES_VAL]);
			return results[BMP280.PRES_VAL];
		} else {
			throw new Exception(SENSOR_NAME + " does not support \"" + sensorName + "\"" );
		}
	}

	@Override
	public VicinityObjectInterface getInstance() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public String getProperty(String oid, String propertyName) {
		try {
			return String.valueOf(getSensorValue(oid, propertyName));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public boolean setProperty(String oid, String propertyName, String value) {
		// TODO Auto-generated method stub
		return false;
	}
}
