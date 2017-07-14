package org.unikl.adapter.integrator;

import java.util.ArrayList;
import java.util.List;

public class Property {
	private Pair<String, String> _pid;
	private Pair<String, String> _monitors;
	private Pair<String, String> _writable;
	
	private Data output;

	private List<Link> _pReadLinks = new ArrayList<Link>();
	private List<Link> _pWriteLinks = new ArrayList<Link>();
	
	public Property (/* TODO: Add type here */) {
		// TODO: generate automatically
		_pid = new Pair<String, String>("pid", "temp1"); // TODO
		_monitors = new Pair<String, String>("monitors", "Temperature"); // TODO
		_writable = new Pair<String, String>("writable", "false"); // TODO: should not be string!!! integer?...
		
		_pReadLinks.add(new Link("properties/" + _pid.getParameterValue(), "application/json"));
		_pWriteLinks.add(new Link("properties/" + _pid.getParameterValue(), "application/json"));
	}
}
