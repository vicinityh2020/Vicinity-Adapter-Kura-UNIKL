package org.unikl.adapter.philipshue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unikl.adapter.VicinityObjectInterface.VicinityObjectInterface;
import org.unikl.adapter.integrator.UniklResourceContainer;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

// generally, this sucks, we cannot differ between which lightbulb connected to which bridge
// we actually do not know anything about bridges!
// ... should I try to do something with this?...

public class PhilipsHue implements VicinityObjectInterface {
	public static final String BUNDLE_ID = "org.unikl.adapter.philipshue";
	private static final Logger s_logger = LoggerFactory.getLogger(PhilipsHue.class);
	// List<PHLight> allLights = new ArrayList<PHLight>();
	Map<String, String> mapping;

	private PHHueSDK phHueSDK;
	// private PHBridge bridge; // TODO: support more than one bridge later

	// PhilipsSDK's brightness varies between 0 and 254
	// However, for VICINITY we should return value between 0 and 100 (percentage)
	private static final double BRIGHTNESS_CONVERT_COEFFICIENT = 2.54;
	// dynamic consumption value, valid only for model Hue A60
	// and light bulbs which have nominal consumption 8.5W
	private static final double CONSUMPTION_COEFFICIENT = 8.5;

	protected void activate(ComponentContext componentContext) {
		s_logger.info("[" + BUNDLE_ID + "]" + " activating...");

		// Gets an instance of the Hue SDK.
		phHueSDK = PHHueSDK.create();

		// phHueSDK.setDeviceName("Bridge Searcher");
		phHueSDK.setAppName("VICINITYAdapterApplication");
		phHueSDK.setDeviceName("VICINITYAdapter");

		// for getting callbacks from bridge
		phHueSDK.getNotificationManager().registerSDKListener(listener);

		// Try to automatically connect to the last known bridge. For first time use
		// this will be empty so a bridge search is automatically started.
		// prefs = HueSharedPreferences.getInstance(getApplicationContext());
		String lastIpAddress = "192.168.2.3";
		String lastUsername = "-Rakz7bPWHp6KfbkgK7pQC8CFANP-p7YtpWJhSBU";
		// Automatically try to connect to the last connected IP Address. For multiple
		// bridge support a different implementation is required.
		if (lastIpAddress != null && !lastIpAddress.equals("")) {
			PHAccessPoint lastAccessPoint = new PHAccessPoint();
			lastAccessPoint.setIpAddress(lastIpAddress);
			lastAccessPoint.setUsername(lastUsername);

			s_logger.info(
					"[" + BUNDLE_ID + "]" + "trying to connect with IP/username:" + lastIpAddress + "/" + lastUsername);

			if (!phHueSDK.isAccessPointConnected(lastAccessPoint)) {
				phHueSDK.connect(lastAccessPoint);
			}
		} else { // First time use, so perform a bridge search.
			searchBridge();
		}

		s_logger.info("[" + BUNDLE_ID + "]" + " activated!");
	}

	protected void deactivate(ComponentContext componentContext) {
		s_logger.info("PhilipsHue bundle... unloading");
		mapping.clear();
		// bridge = null;
		phHueSDK.destroySDK();
		s_logger.info("PhilipsHue bundle... unloaded");
	}

	// TODO: actually noone needs this crap, but I would like to
	// make an easter egg out of this later
	/*
	 * public void randomizeLights(final PHBridge bridge) { for (final PHLight light
	 * : allLights) { Timer timer = new Timer(); timer.scheduleAtFixedRate(new
	 * TimerTask() {
	 * 
	 * @Override public void run() { Random rand = new Random(); final PHLightState
	 * lightState = new PHLightState(); lightState.setBrightness(rand.nextInt(255));
	 * lightState.setHue(rand.nextInt(65535));
	 * lightState.setBrightness(rand.nextInt(65535)); bridge.updateLightState(light,
	 * lightState); } }, 100, 100); } }
	 */

	// +
	public void searchBridge() {
		PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
		sm.search(true, true);
	}

	public void setInstance(String oid) {
		UniklResourceContainer.getInstance().getObjectByObjectID(oid).setVicinityObjectInstance(this);
	}

