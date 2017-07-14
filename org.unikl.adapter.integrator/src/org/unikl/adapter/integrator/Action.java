package org.unikl.adapter.integrator;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Action {
	private Pair<String, String> _aid;
	private Pair<String, String> _affects;

	private Data _input;

	private List<Link> _aReadLinks = new ArrayList<Link>();
	private List<Link> _aWriteLinks = new ArrayList<Link>();

	public Action (/* TODO: Add type here */) {
		// TODO: generate automatically
		_aid = new Pair<String, String>("aid", "switch"); // TODO: hmm
		_affects = new Pair<String, String>("affects", "OnOffStatus"); // TODO
		
		_aReadLinks.add(new Link("actions/" + _aid.getParameterValue(), "application/json"));
		_aWriteLinks.add(new Link("actions/" + _aid.getParameterValue(), "application/json"));
	}

	public JSONObject getAction() {
		JSONObject actions = new JSONObject();
		
		actions.putOpt(_aid.getParameterName(), _aid.getParameterValue());
		actions.putOpt(_affects.getParameterName(), _affects.getParameterValue());
		
		actions.

		return actions;
	}
	
}
