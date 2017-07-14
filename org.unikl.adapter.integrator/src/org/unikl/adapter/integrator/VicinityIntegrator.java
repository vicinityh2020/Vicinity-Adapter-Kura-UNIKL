package org.unikl.adapter.integrator;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VicinityIntegrator {
	private static final String BUNDLE_ID = "org.unikl.adapter.integrator";
	private static final Logger s_logger = LoggerFactory.getLogger(VicinityIntegrator.class);
	private ServiceRegistration<ExampleResource> registration;
    ConfigurationAdmin configurationAdmin;
	
	protected void activate(ComponentContext componentContext) {
		s_logger.debug("[" + BUNDLE_ID + "]" + " activating...");

        Configuration configuration = null;
		try {
			configuration = configurationAdmin.getConfiguration("com.eclipsesource.jaxrs.connector", null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Dictionary props = configuration.getProperties();
        if (props == null) {
            props = new Hashtable();
        }
        props.put("root", "/objects");
        try {
			configuration.update(props);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		registration = componentContext.getBundleContext().registerService(ExampleResource.class, new ExampleResource(), null);
		
		s_logger.debug("[" + BUNDLE_ID + "]" + " activated!");
	}

	protected void deactivate(ComponentContext componentContext) {
		s_logger.debug("[" + BUNDLE_ID + "]" + " deactivating...");

		registration.unregister();

		s_logger.debug("[" + BUNDLE_ID + "]" + " deactivated!");
	}
	
    protected void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
    }

    protected void unsetConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = null;
    }
}
