package org.unikl.adapter.http;

import java.util.Map;

import javax.servlet.ServletException;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UniklHttpServer {
	private static final String BUNDLE_ID = "[org.unikl.adapter.http] UniklHttpServer ";

	private static final Logger s_logger = LoggerFactory.getLogger(UniklHttpServer.class);
	private static ComponentContext _context;

	private HttpService _httpService;

	public void setHttpService(HttpService httpService) {
		this._httpService = httpService;
	}

	public void unsetHttpService(HttpService httpService) {
		this._httpService = httpService;
	}

	protected void activate(ComponentContext componentContext, Map<String, Object> properties) { 
		UniklHttpServer._context = componentContext; 
		s_logger.info(BUNDLE_ID + "activating..."); 

		if (_httpService == null)
			s_logger.error(BUNDLE_ID + "cannot get HTTPService!"); 

		HttpContext httpCtx = new UniklHttpContext(_httpService.createDefaultHttpContext());
		if (httpCtx == null)
			s_logger.error(BUNDLE_ID + "cannot get HTTPContext!"); 

		try { 
			_httpService.registerResources("/site", "www/index.html", httpCtx);
			_httpService.registerResources("/static", "www/static", httpCtx); 

			_httpService.registerServlet("/api/status", new UniklServlet(), null, httpCtx); 
		} catch (NamespaceException e) {
			s_logger.error("No http", e); 
		} catch (ServletException e) {
			s_logger.error("No servlet", e); 
		}
		s_logger.info(BUNDLE_ID + "activated.");
	}
}
