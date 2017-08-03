package org.unikl.adapter.integrator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unikl.adapter.VicinityObjectInterface.VicinityObjectInterface;

public class VicinityObject {
	private static final Logger s_logger = LoggerFactory.getLogger(UniklResourceContainer.class);
	private static int cnt = 1;

	private String TYPE = "type";
	private String OID = "oid";
	private String PROPERTIES = "properties";
	private String ACTIONS = "actions";

	private String type; // crap, there are NO typedefs, screw Java
	private String oid; // TODO: fuck hardcode, fuck! but Peter forced me to do this! he is guilty! 

	private List<Property> properties;
	private List<Action> actions;

	static int a = 0;

	// TODO: Constructor for creating
	public VicinityObject(String type) {
		this.type = type;

		// TODO: get oid from Martin
		oid = "bulb" + cnt++;

		properties = new ArrayList<Property>();
		actions = new ArrayList<Action>();

		if (type.equals("Thermostate")) {
			properties.add(new Property(oid, "temp1", "Temperature", false, "Celsius", "float")); // TODO: haha....hardcode....
			actions.add(new Action(oid, "switch", "OnOffStatus", "Adimentional", "boolean"));
		} else if (type.equals("LightBulb")) {
			properties.add(new Property(oid, "brightness", "Brightness", true, "percentage(0-100)", "int")); // TODO: haha....hardcode....
			properties.add(new Property(oid, "color", "Color", true, "#rgb", "int")); // TODO: haha....hardcode....
			properties.add(new Property(oid, "consumption", "Consumption", false, "watt", "double")); // TODO: haha....hardcode....
		}
	}

	public void setVicinityObjectInstance(VicinityObjectInterface vobjInstance) {
		for (Property prop: properties) {
			s_logger.info("++++++++++++++ Setting Object instance");
			prop.setVicinityObjectInstance(vobjInstance);
		}
		
		for (Action act: actions) {
			s_logger.info("++++++++++++++ Setting Object instance");
			act.setVicinityObjectInstance(vobjInstance);
		}
	}

