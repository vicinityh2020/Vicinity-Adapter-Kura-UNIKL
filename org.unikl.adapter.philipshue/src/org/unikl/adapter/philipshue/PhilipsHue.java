package org.unikl.adapter.philipshue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

public class PhilipsHue {
	public static final String BUNDLE_ID = "org.unikl.adapter.philipshue";
	private static final Logger s_logger = LoggerFactory.getLogger(PhilipsHue.class);
	
    private PHHueSDK phHueSDK;
    PHBridge bridge; // TODO: support more than one bridge later
    
	protected void activate(ComponentContext componentContext) {
		s_logger.info("[" + BUNDLE_ID + "]" + " activating...");

        // Gets an instance of the Hue SDK.
        phHueSDK = PHHueSDK.create();
        phHueSDK.setDeviceName("Bridge Searcher");
        // for getting callbacks from bridge
        phHueSDK.getNotificationManager().registerSDKListener(listener);
        // Perform bridge search

        searchBridge();

		s_logger.info("[" + BUNDLE_ID + "]" + " activated!");
	}

	protected void deactivate(ComponentContext componentContext) {
		s_logger.info("PhilipsHue bundle... unloading");

		s_logger.info("PhilipsHue bundle... unloaded");		
	}
	
    public void randomizeLights(final PHBridge bridge) {
    	List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        for (final PHLight light : allLights) {
        	Timer timer = new Timer();
        	timer.scheduleAtFixedRate(new TimerTask() {
  			@Override
  			public void run() {
  				Random rand = new Random();
  				final PHLightState lightState = new PHLightState();
  					lightState.setBrightness(rand.nextInt(65535));
  			    	lightState.setHue(rand.nextInt(65535));
  			    	lightState.setBrightness(rand.nextInt(65535));
  		            bridge.updateLightState(light, lightState);
  			  }
  			}, 100, 100);
        }
	}

    // +
    public void searchBridge() {
        PHBridgeSearchManager sm = (PHBridgeSearchManager)phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        sm.search(true, true);
    }
    
    // +
    public List<String> getLightIdentifiers() {
    	List<PHLight> allLights = bridge.getResourceCache().getAllLights();
    	List<String> ids = new ArrayList<String>();
        for (final PHLight light : allLights) {
        	ids.add(light.getIdentifier());
        }
        return ids;
    }

    public boolean setBrightness(String id, String value) {
    	int brightness = (value != null) ? Integer.parseInt(value) : 0; 

    	if (brightness < 0 || brightness > 255)
    		return false;

    	List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        for (final PHLight light : allLights) {
        	if (!id.equals(light.getIdentifier()))
        		continue;
        	PHLightState lightState = new PHLightState();
			lightState.setBrightness(brightness);
			bridge.updateLightState(light, lightState);
        }
    	return false;
    }
	
    public boolean setHue(String id, String value) {
    	int hue = (value != null) ? Integer.parseInt(value) : 0; 

    	if (hue < 0 || hue > 65535)
    		return false;

    	List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        for (final PHLight light : allLights) {
        	if (!id.equals(light.getIdentifier()))
        		continue;
        	PHLightState lightState = new PHLightState();
			lightState.setHue(hue);
			bridge.updateLightState(light, lightState);
        }
    	return false;
    }

    public boolean setColor(String id, String R, String G, String B) {
    	int r = (R != null) ? Integer.parseInt(R) : 0; 
    	int g = (G != null) ? Integer.parseInt(G) : 0; 
    	int b = (B != null) ? Integer.parseInt(B) : 0;

    	if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255)
    		return false;
    	
    	/*
    	double r, g, b;
    	r /= 255;
    	g /= 255;
    	b /= 255;
    	*/

    	List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        for (final PHLight light : allLights) {
        	if (!id.equals(light.getIdentifier()))
        		continue;
        	
        	PHLightState lightState = new PHLightState();
        	float[] xy = PHUtilities.calculateXYFromRGB(r, g, b, light.getModelNumber());
            lightState.setX(xy[0]); 
            lightState.setY(xy[1]); 
            bridge.updateLightState(light, lightState);

            return true;
        }
    	return false;
    }

    public String getBrightness(String id) {
    	List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        for (final PHLight light : allLights) {
        	if (!id.equals(light.getIdentifier()))
        		continue;
        
        	PHLightState lightState = light.getLastKnownLightState();;
			return String.valueOf(lightState.getBrightness());
        }
    	return "";
    }
	
    public String getHue(String id) {
    	List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        for (final PHLight light : allLights) {
        	if (!id.equals(light.getIdentifier()))
        		continue;
        
        	PHLightState lightState = light.getLastKnownLightState();;
			return String.valueOf(lightState.getHue());
        }
    	return "";
    }

    public List<String> getColor(String id) {
    	List<String> result = new ArrayList<String>();
    	List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        for (final PHLight light : allLights) {
        	if (!id.equals(light.getIdentifier()))
        		continue;

        	PHLightState lightState = light.getLastKnownLightState();
        	float[] xy = new float[2];
        	xy[0] = lightState.getX();
        	xy[1] = lightState.getY();
        	int rgb = PHUtilities.colorFromXY(xy, light.getModelNumber());
        	int r = (rgb >> 16) & 0xFF;
        	int g = (rgb >> 8) & 0xFF;
        	int b = (rgb >> 0) & 0xFF;
        	result.add(String.valueOf(r));
        	result.add(String.valueOf(g));
        	result.add(String.valueOf(b));
        	return result;
        }

        result.add("");
        result.add("");
        result.add("");
    	return result;
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
    	public void onBridgeConnected(PHBridge br, String arg1) {
    		s_logger.info("[" + BUNDLE_ID + "] bridge connected!");
    		
    		bridge = br;
    		
            phHueSDK.setSelectedBridge(bridge);
            phHueSDK.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);

            s_logger.info("[" + BUNDLE_ID + "] " + br.getResourceCache().getAllLights().size() + " light(s) found");
    	}

    	@Override
    	public void onCacheUpdated(List<Integer> arg0, PHBridge arg1) {
    	}

    	@Override
    	public void onConnectionLost(PHAccessPoint arg0) {
    		s_logger.info("[" + BUNDLE_ID + "] connection to bridge lost... retrying...");
    		bridge = null;
    		searchBridge();
    	}

    	@Override
    	public void onConnectionResumed(PHBridge arg0) {
    	}

    	@Override
    	public void onError(int code, String descr) {
    		s_logger.info("[" + BUNDLE_ID + "] " + code + " " + descr);
    	}

    	@Override
    	public void onParsingErrors(List<PHHueParsingError> arg0) {
    		s_logger.info("[" + BUNDLE_ID + "] parsing error happened!");
    	}
    };
}