	// Local SDK Listener
	private PHSDKListener listener = new PHSDKListener() {

		@Override
		public void onAccessPointsFound(List<PHAccessPoint> aps) {
			s_logger.info("[" + BUNDLE_ID + "]" + " " + aps.size() + " access point(s) found");
			for (int i = 0; i < aps.size(); i++) {
				PHAccessPoint ap = aps.get(i);
				s_logger.info("[" + BUNDLE_ID + "]" + " attempt to connect to bridge:");
				s_logger.info("[" + BUNDLE_ID + "]" + "ID: " + ap.getUsername() + " IP: " + ap.getIpAddress());
				phHueSDK.connect(ap);
			}
		}

		@Override
		public void onAuthenticationRequired(PHAccessPoint ap) {
			s_logger.info("[" + BUNDLE_ID + "] authentification required...");
			s_logger.info("[" + BUNDLE_ID + "] trying PushLinkAuthentification...");
			phHueSDK.startPushlinkAuthentication(ap);
		}

		@Override
		public void onBridgeConnected(PHBridge br, String username) {
			s_logger.info("[" + BUNDLE_ID + "] bridge connected!");

			// bridge = br;

			mapping = new HashMap<String, String>();

			phHueSDK.setSelectedBridge(br);
			phHueSDK.enableHeartbeat(br, PHHueSDK.HB_INTERVAL);

			s_logger.info("[" + BUNDLE_ID + "]" + "onBridgeConnected: " + username);

			List<PHLight> allLights = br.getResourceCache().getAllLights();

			s_logger.info("[" + BUNDLE_ID + "] " + allLights.size() + " light(s) found");

			for (PHLight light : allLights) {
				if (light == null)
					s_logger.info("[" + BUNDLE_ID + "]" + " === 1");

				if (mapping == null)
					s_logger.info("[" + BUNDLE_ID + "]" + " === 2");

				if (allLights == null)
					s_logger.info("[" + BUNDLE_ID + "]" + " === 3");

				if (UniklResourceContainer.getInstance() == null)
					s_logger.info("[" + BUNDLE_ID + "]" + " === 4");

				String oid = UniklResourceContainer.getInstance().addUniklResource("LightBulb");
				mapping.put(oid, light.getUniqueId());
				setInstance(oid);
			}
		}

		@Override
		public void onCacheUpdated(List<Integer> arg0, PHBridge arg1) {
			// TODO: Yeah, I am a lazy bitch. I will implement this, but when I have time, I
			// swear!
		}

		@Override
		public void onConnectionLost(PHAccessPoint arg0) {
			s_logger.info("[" + BUNDLE_ID + "] connection to bridge lost... retrying...");
			// bridge = null;
			mapping.clear();
			searchBridge();
		}

		@Override
		public void onConnectionResumed(PHBridge arg0) {
		}

		@Override
		public void onError(int code, String descr) {
			s_logger.info("[" + BUNDLE_ID + "] onError() " + code + " " + descr);
		}

		@Override
		public void onParsingErrors(List<PHHueParsingError> arg0) {
			s_logger.info("[" + BUNDLE_ID + "] parsing error happened!");
		}
	};

	@Override
	public String getProperty(String oid, String propertyName) {
		s_logger.info("[" + BUNDLE_ID + "] get property: " + propertyName + " oid: " + oid);

		PHBridge bridge = phHueSDK.getSelectedBridge();

		if (bridge == null)
			return "";

		List<PHLight> allLights = bridge.getResourceCache().getAllLights();

		for (PHLight light : allLights) {
			String id = mapping.get(oid);

			if (!light.getUniqueId().equals(id))
				continue;

			PHLightState lightState = light.getLastKnownLightState();

			if (propertyName.equals("brightness")) {
				Integer brightness = null;
				brightness = lightState.getBrightness();
				int value = (int) (brightness.intValue() / BRIGHTNESS_CONVERT_COEFFICIENT);

				return String.valueOf(value);

			} else if (propertyName.equals("color")) {
				StringBuffer result = new StringBuffer("");
				float[] xy = new float[2];
				xy[0] = lightState.getX();
				xy[1] = lightState.getY();
				int rgb = PHUtilities.colorFromXY(xy, light.getModelNumber());
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb >> 0) & 0xFF;
				result.append(String.valueOf(Integer.toHexString(r)));
				result.append(String.valueOf(Integer.toHexString(g)));
				result.append(String.valueOf(Integer.toHexString(b)));

				return result.toString();

			} else if (propertyName.equals("consumption")) {
				Integer brightness = null;
				brightness = lightState.getBrightness();
				double value = brightness.intValue() / BRIGHTNESS_CONVERT_COEFFICIENT;

				return String.valueOf(CONSUMPTION_COEFFICIENT * (value / 100));
			}
		}
		return "";
	}

	@Override
	public boolean setProperty(String oid, String propertyName, String value) {
		s_logger.info("[" + BUNDLE_ID + "] set property: " + propertyName + " oid: " + oid + " value: " + value);

		PHBridge bridge = phHueSDK.getSelectedBridge();

		if (bridge == null)
			return false;

		List<PHLight> allLights = bridge.getResourceCache().getAllLights();

		for (PHLight light : allLights) {
			String id = mapping.get(oid);
			s_logger.info("[" + BUNDLE_ID + "] id = " + id);

			if (!light.getUniqueId().equals(id))
				continue;

			PHLightState lightState = new PHLightState();

			if (propertyName.equals("brightness")) {
				int digValue = (int) (Integer.parseInt(value) * BRIGHTNESS_CONVERT_COEFFICIENT);
				lightState.setBrightness(digValue);
				bridge.updateLightState(light, lightState);

				return true;

			} else if (propertyName.equals("color")) {

				if (value.length() != 6)
					return false;
				float[] xy = PHUtilities.calculateXYFromRGB(Integer.parseInt(value.substring(0, 2), 16),
						Integer.parseInt(value.substring(2, 4), 16), Integer.parseInt(value.substring(4, 6), 16),
						light.getModelNumber());
				s_logger.info("[" + BUNDLE_ID + "] setProperty color " + Integer.parseInt(value.substring(0, 2), 16)
						+ " " + Integer.parseInt(value.substring(2, 4), 16) + " "
						+ Integer.parseInt(value.substring(4, 6), 16));
				lightState.setX(xy[0]);
				lightState.setY(xy[1]);
				bridge.updateLightState(light, lightState);

				return true;
			}
		}
		return false;
	}
}
