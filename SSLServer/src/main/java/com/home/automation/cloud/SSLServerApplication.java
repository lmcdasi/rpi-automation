package com.home.automation.cloud;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Message;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.CacheDirective;
import org.restlet.data.Header;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.util.Series;

import com.home.automation.cloud.config.Appliance;
import com.home.automation.cloud.config.CloudConfig;
import com.home.automation.cloud.devices.DeviceDiscovery;
import com.home.automation.cloud.devices.DeviceTurnOff;
import com.home.automation.cloud.devices.DeviceTurnOn;

public class SSLServerApplication extends Application implements Runnable {
	private static Logger logger = Logger.getLogger(SSLServerApplication.class.getCanonicalName());
	
	private static final String HEADERS_KEY = "org.restlet.http.headers";
	private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin"; 
    private static final String ALLOW_ALL_FROM_ORIGIN = "*"; 
    private static final String ACCESS_CONTROL_ALLOW_METHODS="Access-Control-Allow-Methods";
    private static final String ACCESS_CONTROL_ALLOW_POST="POST";
    
	Router router;
	
    public SSLServerApplication() {
    	logger.debug("In ModspaceRestApplication.ctor");
    }
    
    @Override
    public synchronized Restlet createInboundRoot() {
        router = new Router(getContext());

        logger.debug("In SSLServerApplication.createInboundRoot");

        router.attach(CloudConfig.getCloudConfiguration().getDiscover(), DeviceDiscovery.class);
        
        List<Appliance> appliances = CloudConfig.getCloudConfiguration().getAppliances();
        for (Appliance appliance : appliances) {
        	ArrayList<String> actions = appliance.getActions();
        	for (String action : actions) {
        		if (action.contains("turnon")) {
        			router.attach(action, DeviceTurnOn.class);
        		}
        		if (action.contains("turnoff")) {
        			router.attach(action, DeviceTurnOff.class);
        		}
        	}
        }
               
        return router;
    }
            
    @SuppressWarnings("unchecked") 
    public static Series<Header> configureRestForm(Message message) { 
        ConcurrentMap<String, Object> attrs = message.getAttributes(); 
        Series<Header> headers = (Series<Header>) attrs.get(HEADERS_KEY); 
 
        if (headers == null) { 
            headers = new Series<Header>(Header.class); 
            Series<Header> prev = (Series<Header>) attrs.putIfAbsent(HEADERS_KEY, headers); 
 
            if (prev != null) 
                headers = prev; 
        } 
 
        headers.add(ACCESS_CONTROL_ALLOW_ORIGIN, ALLOW_ALL_FROM_ORIGIN); 
        headers.add(ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_ALLOW_POST);
        message.getCacheDirectives().add(CacheDirective.noCache()); 
        return headers; 
    }

	@Override
	public void run() {
        Component component = new Component();
 
        Server server = component.getServers().add(Protocol.HTTPS, Integer.parseInt(CloudConfig.getCloudConfiguration().getServerPort())); 
        Series<Parameter> parameters = server.getContext().getParameters();
        parameters.add("sslContextFactory", "org.restlet.engine.ssl.DefaultSslContextFactory");
        
        parameters.add("keyStorePath", CloudConfig.getCloudConfiguration().getKeyStore());
        parameters.add("keyStorePassword", CloudConfig.getCloudConfiguration().getKeyPasswd());
        parameters.add("keyPassword", CloudConfig.getCloudConfiguration().getKeyPasswd());
        parameters.add("keyStoreType", CloudConfig.getCloudConfiguration().getStoreType());
        parameters.add("protocol", CloudConfig.getCloudConfiguration().getCloudProto());
    
        component.getDefaultHost().attach(CloudConfig.getCloudConfiguration().getHomeURL(), this);

        try {
			component.start();
		} catch (Exception ex) {
			logger.debug("Exception starting restlet component", ex);
		}
	}
	
    public static void main(String[] args) throws Exception {
    	(new Thread(new SSLServerApplication())).start();
    }
}
