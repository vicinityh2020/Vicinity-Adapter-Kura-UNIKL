package org.unikl.adapter.philipshue;

import java.util.ArrayList;
import java.util.List;
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
	List<PHLight> allLights = new ArrayList<PHLight>();
	
    private PHHueSDK phHueSDK;
    PHBridge bridge; // TODO: support more than one bridge later
    
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
	
	// TODO: actually noone needs this crap, but I would like to
	// make an easter egg out of this later 
    public void randomizeLights(final PHBridge bridge) {
        for (final PHLight light : allLights) {
        	Timer timer = new Timer();
        	timer.scheduleAtFixedRate(new TimerTask() {
  			@Override
  			public void run() {
  				Random rand = new Random();
  				final PHLightState lightState = new PHLightState();
  					lightState.setBrightness(rand.nextInt(255));
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

    public void setInstance (PHLight phlight) {
    	UniklResourceContainer.getInstance().getObjectByObjectID(phlight.getName()).setVicinityObjectInstance(this);
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

        	allLights = bridge.getResourceCache().getAllLights();
            s_logger.info("[" + BUNDLE_ID + "] " + allLights.size() + " light(s) found");
            
            for (PHLight light: allLights) {
            	light.setName(UniklResourceContainer.getInstance().addUniklResource("LightBulb"));
                s_logger.info("[" + BUNDLE_ID + "] === OOO  " + light.getName());
                setInstance(light);
            }
    	}

    	@Override
    	public void onCacheUpdated(List<Integer> arg0, PHBridge arg1) {
    		// TODO: Yeah, I am a lazy bitch. I will implement this, but when I have time, I swear!
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
    
	@Override
	public VicinityObjectInterface getInstance() {
		return this;
	}

	@Override
	public String getProperty(String oid, String propertyName) {
		s_logger.info("[" + BUNDLE_ID + "] getting property " + propertyName + " for oid = " + oid);

        for (PHLight light : allLights) {
        	if (!oid.equals(light.getName()))
        		continue;
        	
        	PHLightState lightState = null;

        	do {
        		lightState = light.getLastKnownLightState();
        		//s_logger.info("[" + BUNDLE_ID + "] ?????? " + lightState == null ? "null" : "got light state");
        	} while (lightState == null);
        	
        	if (propertyName.equals("brightness")) {
        		
        		Integer fuck = null;
        		
            	do {
            		fuck = lightState.getBrightness();
            		s_logger.info("[" + BUNDLE_ID + "] attemting get brightness " + fuck == null ? "null" : "got brightness");
            	} while (fuck == null);
    			
        		int value = (int) (fuck.intValue() / BRIGHTNESS_CONVERT_COEFFICIENT);
    			return String.valueOf(value);
    			
    		}
        	/* This could be nice functionality, but..."what hue exactly means?! we do not need this crap!! so fuck you!!!"

       	 	else if (propertyName.equals("hue")) {
       			s_logger.info("[" + BUNDLE_ID + "] hue " + lightState.getHue());
       			return String.valueOf(lightState.getHue());
       		}
    		*/
        	else if (propertyName.equals("color")) {
    			
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

        	} else if (propertyName.equals("consumption")) { // TODO: generate appropriate output for readonly properties?
        		
        		Integer fuck = null;
        		
            	do {
            		fuck = lightState.getBrightness();
            		s_logger.info("[" + BUNDLE_ID + "] attemting get brightness " + fuck == null ? "null" : "got brightness");
            	} while (fuck == null);
    			
        		double value = fuck.intValue() / BRIGHTNESS_CONVERT_COEFFICIENT;
    			return String.valueOf(CONSUMPTION_COEFFICIENT * (value / 100));
    		}
        }
        return "";
	}

	@Override
	public boolean setProperty(String oid, String propertyName, String value) {
		s_logger.info("[" + BUNDLE_ID + "] setting property " + propertyName + " to value "+ value + " for oid = " + oid);

        for (PHLight light : allLights) {
        	if (!oid.equals(light.getName()))
        		continue;
        
    		PHLightState lightState = new PHLightState();
		
    		if (propertyName.equals("brightness")) {
    			
    			// no idea how precise this shit is...
        		int digValue = (int) (Integer.parseInt(value) * BRIGHTNESS_CONVERT_COEFFICIENT);
    			lightState.setBrightness(digValue);
    			light.setLastKnownLightState(lightState);
    			bridge.updateLightState(light, lightState);
    			return true;
	
    		} 
    		/* look my comment on this in comment for getProperty() method
    		else if (propertyName.equals("hue")) {
        		
        		int digValue = Integer.parseInt(value);
    			lightState.setHue(digValue);
    			bridge.updateLightState(light, lightState);
    			return true;
      		
    		} 
    		*/
    		else if (propertyName.equals("color")) {
    			
    			if (value.length() != 6)
    				return false;
            	float[] xy = PHUtilities.calculateXYFromRGB(Integer.parseInt(value.substring(0, 2), 16), Integer.parseInt(value.substring(2, 4), 16), Integer.parseInt(value.substring(4, 6), 16), light.getModelNumber());
        		s_logger.info("[" + BUNDLE_ID + "] setProperty color " + Integer.parseInt(value.substring(0, 2), 16) + " " + Integer.parseInt(value.substring(2, 4), 16) + " " + Integer.parseInt(value.substring(4, 6), 16));
                lightState.setX(xy[0]); 
                lightState.setY(xy[1]); 
                bridge.updateLightState(light, lightState);
    			light.setLastKnownLightState(lightState); // dirty hack, write more later

    			return true;
    		}
        }
        return false;
    }
}
