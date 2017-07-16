package org.unikl.adapter.integrator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;

@Path( "/" )
public class ExampleResource {
	
	private VicinityObject vObject;
	
	public ExampleResource() {
		vObject = new VicinityObject("Thermosate");
	}
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response helloWorld() {
		vObject = new VicinityObject("Thermosate");
    	String output = vObject.toString();
    	return Response.status(200).entity(output).build();
	}

	@GET
	@Path("/{param}")
	public String getMsg(@PathParam("param") String msg) {
		String output = "Jersey say : " + msg;
		return output;
	}
}