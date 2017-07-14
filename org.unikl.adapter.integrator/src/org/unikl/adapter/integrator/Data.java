package org.unikl.adapter.integrator;

import org.json.JSONObject;

public class Data {
	private Pair<String, String> _units;
	private Pair<String, String> _datatype;
	
	public Data(String units, String datatype) {
		// TODO: I do not like this hardcode
		_units = new Pair<String, String>("units", units);
		_datatype = new Pair<String, String>("datatype", datatype);
	}

	// TODO: i am not sure if i need getters/setters?
	public Pair<String, String> getUnits() {
		return _units;
	}

	public Pair<String, String> getDatatype() {
		return _datatype;
	}

	public JSONObject getJSON() {
		JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt(_units.getParameterName(), _units.getParameterValue());
        jsonObject.putOpt(_datatype.getParameterName(), _datatype.getParameterValue());

        return jsonObject;
	}
}
