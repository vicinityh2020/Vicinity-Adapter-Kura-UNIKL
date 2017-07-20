package org.unikl.adapter.integrator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;
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

	private Pair<String, String> _type; // crap, there are NO typedefs, screw Java
	private Pair<String, String> _oid;

	private List<Property> _properties;
	private List<Action> _actions;

	static int a = 0;

	// TODO: Constructor for creating
	public VicinityObject(String type) {
		_type = new Pair<String, String>("type", type);

		// TODO: get oid from Martin
		Random rand = new Random();
		_oid = new Pair<String, String>("oid", "bulb" + cnt++);

		_properties = new ArrayList<Property>();
		_actions = new ArrayList<Action>();

		if (type == "Thermostate") {
			_properties.add(new Property(_oid.getParameterValue(), "temp1", "Temperature", false, "Celsius", "float")); // TODO: haha....hardcode....
			_actions.add(new Action(_oid.getParameterValue(), "switch", "OnOffStatus", "Adimentional", "boolean"));
		} else if (type == "PHLightBulb") {
			_properties.add(new Property(_oid.getParameterValue(), "hue", "Hue", true, "Adimentional(0-65535)", "int")); // TODO: haha....hardcode....
			_properties.add(new Property(_oid.getParameterValue(), "brightness", "Brightness", true, "Adimentional(0-255)", "int")); // TODO: haha....hardcode....
			_properties.add(new Property(_oid.getParameterValue(), "color", "Color", true, "Adimentional(#rgb)", "int")); // TODO: haha....hardcode....
			_actions.add(new Action(_oid.getParameterValue(), "hue", "Hue", "Adimentional(0-65535)", "int"));
			_actions.add(new Action(_oid.getParameterValue(), "brightness", "Brightness", "Adimentional(0-255)", "int"));
			_actions.add(new Action(_oid.getParameterValue(), "color", "Color", "Adimentional(#rgb)", "int"));
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
		return _oid.getParameterValue();
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
		return new StringBuffer("{").append("\"").append(_type.getParameterName()).append("\":\"")
				.append(_type.getParameterValue()).append("\"").append(",").append("\"").append(_oid.getParameterName())
				.append("\":\"").append(_oid.getParameterValue()).append("\"").append(",").append("\"")
				.append(PROPERTIES).append("\":").append(getPropertiesStr()).append(",").append("\"").append(ACTIONS)
				.append("\":").append(getActionsStr()).append("}").toString();
	}

	public class Property {
		private String READ_LINKS = "read_links";
		private String WRITE_LINKS = "write_links";
		private String OUTPUT = "output";

		private String _oid;
		private Pair<String, String> _pid;
		private Pair<String, String> _monitors;
		private Pair<String, String> _writable;

		private Data _output;

		private List<Link> _pReadLinks = new ArrayList<Link>();
		private List<Link> _pWriteLinks = new ArrayList<Link>();

		String formatter = "yyyy-MM-dd'T'HH:mm:ssz";
		// static int objectCount = 0; // TODO: FUCK!!!

		private VicinityObjectInterface _vobjInstance;
		
		public Property(String oid, String pid, String monitors, boolean writable, String units, String datatype) {
			// TODO: generate automatically
			_oid = oid;
			
			_pid = new Pair<String, String>("pid", pid);
			_monitors = new Pair<String, String>("monitors", monitors);
			_writable = new Pair<String, String>("writable", (writable == true) ? "true" : "false");

			_output = new Data(units, datatype);

			_pReadLinks.add(new Link("/objects/" + oid + "/properties/" + _pid.getParameterValue(), "application/json"));
			_pWriteLinks.add(new Link("/objects/" + oid + "/properties/" + _pid.getParameterValue(), "application/json"));
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
			return _pid.getParameterValue();
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
			return new StringBuffer("{").append("\"value\":").append(getPropertyValue(propertyName)).append(",")
					.append("\"timestamp\":\"").append(new SimpleDateFormat(formatter).format(new Date())).append("\"")
					.append("}").toString();
		}

		@Override
		public String toString() {
			return new StringBuffer("{").append("\"").append(_pid.getParameterName()).append("\":\"")
					.append(_pid.getParameterValue()).append("\"").append(",").append("\"")
					.append(_monitors.getParameterName()).append("\":\"").append(_monitors.getParameterValue())
					.append("\"").append(",").append("\"").append(_writable.getParameterName()).append("\":\"")
					.append(_writable.getParameterValue()).append("\"").append(",").append("\"").append(OUTPUT)
					.append("\":").append(_output.toString()).append(",").append("\"").append(READ_LINKS).append("\":")
					.append(getReadLinks()).append(",").append("\"").append(WRITE_LINKS).append("\":")
					.append(getWriteLinks()).append("}").toString();
		}
	}

	public class Action {
		// TODO: bad performance
		private String AID = "aid";
		private String AFFECTS = "affects";

		private String READ_LINKS = "read_links";
		private String WRITE_LINKS = "write_links";
		private String INPUT = "input";

		private String _oid;

		private Pair<String, String> _aid;
		private Pair<String, String> _affects;

		private Data _input;

		private List<Link> _aReadLinks = new ArrayList<Link>();
		private List<Link> _aWriteLinks = new ArrayList<Link>();

		private VicinityObjectInterface _vobjInstance;

		public Action(String oid, String aid, String affects, String units, String datatype) {
			// TODO: generate automatically
			_oid = oid;
			
			_aid = new Pair<String, String>(AID, aid); // TODO: hmm
			_affects = new Pair<String, String>(AFFECTS, affects); // TODO

			_input = new Data(units, datatype); // TODO
			_aReadLinks.add(new Link("/objects/" + oid + "/actions/" + _aid.getParameterValue(), "application/json"));
			_aWriteLinks.add(new Link("/objects/" + oid + "/actions/" + _aid.getParameterValue(), "application/json"));
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
			return _aid.getParameterValue();
		}

		@Override
		public String toString() {
			return new StringBuffer("{").append("\"").append(_aid.getParameterName()).append("\":\"")
					.append(_aid.getParameterValue()).append("\"").append(",").append("\"")
					.append(_affects.getParameterName()).append("\":\"").append(_affects.getParameterValue())
					.append("\"").append(",").append("\"").append(INPUT).append("\":").append(_input.toString())
					.append(",").append("\"").append(READ_LINKS).append("\":").append(getReadLinks()).append(",")
					.append("\"").append(WRITE_LINKS).append("\":").append(getWriteLinks()).append("}").toString();
		}
	}

	public class Link {
		// TODO: bad performance
		private String HREF = "href";
		private String MEDIATYPE = "mediatype";

		private Pair<String, String> _href;
		private Pair<String, String> _mediatype;

		public Link(String href, String mediaType) {
			// TODO: I do not like this hardcode
			_href = new Pair<String, String>(HREF, href);
			_mediatype = new Pair<String, String>(MEDIATYPE, mediaType);
		}

		@Override
		public String toString() {
			return new StringBuffer("{").append("\"").append(_href.getParameterName()).append("\":\"")
					.append(_href.getParameterValue()).append("\"").append(",").append("\"")
					.append(_mediatype.getParameterName()).append("\":\"").append(_mediatype.getParameterValue())
					.append("\"").append("}").toString();
		}
	}

	public class Data {
		// TODO: bad performance
		public final String UNITS = "units";
		public final String DATATYPE = "datatype";

		private Pair<String, String> _units;
		private Pair<String, String> _datatype;

		public Data(String units, String datatype) {
			// TODO: I do not like this hardcode
			_units = new Pair<String, String>(UNITS, units);
			_datatype = new Pair<String, String>(DATATYPE, datatype);
		}

		@Override
		public String toString() {
			return new StringBuffer("{").append("\"").append(_units.getParameterName()).append("\":\"")
					.append(_units.getParameterValue()).append("\"").append(",").append("\"")
					.append(_datatype.getParameterName()).append("\":\"").append(_datatype.getParameterValue())
					.append("\"").append("}").toString();
		}
	}
};