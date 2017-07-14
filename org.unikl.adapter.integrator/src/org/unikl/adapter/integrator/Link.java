package org.unikl.adapter.integrator;

import org.json.JSONObject;

public class Link {
	private Pair<String, String> _href;
	private Pair<String, String> _mediaType;
	
	public Link(String href, String mediaType) {
		// TODO: I do not like this hardcode
		_href = new Pair<String, String>("href", href);
		_mediaType = new Pair<String, String>("mediaType", mediaType);
	}
	
	// TODO: i am not sure if i need getters/setters?
	public Pair<String, String> getHref() {
		return _href;
	}

	public Pair<String, String> getMediaType() {
		return _mediaType;
	}
	
	public JSONObject getJSON() {
		JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt(_href.getParameterName(), _href.getParameterValue());
        jsonObject.putOpt(_mediaType.getParameterName(), _mediaType.getParameterValue());

        return jsonObject;
	}
}
