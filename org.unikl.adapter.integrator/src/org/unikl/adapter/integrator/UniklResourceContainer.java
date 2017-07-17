package org.unikl.adapter.integrator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;

@Path( "/" )
public class UniklResourceContainer {
	
	private VicinityObject vObject;
	
	public UniklResourceContainer() {
		vObject = new VicinityObject("Thermostate");
	}
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response helloWorld() {
    	String output = vObject.toString();
    	return Response.status(200).entity(output).build();
	}

	@GET
	@Path("/{oid}/{attr}/{pid}")
	public String getMsg(@PathParam("oid") String oid, @PathParam("attr") String attr, @PathParam("pid") String pid) {
		String output = null;
		if (attr.equals("properties")) {
			output = "1: oid=" + oid + " pid: " + pid;			
		} else if (attr.equals("actions")) {
			output = "2: oid=" + oid + " pid: " + pid;
		}
		return output;
	}
}