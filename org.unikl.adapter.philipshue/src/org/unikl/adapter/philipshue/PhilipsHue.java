package org.unikl.adapter.philipshue;

import java.util.List;

import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;

public class PhilipsHue implements PHSDKListener {
	private static final Logger s_logger = LoggerFactory.getLogger(PhilipsHue.class);
	
    private PHHueSDK phHueSDK;
	
	protected void activate(ComponentContext componentContext) {
		s_logger.info("PhilipsHue bundle... loading");

        // Gets an instance of the Hue SDK.
        phHueSDK = PHHueSDK.create();
        phHueSDK.setDeviceName("Bridge Searcher");
        phHueSDK.getNotificationManager().registerSDKListener(this);
        PHBridgeSearchManager sm = (PHBridgeSearchManager)phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);

        // Perform uPnP search
        sm.search(true, true);

		s_logger.info("PhilipsHue bundle... loaded");
	}

	protected void deactivate(ComponentContext componentContext) {
		s_logger.info("PhilipsHue bundle... unloading");

		s_logger.info("PhilipsHue bundle... unloaded");		
	}
	
	@Override
	public void onAccessPointsFound(List<PHAccessPoint> arg0) {
		s_logger.info("== 1");
	}

	@Override
	public void onAuthenticationRequired(PHAccessPoint arg0) {
		s_logger.info("== 2");
	}

	@Override
	public void onBridgeConnected(PHBridge arg0, String arg1) {
		s_logger.info("== 3");
	}

	@Override
	public void onCacheUpdated(List<Integer> arg0, PHBridge arg1) {
		s_logger.info("== 4");
	}

	@Override
	public void onConnectionLost(PHAccessPoint arg0) {
		s_logger.info("== 5");
	}

	@Override
	public void onConnectionResumed(PHBridge arg0) {
		s_logger.info("== 6");
	}

	@Override
	public void onError(int arg0, String arg1) {
		s_logger.info("== 7 " + arg0 + " " +  arg1);
	}

	@Override
	public void onParsingErrors(List<PHHueParsingError> arg0) {
		s_logger.info("== 8");
	}
}
