package org.unikl.adapter.integrator;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// this is classic singleton
@Path("/")
public class UniklResourceContainer {

	private static List<VicinityObject> objects = null;
	private static UniklResourceContainer instance = null;
	private static final Logger s_logger = LoggerFactory.getLogger(UniklResourceContainer.class);

	protected UniklResourceContainer() {
	}

	public static UniklResourceContainer getInstance() {
		if (instance == null) {
			instance = new UniklResourceContainer();
			objects = new ArrayList<VicinityObject>();
		}
		return instance;
	}

	public VicinityObject getObjectByObjectID(String objectID) {
		for (VicinityObject obj : objects) {
			if (objectID.equals(obj.getObjectID()))
				return obj;
		}

		return null;
	}

	public void addUniklResource(VicinityObject vobj) {
		objects.add(vobj);
	}

	public void removeUniklResource(String objectID) {
		objects.remove(getObjectByObjectID(objectID));
	}
	
	public void cleanUniklResourceContainer() {
		//objects.clear();
	}
	
	private String getAll() {
		int n = objects.size();

		StringBuffer sb = new StringBuffer();
		sb.append("[");

		for (int i = 0; i < n; i++) {
			sb.append(objects.get(i).toString());
			if (i != n - 1) // TODO: do something with coma
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
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{oid}/properties/{pid}")
	public Response getMsg(@PathParam("oid") String oid, @PathParam("pid") String pid) {
		// TODO: man....that sucks so much!!!
		// I think Property and Action should implement the same interface
		for (VicinityObject obj : objects) {
			if (oid.equals(obj.getObjectID())) {
				for (VicinityObject.Property prop : obj.getProperties()) {
					if (pid.equals(prop.getPropertyID())) {
						return Response.status(200).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
								.entity(prop.getPropertyValueStr(pid)).build();
					}
				}
			}
		}
		return Response.status(404).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{oid}/properties/{pid}")
	public Response getMsg(PropPojo input, @PathParam("oid") String oid, @PathParam("pid") String pid) {
		// TODO: man....that sucks so much!!!
		// I think Property and Action should implement the same interface
		
		s_logger.info("----------- POST parameter : " + pid + " value : " + input.value);

		for (VicinityObject obj : objects) {
			if (oid.equals(obj.getObjectID())) {
				for (VicinityObject.Property prop : obj.getProperties()) {
					if (pid.equals(prop.getPropertyID())) {
						if (prop.setPropertyValue(pid, input.value))
							return Response.status(200).entity("{\"status\":\"success\"}").build();
						else
							return Response.status(200).entity("{\"status\":\"failure\"}").build();
					}
				}
			}
		}

		return Response.status(200).entity("{\"status\":\"failure\"}").build();
	}

}