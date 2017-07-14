package org.unikl.adapter.integrator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path( "/hello" )
public class ExampleResource {
	
	private VicinityObject vObject;
	
	public ExampleResource() {
		vObject = new VicinityObject("Thermosate");
	}
	
	@GET
	public String helloWorld() {
		return "Hello World";
	}

	@GET
	@Path("/{param}")
	public String getMsg(@PathParam("param") String msg) {
		String output = "Jersey say : " + msg;
		return output;
	}
}