package org.unikl.adapter.integrator;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;

@Path( "/" )
public class UniklResourceContainer {

	private List<VicinityObject> objects;
	
	public UniklResourceContainer() {
		objects = new ArrayList<VicinityObject>();
		objects.add(new VicinityObject("Thermostate"));
	}
	
	private String getAll() {
		int n = objects.size();
		
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		
		for (int i = 0; i < n; i++) {
			sb.append(objects.get(i).toString());
			if (i != n-1) // TODO: do something with coma
				sb.append(",");
		}
		sb.append("]");
    	return sb.toString();	
	}
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response all() {
    	return Response.status(200).entity(getAll()).build();
	}

	@GET
	@Path("/{oid}/{attr}/{paid}") // haha, my paid slut request
	public Response getMsg(@PathParam("oid") String oid, @PathParam("attr") String attr, @PathParam("paid") String paid) {
		if (!(attr.equals("properties") || attr.equals("actions"))) {
	    	return Response.status(404).build();
		}
		
		// TODO: man....that sucks so much!!!
		// I think Property and Action should implement the same interface
		for (VicinityObject obj : objects) {
			if (oid.equals(obj.getObjectID())) {
				if (attr.equals("properties")) {
					for (VicinityObject.Property prop : obj.getProperties()) {
						if (paid.equals(prop.getPropertyID())) {
							return Response.status(200).entity(prop.getPropertyValueStr()).build();
						}
					}
				} else if (attr.equals("actions")) {
					for (VicinityObject.Action act : obj.getActions()) {
						if (paid.equals(act.getActionID())) {
					    	return Response.status(404).build();
//							return Response.status(200).entity(act.getPropertyValueStr()).build();							
						}						
					}
				}
			}
		}
    	return Response.status(404).build();
	}
}