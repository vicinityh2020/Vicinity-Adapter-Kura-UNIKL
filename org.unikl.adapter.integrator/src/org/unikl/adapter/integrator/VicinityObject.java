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

	private static final String TYPE = "type";
	private static final String OID = "oid";
	private static final String PROPERTIES = "properties";
	private static final String ACTIONS = "actions";

	private String _type; // crap, there are NO typedefs, screw Java
	private String _oid; // TODO: fuck hardcode, fuck! but Peter forced me to do this! he is guilty! 

	private List<Property> _properties;
	private List<Action> _actions;

	// TODO: Constructor for creating
	public VicinityObject(String type) {
		_type = new String(type);

		// TODO: get oid from Martin
		_oid = new String("bulb" + cnt++);

		_properties = new ArrayList<Property>();
		_actions = new ArrayList<Action>();

		if (type.equals("Thermostate")) {
			_properties.add(new Property(_oid, "temp1", "Temperature", false, "Celsius", "float")); // TODO: haha....hardcode....
			_actions.add(new Action(_oid, "switch", "OnOffStatus", "Adimentional", "boolean"));
		} else if (type.equals("LightBulb")) {
			_properties.add(new Property(_oid, "brightness", "Brightness", true, "percentage(0-100)", "int")); // TODO: haha....hardcode....
			_properties.add(new Property(_oid, "color", "Color", true, "#rgb", "int")); // TODO: haha....hardcode....
			_properties.add(new Property(_oid, "consumption", "Consumption", false, "watt", "double")); // TODO: haha....hardcode....
		}
	}

	public void setVicinityObjectInstance(VicinityObjectInterface vobjInstance) {
		for (Property prop: _properties) {
			s_logger.info("++++++++++++++ Setting Object instance");
			prop.setVicinityObjectInstance(vobjInstance);
		}
		
		for (Action act: _actions) {
			s_logger.info("++++++++++++++ Setting Object instance");
			act.setVicinityObjectInstance(vobjInstance);
		}
	}

	public String getObjectID() {
		return _oid;
	}

	public List<Property> getProperties() {
		return _properties;
	}

	public List<Action> getActions() {
		return _actions;
	}

	public String getPropertiesStr() {
		int n = _properties.size();

		StringBuffer sb = new StringBuffer();
		sb.append("[");

		for (int i = 0; i < n; i++) {
			sb.append(_properties.get(i).toString());
			if (i != n - 1) // TODO: do something with coma
				sb.append(",");
		}
		sb.append("]");
		return new String(sb.toString());
	}

	public String getActionsStr() {
		int n = _actions.size();

		StringBuffer sb = new StringBuffer();
		sb.append("[");

		for (int i = 0; i < n; i++) {
			sb.append(_actions.get(i).toString());
			if (i != n - 1)
				sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public String toString() {
		return new StringBuffer("{")
				.append("\"").append(TYPE).append("\":\"").append(_type).append("\"")
				.append(",")
				.append("\"").append(OID).append("\":\"").append(_oid).append("\"")
				.append(",")
				.append("\"").append(PROPERTIES).append("\":").append(getPropertiesStr())
				.append(",")
				.append("\"").append(ACTIONS).append("\":").append(getActionsStr())
				.append("}")
				.toString();
	}

	public class Property {
		private static final String PID = "pid";
		private static final String MONITORS = "monitors";
		private static final String WRITABLE = "writable";

		private static final String READ_LINKS = "read_links";
		private static final String WRITE_LINKS = "write_links";
		private static final String OUTPUT = "output";

		private String _oid;
		private String _pid;
		private String _monitors;
		private boolean _writable;

		private Data _output;

		private List<Link> _pReadLinks = new ArrayList<Link>();
		private List<Link> _pWriteLinks = new ArrayList<Link>();

		String formatter = "yyyy-MM-dd'T'HH:mm:ssz";
		// static int objectCount = 0; // TODO: FUCK!!!

		private VicinityObjectInterface _vobjInstance;
		
		public Property(String oid, String pid, String monitors, boolean writable, String units, String datatype) {
			// TODO: generate automatically
			_oid = oid;
			
			_pid = new String(pid);
			_monitors = new String( monitors);
			_writable = writable;

			_output = new Data(units, datatype);

			_pReadLinks.add(new Link("/objects/" + oid + "/properties/" + _pid, "application/json"));
			
			if (writable)
				_pWriteLinks.add(new Link("/objects/" + oid + "/properties/" + _pid, "application/json"));
		}
		
		public void setVicinityObjectInstance(VicinityObjectInterface vobjInstance) {
			this._vobjInstance = vobjInstance;
		}

		// TODO: merge getReadLinks and getWriteLinks
		public String getReadLinks() {
			int n = _pReadLinks.size();

			StringBuffer sb = new StringBuffer();
			sb.append("[");

			for (int i = 0; i < n; i++) {
				sb.append(_pReadLinks.get(i).toString());
				if (i != n - 1)
					sb.append(",");
			}
			sb.append("]");
			return sb.toString();
		}

		public String getWriteLinks() {
			int n = _pWriteLinks.size();

			StringBuffer sb = new StringBuffer();
			sb.append("[");

			for (int i = 0; i < n; i++) {
				sb.append(_pWriteLinks.get(i).toString());
				if (i != n - 1)
					sb.append(",");
			}
			sb.append("]");
			return sb.toString();
		}

		public String getPropertyID() {
			return _pid;
		}

		// TODO: Make more elegant, mother fucker!!!
		public String getPropertyValue(String propertyName) {
			return _vobjInstance.getProperty(_oid, propertyName);
		}

		// TODO: fuck you if it is not writebale!
		public boolean setPropertyValue(String propertyName, String value) {
			return _vobjInstance.setProperty(_oid, propertyName, value);
		}

		public String getPropertyValueStr(String propertyName) {
			return new StringBuffer("{")
					.append("\"value\":\"").append(getPropertyValue(propertyName)).append("\"")
					.append(",")
					.append("\"timestamp\":\"").append(new SimpleDateFormat(formatter).format(new Date())).append("\"")
					.append("}")
					.toString();
		}

		@Override
		public String toString() {
			return new StringBuffer("{")
					.append("\"").append(PID).append("\":\"").append(_pid).append("\"")
					.append(",")
					.append("\"").append(MONITORS).append("\":\"").append(_monitors).append("\"")
					.append(",")
					.append("\"").append(WRITABLE).append("\":\"").append((_writable == true) ? "true" : "false").append("\"")
					.append(",")
					.append("\"").append(OUTPUT).append("\":").append(_output.toString())
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

		private String _oid;

		private String _aid;
		private String _affects;

		private Data _input;

		private List<Link> _aReadLinks = new ArrayList<Link>();
		private List<Link> _aWriteLinks = new ArrayList<Link>();

		private VicinityObjectInterface _vobjInstance;

		public Action(String oid, String aid, String affects, String units, String datatype) {
			// TODO: generate automatically
			_oid = oid;
			
			_aid = new String(aid); // TODO: hmm
			_affects = new String(affects); // TODO

			_input = new Data(units, datatype); // TODO
			_aReadLinks.add(new Link("/objects/" + oid + "/actions/" + _aid, "application/json"));
			_aWriteLinks.add(new Link("/objects/" + oid + "/actions/" + _aid, "application/json"));
		}

		public void setVicinityObjectInstance(VicinityObjectInterface vobjInstance) {
			this._vobjInstance = vobjInstance;
		}

		// hmmm.....
		public boolean setAction(String paramName, String value) {
			return _vobjInstance.setProperty(_oid, paramName, value);
		}

		// TODO: merge getReadLinks and getWriteLinks
		public String getReadLinks() {
			int n = _aReadLinks.size();

			StringBuffer sb = new StringBuffer();
			sb.append("[");

			for (int i = 0; i < n; i++) {
				sb.append(_aReadLinks.get(i).toString());
				if (i != n - 1)
					sb.append(",");
			}
			sb.append("]");
			return sb.toString();
		}

		public String getWriteLinks() {
			int n = _aWriteLinks.size();

			StringBuffer sb = new StringBuffer();
			sb.append("[");

			for (int i = 0; i < n; i++) {
				sb.append(_aWriteLinks.get(i).toString());
				if (i != n - 1)
					sb.append(",");
			}
			sb.append("]");
			return sb.toString();
		}

		public String getActionID() {
			return _aid;
		}

		@Override
		public String toString() {
			return new StringBuffer("{")
					.append("\"").append(AID).append("\":\"").append(_aid).append("\"")
					.append(",")
					.append("\"").append(AFFECTS).append("\":\"").append(_affects).append("\"")
					.append(",")
					.append("\"").append(INPUT).append("\":").append(_input.toString())
					.append(",")
					.append("\"").append(READ_LINKS).append("\":").append(getReadLinks())
					.append(",")
					.append("\"").append(WRITE_LINKS).append("\":").append(getWriteLinks())
					.append("}")
					.toString();
		}
	}

	public class Link {
		// TODO: bad performance
		private static final String HREF = "href";
		private static final String MEDIATYPE = "mediatype";

		private String _href;
		private String _mediatype;

		public Link(String href, String mediaType) {
			// TODO: I do not like this hardcode
			_href = new String(href);
			_mediatype = new String(mediaType);
		}

		@Override
		public String toString() {
			return new StringBuffer("{")
					.append("\"").append(HREF).append("\":\"").append(_href).append("\"")
					.append(",")
					.append("\"").append(MEDIATYPE).append("\":\"").append(_mediatype).append("\"")
					.append("}")
					.toString();
		}
	}

	public class Data {
		// TODO: bad performance
		private static final String UNITS = "units";
		private static final String DATATYPE = "datatype";

		private String _units;
		private String _datatype;

		public Data(String units, String datatype) {
			// TODO: I do not like this hardcode
			_units = new String(units);
			_datatype = new String(datatype);
		}

		@Override
		public String toString() {
			return new StringBuffer("{")
					.append("\"").append(UNITS).append("\":\"").append(_units).append("\"").
					append(",")
					.append("\"").append(DATATYPE).append("\":\"").append(_datatype).append("\"")
					.append("}")
					.toString();
		}
	}
};