	public String getObjectID() {
		return oid;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public List<Action> getActions() {
		return actions;
	}

	public String getPropertiesStr() {
		int n = properties.size();

		StringBuffer sb = new StringBuffer();
		sb.append("[");

		for (int i = 0; i < n; i++) {
			sb.append(properties.get(i).toString());
			if (i != n - 1) // TODO: do something with coma
				sb.append(",");
		}
		sb.append("]");
		return new String(sb.toString());
	}

	public String getActionsStr() {
		int n = actions.size();

		StringBuffer sb = new StringBuffer();
		sb.append("[");

		for (int i = 0; i < n; i++) {
			sb.append(actions.get(i).toString());
			if (i != n - 1)
				sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public String toString() {
		return new StringBuffer("{")
				.append("\"").append(TYPE).append("\":\"").append(type).append("\"")
				.append(",")
				.append("\"").append(OID).append("\":\"").append(oid).append("\"")
				.append(",")
				.append("\"").append(PROPERTIES).append("\":").append(getPropertiesStr())
				.append(",")
				.append("\"").append(ACTIONS).append("\":").append(getActionsStr())
				.append("}")
				.toString();
	}

	public class Property {
		private String PID = "pid";
		private String MONITORS = "monitors";
		private String WRITABLE = "writable";
		private String READ_LINKS = "read_links";
		private String WRITE_LINKS = "write_links";
		private String OUTPUT = "output";

		private String oid;
		private String pid;
		private String monitors;
		private String writable; // TODO: or maybe bool?

		private Data output;

		private List<Link> pReadLinks = new ArrayList<Link>();
		private List<Link> pWriteLinks = new ArrayList<Link>();

		String formatter = "yyyy-MM-dd'T'HH:mm:ssz";

		private VicinityObjectInterface vobjInstance;
		
		public Property(String oid, String pid, String monitors, boolean writable, String units, String datatype) {
			// TODO: generate automatically
			this.oid = oid;
			
			this.pid = pid;
			this.monitors = monitors;
			this.writable = (writable == true) ? "true" : "false";

			this.output = new Data(units, datatype);

			this.pReadLinks.add(new Link("/objects/" + oid + "/properties/" + pid, "application/json"));
			this.pWriteLinks.add(new Link("/objects/" + oid + "/properties/" + pid, "application/json"));
		}
		
		public void setVicinityObjectInstance(VicinityObjectInterface vobjInstance) {
			this.vobjInstance = vobjInstance;
		}

		// TODO: merge getReadLinks and getWriteLinks
		public String getReadLinks() {
			int n = pReadLinks.size();

			StringBuffer sb = new StringBuffer();
			sb.append("[");

			for (int i = 0; i < n; i++) {
				sb.append(pReadLinks.get(i).toString());
				if (i != n - 1)
					sb.append(",");
			}
			sb.append("]");
			return sb.toString();
		}

		public String getWriteLinks() {
			int n = pWriteLinks.size();

			StringBuffer sb = new StringBuffer();
			sb.append("[");

			for (int i = 0; i < n; i++) {
				sb.append(pWriteLinks.get(i).toString());
				if (i != n - 1)
					sb.append(",");
			}
			sb.append("]");
			return sb.toString();
		}

		public String getPropertyID() {
			return pid;
		}

		// TODO: Make more elegant, mother fucker!!!
		public String getPropertyValue(String propertyName) {
			return vobjInstance.getProperty(oid, propertyName);
		}

		// TODO: fuck you if it is not writebale!
		public boolean setPropertyValue(String propertyName, String value) {
			return vobjInstance.setProperty(oid, propertyName, value);
		}

		public String getPropertyValueStr(String propertyName) {
			return new StringBuffer("{")
					.append("\"value\":\"").append(getPropertyValue(propertyName))
					.append("\",")
					.append("\"timestamp\":\"").append(new SimpleDateFormat(formatter).format(new Date())).append("\"")
					.append("}")
					.toString();
		}

		@Override
		public String toString() {
			return new StringBuffer("{")
					.append("\"").append(PID).append("\":\"").append(pid).append("\"")
					.append(",")
					.append("\"").append(MONITORS).append("\":\"").append(monitors).append("\"")
					.append(",")
					.append("\"").append(WRITABLE).append("\":\"").append(writable).append("\"")
					.append(",")
					.append("\"").append(OUTPUT).append("\":").append(output.toString())
					.append(",")
					.append("\"").append(READ_LINKS).append("\":").append(getReadLinks())
					.append(",")
					.append("\"").append(WRITE_LINKS).append("\":").append(getWriteLinks())
					.append("}")
					.toString();
		}
	}

	public class Action {
		// TODO: bad performance
		private String AID = "aid";
		private String AFFECTS = "affects";

		private String READ_LINKS = "read_links";
		private String WRITE_LINKS = "write_links";
		private String INPUT = "input";

		private String oid;

		private String aid;
		private String affects;

		private Data input;

		private List<Link> aReadLinks = new ArrayList<Link>();
		private List<Link> aWriteLinks = new ArrayList<Link>();

		private VicinityObjectInterface vobjInstance;

		public Action(String oid, String aid, String affects, String units, String datatype) {
			// TODO: hmmmmmm
			this.oid = oid;
			
			this.aid = aid;
			this.affects = affects;

			this.input = new Data(units, datatype); // TODO
			this.aReadLinks.add(new Link("/objects/" + oid + "/actions/" + aid, "application/json"));
			this.aWriteLinks.add(new Link("/objects/" + oid + "/actions/" + aid, "application/json"));
		}

		public void setVicinityObjectInstance(VicinityObjectInterface vobjInstance) {
			this.vobjInstance = vobjInstance;
		}

		/* TODO: obviously this is not usable
		public boolean setAction(String paramName, String value) {
			return vobjInstance.setProperty(_oid, paramName, value);
		}
		*/

		// TODO: merge getReadLinks and getWriteLinks
		public String getReadLinks() {
			int n = aReadLinks.size();

			StringBuffer sb = new StringBuffer();
			sb.append("[");

			for (int i = 0; i < n; i++) {
				sb.append(aReadLinks.get(i).toString());
				if (i != n - 1)
					sb.append(",");
			}
			sb.append("]");
			return sb.toString();
		}

		public String getWriteLinks() {
			int n = aWriteLinks.size();

			StringBuffer sb = new StringBuffer();
			sb.append("[");

			for (int i = 0; i < n; i++) {
				sb.append(aWriteLinks.get(i).toString());
				if (i != n - 1)
					sb.append(",");
			}
			sb.append("]");
			return sb.toString();
		}

		public String getActionID() {
			return aid;
		}

		@Override
		public String toString() {
			return new StringBuffer("{")
					.append("\"").append(AID).append("\":\"").append(aid).append("\"")
					.append(",")
					.append("\"").append(AFFECTS).append("\":\"").append(affects).append("\"")
					.append(",")
					.append("\"").append(INPUT).append("\":").append(input.toString())
					.append(",")
					.append("\"").append(READ_LINKS).append("\":").append(getReadLinks())
					.append(",")
					.append("\"").append(WRITE_LINKS).append("\":").append(getWriteLinks())
					.append("}")
					.toString();
		}
	}

	public class Link {
		private String HREF = "href";
		private String MEDIATYPE = "mediatype";

		private String href;
		private String mediatype;

		public Link(String href, String mediaType) {
			this.href = href;
			this.mediatype = mediaType;
		}

		@Override
		public String toString() {
			return new StringBuffer("{")
					.append("\"").append(HREF).append("\":\"").append(href).append("\"")
					.append(",")
					.append("\"").append(MEDIATYPE).append("\":\"").append(mediatype).append("\"")
					.append("}")
					.toString();
		}
	}

	public class Data {
		public final String UNITS = "units";
		public final String DATATYPE = "datatype";

		private String units;
		private String datatype;

		public Data(String units, String datatype) {
			this.units = units;
			this.datatype = datatype;
		}

		@Override
		public String toString() {
			return new StringBuffer("{")
					.append("\"").append(UNITS).append("\":\"").append(units).append("\"")
					.append(",")
					.append("\"").append(DATATYPE).append("\":\"").append(datatype).append("\"")
					.append("}")
					.toString();
		}
	}
};