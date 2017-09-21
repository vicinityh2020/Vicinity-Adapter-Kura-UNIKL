package org.unikl.adapter.integrator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unikl.adapter.VicinityObjectInterface.VicinityObjectInterface;

public class VicinityObject {
	private static final String TYPE = "type";
	private static final String OID = "oid";
	private static final String PROPERTIES = "properties";
	private static final String ACTIONS = "actions";

	private String type; // crap, there are NO typedefs, screw Java
	private String oid; // TODO: fuck hardcode, fuck! but Peter forced me to do this! he is guilty! 

	private List<Property> properties;
	private List<Action> actions;

	// TODO: Constructor for creating
	public VicinityObject(String type, String oid) {
		this.type = type;

		// TODO: get oid from Martin
		this.oid = oid;

		properties = new ArrayList<Property>();
		actions = new ArrayList<Action>();
	}

	public String getObjectID() {
		return oid;
	}

	public void addProperty(VicinityObjectInterface vobj, String pid, String monitors, boolean writable, String units, String datatype) {
		properties.add(new Property(vobj, pid, monitors, writable, units, datatype));
	}

	public void addAction(VicinityObjectInterface vobj, String aid, String affects, String units, String datatype) {
		actions.add(new Action(vobj, aid, affects, units, datatype));
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

		//private String oid;
		private String pid;
		private String monitors;
		private String writable; // TODO: or maybe bool?

		private Data output;

		private List<Link> pReadLinks = new ArrayList<Link>();
		private List<Link> pWriteLinks = new ArrayList<Link>();

		String formatter = "yyyy-MM-dd'T'HH:mm:ssz";

		private VicinityObjectInterface vobjInstance;
		
		public Property(VicinityObjectInterface vobj, String pid, String monitors, boolean writable, String units, String datatype) {
			// TODO: generate automatically
			//this.oid = oid;
			this.vobjInstance = vobj;
			
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
		private String getPropertyValue(String propertyName) {
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
		private static final String AID = "aid";
		private static final String AFFECTS = "affects";

		private static final String READ_LINKS = "read_links";
		private static final String WRITE_LINKS = "write_links";
		private static final String INPUT = "input";

		private String aid;
		private String affects;

		private Data input;

		private List<Link> aReadLinks = new ArrayList<Link>();
		private List<Link> aWriteLinks = new ArrayList<Link>();

		private VicinityObjectInterface vobjInstance;

		String formatter = "yyyy-MM-dd'T'HH:mm:ssz";

		public Action(VicinityObjectInterface vobj, String aid, String affects, String units, String datatype) {
			// TODO: hmmmmmm
			this.vobjInstance = vobj;
			this.aid = aid;
			this.affects = affects;

			this.input = new Data(units, datatype); // TODO
			this.aReadLinks.add(new Link("/objects/" + oid + "/actions/" + aid, "application/json"));
			this.aWriteLinks.add(new Link("/objects/" + oid + "/actions/" + aid, "application/json"));
		}

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

		// TODO: Make more elegant, mother fucker!!!
		private String getActionValue(String actionName) {
			return vobjInstance.getAction(oid, actionName);
		}

		// TODO: fuck you if it is not writebale!
		public boolean setActionValue(String actionName, String value) {
			return vobjInstance.setAction(oid, actionName, value);
		}
	
		public String getActionValueStr(String actionName) {
			return new StringBuffer("{")
					.append("\"value\":\"").append(getActionValue(actionName))
					.append("\",")
					.append("\"timestamp\":\"").append(new SimpleDateFormat(formatter).format(new Date())).append("\"")
					.append("}")
					.toString();
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
