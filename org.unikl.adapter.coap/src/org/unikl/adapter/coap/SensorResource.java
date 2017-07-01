package org.unikl.adapter.coap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class SensorResource extends CoapResource {

	private String sensorValue;
	private String type;
	private String name;

	public SensorResource(String name, String type) {
		super(name);
		
		this.name = name;
		this.type = type;
		
		setObservable(true);
	}

	public String getSensorValue() {
		return sensorValue;
	}

	public void setSensorValue(String sensorValue) {
		this.sensorValue = sensorValue;
		this.changed();
	}

	@Override
	public void handleGET(CoapExchange exchange) {
        final int port = exchange.advanced().getEndpoint().getAddress().getPort();
        exchange.respond(CoAP.ResponseCode.CONTENT,
                "{ \"value\": " + sensorValue 
                + ", \"timestamp\": " + "\"" + System.currentTimeMillis() + "\"" + " }");
	}
}