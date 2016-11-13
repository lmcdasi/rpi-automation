package com.home.automation.cloud.devices;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.log4j.Logger;
import org.restlet.data.MediaType;
import org.restlet.ext.gson.GsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.home.automation.cloud.auth.AuthenticateDevice;
import com.home.automation.cloud.auth.AuthenticateState;
import com.home.automation.cloud.config.Appliance;
import com.home.automation.cloud.config.CloudConfig;
import com.home.automation.cloud.events.Request;

public class DeviceDiscovery extends ServerResource {
    private static Logger logger = Logger.getLogger(DeviceTurnOff.class.getCanonicalName());
    
    @Post("application/json")
    public Representation doPost(Representation entity) throws IOException {
        StringRepresentation stringRep = null;
    	GsonRepresentation<Request> represent = new GsonRepresentation<Request>(entity, Request.class);
    	Request req	= (Request) represent.getObject();
    	
    	AuthenticateDevice authDev = new AuthenticateDevice(req.getAccessToken());
		if (authDev.authDevice() == AuthenticateState.SUCCESS) {
			List<Appliance> appliances = CloudConfig.getCloudConfiguration().getAppliances();
			
			Type listOfApplianceObject = new TypeToken<List<Appliance>>(){}.getType();
			Gson gson = new Gson();
			stringRep = new StringRepresentation(gson.toJson(appliances, listOfApplianceObject), MediaType.APPLICATION_JSON);
			
			logger.info("Discovered Appliances: " + stringRep);
		} 
		
		return stringRep;
    }
}
