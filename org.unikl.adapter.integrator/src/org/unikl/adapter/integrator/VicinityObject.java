package org.unikl.adapter.integrator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class VicinityObject {
	private String TYPE = "type";
	private String OID = "oid";
	private String PROPERTIES = "properties";
	private String ACTIONS = "actions";

	private Pair<String, String> _type; // crap, there are NO typedefs, screw Java
	private Pair<String, String> _oid;
	
	private List<Property> _properties;
	private List<Action> _actions;

	// TODO: Constructor for creating
	public VicinityObject(String type) {
		_type = new Pair<String, String>("type", type);
		
		// TODO: get oid from Martin
		_oid = new Pair<String, String>("oid", "abcdefg-0123456789-xyz");
		
		if (type == "Thermometer") {
			// TODO: read from file???
			_properties = new ArrayList<Property>();
			_actions = new ArrayList<Action>();
			
			_properties.add(new Property());
			_actions.add(new Action());			
		}
	}
	
	private String getProperties() {
		int n = _properties.size();
		
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		
		for (int i = 0; i < n; i++) {
			sb.append(_properties.get(i).toString());
			if (i != n-1) // TODO: do something with coma
				sb.append(",");
		}
		sb.append("]");
		return new String(sb.toString());			
	}
	
	private String getActions() {
		int n = _actions.size();
		
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		
		for (int i = 0; i < n; i++) {
			sb.append(_actions.get(i).toString());
			if (i != n-1)
				sb.append(",");
		}
		sb.append("]");
		return sb.toString();			
	}

	@Override
	public String toString() {
		return new StringBuffer("{")
				.append("\"").append(_type.getParameterName()).append("\":\"").append(_type.getParameterValue()).append("\"").append(",")
				.append("\"").append(_oid.getParameterName()).append("\":\"").append(_oid.getParameterValue()).append("\"").append(",")
					.append("\"").append(PROPERTIES).append("\":").append(getProperties()).append(",")
					.append("\"").append(ACTIONS).append("\":").append(getActions())
				.append("}").toString();
	}
	
	public class Property {
		private String READ_LINKS = "read_links";
		private String WRITE_LINKS = "write_links";
		private String OUTPUT = "output";

		private Pair<String, String> _pid;
		private Pair<String, String> _monitors;
		private Pair<String, String> _writable;
		
		private Data _output;

		private List<Link> _pReadLinks = new ArrayList<Link>();
		private List<Link> _pWriteLinks = new ArrayList<Link>();
		
		public Property (/* TODO: Add type here */) {
			// TODO: generate automatically
			_pid = new Pair<String, String>("pid", "temp1"); // TODO
			_monitors = new Pair<String, String>("monitors", "Temperature"); // TODO
			_writable = new Pair<String, String>("writable", "false"); // TODO: should not be string!!! integer?...
			
			_output = new Data("Celsius", "float"); // TODO			
			
			_pReadLinks.add(new Link("properties/" + _pid.getParameterValue(), "application/json"));
			_pWriteLinks.add(new Link("properties/" + _pid.getParameterValue(), "application/json"));
		}
		
		// TODO: merge getReadLinks and getWriteLinks
		private String getReadLinks() {
			int n = _pReadLinks.size();
			
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			
			for (int i = 0; i < n; i++) {
				sb.append(_pReadLinks.get(i).toString());
				if (i != n-1)
					sb.append(",");
			}
			sb.append("]");
			return sb.toString();			
		}

		private String getWriteLinks() {
			int n = _pWriteLinks.size();
			
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			
			for (int i = 0; i < n; i++) {
				sb.append(_pWriteLinks.get(i).toString());
				if (i != n-1)
					sb.append(",");
			}
			sb.append("]");
			return sb.toString();						
		}

		@Override
		public String toString() {
			return new StringBuffer("{")
					.append("\"").append(_pid.getParameterName()).append("\":\"").append(_pid.getParameterValue()).append("\"").append(",")
					.append("\"").append(_monitors.getParameterName()).append("\":\"").append(_monitors.getParameterValue()).append("\"").append(",")
					.append("\"").append(_writable.getParameterName()).append("\":\"").append(_writable.getParameterValue()).append("\"").append(",")
						.append("\"").append(OUTPUT).append("\":").append(_output.toString()).append(",")
						.append("\"").append(READ_LINKS).append("\":").append(getReadLinks()).append(",")
						.append("\"").append(WRITE_LINKS).append("\":").append(getWriteLinks())
					.append("}").toString();
		}
	}
	
	public class Action {
		// TODO: bad performance
		private String AID = "aid";
		private String AFFECTS = "affects";
		
		private String READ_LINKS = "read_links";
		private String WRITE_LINKS = "write_links";
		private String INPUT = "input";

		private Pair<String, String> _aid;
		private Pair<String, String> _affects;

		private Data _input;

		private List<Link> _aReadLinks = new ArrayList<Link>();
		private List<Link> _aWriteLinks = new ArrayList<Link>();

		public Action (/* TODO: Add type here */) {
			// TODO: generate automatically
			_aid = new Pair<String, String>(AID, "switch"); // TODO: hmm
			_affects = new Pair<String, String>(AFFECTS, "OnOffStatus"); // TODO

			_input = new Data("Adimentional", "boolean"); // TODO
			_aReadLinks.add(new Link("actions/" + _aid.getParameterValue(), "application/json"));
			_aWriteLinks.add(new Link("actions/" + _aid.getParameterValue(), "application/json"));
		}
		
		// TODO: merge getReadLinks and getWriteLinks
		private String getReadLinks() {
			int n = _aReadLinks.size();
			
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			
			for (int i = 0; i < n; i++) {
				sb.append(_aReadLinks.get(i).toString());
				if (i != n-1)
					sb.append(",");
			}
			sb.append("]");
			return sb.toString();			
		}

		private String getWriteLinks() {
			int n = _aWriteLinks.size();
			
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			
			for (int i = 0; i < n; i++) {
				sb.append(_aWriteLinks.get(i).toString());
				if (i != n-1)
					sb.append(",");
			}
			sb.append("]");
			return sb.toString();						
		}

		@Override
		public String toString() {
			return new StringBuffer("{")
					.append("\"").append(_aid.getParameterName()).append("\":\"").append(_aid.getParameterValue()).append("\"").append(",")
					.append("\"").append(_affects.getParameterName()).append("\":\"").append(_affects.getParameterValue()).append("\"").append(",")
						.append("\"").append(INPUT).append("\":").append(_input.toString()).append(",")
						.append("\"").append(READ_LINKS).append("\":").append(getReadLinks()).append(",")
						.append("\"").append(WRITE_LINKS).append("\":").append(getWriteLinks())
					.append("}").toString();
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
			return new StringBuffer("{")
					.append("\"").append(_href.getParameterName()).append("\":\"")
					.append(_href.getParameterValue()).append("\"").append(",")
					.append("\"").append(_mediatype.getParameterName()).append("\":\"")
					.append(_mediatype.getParameterValue()).append("\"")
					.append("}").toString();
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
			return new StringBuffer("{")
					.append("\"").append(_units.getParameterName()).append("\":\"")
					.append(_units.getParameterValue()).append("\"").append(",")
					.append("\"").append(_datatype.getParameterName()).append("\":\"")
					.append(_datatype.getParameterValue()).append("\"")
					.append("}").toString();
		}
	}
};