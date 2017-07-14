package org.unikl.adapter.integrator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class VicinityObject {
	private Pair<String, String> _type; // crap, there are NO typedefs, screw Java
	private Pair<String, String> _oid;
	
	private List<Property> _properties = new ArrayList<Property>();
	private List<Action> _actions = new ArrayList<Action>();

	// TODO: 
	
	// TODO: Constructor for creating
	public VicinityObject(String type) {
		_type = new Pair<String, String>("type", type);
		
		// TODO: get oid from Martin
		_oid = new Pair<String, String>("oid", "abcdefg-0123456789-xyz");
		
		// TODO: read from file???
		_properties.add(arg0);
	}
	
	public JSONObject getJSONOject() {
		
	}
};
